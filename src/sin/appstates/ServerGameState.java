package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import sin.GameServer;
import sin.weapons.RecoilManager;
import sin.world.CG;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class ServerGameState extends AbstractAppState{
    public static GameServer app;
    
    private Node root;
    private Node gui;
    
    @Override
    public void initialize(AppStateManager stateManager, Application theApp){
        super.initialize(stateManager, theApp);
        app = (GameServer) theApp;
        
        // Create nodes:
        root = new Node("Game_Root");
        gui = new Node("Game_GUI");
        
        // Attach Root and GUI:
        app.getRoot().attachChild(root);
        app.getGUI().attachChild(gui);
        
        // Initialize classes:
        CG.initialize(app.getBulletAppState());
        RecoilManager.initialize(app.getCamera());
        
        // Create world:
        int i = 0;
        while(i < World.getMap().size()){
            World.createGeometry(root, World.getMap().get(i));
            i++;
        }
    }
}
