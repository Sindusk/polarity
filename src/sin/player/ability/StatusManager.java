package sin.player.ability;

import sin.player.Player;

/**
 *
 * @author SinisteRing
 */
public class StatusManager {
    private Player player;
    // Statuses:
    public static abstract class StatusEffect{
        private float time;
        private boolean applied;
        
        public boolean isApplied(){
            return applied;
        }
        public float getTime(){
            return time;
        }
        
        public void update(Player player, float tpf){
            time -= tpf;
            if(time <= 0){
                time = 0;
                this.applied = false;
            }
        }
        protected void apply(float time){
            this.time = time;
            this.applied = true;
        }
    }
    public static class Poison extends StatusEffect{
        private float dps;
        
        @Override
        public void update(Player player, float tpf){
            player.damage(player.getID(), tpf*dps);
            super.update(player, tpf);
        }
        
        public void apply(float time, float dps){
            this.dps = dps;
            super.apply(time);
        }
    }
    // End Statuses
    
    public StatusManager(Player player){
        this.player = player;
    }
    
    private Poison poison = new Poison();
    
    public void poison(float time, float dps){
        poison.apply(time, dps);
    }
    
    public void update(float tpf){
        if(poison.isApplied()){
            poison.update(player, tpf);
        }
    }
}
