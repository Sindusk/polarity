package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.hud.HUD;
import sin.network.ClientNetwork;
import sin.player.MovementManager;
import sin.player.PlayerManager;
import sin.world.FloatingTextManager;
import sin.npc.NPCManager;
import sin.tools.S;
import sin.weapons.AttackManager;
import sin.weapons.ProjectileManager;
import sin.weapons.Weapons;
import sin.world.TracerManager;
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
        
        // Initialize classes:
        S.setCollisionNode(collisionNode);
        AttackManager.initialize(app.getCamera(), collisionNode);
        HUD.initialize(app, gui);
        MovementManager.initialize(app.getCamera());
        ProjectileManager.initialize(collisionNode);
        TracerManager.initialize(app);
        
        // Initialize HUD & World:
        World.createSinglePlayerArea(singleNode);
        
        collisionNode.attachChild(NPCManager.getNode());
        collisionNode.attachChild(PlayerManager.getNode());
        collisionNode.attachChild(singleNode);
        collisionNode.attachChild(terrainNode);
        world.attachChild(collisionNode);
        world.attachChild(DecalManager.getNode());
        world.attachChild(FloatingTextManager.getNode());
        world.attachChild(miscNode);
        world.attachChild(ProjectileManager.getNode());
        world.attachChild(Weapons.getNode());
        root.attachChild(world);
        
        app.getInputManager().setCursorVisible(false);
    }
    
    @Override
    public void cleanup(){
        super.cleanup();
        root.removeFromParent();
        //app.getRoot().detachChild(root);
        Weapons.getNode().detachAllChildren();
        terrainNode.detachAllChildren();
        DecalManager.clear();
        app.resetBulletAppState();
        app.getGUI().detachChild(gui);
        HUD.clear();
        app.getInputManager().setCursorVisible(true);
    }
    
    @Override
    public void update(float tpf){
        super.update(tpf); // makes sure to execute AppTasks
        
        // Update audio listeners:
        app.getListener().setLocation(app.getCamera().getLocation());
        app.getListener().setRotation(app.getCamera().getRotation());
        
        // Update character location & hud:
        if(ClientNetwork.isConnected()){
            HUD.update(tpf);
            PlayerManager.update(tpf);
            NPCManager.update(tpf);
        }
        ProjectileManager.update(tpf, false);
        
        // Update network if connected:
        if(ClientNetwork.isConnected()) {
            ClientNetwork.update(tpf);
        }
    }
}
