package sin.character;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.scene.Node;
import sin.animation.Models.PlayerModel;
import sin.netdata.ConnectData;
import sin.netdata.MoveData;
import sin.network.Networking;
import sin.tools.S;

/**
 * PlayerManager - Used for the creation and controlling of networked players.
 * @author SinisteRing
 */
public class PlayerManager{
    private static Node node = new Node("PlayerNode");
    private static Player[] player = new Player[16];
    
    public static class Player{
        private PlayerModel model;
        private Vector3f locA = Vector3f.ZERO;
        private Vector3f locB = Vector3f.ZERO;
        private Quaternion rot = new Quaternion();
        private HostedConnection conn;
        private float interp = 0;
        private boolean connected = false;

        public Player(){}
        
        public boolean isConnected(){
            return connected;
        }
        public HostedConnection getConnection(){
            return conn;
        }
        public Vector3f getLocation(){
            return locB;
        }
        public void setConnection(HostedConnection conn){
            this.conn = conn;
        }
        public void setLocation(Vector3f loc, Quaternion rot){
            this.locA = this.locB.clone();
            this.locB = loc;
            this.rot = rot;
            this.interp = 0;
        }
        
        public void update(float tpf){
            model.update(locA, locB, rot, tpf, interp);
            interp += tpf*Networking.MOVE_INVERSE;
        }
        public void create(int id){
            this.model = new PlayerModel(id, node);
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
    public static Player[] getPlayers(){
        return player;
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
    public static void updatePlayerLocation(MoveData d){
        if(player[d.getID()] != null){
            player[d.getID()].setLocation(d.getLocation(), d.getRotation());
        }
    }
    public static void update(float tpf){
        int i = 0;
        while(i < player.length){
            if(player[i] != null && player[i].isConnected()){
                player[i].update(tpf);
            }
            i++;
        }
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
    public static void add(int id){
        if(player[id] == null || !player[id].isConnected()){
            if(player[id] == null){
                player[id] = new Player();
            }
            player[id].create(id);
        }
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
