package sin.weapons;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import sin.GameClient;
import sin.data.DecalData;
import sin.data.ShotData;
import sin.network.Networking;
import sin.tools.T;

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
    public static void damage(float damage){
        //
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
    
    public static abstract class DamageAction{
        public abstract void action();
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
                    int part = getHitbox(target.getGeometry().getName());
                    if(part >= 0){
                        int player = Integer.parseInt(target.getGeometry().getName().substring(0, 2));
                        float dmg = calculate(part, this.getBase());
                        app.getHUD().addFloatingText(GameClient.getPlayer(player).getLocation().clone().addLocal(T.v3f(0, 4, 0)), GameClient.getCharacter().getLocation(), dmg);
                        if(Networking.isConnected()) {
                            Networking.client.send(new ShotData(Networking.CLIENT_ID, player, dmg));
                        }
                    }else{
                        GameClient.getDCS().createDecal(target.getContactPoint());
                        if(Networking.isConnected()) {
                            Networking.client.send(new DecalData(target.getContactPoint()));
                        }
                    }
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
        private DamageAction func;
        
        public float getSpeed(){
            return speed;
        }
        
        public RangedBulletDamage(float base, float range, float speed, DamageAction func){
            super(base, range);
            this.speed = speed;
            this.func = func;
        }
        public void attack(Ray ray){
            ProjectileManager.add(ray.getOrigin(), ray.getDirection(), this.getRange(), this.getSpeed(), func);
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
                    int part = getHitbox(target.getGeometry().getName());
                    if(part >= 0){
                        int player = Integer.parseInt(target.getGeometry().getName().substring(0, 2));
                        float dmg = calculate(part, this.getBase());
                        app.getHUD().addFloatingText(GameClient.getPlayer(player).getLocation().clone().addLocal(T.v3f(0, 4, 0)), GameClient.getCharacter().getLocation(), dmg);
                        if(Networking.isConnected()) {
                            Networking.client.send(new ShotData(Networking.CLIENT_ID, player, dmg));
                        }
                    }else{
                        GameClient.getDCS().createDecal(target.getContactPoint());
                        if(Networking.isConnected()) {
                            Networking.client.send(new DecalData(target.getContactPoint()));
                        }
                    }
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
