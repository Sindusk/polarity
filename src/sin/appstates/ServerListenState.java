package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import java.io.IOException;
import sin.GameServer;
import sin.netdata.ConnectData;
import sin.netdata.DecalData;
import sin.netdata.DisconnectData;
import sin.netdata.ErrorData;
import sin.netdata.GeometryData;
import sin.netdata.IDData;
import sin.netdata.MoveData;
import sin.netdata.PingData;
import sin.netdata.ProjectileData;
import sin.netdata.ShotData;
import sin.netdata.SoundData;
import sin.tools.T;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class ServerListenState extends AbstractAppState implements ConnectionListener{
    public static GameServer app;
    
    private Player[] players = new Player[16];
    private Server server;
    private ServerListener listener;
    
    public void connectionAdded(Server server, HostedConnection conn) {
        // Nothing needed here.
    }
    public void connectionRemoved(Server server, HostedConnection conn) {
        int i = 0;
        while(i < players.length){
            if(conn == players[i].getConnection()){
                server.broadcast(new DisconnectData(i));
                players[i].disconnect();
                conn.close("Disconnected.");
                T.log("Player "+i+" has disconnected.");
            }
            i++;
        }
    }
    
    private int FindEmptyID(){
        int i = 0;
        while(i < players.length){
            if(!players[i].isConnected()){
                return i;
            }
            i++;
        }
        return -1;
    }
    private void sendGeometry(HostedConnection c){
        int i = 0;
        while(i < World.getMap().size()){
            c.send(World.getMap().get(i));
            i++;
        }
    }
    
    private class ServerListener implements MessageListener<HostedConnection>{
        private HostedConnection connection;
        
        private void ConnectMessage(ConnectData d){
            T.log("Connecting player "+d.getID());
            if(d.GetVersion().equals(app.getVersion())){
                int id = FindEmptyID();
                if(id == -1){
                    T.log("Server full. Player "+d.getID()+" was denied connection.");
                    connection.send(new ErrorData(-1, "Server Full.", true));
                }
                connection.send(new IDData(id, false));
            }else{
                T.log("Client has incorrect version. Player "+d.getID()+" was denied connection.");
                connection.close("Invalid Version.");
            }
        }
        private void DecalMessage(DecalData d){
            server.broadcast(Filters.notEqualTo(connection), d);
        }
        private void ErrorMessage(ErrorData d){
            System.out.println("Handling ErrorData [ID: "+d.getID()+"]...");
            connection.close("Client Disconnection.");
            players[d.getID()].disconnect();
        }
        private void IDMessage(IDData d){
            if(!players[d.getID()].isConnected()){
                T.log("Player "+d.getID()+" [Version: "+app.getVersion()+"] connected successfully.");
                int id = d.getID();
                int i = 0;
                while(i < players.length){
                    if(players[i].isConnected() && i != id) {
                        connection.send(new ConnectData(i));
                    }
                    i++;
                }
                players[id].connect();
                players[id].setConnection(connection);
                server.broadcast(Filters.notEqualTo(connection), new ConnectData(id));
                sendGeometry(connection);
            }else{
                int id = FindEmptyID();
                if(id == -1){
                    connection.send(new ErrorData(-1, "Server Full.", true));
                    connection.close("Server Full");
                }
                connection.send(new IDData(id, false));
            }
        }
        private void MoveMessage(MoveData d){
            //System.out.println("Handling MoveData from client "+d.getID()+"...");
            server.broadcast(Filters.notEqualTo(connection), d);
            players[d.getID()].setLocation(d.getLocation(), d.getRotation());
        }
        private void PingMessage(PingData d){
            connection.send(d);
        }
        private void ProjectileMessage(ProjectileData d){
            server.broadcast(Filters.notEqualTo(connection), d);
        }
        private void ShotMessage(ShotData d){
            //System.out.println("Handling ShotData from client "+d.getID()+"...");
            System.out.println("Player "+d.getID()+" shot Player "+d.getPlayer()+" for "+d.getDamage()+" damage.");
            server.broadcast(d);
        }
        private void SoundMessage(SoundData d){
            //System.out.println("Handling SoundData from client "+d.getID());
            server.broadcast(d);
        }
        
        public void messageReceived(HostedConnection source, Message m) {
            connection = source;
            server = connection.getServer();
            if(m instanceof ConnectData){
                ConnectMessage((ConnectData) m);
            }else if(m instanceof DecalData){
                DecalMessage((DecalData) m);
            }else if(m instanceof ErrorData){
                ErrorMessage((ErrorData) m);
            }else if(m instanceof IDData){
                IDMessage((IDData) m);
            }else if(m instanceof MoveData){
                MoveMessage((MoveData) m);
            }else if(m instanceof PingData){
                PingMessage((PingData) m);
            }else if(m instanceof ProjectileData){
                ProjectileMessage((ProjectileData) m);
            }else if(m instanceof ShotData){
                ShotMessage((ShotData) m);
            }else if(m instanceof SoundData){
                SoundMessage((SoundData) m);
            }
        }
    }
    private static class Player{
        private boolean connected = false;
        private float health = 100;
        private float shields = 100;
        private HostedConnection connection;
        //private Vector3f loc;
        //private Quaternion rot;
        public Player(){
            //loc = v3f(0, 0, 0);
        }
        
        public void setLocation(Vector3f loc, Quaternion rot){
            //this.loc = loc;
            //this.rot = rot;
        }
        public void setConnection(HostedConnection connection){
            this.connection = connection;
        }
        public boolean isConnected(){
            return connected;
        }
        public HostedConnection getConnection(){
            return connection;
        }
        public float getHealth(){
            return health;
        }
        public float getShields(){
            return shields;
        }
        
        public void connect(){
            connected = true;
        }
        public void disconnect(){
            connected = false;
        }
    }
    
    private void registerSerials(){
        Serializer.registerClass(ConnectData.class);
        server.addMessageListener(listener, ConnectData.class);
        Serializer.registerClass(DecalData.class);
        server.addMessageListener(listener, DecalData.class);
        Serializer.registerClass(DisconnectData.class);
        server.addMessageListener(listener, DisconnectData.class);
        Serializer.registerClass(ErrorData.class);
        server.addMessageListener(listener, ErrorData.class);
        Serializer.registerClass(GeometryData.class);
        server.addMessageListener(listener, GeometryData.class);
        Serializer.registerClass(IDData.class);
        server.addMessageListener(listener, IDData.class);
        Serializer.registerClass(MoveData.class);
        server.addMessageListener(listener, MoveData.class);
        Serializer.registerClass(PingData.class);
        server.addMessageListener(listener, PingData.class);
        Serializer.registerClass(ProjectileData.class);
        server.addMessageListener(listener, ProjectileData.class);
        Serializer.registerClass(ShotData.class);
        server.addMessageListener(listener, ShotData.class);
        Serializer.registerClass(SoundData.class);
        server.addMessageListener(listener, SoundData.class);
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        super.initialize(stateManager, app);
        ServerListenState.app = (GameServer) app;
        
        try {
            server = Network.createServer(6143);
        }catch (IOException ex){
            T.log(ex);
        }
        listener = new ServerListener();
        registerSerials();
        server.addConnectionListener(this);
        server.start();
        int i = 0;
        while(i < players.length){
            players[i] = new Player();
            i++;
        }
        World.generateWorldData();
    }
    
    @Override
    public void cleanup(){
        server.close();
    }
}
