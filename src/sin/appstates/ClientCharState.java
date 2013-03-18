package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.animation.Models.PlayerModel;
import sin.character.CharacterScreen;
import sin.input.ClientInputHandler;
import sin.tools.S;
import sin.world.CG;

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
        
        CharacterScreen.initialize(app, root);
        
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
