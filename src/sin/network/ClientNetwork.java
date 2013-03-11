package sin.network;

import com.jme3.audio.AudioNode;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import java.io.IOException;
import java.util.concurrent.Callable;
import sin.GameClient;
import sin.netdata.player.ConnectData;
import sin.netdata.DecalData;
import sin.netdata.player.DisconnectData;
import sin.netdata.ErrorData;
import sin.netdata.player.IDData;
import sin.netdata.player.MoveData;
import sin.netdata.PingData;
import sin.netdata.ProjectileData;
import sin.netdata.DamageData;
import sin.netdata.SoundData;
import sin.player.PlayerManager;
import sin.hud.HUD;
import sin.input.ClientInputHandler;
import sin.netdata.AttackData;
import sin.netdata.CommandData;
import sin.netdata.GeometryData;
import sin.netdata.ability.AbilityCooldownData;
import sin.netdata.ability.AbilityData;
import sin.netdata.npc.GruntData;
import sin.netdata.npc.EntityData;
import sin.netdata.npc.EntityDeathData;
import sin.netdata.npc.OrganismData;
import sin.netdata.player.PlayerData;
import sin.npc.NPCManager;
import sin.player.ability.AbilityManager;
import sin.tools.A;
import sin.tools.S;
import sin.tools.T;
import sin.weapons.ProjectileManager;
import sin.world.DecalManager;
import sin.world.World;

/**
 * ClientNetwork - Used for the connection and maintainence of client-side networking.
 * @author SinisteRing
 */
public class ClientNetwork {
    private static GameClient app;
    private static Client client = null;  // For SpiderMonkey connectivity.
    private static ClientListener listener = new ClientListener();
    
    // Constants:
    public static final float PING_INTERVAL = 1;
    public static final float MOVE_INTERVAL = 0.05f;
    public static final float MOVE_INVERSE = 1.0f/MOVE_INTERVAL;
    
    // ClientNetwork Variables:
    private static boolean CLIENT_CONNECTED = false;
    private static int     CLIENT_ID = -1;

    // Index Holders:
    private static final int PING = 0;
    private static final int MOVE = 1;

    // Instance Variables:
    private static boolean pinging = false;
    private static float[] timers = new float[2];
    private static float time;
    
    public ClientNetwork(){}
    
    public static boolean isConnected(){
        return CLIENT_CONNECTED;
    }
    public static int getID(){
        return CLIENT_ID;
    }
    
    private static void registerClass(Class c){
        Serializer.registerClass(c);
        client.addMessageListener(listener, c);
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
        
        // Ability Serials:
        registerClass(AbilityData.class);
        registerClass(AbilityCooldownData.class);
        
        // NPC Serials:
        registerClass(GruntData.class);
        registerClass(EntityData.class);
        registerClass(EntityDeathData.class);
        registerClass(OrganismData.class);
        
        // Player Serials:
        registerClass(ConnectData.class);
        registerClass(DisconnectData.class);
        registerClass(IDData.class);
        registerClass(MoveData.class);
        registerClass(PlayerData.class);
    }
    
    public static boolean connect(String ip){
        try {
            client = Network.connectToServer(ip, 6143);
            registerSerials();
            client.addClientStateListener(listener);
            client.start();
            client.send(new ConnectData(S.getVersion()));
            timers[PING] = 1;
            timers[MOVE] = 0;
            return true;
        } catch (IOException ex) {
            T.log(ex);
            return false;
        }
    }
    public static void disconnect(){
        if(!CLIENT_CONNECTED){
            return;
        }
        CLIENT_ID = -1;
        CLIENT_CONNECTED = false;
        client.close();
    }
    public static void close(){
        client.close();
    }
    public static void update(float tpf){
        int i = 0;
        while(i < timers.length){
            timers[i] += tpf;
            i++;
        }

        // Ping:
        if(timers[PING] >= PING_INTERVAL && !pinging){
            time = S.getTimer().getTimeInSeconds();
            client.send(new PingData());
            timers[PING] = 0;
        }

        // Send updated movement data:
        if(timers[MOVE] >= MOVE_INTERVAL){
            client.send(new MoveData(CLIENT_ID, PlayerManager.getPlayer(CLIENT_ID).getControl().getPhysicsLocation(), S.getCamera().getRotation()));
            timers[MOVE] = 0;
        }
    }
    
    public static void send(Message message){
        if(isConnected()){
            client.send(message);
        }
    }
    public static void sendPlayerData(){
        
    }
    public static void sendSound(String name){
        send(new SoundData(CLIENT_ID, name));
    }
    
    private static class ClientListener implements MessageListener<Client>, ClientStateListener {
        private Client client;
        
