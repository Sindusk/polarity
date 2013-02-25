package sin.input;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.system.JmeContext;
import sin.GameClient;
import sin.hud.HUD;
import sin.character.MovementManager;
import sin.character.MovementManager.MH;
import sin.weapons.RecoilManager;
import sin.weapons.TracerManager;
import sin.world.DecalManager;

/**
 * InputHandler - Handles all input from users and organizes them based on conditions.
 * @author SinisteRing
 */
public class InputHandler{
    private static GameClient app;
    
    // Constant Variables:
    public static final float MOUSE_SENSITIVITY = 1;
    
    private static boolean inGameplay(){
        return app.getStateManager().hasState(app.getGameplayState()) && app.getMenuState().getNifty().getCurrentScreen().getScreenId().equals("empty");
    }
    
    private static ActionListener gameplayAction = new ActionListener(){
        public void onAction(String bind, boolean down, float tpf){
            if(!inGameplay()){
                return;
            }
            // Movement:
            if(bind.equals("Move_Left")){
                MovementManager.setMove(MH.LEFT, down);
            }else if(bind.equals("Move_Right")){
                MovementManager.setMove(MH.RIGHT, down);
            }else if(bind.equals("Move_Forward")){
                MovementManager.setMove(MH.FORWARD, down);
            }else if(bind.equals("Move_Backward")){
                MovementManager.setMove(MH.BACKWARD, down);
            }else if(bind.equals("Move_Crouch")){
                MovementManager.setMove(MH.CROUCH, down);
            }else if(bind.equals("Move_Jump") && down){
                app.getCharacter().getPlayer().jump();
            }
            // Actions:
            else if(bind.equals("Trigger_Right")){
                app.getCharacter().setFiring(false, down);
            }else if(bind.equals("Trigger_Left")){
                app.getCharacter().setFiring(true, down);
            }
            if(down){
                // Weapon Swapping:
                if(bind.equals("Swap")){
                    app.getCharacter().swapGuns();
                }else if(bind.equals("Reload")){
                    app.getCharacter().reload();
                }
                // Miscellaneous:
                else if(bind.equals("Misc_Key_1")){
                    TracerManager.toggle();
                }else if(bind.equals("Misc_Key_2")){
                    TracerManager.clear();
                    DecalManager.resetDecals();
                }else if(bind.equals("Misc_Key_3")){
                    //
                }else if(bind.equals("Misc_Key_4")){
                    //
                }else if(bind.equals("Game_Menu")){
                    app.getMenuState().getNifty().gotoScreen("game.menu");
                    app.getInputManager().setCursorVisible(true);
                    HUD.showCrosshairs(false);
                }
            }
        }
    };
    private static AnalogListener gameplayAnalog = new AnalogListener(){
        public void onAnalog(String name, float value, float tpf) {
            if(!inGameplay()){
                return;
            }
            // Camera:
            if (name.equals("Cam_Left")){
                RecoilManager.rotateCamera(value, MOUSE_SENSITIVITY, Vector3f.UNIT_Y);
            }else if (name.equals("Cam_Right")){
                RecoilManager.rotateCamera(-value, MOUSE_SENSITIVITY, Vector3f.UNIT_Y);
            }else if (name.equals("Cam_Up")){
                RecoilManager.rotateCamera(-value, MOUSE_SENSITIVITY, app.getCamera().getLeft());
            }else if (name.equals("Cam_Down")){
                RecoilManager.rotateCamera(value, MOUSE_SENSITIVITY, app.getCamera().getLeft());
            }
        }
    };
    
    private static void createMapping(String name, KeyTrigger trigger){
        app.getInputManager().addMapping(name, trigger);
        app.getInputManager().addListener(gameplayAction, name);
    }
    private static void createMapping(String name, MouseButtonTrigger trigger){
        app.getInputManager().addMapping(name, trigger);
        app.getInputManager().addListener(gameplayAction, name);
    }
    private static void createMapping(String name, MouseAxisTrigger trigger){
        app.getInputManager().addMapping(name, trigger);
        app.getInputManager().addListener(gameplayAnalog, name);
    }
    
    public static void initialize(GameClient app, JmeContext context){
        InputHandler.app = app;
        // Camera:
        createMapping("Cam_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        createMapping("Cam_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        createMapping("Cam_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        createMapping("Cam_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        // Movement:
        createMapping("Move_Left", new KeyTrigger(KeyInput.KEY_A));
        createMapping("Move_Right", new KeyTrigger(KeyInput.KEY_D));
        createMapping("Move_Forward", new KeyTrigger(KeyInput.KEY_W));
        createMapping("Move_Backward", new KeyTrigger(KeyInput.KEY_S));
        createMapping("Move_Crouch", new KeyTrigger(KeyInput.KEY_LCONTROL));
        createMapping("Move_Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        // Attacks:
        createMapping("Trigger_Left", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        createMapping("Trigger_Right", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        // Actions:
        createMapping("Reload", new KeyTrigger(KeyInput.KEY_R));
        createMapping("Swap", new KeyTrigger(KeyInput.KEY_Q));
        createMapping("Ability_1", new KeyTrigger(KeyInput.KEY_1));
        createMapping("Ability_2", new KeyTrigger(KeyInput.KEY_2));
        createMapping("Ability_3", new KeyTrigger(KeyInput.KEY_3));
        createMapping("Ability_4", new KeyTrigger(KeyInput.KEY_4));
        // Miscellaneous:
        createMapping("Game_Menu", new KeyTrigger(KeyInput.KEY_ESCAPE));
        createMapping("Misc_Key_1", new KeyTrigger(KeyInput.KEY_V));
        createMapping("Misc_Key_2", new KeyTrigger(KeyInput.KEY_B));
        createMapping("Misc_Key_3", new KeyTrigger(KeyInput.KEY_O));
        createMapping("Misc_Key_4", new KeyTrigger(KeyInput.KEY_I));
    }
}
