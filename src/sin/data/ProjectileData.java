package sin.data;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * ProjectileData - Synchronizes all projectiles currently in the air.
 * @author SinisteRing
 */
@Serializable
public class ProjectileData extends AbstractMessage {
    private Vector3f loc;
    private Vector3f dir;
    private float dist;
    private float speed;
    private String update;
    private String collision;
    public ProjectileData(){}
    public ProjectileData(Vector3f loc, Vector3f dir, float dist, float speed, String update, String collision){
        this.loc = loc;
        this.dir = dir;
        this.dist = dist;
        this.speed = speed;
        this.update = update;
        this.collision = collision;
    }
    public Vector3f getLocation(){
        return loc;
    }
    public Vector3f getDirection(){
        return dir;
    }
    public float getDistance(){
        return dist;
    }
    public float getSpeed(){
        return speed;
    }
    public String getUpdate(){
        return update;
    }
    public String getCollision(){
        return collision;
    }
}
