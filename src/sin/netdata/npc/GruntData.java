package sin.netdata.npc;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class GruntData extends OrganismData {
    public GruntData(){}
    public GruntData(int id, String type, Vector3f location, float health, float maxHealth){
        super(id, type, location, health, maxHealth);
        this.setReliable(true);
    }
}
