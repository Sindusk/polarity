package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
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
    
    public Nifty getNifty(){
        return nifty;
    }
    
    public MenuState(){
        //
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        MenuState.app = (GameClient) app;
        
        // Initialize nifty display:
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                MenuState.app.getAssetManager(), MenuState.app.getInputManager(),
                MenuState.app.getAudioRenderer(), MenuState.app.getGuiViewPort());
        this.nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/GUI.xml", "menu");
        MenuState.app.getGuiViewPort().addProcessor(niftyDisplay);
        
        // Get rid of (most) logger spam:
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
    
    public void action(String action){
        // Main Menu:
        if(action.equals("start")){
            MenuState.app.getStateManager().attach(GameClient.getGameplayState());
            nifty.gotoScreen("empty");
        }else if(action.equals("multiplayer")){
            nifty.gotoScreen("menu.multiplayer");
        }else if(action.equals("options")){
            nifty.gotoScreen("menu.options");
        }else if(action.equals("quit")){
            MenuState.app.stop();
        }
        // Multiplayer Menu:
        else if(action.equals("multiplayer.connect")){
            ListBox<String> list = screen.findNiftyControl("multiplayer.serverlist", ListBox.class);
            String s = list.getSelection().get(0);
            String q = s.substring(1, s.indexOf(']'));
            GameClient.getLogger().info(q);
            if(GameClient.getNetwork().connect(q)){
                action("start");
            }
        }else if(action.equals("multiplayer.refresh")){
            Label label = screen.findNiftyControl("multiplayer.message", Label.class);
            label.setColor(Color.BLACK);
            label.setText("Not Yet Implemented!");
        }else if(action.equals("multiplayer.back")){
            nifty.gotoScreen("menu");
        }
        // Options Menu:
        else if(action.equals("options.back")){
            nifty.gotoScreen("menu");
        }
    }
    
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        if(screen.getScreenId().equals("menu.multiplayer")){
            ListBox<String> list = screen.findNiftyControl("multiplayer.serverlist", ListBox.class);
            list.addItem("[localhost] Local Server");
            list.addItem("[25.216.174.196] Sinister Server");
        }else if(screen.getScreenId().equals("menu.options")){
            DropDown<String> dd = screen.findNiftyControl("options.resolution", DropDown.class);
            dd.addItem("1024 x 720");
            dd.addItem("1680 x 1050");
        }
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onStartScreen() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onEndScreen() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
