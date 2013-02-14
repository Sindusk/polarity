package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.logging.Level;
import java.util.logging.Logger;
import sin.GameClient;

/**
 *
 * @author SinisteRing
 */
public class MenuState extends AbstractAppState implements ScreenController {
    private static GameClient app;
    
    private Nifty nifty;
    private Screen screen;
    
    public MenuState(){
        //
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        MenuState.app = (GameClient) app;
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                MenuState.app.getAssetManager(), MenuState.app.getInputManager(),
                MenuState.app.getAudioRenderer(), MenuState.app.getGuiViewPort());
        this.nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/MainMenu.xml", "start");
        MenuState.app.getGuiViewPort().addProcessor(niftyDisplay);
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE);
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE);
        Logger.getLogger("NiftyEventBusLog").setLevel(Level.SEVERE);
    }
    
    @Override
    public void cleanup(){
        super.cleanup();
    }
    
    @Override
    public void update(float tpf){
        super.update(tpf);
    }
    
    public void startGame(){
        MenuState.app.getStateManager().attach(GameClient.getGameplayState());
        nifty.exit();
    }
    
    public void quitGame(){
        GameClient.getLogger().info("meow");
        MenuState.app.stop();
    }

    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onStartScreen() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onEndScreen() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
