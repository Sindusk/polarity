package sin.weapons;

import com.jme3.collision.CollisionResult;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import sin.netdata.ProjectileData;
import sin.network.ClientNetwork;
import sin.tools.A;
import sin.tools.S;
import sin.tools.T;
import sin.world.CG;

/**
 * Projectile - Used for the creation and handling of all game projectiles.
 * @author SinisteRing
 */
public class ProjectileManager {
    private static Node node = new Node("ProjectileNode");
    
    // Array for handling all projectiles:
    private static Projectile[] projectiles = new Projectile[500];
    
    public static class Projectile{
        private Node projectile = new Node("Projectile");
        private int id;
        private int owner;
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
        
        public int getID(){
            return id;
        }
        public int getOwner(){
            return owner;
        }
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
                A.parseCollision(this, target);
            }
        }
        public void update(float tpf, boolean doActions){
            Vector3f movement = direction.clone().mult(speed*tpf);
            float dist = movement.distance(Vector3f.ZERO);
            T.addv3f(location, movement);
            projectile.setLocalTranslation(location);
            distance += dist;
            if(distance > maxDistance){
                this.destroy();
            }
            if(doActions){
                CollisionResult target = A.getClosestCollisionNotPlayer(S.getCollisionNode(), new Ray(location, direction), owner);
                if(target != null){
                    this.collide(target, dist);
                }
                A.parseUpdate(this, tpf);
            }
        }
        public void create(int id, int owner, Vector3f location, Vector3f direction, Vector3f up, float speed, float distance, String update, String collision){
            this.id = id;
            this.owner = owner;
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
            A.initializeUpdate(this);
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
    public static Projectile getProjectile(int id){
        return projectiles[id];
    }
    
    public static void update(float tpf, boolean doActions){
        int i = 0;
        while(i < projectiles.length){
            if(projectiles[i] == null){
                break;
            }
            if(projectiles[i].isUsed()){
                projectiles[i].update(tpf, doActions);
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
    public static void add(int owner, Vector3f loc, Vector3f dir, Vector3f up, float dist, float speed, String update, String collision){
        int i = findEmptyProjectile();
        if(i != -1){
            if(projectiles[i] == null){
                projectiles[i] = new Projectile();
            }
            projectiles[i].create(i, owner, loc, dir, up, speed, dist, update, collision);
        }else{
            T.log("Projectile System Overload!");
        }
    }
    public static void add(ProjectileData d){
        add(d.getOwner(), d.getLocation(), d.getDirection(), d.getUp(), d.getDistance(), d.getSpeed(), d.getUpdate(), d.getCollision());
    }
    public static void addNew(int owner, Vector3f loc, Vector3f dir, Vector3f up, float dist, float speed, String update, String collision){
        ClientNetwork.send(new ProjectileData(owner, loc, dir, up, dist, speed, update, collision));
        add(owner, loc, dir, up, dist, speed, update, collision);
    }
}
