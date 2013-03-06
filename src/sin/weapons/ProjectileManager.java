package sin.weapons;

import com.jme3.collision.CollisionResult;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import sin.netdata.ProjectileData;
import sin.network.Networking;
import sin.tools.T;
import sin.world.CG;

/**
 * Projectile - Used for the creation and handling of all game projectiles.
 * @author SinisteRing
 */
public class ProjectileManager {
    private static Node node = new Node("ProjectileNode");
    private static Node collisionNode;
    
    // Array for handling all projectiles:
    private static Projectile[] projectiles = new Projectile[500];
    
    public static class Projectile{
        private Node projectile = new Node("Projectile");
        private Vector3f location;
        private Vector3f direction;
        private Vector3f up;
        private boolean used = false;
        private float speed;
        private float distance = 0;
        private float maxDistance;
        private String update;
        private String collision;
        
        public Projectile(){}
        
        public Vector3f getLocation(){
            return location;
        }
        public Vector3f getDirection(){
            return direction;
        }
        public Vector3f getUp(){
            return up;
        }
        public String getUpdate(){
            return update;
        }
        public String getCollision(){
            return collision;
        }
        public boolean isUsed(){
            return used;
        }
        
        private void collide(CollisionResult target, float dist){
            if(target.getContactPoint().distance(location) <= dist*2){
                T.ParseCollision(this, target);
            }
        }
        public void update(float tpf){
            Vector3f movement = direction.clone().mult(speed*tpf);
            float dist = movement.distance(Vector3f.ZERO);
            T.addv3f(location, movement);
            projectile.setLocalTranslation(location);
            CollisionResult target = T.getClosestCollision(new Ray(location, direction), collisionNode);
            if(target != null){
                this.collide(target, dist);
            }
            distance += dist;
            if(distance > maxDistance){
                this.destroy();
            }
            T.ParseUpdate(this, tpf);
        }
        public void create(Vector3f location, Vector3f direction, Vector3f up, float speed, float distance, String update, String collision){
            this.location = location;
            this.direction = direction;
            this.up = up;
            this.speed = speed;
            this.maxDistance = distance;
            this.update = update;
            this.collision = collision;
            
            // Create the Projectile if it has not been created:
            if(!node.hasChild(projectile)){
                CG.createSphere(projectile, "", 0.4f, Vector3f.ZERO, ColorRGBA.Magenta);
                node.attachChild(projectile);
            }
            
            // Move the projectile away from the shooter slightly:
            T.addv3f(location, direction);
            projectile.setLocalTranslation(location);
            used = true;
            T.InitializeUpdate(this);
        }
        public void destroy(){
            distance = 0;
            projectile.removeFromParent();
            used = false;
        }
    }
    
    public static Node getNode(){
        return node;
    }
    
    public static void update(float tpf){
        int i = 0;
        while(i < projectiles.length){
            if(projectiles[i] == null){
                break;
            }
            if(projectiles[i].isUsed()){
                projectiles[i].update(tpf);
            }
            i++;
        }
    }
    private static int findEmptyProjectile(){
        int i = 0;
        while(i < projectiles.length){
            if(projectiles[i] == null || !projectiles[i].isUsed()){
                return i;
            }
            i++;
        }
        return -1;
    }
    public static void add(Vector3f loc, Vector3f dir, Vector3f up, float dist, float speed, String update, String collision){
        int i = findEmptyProjectile();
        if(i != -1){
            if(projectiles[i] == null){
                projectiles[i] = new Projectile();
            }
            projectiles[i].create(loc, dir, up, speed, dist, update, collision);
        }else{
            T.log("Projectile System Overload!");
        }
    }
    public static void add(ProjectileData d){
        add(d.getLocation(), d.getDirection(), d.getUp(), d.getDistance(), d.getSpeed(), d.getUpdate(), d.getCollision());
    }
    public static void addNew(Vector3f loc, Vector3f dir, Vector3f up, float dist, float speed, String update, String collision){
        if(Networking.isConnected()){
            Networking.send(new ProjectileData(loc, dir, up, dist, speed, update, collision));
        }
        add(loc, dir, up, dist, speed, update, collision);
    }
    
    public static void initialize(Node collisionNode){
        ProjectileManager.collisionNode = collisionNode;
    }
}
