package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.animation.Models;
import sin.hud.HUD;
import sin.network.Networking;
import sin.character.Character;
import sin.character.MovementManager;
import sin.character.StatsManager;
import sin.npc.NPCManager;
import sin.weapons.AmmoManager;
import sin.weapons.AttackManager;
import sin.weapons.ProjectileManager;
import sin.weapons.RecoilManager;
import sin.world.TracerManager;
import sin.weapons.Weapons.AK47;
import sin.weapons.Weapons.M4A1;
import sin.weapons.Weapons.Raygun;
import sin.weapons.Weapons.RocketLauncher;
import sin.world.DecalManager;
import sin.world.World;

/**
 * Client Game State - AppState which controls the in-game functions for Polarity.
 * @author SinisteRing
 */
public class ClientGameState extends AbstractAppState{
    private static GameClient app;
    
    // All nodes used in the Gameplay:
    private Node root;      // Root Node (3D).
    private Node gui;       // GUI Node (2D).
    
    // Other nodes:
    private Node collisionNode = new Node("");  // Node encompassing anything able to be shot [single, world, player].
    private Node miscNode = new Node("");       // Node encompassing all miscellaneous geometry [floating text].
    private Node singleNode = new Node("");     // Node encompassing single player testing (Static).
    private Node playerNode = new Node("");     // Node encompassing player models (Kinematic).
    private Node projectileNode = new Node(""); // Node for all projectiles.
    private Node terrainNode = new Node("");    // Node for all world terrain.
    private Node tracerNode = new Node("");     // Node encompassing tracers, mainly for testing.
    private Node world = new Node("");          // Node encompassing all world objects.
    
    public ClientGameState(){}
    
    public Node getRoot(){
        return root;
    }
    public Node getWorld(){
        return world;
    }
    public Node getCollisionNode(){
        return collisionNode;
    }
    public Node getMiscNode(){
        return miscNode;
    }
    public Node getSingleNode(){
        return singleNode;
    }
    public Node getPlayerNode(){
        return playerNode;
    }
    public Node getProjectileNode(){
        return projectileNode;
    }
    public Node getTerrainNode(){
        return terrainNode;
    }
    public Node getTracerNode(){
        return tracerNode;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application theApp){
        // Basic initialization:
        super.initialize(stateManager, theApp);
        app = (GameClient) theApp;
        
        // Create nodes:
        root = new Node("Game_Root");
        gui = new Node("Game_GUI");
        
        // Attach GUI and Root nodes:
        app.getInputManager().setCursorVisible(false);
        app.getRoot().attachChild(root);
        app.getGUI().attachChild(gui);
        
        // Initialize Projectiles:
        AmmoManager.initialize(app);
        Character.initialize(app);
        AttackManager.initialize(app);
        DecalManager.initialize(app);
        HUD.initialize(app, gui);
        Models.initialize(app);
        MovementManager.initialize(app.getCamera());
        NPCManager.initialize(app);
        ProjectileManager.initialize(app);
        RecoilManager.initialize(app.getCamera());
        TracerManager.initialize(app);
        World.initialize(app);
        
        // Initialize HUD & World:
        World.createSinglePlayerArea(singleNode);
        
        Character.create(
                new M4A1(true), new RocketLauncher(false),
                new Raygun(true), new AK47(false), 100, 100);
        world.attachChild(Character.getNode());
        
        collisionNode.attachChild(singleNode);
        collisionNode.attachChild(playerNode);
        collisionNode.attachChild(terrainNode);
        world.attachChild(collisionNode);
        world.attachChild(DecalManager.getNode());
        world.attachChild(miscNode);
        world.attachChild(projectileNode);
        root.attachChild(world);
        
        app.getInputManager().setCursorVisible(false);
    }
    
    @Override
    public void cleanup(){
        super.cleanup();
        app.getRoot().detachChild(root);
        World.clear();
        DecalManager.clear();
        app.resetBulletAppState();
        app.getGUI().detachChild(gui);
        HUD.clear();
        //gui.detachAllChildren();
        //world.detachChild(character.getNode());
        app.getInputManager().setCursorVisible(true);
    }
    
    @Override
    public void update(float tpf){
        super.update(tpf); // makes sure to execute AppTasks
        
        // Update audio listeners:
        app.getListener().setLocation(app.getCamera().getLocation());
        app.getListener().setRotation(app.getCamera().getRotation());
        
        // Update character location & hud:
        Character.update(tpf);
        HUD.update(tpf);
        ProjectileManager.update(tpf);
        
        // Update network if connected:
        if(Networking.isConnected()) {
            Networking.update(tpf);
        }
    }
}
