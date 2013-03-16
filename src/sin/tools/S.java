package sin.tools;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
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
    public static final int GROUP_TERRAIN = 0;
    public static final int GROUP_PLAYER = 1;
    public static final int GROUP_ENTITY = 2;
    
    private static AssetManager assetManager;
    private static BulletAppState bulletAppState;
    private static Camera camera;
    private static InputManager inputManager;
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
    public static InputManager getInputManager(){
        return inputManager;
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
    public static void setInputManager(InputManager inputManager){
        S.inputManager = inputManager;
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
