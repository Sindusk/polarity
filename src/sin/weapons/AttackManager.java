package sin.weapons;

import sin.world.TracerManager;
import com.jme3.collision.CollisionResult;
import com.jme3.math.Ray;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import sin.netdata.AttackData;
import sin.network.Networking;
import sin.tools.A;
import sin.tools.T;

/**
 * AttackManager - Used for aiding the damage functions for weaponry.
 * @author SinisteRing
 */
public class AttackManager {
    private static Camera cam;
    private static Node collisionNode;
    
    public static abstract class AttackTemplate{
        private String collision;
        
        public AttackTemplate(String collision){
            this.collision = collision;
        }
        protected String getCollision(){
            return collision;
        }
        
        public abstract void attack(Ray ray);
    }
    public static class MeleeAttack extends AttackTemplate{
        private float range;
        
        public MeleeAttack(String collision, float range){
            super(collision);
            this.range = range;
        }
        
        public float getRange(){
            return range;
        }
        
        public void attack(Ray ray){
            //A.rayAttack(ray, this.getRange(), this.getCollision());
            Networking.send(new AttackData(Networking.getID(), ray, this.getRange(), this.getCollision()));
        }
    }
    public static abstract class RangedAttack extends AttackTemplate{
        private float range;
        
        public RangedAttack(String collision, float range){
            super(collision);
            this.range = range;
        }
        
        public float getRange(){
            return range;
        }
        
        public abstract void attack(Ray ray);
    }
    public static class RangedProjectileAttack extends RangedAttack{
        private float speed;
        private String update;
        
        public float getSpeed(){
            return speed;
        }
        
        public RangedProjectileAttack(String update, String collision, float range, float speed){
            super(collision, range);
            this.speed = speed;
            this.update = update;
        }
        public void attack(Ray ray){
            ProjectileManager.addNew(Networking.getID(), ray.getOrigin(), ray.getDirection(), cam.getUp(), this.getRange(), this.getSpeed(), update, this.getCollision());
        }
    }
    public static class RangedRayAttack extends RangedAttack{
        public RangedRayAttack(String collision, float range){
            super(collision, range);
        }
        public void attack(Ray ray){
            Networking.send(new AttackData(Networking.getID(), ray, this.getRange(), this.getCollision()));
            /*CollisionResult target = A.getClosestCollision(ray, collisionNode);
            if(target != null){
                float d = A.getDistance(ray.getOrigin(), target.getContactPoint());
                if(d < this.getRange()){
                    TracerManager.add(ray.getOrigin(), target.getContactPoint());
                    T.parseCollision(Networking.getID(), this.getCollision(), target);
                }else{
                    TracerManager.add(ray, this.getRange());
                }
            }else{
                TracerManager.add(ray, this.getRange());
            }*/
        }
    }
    
    public static void initialize(Camera cam, Node collisionNode){
        AttackManager.cam = cam;
        AttackManager.collisionNode = collisionNode;
    }
}
