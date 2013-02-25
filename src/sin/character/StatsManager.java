package sin.character;

import com.jme3.math.FastMath;
import java.util.HashMap;
import sin.GameClient;
import sin.hud.BarManager;
import sin.character.PlayerManager.Player;

/**
 * StatsManager - Used for the managment of player stats.
 * @author SinisteRing
 */
public class StatsManager {
    private static GameClient app;
    
    private static PlayerStats characterStats = new PlayerStats();
    private static HashMap<Player, PlayerStats> playerStats = new HashMap();
    
    public static class PlayerStats{
        // Instance Variables:
        private float health;
        private float health_max;
        private float shields;
        private float shields_max;

        public PlayerStats(){
            // Constructor
        }
        public PlayerStats(float health_max, float shields_max){
            this.health = health_max;
            this.health_max = health_max;
            this.shields = shields_max;
            this.shields_max = shields_max;
            app.getHUD().setBarMax(BarManager.BH.HEALTH, (int) health_max);
            app.getHUD().setBarMax(BarManager.BH.SHIELDS, (int) shields_max);
            app.getHUD().updateBar(BarManager.BH.HEALTH, (int) FastMath.ceil(health));
            app.getHUD().updateBar(BarManager.BH.SHIELDS, (int) FastMath.ceil(shields));
        }

        public void update(){
            app.getHUD().updateBar(BarManager.BH.HEALTH, (int) FastMath.ceil(health));
            app.getHUD().updateBar(BarManager.BH.SHIELDS, (int) FastMath.ceil(shields));
        }
        public void damage(float damage){
            if(shields > 0){
                shields -= damage;
                if(shields <= 0){
                    health += shields;
                    shields = 0;
                    if(health <= 0){
                        app.getCharacter().kill();
                    }
                }
            }else{
                health -= damage;
                if(health <= 0){
                    app.getCharacter().kill();
                }
            }
            update();
        }
        public void refresh(){
            health = health_max;
            shields = shields_max;
            update();
        }
    }
    
    public static void damage(float amount){
        characterStats.damage(amount);
        characterStats.update();
    }
    public static void damage(Player p, float amount){
        playerStats.get(p).damage(amount);
    }
    public static void add(Player p, float health, float shields){
        playerStats.put(p, new PlayerStats(health, shields));
    }
    public static void createCharacter(float health, float shields){
        characterStats = new PlayerStats(health, shields);
    }
    public static void refreshCharacter(){
        characterStats.refresh();
    }
    
    public static void initialize(GameClient app){
        StatsManager.app = app;
    }
}
