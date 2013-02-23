package sin.weapons;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import sin.GameClient;
import sin.data.DecalData;
import sin.data.ShotData;
import sin.network.Networking;
import sin.player.PlayerManager;
import sin.tools.T;
import sin.world.DecalManager;

/**
 * Damage - Used for aiding the damage functions for weaponry.
 * @author SinisteRing
 */
public class DamageManager {
    private static GameClient app;
    
    // Helper Functions:
    public static float calculate(int part, float dmg){
        if(part == 0){
            dmg *= 1.5f;
        }
        return dmg;
    }
    public static void damage(CollisionResult target, float damage){
        int part = getHitbox(target.getGeometry().getName());
        if(part >= 0){
            int player = Integer.parseInt(target.getGeometry().getName().substring(0, 2));
            float dmg = calculate(part, damage);
            app.getHUD().addFloatingText(PlayerManager.getPlayer(player).getLocation().clone().addLocal(T.v3f(0, 4, 0)), app.getCharacter().getLocation(), dmg);
            if(Networking.isConnected()) {
                Networking.send(new ShotData(Networking.getID(), player, dmg));
            }
        }else{
            DecalManager.create(target.getContactPoint());
            if(Networking.isConnected()) {
                Networking.send(new DecalData(target.getContactPoint()));
            }
        }
    }
    
    public static float getDistance(Vector3f player, Vector3f target){
        return target.distance(player);
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
    
    public static abstract class DamageTemplate{
        private float base;

        public DamageTemplate(float base){
            this.base = base;
        }

        public float getBase(){
            return base;
        }
        public abstract void attack(Ray ray);
    }
    public static class MeleeDamage extends DamageTemplate{
        private float range;
        
        public MeleeDamage(float base, float range){
            super(base);
            this.range = range;
        }
        
        private float getRange(){
            return range;
        }
        
        public void attack(Ray ray){
            CollisionResult target = T.getClosestCollision(ray);
            if(target != null){
                float d = getDistance(ray.getOrigin(), target.getContactPoint());
                if(d < this.getRange()){
                    damage(target, this.getBase());
                    /*int part = getHitbox(target.getGeometry().getName());
                    if(part >= 0){
                        int player = Integer.parseInt(target.getGeometry().getName().substring(0, 2));
                        float dmg = calculate(part, this.getBase());
                        app.getHUD().addFloatingText(PlayerManager.getPlayer(player).getLocation().clone().addLocal(T.v3f(0, 4, 0)), app.getCharacter().getLocation(), dmg);
                        if(Networking.isConnected()) {
                            Networking.send(new ShotData(Networking.getID(), player, dmg));
                        }
                    }else{
                        DecalManager.create(target.getContactPoint());
                        if(Networking.isConnected()) {
                            Networking.send(new DecalData(target.getContactPoint()));
                        }
                    }*/
                }
            }
        }
    }
    public static abstract class RangedDamage extends DamageTemplate{
        private float range;
        
        public RangedDamage(float base, float range){
            super(base);
            this.range = range;
        }
        
        public float getRange(){
            return range;
        }
        
        public abstract void attack(Ray ray);
    }
    public static class RangedBulletDamage extends RangedDamage{
        private float speed;
        private String update;
        private String collision;
        
        public float getSpeed(){
            return speed;
        }
        
        public RangedBulletDamage(float base, float range, float speed, String update, String collision){
            super(base, range);
            this.speed = speed;
            this.update = update;
            this.collision = collision;
        }
        public void attack(Ray ray){
            ProjectileManager.addNew(ray.getOrigin(), ray.getDirection(), this.getRange(), this.getSpeed(), update, collision);
        }
    }
    public static class RangedLaserDamage extends RangedDamage{
        public RangedLaserDamage(float base, float range){
            super(base, range);
        }
        public void attack(Ray ray){
            CollisionResult target = T.getClosestCollision(ray);
            if(target != null){
                float d = getDistance(ray.getOrigin(), target.getContactPoint());
                if(d < this.getRange()){
                    TracerManager.add(ray.getOrigin(), target.getContactPoint());
                    damage(target, this.getBase());
                    /*int part = getHitbox(target.getGeometry().getName());
                    if(part >= 0){
                        int player = Integer.parseInt(target.getGeometry().getName().substring(0, 2));
                        float dmg = calculate(part, this.getBase());
                        app.getHUD().addFloatingText(PlayerManager.getPlayer(player).getLocation().clone().addLocal(T.v3f(0, 4, 0)), app.getCharacter().getLocation(), dmg);
                        if(Networking.isConnected()) {
                            Networking.send(new ShotData(Networking.getID(), player, dmg));
                        }
                    }else{
                        DecalManager.create(target.getContactPoint());
                        if(Networking.isConnected()) {
                            Networking.send(new DecalData(target.getContactPoint()));
                        }
                    }*/
                }else{
                    TracerManager.add(ray, this.getRange());
                }
            }else{
                TracerManager.add(ray, this.getRange());
            }
        }
    }
    
    public static void initialize(GameClient app){
        DamageManager.app = app;
    }
}
