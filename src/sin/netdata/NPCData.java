package sin.netdata;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class NPCData extends AbstractMessage {
    private int id;
    private String type;
    private Vector3f location;
    public NPCData(){}
    public NPCData(int id, String type, Vector3f location){
        this.id = id;
        this.type = type;
        this.location = location;
        this.setReliable(true);
    }
    public int getID(){
        return id;
    }
    public String getType(){
        return type;
    }
    public Vector3f getLocation(){
        return location;
    }
}
