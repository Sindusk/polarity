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
import sin.GameServer;
import sin.character.MovementManager;
import sin.character.MovementManager.MH;
import sin.tools.T;
import sin.weapons.RecoilManager;
import sin.world.TracerManager;
import sin.world.DecalManager;
import sin.world.World;

/**
 * ClientInputHandler - Handles all input from users and organizes them based on conditions.
 * @author SinisteRing
 */
public class ServerInputHandler{
    private static GameServer app;
    
    // Constant Variables:
    public static final float MOUSE_SENSITIVITY = 1;
    
    private static boolean inGameplay(){
        return app.getStateManager().hasState(app.getGameState()) && app.getMenuState().getNifty().getCurrentScreen().getScreenId().equals("empty");
    }
    
    private static ActionListener gameAction = new ActionListener(){
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
                //app.getCharacter().getPlayer().jump();
            }
            // Actions:
            else if(bind.equals("Trigger_Right")){
                //app.getCharacter().setFiring(false, down);
            }else if(bind.equals("Trigger_Left")){
                //app.getCharacter().setFiring(true, down);
            }
            if(down){
                // Weapon Swapping:
                if(bind.equals("Swap")){
                    //app.getCharacter().swapGuns();
                }else if(bind.equals("Reload")){
                    //app.getCharacter().reload();
                }
                // Miscellaneous:
                else if(bind.equals("Misc_Key_1")){
                    TracerManager.toggle();
                }else if(bind.equals("Misc_Key_2")){
                    TracerManager.clear();
                    DecalManager.clear();
                }else if(bind.equals("Misc_Key_3")){
                    //app.getCharacter().getPlayer().setPhysicsLocation(T.v3f(0, 110, 0));
                }else if(bind.equals("Misc_Key_4")){
                    World.toggleWireframe();
                }else if(bind.equals("Game_Menu")){
                    //app.getMenuState().toggleGameMenu();
                }
            }
        }
    };
    private static AnalogListener gameAnalog = new AnalogListener(){
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
    
    public static void initialize(GameServer app){
        ServerInputHandler.app = app;
        // Camera:
        T.createMapping(gameAnalog, "Cam_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        T.createMapping(gameAnalog, "Cam_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        T.createMapping(gameAnalog, "Cam_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        T.createMapping(gameAnalog, "Cam_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        // Movement:
        T.createMapping(gameAction, "Move_Left", new KeyTrigger(KeyInput.KEY_A));
        T.createMapping(gameAction, "Move_Right", new KeyTrigger(KeyInput.KEY_D));
        T.createMapping(gameAction, "Move_Forward", new KeyTrigger(KeyInput.KEY_W));
        T.createMapping(gameAction, "Move_Backward", new KeyTrigger(KeyInput.KEY_S));
        T.createMapping(gameAction, "Move_Crouch", new KeyTrigger(KeyInput.KEY_LCONTROL));
        T.createMapping(gameAction, "Move_Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        // Attacks:
        T.createMapping(gameAction, "Trigger_Left", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        T.createMapping(gameAction, "Trigger_Right", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        // Actions:
        T.createMapping(gameAction, "Reload", new KeyTrigger(KeyInput.KEY_R));
        T.createMapping(gameAction, "Swap", new KeyTrigger(KeyInput.KEY_Q));
        // Abilities:
        T.createMapping(gameAction, "Ability_1", new KeyTrigger(KeyInput.KEY_1));
        T.createMapping(gameAction, "Ability_2", new KeyTrigger(KeyInput.KEY_2));
        T.createMapping(gameAction, "Ability_3", new KeyTrigger(KeyInput.KEY_3));
        T.createMapping(gameAction, "Ability_4", new KeyTrigger(KeyInput.KEY_4));
        // Miscellaneous:
        T.createMapping(gameAction, "Game_Menu", new KeyTrigger(KeyInput.KEY_ESCAPE));
        T.createMapping(gameAction, "Misc_Key_1", new KeyTrigger(KeyInput.KEY_V));
        T.createMapping(gameAction, "Misc_Key_2", new KeyTrigger(KeyInput.KEY_B));
        T.createMapping(gameAction, "Misc_Key_3", new KeyTrigger(KeyInput.KEY_T));
        T.createMapping(gameAction, "Misc_Key_4", new KeyTrigger(KeyInput.KEY_G));
    }
}
