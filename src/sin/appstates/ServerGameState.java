package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import sin.GameServer;

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
    }
}
