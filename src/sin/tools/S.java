package sin.tools;

import com.jme3.network.Client;
import com.jme3.network.Server;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.Timer;

/**
 *
 * @author SinisteRing
 */
public class S {
    private static Camera camera;
    private static Node collisionNode;
    private static Server server;
    private static String version;
    private static Timer timer;
    
    public static Camera getCamera(){
        return camera;
    }
    public static Node getCollisionNode(){
        return collisionNode;
    }
    public static Server getServer(){
        return server;
    }
    public static String getVersion(){
        return version;
    }
    public static Timer getTimer(){
        return timer;
    }
    
    public static void setCamera(Camera camera){
        S.camera = camera;
    }
    public static void setCollisionNode(Node collisionNode){
        S.collisionNode = collisionNode;
    }
    public static void setServer(Server server){
        S.server = server;
    }
    public static void setVersion(String version){
        S.version = version;
    }
    public static void setTimer(Timer timer){
        S.timer = timer;
    }
}
