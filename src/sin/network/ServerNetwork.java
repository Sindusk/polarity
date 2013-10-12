package sin.network;

import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import java.io.IOException;
import java.util.concurrent.Callable;
import sin.GameServer;
import sin.netdata.AttackData;
import sin.netdata.CommandData;
import sin.netdata.DamageData;
import sin.netdata.DecalData;
import sin.netdata.ErrorData;
import sin.netdata.GeometryData;
import sin.netdata.PingData;
import sin.netdata.ProjectileData;
import sin.netdata.SoundData;
import sin.netdata.ability.AbilityCooldownData;
import sin.netdata.ability.AbilityData;
import sin.netdata.npc.EntityData;
import sin.netdata.npc.EntityDeathData;
import sin.netdata.npc.GruntData;
import sin.netdata.npc.OrganismData;
import sin.netdata.player.ConnectData;
import sin.netdata.player.DisconnectData;
import sin.netdata.player.IDData;
import sin.netdata.player.MoveData;
import sin.netdata.player.PlayerData;
import sin.ai.NPCManager;
import sin.player.PlayerManager;
import sin.tools.A;
import sin.tools.T;
import sin.weapons.ProjectileManager;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class ServerNetwork{
    private static GameServer app;
    private static Server server;
    private static ServerListener listener;
    
    private static void registerClass(Class c){
        Serializer.registerClass(c);
        server.addMessageListener(listener, c);
    }
    private static void registerSerials(){
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
    
    public static void create(){
        try {
            server = Network.createServer(6143);
            listener = new ServerListener();
            registerSerials();
            server.addConnectionListener(listener);
            server.start();
        }catch (IOException ex){
            T.log(ex);
        }
    }
    public static void stop(){
        server.close();
    }
    
    public static void sendGeometry(HostedConnection c){
        int i = 0;
        while(i < World.getMap().size()){
            c.send(World.getMap().get(i));
            i++;
        }
    }
    public static void broadcast(Message m){
        if(server != null){
            server.broadcast(m);
        }
    }
    
    private static class ServerListener implements MessageListener<HostedConnection>, ConnectionListener{
        private HostedConnection connection;
        
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
    
    public static void initialize(GameServer app){
        ServerNetwork.app = app;
    }
}
