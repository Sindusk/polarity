package sin.abilities;

/**
 *
 * @author SinisteRing
 */
public class AbilityActions {
    public static abstract class AbilityAction{
        public static String HANDLE = "abilityaction";
    }
    public static class Blind extends AbilityAction{
        private float dur;
        
        public Blind(float dur){
            this.dur = dur;
            HANDLE = "blind";
        }
        
        public float getDuration(){
            return dur;
        }
    }
    public static class Damage extends AbilityAction{
        private float damage;
        
        public Damage(float damage){
            this.damage = damage;
            HANDLE = "damage";
        }
        
        public float getDamage(){
            return damage;
        }
    }
    public static class Slow extends AbilityAction{
        private float perc;
        private float dur;
        
        public Slow(float perc, float dur){
            this.perc = perc;
            this.dur = dur;
            HANDLE = "slow";
        }
        
        public float getPercentage(){
            return perc;
        }
        public float getDuration(){
            return dur;
        }
    }
    public static class Stun extends AbilityAction{
        private float dur;
        
        public Stun(float dur){
            this.dur = dur;
            HANDLE = "stun";
        }
        
        public float getDuration(){
            return dur;
        }
    }
}
