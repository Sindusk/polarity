package sin.netdata.npc;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class OrganismData extends EntityData {
    private float health;
    private float maxHealth;
    public OrganismData(){}
    public OrganismData(int id, String type, Vector3f location, float health, float maxHealth){
        super(id, type, location);
        this.health = health;
        this.maxHealth = maxHealth;
        this.setReliable(true);
    }
    public float getHealth(){
        return health;
    }
    public float getMaxHealth(){
        return maxHealth;
    }
}
