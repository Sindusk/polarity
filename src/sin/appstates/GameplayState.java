package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.hud.HUD;
import sin.network.Networking;
import sin.player.Char;
import sin.player.Player;
import sin.tools.T;
import sin.weapons.AmmoManager;
import sin.weapons.DamageManager;
import sin.weapons.ProjectileManager;
import sin.weapons.RecoilManager;
import sin.weapons.TracerManager;
import sin.weapons.Weapons.AK47;
import sin.weapons.Weapons.M4A1;
import sin.weapons.Weapons.Raygun;
import sin.weapons.Weapons.Sword;
import sin.world.Decals;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class GameplayState extends AbstractAppState {
    private static GameClient app;
    
    private static boolean CLIENT_KEYS_CLEARED = false; // Boolean for stupid keys.
    
    // Classes used for logic:
    private Char character;                     // Used for character (user) control.
    private Decals dcs = new Decals();          // Used for decals on the world.
    private HUD hud = new HUD();                // Used for GUI and HUD elements.
    private Player[] player = new Player[16];   // Used for networked player data.
    
    // All nodes used for use in the Gameplay:
    private Node root;      // Root Node.
    private Node gui;       // Node for 2D GUI.
    private Node world;     // Node for 3D world.
    
    // Other nodes:
    private Node collisionNode = new Node();    // Node encompassing anything able to be shot [single, world, player].
    private Node miscNode = new Node();         // Node encompassing all miscellaneous geometry [floating text].
    private Node singleNode = new Node();       // Node encompassing single player testing (Static).
    private Node playerNode = new Node();       // Node encompassing player models (Kinematic).
    private Node projectileNode = new Node();   // Node for all projectiles.
    private Node terrainNode = new Node();      // Node for all world terrain.
    private Node tracerNode = new Node();       // Node encompassing tracers, mainly for testing.
    
    public GameplayState(){
        //
    }
    
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
    
    public Char getCharacter(){
        return character;
    }
    public Decals getDCS(){
        return dcs;
    }
    public HUD getHUD(){
        return hud;
    }
    public Player getPlayer(int index){
        return player[index];
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application theApp){
        // Basic initialization:
        super.initialize(stateManager, theApp);
        GameplayState.app = (GameClient) theApp;
        
        // Create nodes:
        root = new Node("Gameplay_Root");
        world = new Node("Gameplay_World");
        gui = new Node("Gameplay_GUI");
        
        // Attach GUI and Root nodes:
        app.getInputManager().setCursorVisible(false);
        app.getRoot().attachChild(root);
        app.getGUI().attachChild(gui);
        
        // Initialize Projectiles:
        AmmoManager.initialize(app);
        DamageManager.initialize(app);
        ProjectileManager.initialize(app);
        RecoilManager.initialize(app);
        TracerManager.initialize(app);
        
        // Initialize HUD & World:
        World.initialize(app);
        World.createSinglePlayerArea(singleNode);
        hud.initialize(app, gui);
        dcs.initialize();
        
        character = new Char(
                new M4A1(true), new Sword(false),
                new Raygun(true), new AK47(false), 100, 100);
        world.attachChild(character.getNode());
        
        int i = 0;
        while(i < 16){
            player[i] = new Player(i, T.v3f(0, 10, 0));
            i++;
        }
        
        collisionNode.attachChild(singleNode);
        collisionNode.attachChild(playerNode);
        collisionNode.attachChild(terrainNode);
        world.attachChild(collisionNode);
        world.attachChild(dcs.getNode());
        world.attachChild(miscNode);
        world.attachChild(projectileNode);
        root.attachChild(world);
    }
    
    @Override
    public void cleanup(){
        super.cleanup();
        app.getRoot().detachChild(root);
        app.getGUI().detachChild(gui);
        gui.detachAllChildren();
        //world.detachChild(character.getNode());
        app.getInputManager().setCursorVisible(true);
    }
    
    @Override
    public void update(float tpf){
        super.update(tpf); // makes sure to execute AppTasks

        // Clear client keys which are buggy and annoying:
        if(!CLIENT_KEYS_CLEARED) {
            ClearClientKeys();
        }
        
        // Update audio listeners:
        app.getListener().setLocation(app.getCamera().getLocation());
        app.getListener().setRotation(app.getCamera().getRotation());
        
        // Update character location & hud:
        character.update(tpf);
        hud.update(tpf);
        ProjectileManager.update(tpf);
        
        // Update network if connected:
        if(Networking.isConnected()) {
            Networking.update(tpf);
        }
    }
    private void ClearClientKeys(){
        app.getInputManager().setCursorVisible(false);
        CLIENT_KEYS_CLEARED = true;
    }
}
