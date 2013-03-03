package sin.weapons;

import sin.world.TracerManager;
import com.jme3.collision.CollisionResult;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import sin.GameClient;
import sin.netdata.DecalData;
import sin.netdata.ShotData;
import sin.network.Networking;
import sin.character.PlayerManager;
import sin.hud.HUD;
import sin.tools.T;
import sin.world.DecalManager;

/**
 * AttackManager - Used for aiding the damage functions for weaponry.
 * @author SinisteRing
 */
public class AttackManager {
    private static GameClient app;
    
    public static float modDamage(String part, float dmg){
        if(part.equals("head")){
            dmg *= 1.5f;
        }
        return dmg;
    }
    public static void damage(CollisionResult target, float damage){
        String data[] = getPartData(target);
        if(data == null){
            return;
        }
        if(data[0].equals("player")){
            int id = Integer.parseInt(data[1]);
            damage = modDamage(data[2], damage);
            HUD.addFloatingText(PlayerManager.getPlayer(id).getLocation().clone().addLocal(T.v3f(0, 4, 0)), app.getCharacter().getLocation(), damage);
            Networking.send(new ShotData(Networking.getID(), id, damage));
        }else{
            DecalManager.create(target.getContactPoint());
            Networking.send(new DecalData(target.getContactPoint()));
        }
    }
    public static void damageAoE(CollisionResult target, float radius, float damage){
        DecalManager.create(target.getContactPoint());
        Networking.send(new DecalData(target.getContactPoint()));
    }
    
    public static float getDistance(Vector3f player, Vector3f target){
        return target.distance(player);
    }
    public static String[] getPartData(CollisionResult target){
        return target.getGeometry().getParent().getName().split(":");
    }
    public static int getHitbox(String name){
        if(name.contains("head")) {
            return 0;
        }else if(name.contains("torso")) {
            return 1;
        }else if(name.contains("arm")) {
            return 2;
        }else if(name.contains("leg")) {
            return 3;
        }
        return -1;
    }
    
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
        
        private float getRange(){
            return range;
        }
        
        public void attack(Ray ray){
            CollisionResult target = T.getClosestCollision(ray, app.getCollisionNode());
            if(target != null){
                float d = getDistance(ray.getOrigin(), target.getContactPoint());
                if(d < this.getRange()){
                    T.ParseCollision(this.getCollision(), target);
                }
            }
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
            ProjectileManager.addNew(ray.getOrigin(), ray.getDirection(), app.getCamera().getUp(), this.getRange(), this.getSpeed(), update, this.getCollision());
        }
    }
    public static class RangedRayAttack extends RangedAttack{
        public RangedRayAttack(String collision, float range){
            super(collision, range);
        }
        public void attack(Ray ray){
            CollisionResult target = T.getClosestCollision(ray, app.getCollisionNode());
            if(target != null){
                float d = getDistance(ray.getOrigin(), target.getContactPoint());
                if(d < this.getRange()){
                    TracerManager.add(ray.getOrigin(), target.getContactPoint());
                    T.ParseCollision(this.getCollision(), target);
                }else{
                    TracerManager.add(ray, this.getRange());
                }
            }else{
                TracerManager.add(ray, this.getRange());
            }
        }
    }
    
    public static void initialize(GameClient app){
        AttackManager.app = app;
    }
}
