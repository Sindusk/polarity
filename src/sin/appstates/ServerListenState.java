package sin.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.scene.Node;
import java.io.IOException;
import java.util.concurrent.Callable;
import sin.GameServer;
import sin.character.PlayerManager;
import sin.netdata.AttackData;
import sin.netdata.CommandData;
import sin.netdata.ConnectData;
import sin.netdata.DecalData;
import sin.netdata.DisconnectData;
import sin.netdata.ErrorData;
import sin.netdata.GeometryData;
import sin.netdata.IDData;
import sin.netdata.MoveData;
import sin.netdata.PingData;
import sin.netdata.ProjectileData;
import sin.netdata.DamageData;
import sin.netdata.SoundData;
import sin.tools.A;
import sin.tools.S;
import sin.tools.T;
import sin.weapons.ProjectileManager;
import sin.world.DecalManager;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class ServerListenState extends AbstractAppState implements ConnectionListener{
    public static GameServer app;
    
    private Server server;
    private ServerListener listener;
    
    // Nodes:
    private Node collisionNode = new Node("CollisionNode");
    private Node world = new Node("World");
    
    public void connectionAdded(Server server, HostedConnection conn) {
        // Nothing needed here.
    }
    public void connectionRemoved(Server server, HostedConnection conn) {
        int id = PlayerManager.remove(conn);
        if(id == -1){
            return;
        }
        server.broadcast(new DisconnectData(id));
        conn.close("Disconnected");
        T.log("Player "+id+" has disconnected.");
    }
    
    public void sendGeometry(HostedConnection c){
        int i = 0;
        while(i < World.getMap().size()){
            c.send(World.getMap().get(i));
            i++;
        }
    }
    public void send(Message m){
        server.broadcast(m);
    }
    
    private class ServerListener implements MessageListener<HostedConnection>{
        private HostedConnection connection;
        
        private void AttackMessage(AttackData d){
            final AttackData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    A.rayAttack(m);
                    return null;
                }
            });
        }
        private void ConnectMessage(ConnectData d){
            T.log("Connecting new player...");
            if(d.GetVersion().equals(app.getVersion())){
                app.enqueue(new Callable<Void>(){
                    public Void call() throws Exception{
                        int id = PlayerManager.addNew();
                        if(id == -1){
                            T.log("Server full. Player was denied connection.");
                            connection.send(new ErrorData(-1, "Server Full.", true));
                        }else{
                            connection.send(new IDData(id, false));
                        }
                        return null;
                    }
                });
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
            PlayerManager.getPlayer(d.getID()).destroy();
        }
        private void IDMessage(IDData d){
            if(PlayerManager.getPlayer(d.getID()).isConnected()){
                T.log("Player "+d.getID()+" [Version: "+app.getVersion()+"] connected successfully.");
                int id = d.getID();
                PlayerManager.sendData(connection);
                PlayerManager.add(id);
                PlayerManager.getPlayer(id).setConnection(connection);
                server.broadcast(Filters.notEqualTo(connection), new ConnectData(id));
                sendGeometry(connection);
            }
        }
        private void MoveMessage(MoveData d){
            server.broadcast(Filters.notEqualTo(connection), d);
            final MoveData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    PlayerManager.updatePlayer(m);
                    return null;
                }
            });
            //PlayerManager.getPlayer(d.getID()).update(d.getLocation(), d.getRotation());
        }
        private void PingMessage(PingData d){
            connection.send(d);
        }
        private void ProjectileMessage(ProjectileData d){
            final ProjectileData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    ProjectileManager.add(m);
                    return null;
                }
            });
            server.broadcast(Filters.notEqualTo(connection), d);
        }
        private void DamageMessage(DamageData d){
            //System.out.println("Handling DamageData from client "+d.getID()+"...");
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
            if(m instanceof AttackData){
                AttackMessage((AttackData) m);
            }if(m instanceof ConnectData){
                ConnectMessage((ConnectData) m);
            }else if(m instanceof DamageData){
                DamageMessage((DamageData) m);
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
            }else if(m instanceof SoundData){
                SoundMessage((SoundData) m);
            }
        }
    }
    
    private void registerSerials(){
        Serializer.registerClass(AttackData.class);
        server.addMessageListener(listener, AttackData.class);
        Serializer.registerClass(CommandData.class);
        server.addMessageListener(listener, CommandData.class);
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
        Serializer.registerClass(DamageData.class);
        server.addMessageListener(listener, DamageData.class);
        Serializer.registerClass(SoundData.class);
        server.addMessageListener(listener, SoundData.class);
    }
    
    public Node getWorld(){
        return world;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application theApp){
        super.initialize(stateManager, theApp);
        ServerListenState.app = (GameServer) theApp;
        
        // Create server to listen:
        try {
            server = Network.createServer(6143);
            listener = new ServerListener();
            registerSerials();
            server.addConnectionListener(this);
            server.start();
        }catch (IOException ex){
            T.log(ex);
        }
        
        // Initialize Nodes:
        collisionNode.attachChild(PlayerManager.getNode());
        world.attachChild(ProjectileManager.getNode());
        world.attachChild(DecalManager.getNode());
        world.attachChild(collisionNode);
        
        // Initialize classes:
        S.setCollisionNode(collisionNode);
        S.setServer(server);
        ProjectileManager.initialize(collisionNode);
        
        // Create world:
        World.generateWorldData();
        int i = 0;
        while(i < World.getMap().size()){
            World.createGeometry(collisionNode, World.getMap().get(i));
            i++;
        }
    }
    
    @Override
    public void update(float tpf){
        super.update(tpf);  // Execute AppTasks.
        
        ProjectileManager.update(tpf, true);
    }
    
    @Override
    public void cleanup(){
        server.close();
    }
}
