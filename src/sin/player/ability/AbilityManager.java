package sin.player.ability;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import sin.netdata.CommandData;
import sin.player.PlayerManager;
import sin.tools.A;
import sin.tools.S;

/**
 * AbilityManager - Used to keep track of and execute abilities.
 * @author SinisteRing
 */
public class AbilityManager {
    public static abstract class Ability{
        private float cooldown;
        private float cooling;
        
        public Ability(float cooldown){
            this.cooldown = cooldown;
            this.cooling = 0;
        }
        
        public float getCooldown(){
            return cooldown;
        }
        public float getCooling(){
            return cooling;
        }
        
        public abstract void execute(int attacker, Ray ray);
    }
    public static abstract class RangedAbility extends Ability{
        private float range;
        
        public RangedAbility(float cooldown, float range){
            super(cooldown);
            this.range = range;
        }
        
        public float getRange(){
            return range;
        }
    }
    public static class Blink extends RangedAbility{
        public Blink(float cooldown, float range){
            super(cooldown, range);
        }
        
        public void execute(int attacker, Ray ray){
            CollisionResult target = A.getClosestCollisionByRange(ray, S.getCollisionNode(), attacker, this.getRange());
            if(target != null){
                Vector3f loc = target.getContactPoint();
                PlayerManager.getPlayer(attacker).getConnection().send(new CommandData("teleport:"+loc.getX()+","+loc.getY()+","+loc.getZ()));
            }
        }
    }
    public static class Infect extends RangedAbility{
        private float time;
        private float dps;
        
        public Infect(float cooldown, float range, float time, float dps){
            super(cooldown, range);
            this.time = time;
            this.dps = dps;
        }
        
        public void execute(int attacker, Ray ray){
            CollisionResult target = A.getClosestCollisionByRange(ray, S.getCollisionNode(), attacker, this.getRange());
            if(target != null){
                A.applyPoison(target, time, dps);
            }
        }
    }
}
