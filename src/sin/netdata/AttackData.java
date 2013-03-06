package sin.netdata;

import com.jme3.math.Ray;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class AttackData extends AbstractMessage {
    private int attacker;
    private Ray ray;
    private float range;
    private String collision;
    public AttackData() {}
    public AttackData(int attacker, Ray ray, float range, String collision){
        this.attacker = attacker;
        this.ray = ray;
        this.range = range;
        this.collision = collision;
        this.setReliable(true);
    }
    public int getAttacker(){
        return attacker;
    }
    public Ray getRay(){
        return ray;
    }
    public float getRange(){
        return range;
    }
    public String getCollision(){
        return collision;
    }
}
