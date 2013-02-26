package sin.netdata;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class GeometryData extends AbstractMessage {
    private String type;
    private Vector3f size;
    private Vector3f trans;
    private Quaternion rot;
    private String tex;
    private Vector2f scale;
    private boolean phy;
    public GeometryData(){}
    public GeometryData(String type, Vector3f size, Vector3f trans, Quaternion rot, String tex, Vector2f scale, boolean phy){
        this.type = type;
        this.size = size;
        this.trans = trans;
        this.rot = rot;
        this.tex = tex;
        this.scale = scale;
        this.phy = phy;
    }
    public String getType(){
        return type;
    }
    public Vector3f getSize(){
        return size;
    }
    public Vector3f getTrans(){
        return trans;
    }
    public Quaternion getRot(){
        return rot;
    }
    public String getTex(){
        return tex;
    }
    public Vector2f getScale(){
        return scale;
    }
    public boolean getPhy(){
        return phy;
    }
}
