package sin.character;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import sin.tools.A;
import sin.tools.S;

/**
 * AbilityManager - Used to keep track of and execute abilities.
 * @author SinisteRing
 */
public class AbilityManager {
    private static Ability[] ability = new Ability[4];
    
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
        
        public abstract void execute(Ray ray);
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
        
        public void execute(Ray ray){
            CollisionResult target = A.getClosestCollisionByRange(ray, S.getCollisionNode(), this.getRange());
            if(target != null){
                Character.getControl().setPhysicsLocation(target.getContactPoint().add(new Vector3f(0, 5, 0)));
            }
        }
    }
    public static class Infect extends RangedAbility{
        public Infect(float cooldown, float range){
            super(cooldown, range);
        }
        
        public void execute(Ray ray){
            CollisionResult target = A.getClosestCollisionByRange(ray, S.getCollisionNode(), this.getRange());
            if(target != null){
                //Apply poison.
            }
        }
    }
    
    public static Ability getAbility(int id){
        return ability[id];
    }
    public static void initialize(){
        ability[0] = new Blink(5, 300);
        ability[1] = new Infect(4, 150);
    }
}
