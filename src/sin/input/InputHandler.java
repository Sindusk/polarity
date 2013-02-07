/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.input;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.JmeContext;
import sin.GameClient;
import sin.network.Networking;
import sin.player.Char;
import sin.tools.T;

/**
 *
 * @author SinisteRing
 */
public class InputHandler implements ActionListener, AnalogListener{
    private static GameClient app;
    
    // Constant Variables:
    public static final float MOUSE_SENSITIVITY = 1;
    public void onAction(String bind, boolean down, float tpf) {
        // Movement:
        if(bind.equals("Move_Left")){
            GameClient.getCharacter().movement[Char.MOVE_LEFT] = down;
        }else if(bind.equals("Move_Right")){
            GameClient.getCharacter().movement[Char.MOVE_RIGHT] = down;
        }else if(bind.equals("Move_Forward")){
            GameClient.getCharacter().movement[Char.MOVE_FORWARD] = down;
        }else if(bind.equals("Move_Backward")){
            GameClient.getCharacter().movement[Char.MOVE_BACKWARD] = down;
        }else if(bind.equals("Move_Crouch")){
            GameClient.getCharacter().movement[Char.MOVE_CROUCH] = down;
        }else if(bind.equals("Move_Jump") && down){
            GameClient.getCharacter().getPlayer().jump();
        }
        // Actions:
        else if(bind.equals("Trigger_Right")){
            GameClient.getCharacter().setFiring(false, down);
        }else if(bind.equals("Trigger_Left")){
            GameClient.getCharacter().setFiring(true, down);
        }else if(bind.equals("Trigger_Reload")){
            if(down) {
                GameClient.getCharacter().reload();
            }
        }
        if(down){
            // Weapon Swapping:
            if(bind.equals("Swap_1")){
                GameClient.getCharacter().swapGuns();
            }
            // Miscellaneous:
            else if(bind.equals("Server_Connect")){
                // Networking:
                GameClient.getNetwork().connect();
            }else if(bind.equals("Misc_Key_1")){
                if(GameClient.getRoot().hasChild(GameClient.getTracerNode())) {
                    GameClient.getRoot().detachChild(GameClient.getTracerNode());
                }
                else {
                    GameClient.getRoot().attachChild(GameClient.getTracerNode());
                }
            }else if(bind.equals("Misc_Key_2")){
                GameClient.getTracerNode().detachAllChildren();
                GameClient.getDCS().resetDecals();
            }else if(bind.equals("Misc_Key_3")){
                GameClient.getPlayer(4).create();
                GameClient.getPlayer(4).move(T.v3f(0, 105, -45), Quaternion.ZERO);
            }else if(bind.equals("Misc_Key_4")){
                //hud.bar[0].update(30);
            }else if(bind.equals("Exit")){
                app.stop();
            }
        }
    }

    public void onAnalog(String name, float value, float tpf) {
        // Camera:
        if (name.equals("Cam_Left")){
            GameClient.getRecoil().rotateCamera(value, MOUSE_SENSITIVITY, Vector3f.UNIT_Y);
        }else if (name.equals("Cam_Right")){
            GameClient.getRecoil().rotateCamera(-value, MOUSE_SENSITIVITY, Vector3f.UNIT_Y);
        }else if (name.equals("Cam_Up")){
            GameClient.getRecoil().rotateCamera(-value, MOUSE_SENSITIVITY, app.getCamera().getLeft());
        }else if (name.equals("Cam_Down")){
            GameClient.getRecoil().rotateCamera(value, MOUSE_SENSITIVITY, app.getCamera().getLeft());
        }
    }

    private void createMapping(String name, KeyTrigger trigger){
        app.getInputManager().addMapping(name, trigger);
        app.getInputManager().addListener(this, name);
    }
    private void createMapping(String name, MouseButtonTrigger trigger){
        app.getInputManager().addMapping(name, trigger);
        app.getInputManager().addListener(this, name);
    }
    private void createMapping(String name, MouseAxisTrigger trigger){
        app.getInputManager().addMapping(name, trigger);
        app.getInputManager().addListener(this, name);
    }
    public void initialize(GameClient app, JmeContext context){
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
        // Actions:
        createMapping("Trigger_Left", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        createMapping("Trigger_Right", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        createMapping("Trigger_Reload", new KeyTrigger(KeyInput.KEY_R));
        // Weapon Swapping:
        createMapping("Swap_1", new KeyTrigger(KeyInput.KEY_Q));
        // Miscellaneous:
        createMapping("Server_Connect", new KeyTrigger(KeyInput.KEY_C));
        createMapping("Game_Menu", new KeyTrigger(KeyInput.KEY_ESCAPE));
        createMapping("Misc_Key_1", new KeyTrigger(KeyInput.KEY_V));
        createMapping("Misc_Key_2", new KeyTrigger(KeyInput.KEY_B));
        createMapping("Misc_Key_3", new KeyTrigger(KeyInput.KEY_O));
        createMapping("Misc_Key_4", new KeyTrigger(KeyInput.KEY_I));
        // Menu/Swapping:
        if(context.getType() == JmeContext.Type.Display){
            createMapping("Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
        }
    }
}
