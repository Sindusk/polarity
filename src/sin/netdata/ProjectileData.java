package sin.netdata;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * ProjectileData - Synchronizes all projectiles currently in the air.
 * @author SinisteRing
 */
@Serializable
public class ProjectileData extends AbstractMessage {
    private int owner;
    private Vector3f loc;
    private Vector3f dir;
    private Vector3f up;
    private float dist;
    private float speed;
    private String update;
    private String collision;
    public ProjectileData(){}
    public ProjectileData(int owner, Vector3f loc, Vector3f dir, Vector3f up, float dist, float speed, String update, String collision){
        this.owner = owner;
        this.loc = loc;
        this.dir = dir;
        this.up = up;
        this.dist = dist;
        this.speed = speed;
        this.update = update;
        this.collision = collision;
    }
    public int getOwner(){
        return owner;
    }
    public Vector3f getLocation(){
        return loc;
    }
    public Vector3f getDirection(){
        return dir;
    }
    public Vector3f getUp(){
        return up;
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
