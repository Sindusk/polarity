package sin.network;

import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.scene.Geometry;
import java.io.IOException;
import java.util.concurrent.Callable;
import sin.GameClient;
import sin.netdata.ConnectData;
import sin.netdata.DecalData;
import sin.netdata.DisconnectData;
import sin.netdata.ErrorData;
import sin.netdata.IDData;
import sin.netdata.MoveData;
import sin.netdata.PingData;
import sin.netdata.ProjectileData;
import sin.netdata.ShotData;
import sin.netdata.SoundData;
import sin.character.PlayerManager;
import sin.hud.HUD;
import sin.netdata.GeometryData;
import sin.tools.T;
import sin.weapons.ProjectileManager;
import sin.world.DecalManager;
import sin.world.World;

/**
 * Networking - Used for the connection and maintainence of client-side networking.
 * @author SinisteRing
 */
public class Networking {
    private static GameClient app;
    private static Client client = null;  // For SpiderMonkey connectivity.
    
    // Networking Variables:
    private static boolean CLIENT_CONNECTED = false;
    private static int     CLIENT_ID = -1;
    // Constant Variables:
    private static final float PING_INTERVAL = 1;
    private static final float MOVE_INTERVAL = 0.1f;

    // Index Holders:
    private static final int PING = 0;
    private static final int MOVE = 1;

    // Instance Variables:
    private static boolean pinging = false;
    private static float[] timers = new float[2];
    private static float time;
    
    public Networking(){
        //
    }
    
    public static boolean isConnected(){
        return CLIENT_CONNECTED;
    }
    public static int getID(){
        return CLIENT_ID;
    }
    
    private static void registerSerials(){
        Serializer.registerClass(ConnectData.class);
        client.addMessageListener(new ClientListener(), ConnectData.class);
        Serializer.registerClass(DecalData.class);
        client.addMessageListener(new ClientListener(), DecalData.class);
        Serializer.registerClass(DisconnectData.class);
        client.addMessageListener(new ClientListener(), DisconnectData.class);
        Serializer.registerClass(ErrorData.class);
        client.addMessageListener(new ClientListener(), ErrorData.class);
        Serializer.registerClass(GeometryData.class);
        client.addMessageListener(new ClientListener(), GeometryData.class);
        Serializer.registerClass(IDData.class);
        client.addMessageListener(new ClientListener(), IDData.class);
        Serializer.registerClass(MoveData.class);
        client.addMessageListener(new ClientListener(), MoveData.class);
        Serializer.registerClass(PingData.class);
        client.addMessageListener(new ClientListener(), PingData.class);
        Serializer.registerClass(ProjectileData.class);
        client.addMessageListener(new ClientListener(), ProjectileData.class);
        Serializer.registerClass(ShotData.class);
        client.addMessageListener(new ClientListener(), ShotData.class);
        Serializer.registerClass(SoundData.class);
        client.addMessageListener(new ClientListener(), SoundData.class);
    }
    public static boolean connect(String ip){
        try {
            client = Network.connectToServer(ip, 6143);
            Networking.registerSerials();
            client.addClientStateListener(new ClientListener());
            client.start();
            client.send(new ConnectData(app.getVersion()));
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
            time = app.getTimer().getTimeInSeconds();
            client.send(new PingData());
            timers[PING] = 0;
        }

        // Send updated movement data:
        if(timers[MOVE] >= MOVE_INTERVAL){
            client.send(new MoveData(CLIENT_ID, app.getCharacter().getPlayer().getPhysicsLocation(), app.getCamera().getRotation()));
            timers[MOVE] = 0;
        }
    }
    
    public static void send(Message message){
        if(isConnected()){
            client.send(message);
        }
    }
    public static void sendSound(String name){
        send(new SoundData(CLIENT_ID, name));
    }
    
    private static class ClientListener implements MessageListener<Client>, ClientStateListener {
        private Client client;

        private void ConnectMessage(ConnectData d){
            final int id = d.getID();
            //if(!PlayerManager.getPlayer(d.getID()).isUsed()){
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    //app.getPlayer(id).create();
                    if(!PlayerManager.add(id, Vector3f.ZERO)){
                        T.log("Player already connected.");
                    }
                    return null;
                }
            });
            //}
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
        private void ErrorMessage(ErrorData d){
            T.log(d.getError());
        }
        private void GeometryMessage(GeometryData d){
            final GeometryData w = d;
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    World.createGeometry(w);
                    app.getCharacter().kill();
                    return null;
                }
            });
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
                    //app.getPlayer(id).move(loc, rot);
                    PlayerManager.update(id, loc, rot);
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
            final Vector3f loc = d.getLocation();
            final Vector3f dir = d.getDirection();
            final Vector3f up = d.getUp();
            final float dist = d.getDistance();
            final float speed = d.getSpeed();
            final String update = d.getUpdate();
            final String collision = d.getCollision();
            app.enqueue(new Callable<Void>(){
                public Void call() throws Exception{
                    ProjectileManager.add(loc, dir, up, dist, speed, update, collision);
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
                        app.getCharacter().damage(damage);
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
            }else if(m instanceof ShotData){
                ShotMessage((ShotData) m);
            }else if(m instanceof SoundData){
                SoundMessage((SoundData) m);
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
        Networking.app = app;
    }
}
