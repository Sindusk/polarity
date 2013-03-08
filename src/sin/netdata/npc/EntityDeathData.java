package sin.netdata.npc;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class EntityDeathData extends AbstractMessage {
    private int id;
    private String type;
    public EntityDeathData(){}
    public EntityDeathData(int id, String type){
        this.id = id;
        this.type = type;
        this.setReliable(true);
    }
    public int getID(){
        return id;
    }
    public String getType(){
        return type;
    }
}