        private void CommandMessage(CommandData d){
            final CommandData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    A.handleCommand(m.getCommand());
                    return null;
                }
            });
        }
        private void DamageMessage(DamageData d){
            final DamageData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    A.handleDamage(m);
                    return null;
                }
            });
        }
        private void DecalMessage(DecalData d){
            final Vector3f loc = d.getLocation();
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    DecalManager.create(loc);
                    return null;
                }
            });
        }
        private void DisconnectMessage(DisconnectData d){
            final int id = d.getID();
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    //app.getPlayer(id).destroy();
                    PlayerManager.remove(id);
                    return null;
                }
            });
        }
        private void EntityMessage(EntityData d){
            final EntityData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    NPCManager.add(m);
                    return null;
                }
            });
        }
        private void EntityDeathMessage(EntityDeathData d){
            final EntityDeathData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    NPCManager.destroyNPC(m);
                    return null;
                }
            });
        }
        private void ErrorMessage(ErrorData d){
            T.log(d.getError());
        }
        private void GeometryMessage(GeometryData d){
            final GeometryData w = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    World.createGeometry(app.getTerrain(), w);
                    PlayerManager.getPlayer(CLIENT_ID).kill();
                    return null;
                }
            });
        }
        private void IDMessage(IDData d){
            CLIENT_ID = d.getID();
            client.send(new PlayerData(CLIENT_ID, "M4A1:RocketLauncher-Raygun:AK47"));
        }
        private void MoveMessage(MoveData d){
            final MoveData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    PlayerManager.updatePlayerLocation(m);
                    return null;
                }
            });
        }
        private void PingMessage(){
            final float pingTime = app.getTimer().getTimeInSeconds() - time;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    HUD.getPing().setText("Ping: "+(int) FastMath.ceil(pingTime*1000)+" ms");
                    return null;
                }
            });
        }
        private void ProjectileMessage(ProjectileData d){
            final ProjectileData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    ProjectileManager.add(m);
                    return null;
                }
            });
        }
        private void SoundMessage(SoundData d){
            if(d.getID() == CLIENT_ID) {
                return;
            }
            final String s = d.getSound();
            final Vector3f loc = PlayerManager.getPlayer(d.getID()).getLocation();
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    AudioNode node = new AudioNode(app.getAssetManager(), s);
                    node.setPositional(true);
                    node.setLocalTranslation(loc);
                    node.playInstance();
                    return null;
                }
            });
        }
        // Ability:
        private void AbilityCooldownMessage(AbilityCooldownData d){
            final AbilityCooldownData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    AbilityManager.addCooldown(m);
                    return null;
                }
            });
        }
        // Player:
        private void PlayerMessage(PlayerData d){
            final PlayerData m = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    PlayerManager.add(m);
                    if(m.getID() == CLIENT_ID){
                        ClientInputHandler.initialize();
                        CLIENT_CONNECTED = true;
                    }
                    return null;
                }
            });
        }

        public void messageReceived(Client source, Message m) {
            client = source;
            
            if(m instanceof CommandData){
                CommandMessage((CommandData) m);
            }else if(m instanceof DamageData){
                DamageMessage((DamageData) m);
            }else if(m instanceof DecalData){
                DecalMessage((DecalData) m);
            }else if(m instanceof DisconnectData){
                DisconnectMessage((DisconnectData) m);
            }else if(m instanceof EntityData){
                EntityMessage((EntityData) m);
            }else if(m instanceof EntityDeathData){
                EntityDeathMessage((EntityDeathData) m);
            }else if(m instanceof ErrorData){
                ErrorMessage((ErrorData) m);
            }else if(m instanceof GeometryData){
                GeometryMessage((GeometryData) m);
            }else if(m instanceof IDData){
                IDMessage((IDData) m);
            }else if(m instanceof MoveData){
                MoveMessage((MoveData) m);
            }else if(m instanceof PingData){
                PingMessage();
            }else if(m instanceof ProjectileData){
                ProjectileMessage((ProjectileData) m);
            }else if(m instanceof SoundData){
                SoundMessage((SoundData) m);
            }
            // Ability:
            else if(m instanceof AbilityCooldownData){
                AbilityCooldownMessage((AbilityCooldownData) m);
            }
            // Player:
            else if(m instanceof PlayerData){
                PlayerMessage((PlayerData) m);
            }
        }

        public void clientConnected(Client c) {
            T.log("Welcome to the server.");
        }

        public void clientDisconnected(Client c, DisconnectInfo info) {
            app.getMenuState().action("game.mainmenu");
        }
    }
    
    public static void initialize(GameClient app){
        ClientNetwork.app = app;
    }
}
