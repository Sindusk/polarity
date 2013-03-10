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
import java.util.logging.Level;
import java.util.logging.Logger;
import sin.GameClient;
import sin.hud.HUD;
import sin.network.ClientNetwork;

/**
 *
 * @author SinisteRing
 */
public class ClientMenuState extends AbstractAppState implements ScreenController {
    private static GameClient app;
    
    private Nifty nifty;
    private Screen screen;
    
    public Nifty getNifty(){
        return nifty;
    }
    
    public ClientMenuState(){}
    
    public void toggleGameMenu(){
        if(nifty.getCurrentScreen().getScreenId().equals("game.menu")){
            nifty.gotoScreen("empty");
            app.getInputManager().setCursorVisible(false);
            HUD.showCrosshairs(true);
        }else{
            nifty.gotoScreen("game.menu");
            app.getInputManager().setCursorVisible(true);
            HUD.showCrosshairs(false);
        }
    }
    public void action(String action){
        // Main Menu:
        if(action.equals("start")){
            app.getStateManager().attach(app.getGameplayState());
            nifty.gotoScreen("empty");
        }
        else if(action.equals("single")){
            action("start");
        }else if(action.equals("multiplayer")){
            nifty.gotoScreen("menu.multiplayer");
        }else if(action.equals("character")){
            nifty.gotoScreen("menu.character");
        }else if(action.equals("options")){
            nifty.gotoScreen("menu.options");
        }else if(action.equals("quit")){
            app.stop();
        }
        // Multiplayer Menu:
        else if(action.equals("multiplayer.connect")){
            ListBox<String> list = screen.findNiftyControl("multiplayer.serverlist", ListBox.class);
            String s = list.getSelection().get(0);
            String q = s.substring(1, s.indexOf(']'));
            Label label = screen.findNiftyControl("multiplayer.message", Label.class);
            label.setText("Contacting Server: "+q+"...");
            if(ClientNetwork.connect(q)){
                label.setText("Connection Successful!");
                action("start");
            }else{
                label.setText("Connection Failed.");
            }
        }else if(action.equals("multiplayer.refresh")){
            Label label = screen.findNiftyControl("multiplayer.message", Label.class);
            label.setText("Not Yet Implemented!");
        }else if(action.equals("multiplayer.back")){
            nifty.gotoScreen("menu");
        }
        // Character Menu:
        else if(action.equals("character.back")){
            nifty.gotoScreen("menu");
        }
        // Options Menu:
        else if(action.equals("options.back")){
            nifty.gotoScreen("menu");
        }
        // Game Menu:
        else if(action.equals("game.return")){
            nifty.gotoScreen("empty");
            app.getInputManager().setCursorVisible(false);
            HUD.showCrosshairs(true);
        }else if(action.equals("game.mainmenu")){
            app.getStateManager().detach(app.getGameplayState());
            nifty.gotoScreen("menu");
            ClientNetwork.disconnect();
        }
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        ClientMenuState.app = (GameClient) app;
        
        //Turn off the super annoying loggers:
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE);
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE);
        Logger.getLogger("NiftyEventBusLog").setLevel(Level.SEVERE);
        Logger.getAnonymousLogger().getParent().setLevel(Level.SEVERE);
        Logger.getLogger("de.lessvoid.nifty.*").setLevel(Level.SEVERE);
        
        // Initialize nifty display:
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                ClientMenuState.app.getAssetManager(), ClientMenuState.app.getInputManager(),
                ClientMenuState.app.getAudioRenderer(), ClientMenuState.app.getGuiViewPort());
        this.nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/ClientGUI.xml", "menu");
        ClientMenuState.app.getGuiViewPort().addProcessor(niftyDisplay);
    }
    
    @Override
    public void cleanup(){
        super.cleanup();
    }
    
    @Override
    public void update(float tpf){
        super.update(tpf);
    }
    
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        if(screen.getScreenId().equals("menu")){
            Label t = screen.findNiftyControl("menu.version", Label.class);
            t.setText(app.getVersion());
        }else if(screen.getScreenId().equals("menu.multiplayer")){
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
