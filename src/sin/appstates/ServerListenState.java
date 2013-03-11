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
import sin.player.PlayerManager;
import sin.netdata.AttackData;
import sin.netdata.CommandData;
import sin.netdata.player.ConnectData;
import sin.netdata.DecalData;
import sin.netdata.player.DisconnectData;
import sin.netdata.ErrorData;
import sin.netdata.GeometryData;
import sin.netdata.player.IDData;
import sin.netdata.player.MoveData;
import sin.netdata.PingData;
import sin.netdata.ProjectileData;
import sin.netdata.DamageData;
import sin.netdata.npc.EntityData;
import sin.netdata.SoundData;
import sin.netdata.ability.AbilityCooldownData;
import sin.netdata.ability.AbilityData;
import sin.netdata.npc.GruntData;
import sin.netdata.npc.EntityDeathData;
import sin.netdata.npc.OrganismData;
import sin.netdata.player.PlayerData;
import sin.npc.NPCManager;
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
        
        // Abilities:
        private void AbilityMessage(AbilityData d){
            final AbilityData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    PlayerManager.getPlayer(m.getAttacker()).cast(m.getAbility(), m.getRay());
                    return null;
                }
            });
        }
        
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
                        int id = PlayerManager.findEmptyPlayer();
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
        private void MoveMessage(MoveData d){
            server.broadcast(Filters.notEqualTo(connection), d);
            final MoveData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    PlayerManager.updatePlayerLocation(m);
                    return null;
                }
            });
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
        private void SoundMessage(SoundData d){
            //System.out.println("Handling SoundData from client "+d.getID());
            server.broadcast(d);
        }
        // Players:
        private void PlayerMessage(PlayerData d){
            T.log("Player "+d.getID()+" [Version "+app.getVersion()+"] connected successfully.");
            int id = d.getID();
            PlayerManager.add(d);
            PlayerManager.getPlayer(id).setConnection(connection);
            PlayerManager.sendData(connection);
            sendGeometry(connection);
            NPCManager.sendData(connection);
        }
        
        public void messageReceived(HostedConnection source, Message m) {
            connection = source;
            server = connection.getServer();
            
            if(m instanceof AttackData){
                AttackMessage((AttackData) m);
            }if(m instanceof ConnectData){
                ConnectMessage((ConnectData) m);
            }else if(m instanceof DecalData){
                DecalMessage((DecalData) m);
            }else if(m instanceof ErrorData){
                ErrorMessage((ErrorData) m);
            }else if(m instanceof MoveData){
                MoveMessage((MoveData) m);
            }else if(m instanceof PingData){
                PingMessage((PingData) m);
            }else if(m instanceof ProjectileData){
                ProjectileMessage((ProjectileData) m);
            }else if(m instanceof SoundData){
                SoundMessage((SoundData) m);
            }
            // Abilities:
            else if(m instanceof AbilityData){
                AbilityMessage((AbilityData) m);
            }
            // Players:
            else if(m instanceof PlayerData){
                PlayerMessage((PlayerData) m);
            }
        }
    }
    
    private void registerClass(Class c){
        Serializer.registerClass(c);
        server.addMessageListener(listener, c);
    }
    private void registerSerials(){
        registerClass(AttackData.class);
        registerClass(CommandData.class);
        registerClass(DamageData.class);
        registerClass(DecalData.class);
        registerClass(ErrorData.class);
        registerClass(GeometryData.class);
        registerClass(PingData.class);
        registerClass(ProjectileData.class);
        registerClass(SoundData.class);
        
        // Ability Data:
        registerClass(AbilityData.class);
        registerClass(AbilityCooldownData.class);
        
        // NPC Data:
        registerClass(GruntData.class);
        registerClass(EntityData.class);
        registerClass(EntityDeathData.class);
        registerClass(OrganismData.class);
        
        // Player Data:
        registerClass(ConnectData.class);
        registerClass(DisconnectData.class);
        registerClass(IDData.class);
        registerClass(MoveData.class);
        registerClass(PlayerData.class);
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
        collisionNode.attachChild(NPCManager.getNode());
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
        
        PlayerManager.update(tpf);
        ProjectileManager.update(tpf, true);
    }
    
    @Override
    public void cleanup(){
        server.close();
    }
}
