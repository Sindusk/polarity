package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import sin.GameServer;
import sin.character.MovementManager;
import sin.tools.Tile;
import sin.weapons.RecoilManager;
import sin.world.CG;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class ServerGameState extends AbstractAppState{
    public static GameServer app;
    
    @Override
    public void initialize(AppStateManager stateManager, Application theApp){
        super.initialize(stateManager, theApp);
        app = (GameServer) theApp;
        
        // Attach Nodes:
        app.getRoot().attachChild(app.getWorld());
        
        // Initialize classes:
        MovementManager.initialize(app.getCamera());
        MovementManager.setGrounded(false);
        RecoilManager.initialize(app.getCamera());
    }
    
    @Override
    public void update(float tpf){
        MovementManager.move(tpf);
    }
    
    @Override
    public void cleanup(){
        app.getRoot().detachChild(app.getWorld());
    }
}
