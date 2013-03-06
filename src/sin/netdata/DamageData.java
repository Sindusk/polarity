package sin.netdata;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class DamageData extends AbstractMessage {
    private int id;
    private int player;
    private float damage;
    public DamageData(){}
    public DamageData(int id, int player, float damage){
        this.id = id;
        this.player = player;
        this.damage = damage;
        this.setReliable(true);
    }
    public int getID(){
        return id;
    }
    public int getPlayer(){
        return player;
    }
    public float getDamage(){
        return damage;
    }
}
