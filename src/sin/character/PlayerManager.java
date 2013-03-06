package sin.character;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.scene.Node;
import sin.animation.Models.PlayerModel;
import sin.netdata.ConnectData;

/**
 * PlayerManager - Used for the creation and controlling of networked players.
 * @author SinisteRing
 */
public class PlayerManager{
    private static Node node = new Node("PlayerNode");
    private static Player[] player = new Player[16];
    
    public static class Player{
        private PlayerModel model;
        private Vector3f loc;
        private HostedConnection conn;
        private boolean connected = false;

        public Player(){}
        
        public boolean isConnected(){
            return connected;
        }
        public HostedConnection getConnection(){
            return conn;
        }
        public Vector3f getLocation(){
            return loc;
        }
        public void setConnection(HostedConnection conn){
            this.conn = conn;
        }
        
        public void update(Vector3f loc, Quaternion rot){
            this.loc = loc;
            model.update(loc, rot);
        }
        public void create(int id){
            this.model = new PlayerModel(node, id);
            connected = true;
        }
        public void destroy(){
            model.destroy();
            connected = false;
        }
    }
    
    public static Node getNode(){
        return node;
    }
    public static Player getPlayer(int index){
        return player[index];
    }
    
    public static void sendData(HostedConnection conn){
        int i = 0;
        while(i < player.length){
            if(player[i] != null && player[i].isConnected()){
                conn.send(new ConnectData(i));
            }
            i++;
        }
    }
    public static void updatePlayer(int id, Vector3f loc, Quaternion rot){
        player[id].update(loc, rot);
    }
    
    private static int findEmptyPlayer(){
        int i = 0;
        while(i < player.length){
            if(player[i] == null || !player[i].isConnected()){
                return i;
            }
            i++;
        }
        return -1;
    }
    public static boolean add(int id){
        if(player[id] == null || !player[id].isConnected()){
            if(player[id] == null){
                player[id] = new Player();
            }
            player[id].create(id);
            return true;
        }
        return false;
    }
    public static int addNew(){
        int id = findEmptyPlayer();
        if(id != -1){
            add(id);
        }
        return id;
    }
    public static void remove(int id){
        player[id].destroy();
    }
    public static int remove(HostedConnection conn){
        int i = 0;
        while(i < player.length){
            if(player[i] != null && conn == player[i].getConnection()){
                player[i].destroy();
                return i;
            }
            i++;
        }
        return -1;
    }
}
