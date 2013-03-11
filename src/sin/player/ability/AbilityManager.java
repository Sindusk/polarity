package sin.player.ability;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import sin.hud.AbilityBar;
import sin.netdata.CommandData;
import sin.netdata.ability.AbilityCooldownData;
import sin.player.PlayerManager;
import sin.tools.A;
import sin.tools.S;

/**
 * AbilityManager - Used to keep track of and execute abilities.
 * @author SinisteRing
 */
public class AbilityManager {
    public static abstract class Ability{
        private String name;
        private float cooldownMax;
        private float cooldown;
        private boolean cooling = false;
        
        public Ability(String name, float cooldown){
            this.name = name;
            this.cooldownMax = cooldown;
            this.cooldown = 0;
        }
        
        public String getName(){
            return name;
        }
        public float getCooldownMax(){
            return cooldownMax;
        }
        public float getCooldown(){
            return cooldown;
        }
        
        public void casted(){
            cooldown = cooldownMax;
            cooling = true;
        }
        public void update(float tpf){
            cooldown -= tpf;
            if(cooldown <= 0){
                cooling = false;
            }
        }
        
        public abstract void execute(int attacker, Ray ray);
    }
    public static abstract class RangedAbility extends Ability{
        private float range;
        
        public RangedAbility(String name, float cooldown, float range){
            super(name, cooldown);
            this.range = range;
        }
        
        public float getRange(){
            return range;
        }
    }
    public static class Blink extends RangedAbility{
        public Blink(float cooldown, float range){
            super("Blink", cooldown, range);
        }
        
        public void execute(int attacker, Ray ray){
            if(this.getCooldown() > 0){
                return;
            }
            CollisionResult target = A.getClosestCollisionByRange(ray, S.getCollisionNode(), attacker, this.getRange());
            if(target != null){
                Vector3f loc = target.getContactPoint();
                PlayerManager.getPlayer(attacker).getConnection().send(new CommandData("teleport:"+loc.getX()+","+loc.getY()+","+loc.getZ()));
                super.casted();
            }
        }
    }
    public static class Infect extends RangedAbility{
        private float time;
        private float dps;
        
        public Infect(float cooldown, float range, float time, float dps){
            super("Infect", cooldown, range);
            this.time = time;
            this.dps = dps;
        }
        
        public void execute(int attacker, Ray ray){
            if(this.getCooldown() > 0){
                return;
            }
            CollisionResult target = A.getClosestCollisionByRange(ray, S.getCollisionNode(), attacker, this.getRange());
            if(target != null){
                A.applyPoison(target, time, dps);
                super.casted();
            }
        }
    }
    
    public static void addCooldown(AbilityCooldownData d){
        AbilityBar.addCooldown(d.getAbility(), d.getCooldown());
    }
}
