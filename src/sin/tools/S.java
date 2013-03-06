package sin.tools;

import com.jme3.network.Server;
import com.jme3.scene.Node;

/**
 *
 * @author SinisteRing
 */
public class S {
    private static Node collisionNode;
    private static Server server;
    
    public static Node getCollisionNode(){
        return collisionNode;
    }
    public static Server getServer(){
        return server;
    }
    
    public static void setCollisionNode(Node collisionNode){
        S.collisionNode = collisionNode;
    }
    public static void setServer(Server server){
        S.server = server;
    }
}
