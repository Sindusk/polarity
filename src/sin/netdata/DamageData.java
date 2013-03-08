package sin.netdata;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class DamageData extends AbstractMessage {
    private int attacker;
    private String type;
    private int target;
    private float damage;
    public DamageData(){}
    public DamageData(int attacker, String type, int target, float damage){
        this.attacker = attacker;
        this.type = type;
        this.target = target;
        this.damage = damage;
        this.setReliable(true);
    }
    public int getAttacker(){
        return attacker;
    }
    public String getType(){
        return type;
    }
    public int getTarget(){
        return target;
    }
    public float getDamage(){
        return damage;
    }
}
