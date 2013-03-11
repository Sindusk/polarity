package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.logging.Level;
import java.util.logging.Logger;
import sin.GameServer;
import sin.network.ServerNetwork;
import sin.npc.NPCManager;
import sin.player.PlayerManager;
import sin.tools.S;
import sin.weapons.ProjectileManager;
import sin.world.DecalManager;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class ServerMenuState extends AbstractAppState implements ScreenController{
    public static GameServer app;
    
    // Nifty:
    private Nifty nifty;
    private Screen screen;
    
    // Nodes:
    private Node collisionNode = new Node("CollisionNode");
    private Node world = new Node("World");
    
    public Nifty getNifty(){
        return nifty;
    }
    public Screen getScreen(){
        return screen;
    }
    public Node getWorld(){
        return world;
    }
    
    public void toggleGameMenu(){
        if(nifty.getCurrentScreen().getScreenId().equals("game.menu")){
            nifty.gotoScreen("empty");
            app.getInputManager().setCursorVisible(false);
        }else{
            nifty.gotoScreen("game.menu");
            app.getInputManager().setCursorVisible(true);
        }
    }
    public void action(String action){
        // Main Menu:
        if(action.equals("menu.game")){
            app.getStateManager().attach(app.getGameState());
            app.getInputManager().setCursorVisible(false);
            nifty.gotoScreen("empty");
        }else if(action.equals("menu.quit")){
            app.stop();
        }
        // Game Menu:
        else if(action.equals("game.return")){
            nifty.gotoScreen("empty");
            app.getInputManager().setCursorVisible(false);
        }else if(action.equals("game.mainmenu")){
            app.getStateManager().detach(app.getGameState());
            nifty.gotoScreen("menu");
        }
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        ServerMenuState.app = (GameServer) app;
        
        //Turn off the super annoying loggers:
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE);
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE);
        Logger.getLogger("NiftyEventBusLog").setLevel(Level.SEVERE);
        Logger.getAnonymousLogger().getParent().setLevel(Level.SEVERE);
        Logger.getLogger("de.lessvoid.nifty.*").setLevel(Level.SEVERE);
        
        // Initialize nifty display:
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                ServerMenuState.app.getAssetManager(), ServerMenuState.app.getInputManager(),
                ServerMenuState.app.getAudioRenderer(), ServerMenuState.app.getGuiViewPort());
        this.nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/ServerGUI.xml", "menu");
        ServerMenuState.app.getGuiViewPort().addProcessor(niftyDisplay);
        
        ServerNetwork.create();
        
        // Initialize Nodes:
        collisionNode.attachChild(NPCManager.getNode());
        collisionNode.attachChild(PlayerManager.getNode());
        world.attachChild(ProjectileManager.getNode());
        world.attachChild(DecalManager.getNode());
        world.attachChild(collisionNode);
        
        S.setCollisionNode(collisionNode);
        ProjectileManager.initialize(collisionNode);
        
        // Create world:
        World.generateWorldData();
        int i = 0;
        while(i < World.getMap().size()){
            World.createGeometry(collisionNode, World.getMap().get(i));
            i++;
        }
    }
    
    @Override
    public void update(float tpf){
        super.update(tpf);  // Execute AppTasks.
        
        PlayerManager.update(tpf);
        ProjectileManager.update(tpf, true);
    }
    
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
        if(screen.getScreenId().equals("menu")){
            Label t = screen.findNiftyControl("menu.version", Label.class);
            t.setText(app.getVersion());
        }else if(screen.getScreenId().equals("console")){
            Label t = screen.findNiftyControl("console.version", Label.class);
            t.setText(app.getVersion());
        }
    }
    
    public void onStartScreen() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void onEndScreen() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
