package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import sin.GameServer;
import sin.character.MovementManager;
import sin.weapons.RecoilManager;

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
        super.update(tpf);
        
        MovementManager.move(tpf);
    }
    
    @Override
    public void cleanup(){
        app.getRoot().detachChild(app.getWorld());
    }
}
