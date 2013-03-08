package sin.tools;

import com.jme3.network.Server;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author SinisteRing
 */
public class S {
    // Constants:
    public static final float PING_INTERVAL = 1;
    public static final float MOVE_INTERVAL = 0.05f;
    public static final float MOVE_INVERSE = 1.0f/MOVE_INTERVAL;
    
    private static Camera camera;
    private static Node collisionNode;
    private static Server server;
    
    public static Camera getCamera(){
        return camera;
    }
    public static Node getCollisionNode(){
        return collisionNode;
    }
    public static Server getServer(){
        return server;
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
}
