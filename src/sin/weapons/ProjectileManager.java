package sin.weapons;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.tools.T;
import sin.weapons.DamageManager.DamageAction;
import sin.world.World;

/**
 * Projectile - Used for the creation and handling of all game projectiles.
 * @author SinisteRing
 */
public class ProjectileManager {
    private static GameClient app;
    
    // Array for handling all projectiles:
    private static Projectile[] projectiles = new Projectile[50];
    
    public static class Projectile{
        private Node projectile = new Node();
        private Vector3f location;
        private Vector3f direction;
        private DamageAction func;
        private boolean inUse = false;
        private float speed;
        private float distance = 0;
        private float maxDistance;
        
        public Projectile(){
            //
        }
        
        public boolean isUsed(){
            return inUse;
        }
        private void collide(CollisionResults results){
            CollisionResult target = results.getClosestCollision();
            if(target.getContactPoint().distance(location) < 0.2){
                this.destroy();
                try {
                    func.action();
                } catch (Exception ex) {
                    T.log(ex);
                }
            }
        }
        public void move(float tpf){
            Vector3f movement = direction.clone().mult(speed*tpf);
            float dist = movement.distance(Vector3f.ZERO);
            T.addv3f(location, movement);
            projectile.setLocalTranslation(location);
            CollisionResults results = new CollisionResults();
            GameClient.getCollisionNode().collideWith(new Ray(location, direction), results);
            if(results.size() > 0){
                this.collide(results);
            }
            distance += dist;
            if(distance > maxDistance){
                this.destroy();
            }
        }
        public void create(Vector3f location, Vector3f direction, float speed, float distance, DamageAction func){
            this.location = location;
            this.direction = direction;
            this.speed = speed;
            this.maxDistance = distance;
            this.func = func;
            if(!GameClient.getTerrain().hasChild(projectile)){
                World.CG.createSphere(projectile, "", 0.4f, Vector3f.ZERO, ColorRGBA.Magenta);
                GameClient.getTerrain().attachChild(projectile);
            }
            T.addv3f(location, direction);
            projectile.setLocalTranslation(location);
            inUse = true;
        }
        public void destroy(){
            distance = 0;
            projectile.setLocalTranslation(T.EMPTY_SPACE);
            inUse = false;
        }
    }
    
    public static void update(float tpf){
        int i = 0;
        while(i < projectiles.length){
            if(projectiles[i] == null){
                break;
            }
            if(projectiles[i].isUsed()){
                projectiles[i].move(tpf);
            }
            i++;
        }
    }
    private static int findEmptyProjectileSlot(){
        int i = 0;
        while(i < projectiles.length){
            if(projectiles[i] == null || !projectiles[i].isUsed()){
                return i;
            }
            i++;
        }
        return -1;
    }
    public static void add(Vector3f location, Vector3f direction, float distance, float speed, DamageAction func){
        int i = findEmptyProjectileSlot();
        if(i != -1){
            if(projectiles[i] == null){
                projectiles[i] = new Projectile();
            }
            projectiles[i].create(location, direction, speed, distance, func);
        }
    }
    
    public static void initialize(GameClient app){
        ProjectileManager.app = app;
    }
}