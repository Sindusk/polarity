package sin.netdata.ability;

import com.jme3.math.Ray;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class AbilityData extends AbstractMessage{
    private int attacker;
    private int ability;
    private Ray ray;
    public AbilityData() {}
    public AbilityData(int attacker, int ability, Ray ray){
        this.attacker = attacker;
        this.ability = ability;
        this.ray = ray;
        this.setReliable(true);
    }
    public int getAttacker(){
        return attacker;
    }
    public int getAbility(){
        return ability;
    }
    public Ray getRay(){
        return ray;
    }
}
