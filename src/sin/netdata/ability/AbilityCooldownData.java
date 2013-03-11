package sin.netdata.ability;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class AbilityCooldownData extends AbstractMessage{
    private int ability;
    private float cooldown;
    public AbilityCooldownData() {}
    public AbilityCooldownData(int ability, float cooldown){
        this.ability = ability;
        this.cooldown = cooldown;
        this.setReliable(true);
    }
    public int getAbility(){
        return ability;
    }
    public float getCooldown(){
        return cooldown;
    }
}
