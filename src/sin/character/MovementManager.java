package sin.character;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import java.util.HashMap;
import sin.tools.T;

/**
 * MovementManager - Used for the control and handling of all player movement.
 * @author SinisteRing
 */
public class MovementManager {
    //private static GameClient app;
    private static Camera cam;
    
    // Constant Variables:
    private static final float MOVE_SPEED = 0.50f;
    private static final float CROUCH_PENALTY = 0.6f;
    
    private static HashMap<MH, Boolean> movement = new HashMap();
    
    public static enum MH{
        FORWARD, RIGHT, BACKWARD, LEFT, CROUCH
    }
    
    public static boolean getMove(MH handle){
        return movement.get(handle);
    }
    public static void setMove(MH handle, boolean b){
        movement.put(handle, b);
    }
    
    public static void move(){
        // Initialize temporary variables:
        Vector3f dir;
        Vector3f wd = T.v3f(0, 0, 0);
        float x, z, angle;
        double rad;
        // Calculate forward/backward points:
        if(getMove(MH.FORWARD) || getMove(MH.BACKWARD)){
            dir = cam.getDirection();
            x = dir.getZ();
            z = dir.getX();
            angle = (float) Math.toDegrees(Math.atan2(x/2 - x, z/2 - z));
            if(angle < 0){
                angle += 360;
            }
            rad = Math.toRadians(angle);
            float xWard = (float) -Math.cos(rad) * MOVE_SPEED;
            float zWard = (float) -Math.sin(rad) * MOVE_SPEED;
            if(getMove(MH.FORWARD)) {
                wd.addLocal(xWard, 0, zWard);
            }
            if(getMove(MH.BACKWARD)) {
                wd.addLocal(-xWard, 0, -zWard);
            }
        }
        // Calculate sidestep points:
        if(getMove(MH.LEFT) || getMove(MH.RIGHT)){
            dir = cam.getLeft();
            x = dir.getZ();
            z = dir.getX();
            angle = (float) Math.toDegrees(Math.atan2(x/2 - x, z/2 - z));
            if(angle < 0){
                angle += 360;
            }
            rad = Math.toRadians(angle);
            float xSide = (float) -Math.cos(rad) * MOVE_SPEED;
            float zSide = (float) -Math.sin(rad) * MOVE_SPEED;
            if(getMove(MH.LEFT)) {
                wd.addLocal(xSide, 0, zSide);
            }
            if(getMove(MH.RIGHT)) {
                wd.addLocal(-xSide, 0, -zSide);
            }
        }

        // If moving at a diagonal, reduce movement.
        if((getMove(MH.FORWARD) || getMove(MH.BACKWARD)) && (getMove(MH.LEFT) || getMove(MH.RIGHT))){
            wd.setX(wd.getX()/1.414f);
            wd.setZ(wd.getZ()/1.414f);
        }

        // If crouching, lower view.
        if(getMove(MH.CROUCH)){
            cam.setLocation(Character.getPlayer().getPhysicsLocation().add(T.v3f(0, -1.5f, 0)));
            wd.setX(wd.getX()*CROUCH_PENALTY);
            wd.setZ(wd.getZ()*CROUCH_PENALTY);
        }else{
            cam.setLocation(Character.getPlayer().getPhysicsLocation());
        }

        Character.getPlayer().setWalkDirection(wd);
        Character.getNode().setLocalTranslation(cam.getLocation());
        if(Character.getLocation().getY() < -20){
            Character.kill();
        }
        //UpdateWeaponUI();
    }
    
    public static void initialize(Camera cam){
        MovementManager.cam = cam;
        
        // Initialize handles:
        setMove(MH.FORWARD, false);
        setMove(MH.RIGHT, false);
        setMove(MH.BACKWARD, false);
        setMove(MH.LEFT, false);
        setMove(MH.CROUCH, false);
    }
}
