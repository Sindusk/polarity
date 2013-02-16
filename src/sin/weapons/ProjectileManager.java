package sin.weapons;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.tools.T;
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
        public void move(float tpf){
            Vector3f movement = direction.clone().mult(speed*tpf);
            float dist = movement.distance(Vector3f.ZERO);
            location.setX(location.getX() + movement.getX());
            location.setY(location.getY() + movement.getY());
            location.setZ(location.getZ() + movement.getZ());
            projectile.setLocalTranslation(location);
            distance += dist;
            if(distance > maxDistance){
                this.destroy();
            }
        }
        public void create(Vector3f location, Vector3f direction, float speed, float distance){
            this.location = location;
            this.direction = direction;
            this.speed = speed;
            this.maxDistance = distance;
            if(!GameClient.getTerrain().hasChild(projectile)){
                World.CG.createSphere(projectile, "", 0.4f, Vector3f.ZERO, ColorRGBA.Magenta);
                GameClient.getTerrain().attachChild(projectile);
            }
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
    public static void add(Vector3f location, Vector3f direction, float speed, float distance){
        int i = findEmptyProjectileSlot();
        if(i != -1){
            if(projectiles[i] == null){
                projectiles[i] = new Projectile();
            }
            projectiles[i].create(location, direction, speed, distance);
        }
    }
    
    public static void initialize(GameClient app){
        ProjectileManager.app = app;
    }
}
