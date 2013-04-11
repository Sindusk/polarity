package sin.player;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import java.util.HashMap;
import sin.network.ClientNetwork;
import sin.tools.S;
import sin.tools.T;
import sin.weapons.Weapons;

/**
 * MovementManager - Used for the control and handling of all player movement.
 * @author SinisteRing
 */
public class MovementManager {
    private static Camera cam;
    
    // Constant Variables:
    private static final float MOVE_SPEED = 0.40f;
    private static final float CROUCH_PENALTY = 0.6f;
    
    private static HashMap<MH, Boolean> movement = new HashMap();
    private static boolean grounded = true;
    
    public static enum MH{
        FORWARD, RIGHT, BACKWARD, LEFT, CROUCH, JUMP
    }
    
    public static boolean getMove(MH handle){
        return movement.get(handle);
    }
    
    public static void setGrounded(boolean b){
        grounded = b;
    }
    public static void setMove(MH handle, boolean b){
        movement.put(handle, b);
    }
    
    public static void moveFlying(float tpf){
        Vector3f dir = new Vector3f(0, 0, 0);
        if(getMove(MH.FORWARD)){
            dir.addLocal(cam.getDirection());
        }
        if(getMove(MH.BACKWARD)){
            dir.addLocal(cam.getDirection().mult(-1));
        }
        if(getMove(MH.LEFT)){
            dir.addLocal(cam.getLeft());
        }
        if(getMove(MH.RIGHT)){
            dir.addLocal(cam.getLeft().mult(-1));
        }
        if(getMove(MH.CROUCH)){
            dir.addLocal(0, -tpf*1000, 0);
        }
        if(getMove(MH.JUMP)){
            dir.addLocal(0, tpf*1000, 0);
        }
        cam.setLocation(cam.getLocation().add(dir.mult(tpf*100.0f)));
    }
    public static void moveGrounded(){
        // Initialize temporary variables:
        Vector3f dir;
        Vector3f wd = new Vector3f(0, 0, 0);
        float angle;
        // Calculate forward/backward points:
        if(getMove(MH.FORWARD) || getMove(MH.BACKWARD)){
            dir = cam.getDirection();
            angle = (float) Math.atan2(-dir.getZ()*0.5, -dir.getX()*0.5);
            if(angle < 0){
                angle += FastMath.TWO_PI;
            }
            float xWard = (float) -Math.cos(angle) * MOVE_SPEED;
            float zWard = (float) -Math.sin(angle) * MOVE_SPEED;
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
            angle = (float) Math.atan2(-dir.getZ()*0.5, -dir.getX()*0.5);
            if(angle < 0){
                angle += FastMath.TWO_PI;
            }
            float xSide = (float) -Math.cos(angle) * MOVE_SPEED;
            float zSide = (float) -Math.sin(angle) * MOVE_SPEED;
            if(getMove(MH.LEFT)) {
                wd.addLocal(xSide, 0, zSide);
            }
            if(getMove(MH.RIGHT)) {
                wd.addLocal(-xSide, 0, -zSide);
            }
        }
        
        // If moving at a diagonal, reduce movement.
        if((getMove(MH.FORWARD) || getMove(MH.BACKWARD)) && (getMove(MH.LEFT) || getMove(MH.RIGHT))){
            wd.setX(wd.getX()*T.ROOT_HALF);
            wd.setZ(wd.getZ()*T.ROOT_HALF);
        }
        
        // If crouching, lower view.
        if(getMove(MH.CROUCH)){
            cam.setLocation(PlayerManager.getPlayer(ClientNetwork.getID()).getControl().getPhysicsLocation().add(new Vector3f(0, -1.5f, 0)));
            wd.setX(wd.getX()*CROUCH_PENALTY);
            wd.setZ(wd.getZ()*CROUCH_PENALTY);
        }else{
            S.getCamera().setLocation(PlayerManager.getPlayer(ClientNetwork.getID()).getControl().getPhysicsLocation());
        }
        
        PlayerManager.getPlayer(ClientNetwork.getID()).getControl().setWalkDirection(wd);
        Weapons.getNode().setLocalTranslation(cam.getLocation());
        if(PlayerManager.getPlayer(ClientNetwork.getID()).getLocation().getY() < -20){
            PlayerManager.getPlayer(ClientNetwork.getID()).kill();
        }
    }
    public static void move(float tpf){
        if(grounded){
            moveGrounded();
        }else{
            moveFlying(tpf);
        }
    }
    
    public static void initialize(Camera cam){
        MovementManager.cam = cam;
        
        // Initialize handles:
        setMove(MH.FORWARD, false);
        setMove(MH.RIGHT, false);
        setMove(MH.BACKWARD, false);
        setMove(MH.LEFT, false);
        setMove(MH.CROUCH, false);
        setMove(MH.JUMP, false);
    }
}
