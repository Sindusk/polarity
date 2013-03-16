package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.animation.Models.PlayerModel;
import sin.input.ClientInputHandler;
import sin.tools.S;

/**
 *
 * @author SinisteRing
 */
public class ClientCharState extends AbstractAppState {
    private static GameClient app;
    
    // All nodes used in Character screen:
    private Node root;      // Root Node.
    private Node gui;       // Node for 2D GUI.
    
    @Override
    public void initialize(AppStateManager stateManager, Application theApp){
        // Basic initialization:
        super.initialize(stateManager, theApp);
        app = (GameClient) theApp;
        
        // Initialize Nodes:
        root = new Node("Character_Root");
        gui = new Node("Character_GUI");
        app.getRoot().attachChild(root);
        app.getGUI().attachChild(gui);
        
        S.setCollisionNode(root);
        
        PlayerModel model = new PlayerModel(0, root);
        //CG.createBox(root, "meow", new Vector3f(1, 1, 1), new Vector3f(0, 0, 0), T.getMaterialPath("wall"), new Vector2f(1, 1));
        
        // Initialize Input:
        ClientInputHandler.initializeChar();
    }
    
    @Override
    public void cleanup(){
        super.cleanup();
        root.removeFromParent();
        gui.removeFromParent();
    }
}
