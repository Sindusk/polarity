package sin.character;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import sin.GameClient;
import sin.animation.Models.PlayerModel;

/**
 * PlayerManager - Used for the creation and controlling of networked players.
 * @author SinisteRing
 */
public class PlayerManager{
    private static GameClient app;
    
    private static Player[] player = new Player[16];
    
    public static class Player{
        private PlayerModel model;
        private Vector3f loc;
        private boolean used = false;

        public Player(){}
        
        public Vector3f getLocation(){
            return loc;
        }
        public boolean isUsed(){
            return used;
        }
        
        public void update(Vector3f loc, Quaternion rot){
            this.loc = loc;
            model.update(loc, rot);
        }
        public void create(int id, Vector3f loc){
            this.model = new PlayerModel(app.getPlayerNode(), id);
            used = true;
        }
        public void destroy(){
            model.destroy();
            used = false;
        }
    }
    
    public static Player getPlayer(int index){
        return player[index];
    }
    
    public static void updatePlayer(int id, Vector3f loc, Quaternion rot){
        player[id].update(loc, rot);
    }
    public static boolean add(int id, Vector3f loc){
        if(player[id] == null || !player[id].isUsed()){
            if(player[id] == null){
                player[id] = new Player();
            }
            player[id].create(id, loc);
            return true;
        }
        return false;
    }
    public static void remove(int id){
        player[id].destroy();
    }
    
    public static void initialize(GameClient app){
        PlayerManager.app = app;
    }
}
