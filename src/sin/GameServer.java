package sin;

import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
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
import com.jme3.renderer.RenderManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import sin.world.*;

/**
Copyright (c) 2003-2011 jMonkeyEngine
All rights reserved.
 
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 
Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
 
Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
 
Neither the name of 'jMonkeyEngine' nor the names of its contributors 
may be used to endorse or promote products derived from this software 
without specific prior written permission.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * Game Server
 * @author SinisteRing
 */
public class GameServer extends SimpleApplication implements ConnectionListener {
    
    private static final String SERVER_VERSION = "ALPHA 0.05";
    private static final Logger logger = Logger.getLogger(GameServer.class.getName());
    private Player[] players = new Player[16];
    private int[][] world;
    private Server myServer = null;
    
    private Vector3f v3f(float x, float y, float z){
        return new Vector3f(x, y, z);
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
            }
            i++;
        }
    }
    
    private void RegisterSerials(){
        Serializer.registerClass(ConnectData.class);
        myServer.addMessageListener(new ServerListener(), ConnectData.class);
        Serializer.registerClass(DecalData.class);
        myServer.addMessageListener(new ServerListener(), DecalData.class);
        Serializer.registerClass(DisconnectData.class);
        myServer.addMessageListener(new ServerListener(), DisconnectData.class);
        Serializer.registerClass(ErrorData.class);
        myServer.addMessageListener(new ServerListener(), ErrorData.class);
        Serializer.registerClass(IDData.class);
        myServer.addMessageListener(new ServerListener(), IDData.class);
        Serializer.registerClass(MoveData.class);
        myServer.addMessageListener(new ServerListener(), MoveData.class);
        Serializer.registerClass(PingData.class);
        myServer.addMessageListener(new ServerListener(), PingData.class);
        Serializer.registerClass(ShotData.class);
        myServer.addMessageListener(new ServerListener(), ShotData.class);
        Serializer.registerClass(SoundData.class);
        myServer.addMessageListener(new ServerListener(), SoundData.class);
        Serializer.registerClass(WorldData.class);
        myServer.addMessageListener(new ServerListener(), WorldData.class);
    }
    
    private class ServerListener implements MessageListener<HostedConnection>{
        private HostedConnection connection;
        private Server server;
        
        private void ConnectMessage(ConnectData d){
            System.out.println("Handling ConnectData...");
            if(d.GetVersion().equals(SERVER_VERSION)){
                int id = FindEmptyID();
                if(id == -1){
                    System.out.println("Server full. [CM]");
                    connection.send(new ErrorData(-1, "Server Full.", true));
                }
                //players[id].setConnection(connection);
                System.out.println("Connecting player "+id+" [Version: "+SERVER_VERSION+"]... [CM]");
                connection.send(new IDData(id, false));
            }else{
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
            System.out.println("Handling IDData for "+d.getID()+"...");
            if(!players[d.getID()].isConnected()){
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
                connection.send(new WorldData(world));
            }else{
                int id = FindEmptyID();
                if(id == -1){
                    System.out.println("Server full. [IDM]");
                    connection.send(new ErrorData(-1, "Server Full.", true));
                    connection.close("Server Full");
                }
                System.out.println("Connecting player "+id+" [Version: "+SERVER_VERSION+"]... [IDM]");
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
            }else if(m instanceof ShotData){
                ShotMessage((ShotData) m);
            }else if(m instanceof SoundData){
                SoundMessage((SoundData) m);
            }
        }
    }
    
    private class Player{
        private boolean connected = false;
        private float health = 100;
        private float shields = 100;
        private HostedConnection connection;
        private Vector3f loc;
        private Quaternion rot;
        public Player(){
            loc = v3f(0, 0, 0);
        }
        
        public void setLocation(Vector3f loc, Quaternion rot){
            this.loc = loc;
            this.rot = rot;
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
    
    public static void main(String[] args) throws IOException {
        Logger.getLogger("com.jme3").setLevel(Level.WARNING);
        GameServer app = new GameServer();
        //AppSettings set = new AppSettings(true);
        app.showSettings = false;
        app.start();
    }

    private void initWorld(){
        int x = 0;
        int z;
        world = new int[World.ZONE_X_NUM][World.ZONE_Z_NUM];
        while(x < World.ZONE_X_NUM){
            z = 0;
            while(z < World.ZONE_Z_NUM){
                world[x][z] = FastMath.rand.nextInt(World.ZONE_VARIATIONS);
                z++;
            }
            x++;
        }
    }
    @Override
    public void simpleInitApp() {
        try {
            myServer = Network.createServer(6143);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        RegisterSerials();
        myServer.addConnectionListener(this);
        myServer.start();
        int i = 0;
        while(i < players.length){
            players[i] = new Player();
            i++;
        }
        flyCam.setDragToRotate(true);
        initWorld();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    @Override
    public void destroy(){
        myServer.close();
        super.destroy();
    }
}
