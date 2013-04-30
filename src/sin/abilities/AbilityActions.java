package sin.abilities;

/**
 *
 * @author SinisteRing
 */
public class AbilityActions {
    public static abstract class AbilityAction{
        public static String HANDLE = "abilityaction";
    }
    public static class Damage extends AbilityAction{
        private float damage;
        
        public Damage(float damage){
            this.damage = damage;
            HANDLE = "damage";
        }
    }
}
