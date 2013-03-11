package sin.player;

import com.jme3.network.HostedConnection;
import com.jme3.scene.Node;
import sin.netdata.player.MoveData;
import sin.netdata.player.PlayerData;
import sin.network.ServerNetwork;
import sin.tools.T;

/**
 * PlayerManager - Used for the creation and controlling of networked players.
 * @author SinisteRing
 */
public class PlayerManager{
    private static Node node = new Node("PlayerNode");
    private static Player[] player = new Player[16];
    
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
            if(player[i] != null && player[i].isConnected() && player[i].getConnection() != conn){
                conn.send(player[i].getData());
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
    
    public static int findEmptyPlayer(){
        int i = 0;
        while(i < player.length){
            if(player[i] == null || !player[i].isConnected()){
                return i;
            }
            i++;
        }
        return -1;
    }
    public static void add(PlayerData d){
        int id = d.getID();
        if(player[id] == null || !player[id].isConnected()){
            if(player[id] == null){
                player[id] = new Player();
            }
            player[id].create(d);
        }
        ServerNetwork.broadcast(d);
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
