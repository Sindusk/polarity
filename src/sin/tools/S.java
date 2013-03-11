package sin.tools;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.Timer;
import sin.appstates.ClientGameState;
import sin.appstates.ClientMenuState;

/**
 *
 * @author SinisteRing
 */
public class S {
    private static AssetManager assetManager;
    private static BulletAppState bulletAppState;
    private static Camera camera;
    private static ClientGameState clientGameState;
    private static ClientMenuState clientMenuState;
    private static Node collisionNode;
    private static AppStateManager stateManager;
    private static String version;
    private static Timer timer;
    
    public static AssetManager getAssetManager(){
        return assetManager;
    }
    public static BulletAppState getBulletAppState(){
        return bulletAppState;
    }
    public static Camera getCamera(){
        return camera;
    }
    public static ClientGameState getClientGameState(){
        return clientGameState;
    }
    public static ClientMenuState getClientMenuState(){
        return clientMenuState;
    }
    public static Node getCollisionNode(){
        return collisionNode;
    }
    public static AppStateManager getStateManager(){
        return stateManager;
    }
    public static String getVersion(){
        return version;
    }
    public static Timer getTimer(){
        return timer;
    }
    
    public static void setAssetManager(AssetManager assetManager){
        S.assetManager = assetManager;
    }
    public static void setBulletAppState(BulletAppState bulletAppState){
        S.bulletAppState = bulletAppState;
    }
    public static void setCamera(Camera camera){
        S.camera = camera;
    }
    public static void setClientGameState(ClientGameState clientGameState){
        S.clientGameState = clientGameState;
    }
    public static void setClientMenuState(ClientMenuState clientMenuState){
        S.clientMenuState = clientMenuState;
    }
    public static void setCollisionNode(Node collisionNode){
        S.collisionNode = collisionNode;
    }
    public static void setStateManager(AppStateManager stateManager){
        S.stateManager = stateManager;
    }
    public static void setVersion(String version){
        S.version = version;
    }
    public static void setTimer(Timer timer){
        S.timer = timer;
    }
}
