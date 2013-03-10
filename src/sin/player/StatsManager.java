package sin.player;

import com.jme3.math.FastMath;

/**
 * StatsManager - Used for the managment of player stats.
 * @author SinisteRing
 */
public class StatsManager {
    public static abstract class StatsTemplate{
        private float health;
        private float maxHealth;
        
        public StatsTemplate(float health, float maxHealth){
            this.health = health;
            this.maxHealth = maxHealth;
        }
        
        public float getHealth(){
            return health;
        }
        
        public boolean damage(float damage){
            health -= damage;
            if(health <= 0){
                health = 0;
                return true;
            }
            return false;
        }
        public void refresh(){
            health = maxHealth;
        }
    }
    public static class PlayerStats extends StatsTemplate{
        // Instance Variables:
        private float shields;
        private float maxShields;
        
        public PlayerStats(float health, float maxHealth, float shields, float maxShields){
            super(health, maxHealth);
            this.shields = shields;
            this.maxShields = maxShields;
        }
        
        public float getShields(){
            return shields;
        }
        
        @Override
        public boolean damage(float damage){
            if(shields > 0){
                shields -= damage;
                if(shields <= 0){
                    shields = 0;
                    return super.damage(FastMath.abs(shields));
                }
            }else{
                return super.damage(damage);
            }
            return false;
        }
        @Override
        public void refresh(){
            super.refresh();
            shields = maxShields;
        }
    }
}
