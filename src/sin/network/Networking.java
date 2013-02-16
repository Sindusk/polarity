package sin.network;

import com.jme3.audio.AudioNode;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import sin.GameClient;
import sin.data.ConnectData;
import sin.data.DecalData;
import sin.data.DisconnectData;
import sin.data.ErrorData;
import sin.data.IDData;
import sin.data.MoveData;
import sin.data.PingData;
import sin.data.ShotData;
import sin.data.SoundData;
import sin.data.WorldData;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class Networking {
    private static GameClient app;
    public static Client client = null;  // For SpiderMonkey connectivity.
    
    // Networking Variables:
    public static boolean CLIENT_CONNECTED = false;
    public static int     CLIENT_ID = -1;
    // Constant Variables:
    private static final float PING_INTERVAL = 1;
    private static final float MOVE_INTERVAL = 0.2f;

    // Index Holders:
    private static final int PING = 0;
    private static final int MOVE = 1;

    // Instance Variables:
    public static boolean pinging = false;
    private static float[] timers = new float[2];
    private static float time;

    public Networking(){
        //
    }

    private void registerSerials(){
        Serializer.registerClass(ConnectData.class);
        client.addMessageListener(new ClientListener(), ConnectData.class);
        Serializer.registerClass(DecalData.class);
        client.addMessageListener(new ClientListener(), DecalData.class);
        Serializer.registerClass(DisconnectData.class);
        client.addMessageListener(new ClientListener(), DisconnectData.class);
        Serializer.registerClass(ErrorData.class);
        client.addMessageListener(new ClientListener(), ErrorData.class);
        Serializer.registerClass(IDData.class);
        client.addMessageListener(new ClientListener(), IDData.class);
        Serializer.registerClass(MoveData.class);
        client.addMessageListener(new ClientListener(), MoveData.class);
        Serializer.registerClass(PingData.class);
        client.addMessageListener(new ClientListener(), PingData.class);
        Serializer.registerClass(ShotData.class);
        client.addMessageListener(new ClientListener(), ShotData.class);
        Serializer.registerClass(SoundData.class);
        client.addMessageListener(new ClientListener(), SoundData.class);
        Serializer.registerClass(WorldData.class);
        client.addMessageListener(new ClientListener(), WorldData.class);
    }
    public boolean connect(String ip){
        if(client == null){
            try {
                client = Network.connectToServer(ip, 6143);
            } catch (IOException ex) {
                GameClient.getLogger().log(Level.SEVERE, null, ex);
                return false;
            }
            this.registerSerials();
            client.start();
            client.send(new ConnectData(GameClient.CLIENT_VERSION));
            timers[PING] = 1;
            timers[MOVE] = 0;
            return true;
        }
        return false;
    }
    public static boolean isConnected(){
        return CLIENT_CONNECTED;
    }
    public static void update(float tpf){
        int i = 0;
        while(i < timers.length){
            timers[i] += tpf;
            i++;
        }

        // Ping:
        if(timers[PING] >= PING_INTERVAL && !pinging){
            time = app.getTimer().getTimeInSeconds();
            client.send(new PingData());
            timers[PING] = 0;
        }

        // Send updated movement data:
        if(timers[MOVE] >= MOVE_INTERVAL){
            client.send(new MoveData(CLIENT_ID, GameClient.getCharacter().getPlayer().getPhysicsLocation(), app.getCamera().getRotation()));
            timers[MOVE] = 0;
        }
    }

    private class ClientListener implements MessageListener<Client> {
        private Client client;

        private void ConnectMessage(ConnectData d){
            final int id = d.getID();
            if(!GameClient.getPlayer(d.getID()).created){
                app.enqueue(new Callable<Void>(){
                    public Void call() throws Exception{
                        GameClient.getPlayer(id).create();
                        return null;
                    }
                });
            }
        }
        private void DecalMessage(DecalData d){
            final Vector3f loc = d.getLocation();
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    GameClient.getDCS().createDecal(loc);
                    return null;
                }
            });
        }
        private void DisconnectMessage(DisconnectData d){
            final int id = d.getID();
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    GameClient.getPlayer(id).destroy();
                    return null;
                }
            });
        }
        private void ErrorMessage(ErrorData d){
            //players[d.getID()].change = true;
        }
        private void IDMessage(IDData d){
            CLIENT_CONNECTED = true;
            CLIENT_ID = d.getID();
            client.send(new IDData(CLIENT_ID, true));
        }
        private void MoveMessage(MoveData d){
            final int id = d.getID();
            final Vector3f loc = d.getLocation();
            final Quaternion rot = d.getRotation();
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    GameClient.getPlayer(id).move(loc, rot);
                    return null;
                }
            });
        }
        private void PingMessage(PingData d){
            final float pingTime = app.getTimer().getTimeInSeconds() - time;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    GameClient.getHUD().ping.setText("Ping: "+(int) FastMath.ceil(pingTime*1000)+" ms");
                    return null;
                }
            });
        }
        private void ShotMessage(ShotData d){
            if(d.getPlayer() == CLIENT_ID){
                //System.out.println("Took "+d.getDamage()+" damage.");
                final float damage = d.getDamage();
                app.enqueue(new Callable<Void>(){
                    public Void call() throws Exception{
                        GameClient.getCharacter().damage(damage);
                        return null;
                    }
                });
            }
        }
        private void SoundMessage(SoundData d){
            if(d.getID() == CLIENT_ID) {
                return;
            }
            final String s = d.getSound();
            final Vector3f loc = GameClient.getPlayer(d.getID()).getLocation();
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
        private void WorldMessage(WorldData d){
            //world = d.getWorld();
            final int[][] world = d.getWorld();
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    //worldNode.detachAllChildren();
                    GameClient.getCharacter().kill();
                    World.create(world, GameClient.getTerrain());
                    return null;
                }
            });
        }

        public void messageReceived(Client source, Message m) {
            client = source;
            if(m instanceof ConnectData){
                ConnectMessage((ConnectData) m);
            }else if(m instanceof DecalData){
                DecalMessage((DecalData) m);
            }else if(m instanceof DisconnectData){
                DisconnectMessage((DisconnectData) m);
            }else if(m instanceof ErrorData){
                ErrorMessage((ErrorData) m);
            }else if(m instanceof IDData){
                IDMessage((IDData) m);
            }else if(m instanceof MoveData){
                MoveMessage((MoveData) m);
            }else if(m instanceof PingData){
                PingMessage((PingData) m);
            }else if(m instanceof ShotData){
                ShotMessage((ShotData) m);
            }else if(m instanceof SoundData){
                SoundMessage((SoundData) m);
            }else if(m instanceof WorldData){
                WorldMessage((WorldData) m);
            }
        }
    }
    
    public static void initialize(GameClient app){
        Networking.app = app;
    }
}
