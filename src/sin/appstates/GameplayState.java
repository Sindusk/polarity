/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.network.Networking;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class GameplayState extends AbstractAppState {
    private GameClient app;
    
    private static boolean CLIENT_KEYS_CLEARED = false; // Boolean for stupid keys.
    private Node root = new Node("Root_Gameplay");      // Root Node for Gameplay.
    private Node collisionNode = new Node();     // Node encompassing anything able to be shot [single, world, player].
    private Node singleNode = new Node();        // Node encompassing single player testing (Static).
    private Node worldNode = new Node();         // Node encompassing terrain and environment (Static).
    private Node playerNode = new Node();        // Node encompassing player models (Kinematic).
    private Node tracerNode = new Node();        // Node encompassing tracers, mainly for testing.
    
    public GameplayState(){
        //
    }
    
    public Node getRoot(){
        return root;
    }
    public Node getCollisionNode(){
        return collisionNode;
    }
    public Node getSingleNode(){
        return singleNode;
    }
    public Node getWorld(){
        return worldNode;
    }
    public Node getPlayerNode(){
        return playerNode;
    }
    public Node getTracerNode(){
        return tracerNode;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        this.app = (GameClient) app;
        GameClient.getRoot().attachChild(root);
        World.createSinglePlayerArea(singleNode);
        collisionNode.attachChild(singleNode);
        collisionNode.attachChild(worldNode);
        collisionNode.attachChild(playerNode);
        root.attachChild(collisionNode);
    }
    
    @Override
    public void cleanup(){
        super.cleanup();
        GameClient.getRoot().detachChild(root);
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
        GameClient.getCharacter().update(tpf);
        GameClient.getHUD().update(tpf);
        
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
