/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.weapons;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import sin.GameClient;

/**
 *
 * @author SinisteRing
 */
public class Recoil{
    public static GameClient app;
    
    // Constant Variables:
    private static final float RECOIL_SENSITIVITY = 1;
    private static final float RECOIL_UP_INC = FastMath.PI*0.0001f;
    private static final float RECOIL_LEFT_INC = FastMath.PI*0.0003f;
    private static final float DECOIL_UP_PERC_MULT = 3;
    private static final float DECOIL_LEFT_PERC_MULT = 3;

    // Index Holders:
    private static final int UP = 0;
    private static final int UP_TOTAL = 1;
    private static final int LEFT = 2;
    private static final int LEFT_TOTAL = 3;
    // Recoil: 0 = up, 1 = up-total, 2 = left, 3 = left-total
    public float recoil[] = new float[]{0, 0, 0, 0};

    public void rotateCamera(float value, float sensitivity, Vector3f axis){
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

    public void RecoilUp(float mod){
        app.getCamera().getRotation().multLocal(new Quaternion().fromAngleAxis(-RECOIL_UP_INC*mod, Vector3f.UNIT_X));

        // Update variables:
        recoil[UP] += RECOIL_UP_INC*mod;
        recoil[UP_TOTAL] = recoil[UP];
    }
    public void RecoilLeft(float mod){
        rotateCamera(RECOIL_LEFT_INC*mod, RECOIL_SENSITIVITY, Vector3f.UNIT_Y);

        // Update variables:
        recoil[LEFT] += RECOIL_LEFT_INC*mod;
        recoil[LEFT_TOTAL] = recoil[LEFT];
    }
    public void DecoilUp(float mod){
        app.getCamera().getRotation().multLocal(new Quaternion().fromAngleAxis(-mod, Vector3f.UNIT_X));
    }
    public void DecoilLeft(float mod){
        rotateCamera(mod, RECOIL_SENSITIVITY, Vector3f.UNIT_Y);
    }

    public Recoil(){
        //
    }

    public float getSpreadMod(){
        return (FastMath.abs(recoil[UP])+FastMath.abs(recoil[LEFT]))*100;
    }
    public float getRecoil(boolean up){
        if(up) {
            return recoil[UP];
        }
        else {
            return recoil[LEFT];
        }
    }
    public void recoil(float up, float left){
        RecoilUp(up);
        RecoilLeft(left);
    }
    public void decoil(float tpf){
        if(recoil[UP] != 0){
            float decoil_up = RECOIL_UP_INC*tpf;
            float decoil_up_perc = recoil[UP_TOTAL]*DECOIL_UP_PERC_MULT*tpf;
            if(recoil[UP] < 0){
                decoil_up *= -1;
                //decoil_up_perc *= -1;
            }
            decoil_up += decoil_up_perc;
            if(FastMath.abs(recoil[UP]) < FastMath.abs(decoil_up)){
                decoil_up = recoil[UP];
                recoil[UP] = 0;
            }else{
                recoil[UP] -= decoil_up;
            }
            DecoilUp(-decoil_up);
        }

        if(recoil[LEFT] != 0){
            float decoil_left = RECOIL_LEFT_INC*tpf;
            float decoil_left_perc = recoil[LEFT_TOTAL]*DECOIL_LEFT_PERC_MULT*tpf;
            if(recoil[LEFT] < 0){
                decoil_left *= -1;
                //decoil_left_perc *= -1;
            }
            decoil_left += decoil_left_perc;
            if(FastMath.abs(recoil[LEFT]) < FastMath.abs(decoil_left)){
                decoil_left = recoil[LEFT];
                recoil[LEFT] = 0;
            }else{
                recoil[LEFT] -= decoil_left;
            }
            DecoilLeft(-decoil_left);
        }
    }
    
    public static void initialize(GameClient app){
        Recoil.app = app;
    }
}
