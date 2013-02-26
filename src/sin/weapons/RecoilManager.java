package sin.weapons;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import sin.GameClient;
import sin.hud.HUD;

/**
 * RecoilManager - Used for all recoil management.
 * @author SinisteRing
 */
public class RecoilManager{
    public static GameClient app;
    
    // Constant Variables:
    private static final float RECOIL_SENSITIVITY = 1;
    private static final float RECOIL_UP_INC = FastMath.PI*0.0001f;
    private static final float RECOIL_LEFT_INC = FastMath.PI*0.0003f;
    private static final float DECOIL_UP_PERC_MULT = 3;
    private static final float DECOIL_LEFT_PERC_MULT = 3;
    
    public static enum RH{
        UP, UP_TOTAL, LEFT, LEFT_TOTAL
    }
    private static HashMap<RH, Float> recoil = new HashMap();
    
    public static void addRecoil(RH handle, float value){
        recoil.put(handle, getRecoil(handle)+value);
    }
    public static void setRecoil(RH handle, float value){
        recoil.put(handle, value);
    }
    public static float getRecoil(RH handle){
        return recoil.get(handle);
    }

    public static void rotateCamera(float value, float sensitivity, Vector3f axis){
        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(sensitivity * value, axis);

        Vector3f up = app.getCamera().getUp();
        Vector3f left = app.getCamera().getLeft();
        Vector3f dir = app.getCamera().getDirection();
        Quaternion quat = new Quaternion();
        quat.lookAt(dir, up);

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        Quaternion q = new Quaternion();
        q.fromAxes(left, up, dir);
        q.normalizeLocal();

        float angleY = dir.angleBetween(Vector3f.UNIT_Y);
        float angleYDegree = angleY * 180 / FastMath.PI ;
        if(angleYDegree>=5 && angleYDegree<=175 && up.y>=0) {
            app.getCamera().setAxes(q);
        }
    }

    public static void RecoilUp(float mod){
        app.getCamera().getRotation().multLocal(new Quaternion().fromAngleAxis(-RECOIL_UP_INC*mod, Vector3f.UNIT_X));

        // Update variables:
        addRecoil(RH.UP, RECOIL_UP_INC*mod);
        setRecoil(RH.UP_TOTAL, getRecoil(RH.UP));
    }
    public static void RecoilLeft(float mod){
        rotateCamera(RECOIL_LEFT_INC*mod, RECOIL_SENSITIVITY, Vector3f.UNIT_Y);

        // Update variables:
        addRecoil(RH.LEFT, RECOIL_LEFT_INC*mod);
        setRecoil(RH.LEFT_TOTAL, getRecoil(RH.LEFT));
    }
    public static void DecoilUp(float mod){
        app.getCamera().getRotation().multLocal(new Quaternion().fromAngleAxis(-mod, Vector3f.UNIT_X));
    }
    public static void DecoilLeft(float mod){
        rotateCamera(mod, RECOIL_SENSITIVITY, Vector3f.UNIT_Y);
    }

    public static float getSpreadMod(){
        return (FastMath.abs(getRecoil(RH.UP))+FastMath.abs(getRecoil(RH.LEFT)))*100;
    }
    public static void updateCrosshairs(){
        if(getSpreadMod() != 0){
            HUD.updateCrosshairs();
        }
    }
    public static void recoil(float up, float left){
        RecoilUp(up);
        RecoilLeft(left);
    }
    public static void decoil(float tpf){
            if(getRecoil(RH.UP) != 0){
                float decoil_up = RECOIL_UP_INC*tpf;
                float decoil_up_perc = getRecoil(RH.UP_TOTAL)*DECOIL_UP_PERC_MULT*tpf;
                if(getRecoil(RH.UP) < 0){
                    decoil_up *= -1;
                    //decoil_up_perc *= -1;
                }
                decoil_up += decoil_up_perc;
                if(FastMath.abs(getRecoil(RH.UP)) < FastMath.abs(decoil_up)){
                    decoil_up = getRecoil(RH.UP);
                    setRecoil(RH.UP, 0);
                }else{
                    addRecoil(RH.UP, -decoil_up);
                }
                DecoilUp(-decoil_up);
            }

            if(getRecoil(RH.LEFT) != 0){
                float decoil_left = RECOIL_LEFT_INC*tpf;
                float decoil_left_perc = getRecoil(RH.LEFT_TOTAL)*DECOIL_LEFT_PERC_MULT*tpf;
                if(getRecoil(RH.LEFT) < 0){
                    decoil_left *= -1;
                    //decoil_left_perc *= -1;
                }
                decoil_left += decoil_left_perc;
                if(FastMath.abs(getRecoil(RH.LEFT)) < FastMath.abs(decoil_left)){
                    decoil_left = getRecoil(RH.LEFT);
                    setRecoil(RH.LEFT, 0);
                }else{
                    addRecoil(RH.LEFT, -decoil_left);
                }
                DecoilLeft(-decoil_left);
            }
        }
    
    public static void initialize(GameClient app){
        RecoilManager.app = app;
        recoil.put(RH.UP, 0f);
        recoil.put(RH.UP_TOTAL, 0f);
        recoil.put(RH.LEFT, 0f);
        recoil.put(RH.LEFT_TOTAL, 0f);
    }
}
