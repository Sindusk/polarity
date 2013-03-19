package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.character.CharacterScreen;
import sin.input.ClientInputHandler;

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
        
        root.attachChild(CharacterScreen.getNode());
        gui.attachChild(CharacterScreen.getGUI());
        
        // Initialize Input:
        CharacterScreen.initialize();
        ClientInputHandler.initializeChar();
    }
    
    @Override
    public void cleanup(){
        super.cleanup();
        root.removeFromParent();
        gui.removeFromParent();
    }
}
