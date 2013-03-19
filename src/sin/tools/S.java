package sin.tools;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.Timer;

/**
 *
 * @author SinisteRing
 */
public class S {
    public static final int GROUP_TERRAIN = 0;
    public static final int GROUP_PLAYER = 1;
    public static final int GROUP_ENTITY = 2;
    
    public static int height = 0;
    public static int width = 0;
    
    // Nodes:
    private static Node rootNode;
    public static Node getRootNode(){
        return rootNode;
    }
    public static void setRootNode(Node rootNode){
        S.rootNode = rootNode;
    }
    
    private static Node collisionNode;
    public static Node getCollisionNode(){
        return collisionNode;
    }
    public static void setCollisionNode(Node collisionNode){
        S.collisionNode = collisionNode;
    }
    
    // Managers:
    private static AssetManager assetManager;
    public static AssetManager getAssetManager(){
        return assetManager;
    }
    public static void setAssetManager(AssetManager assetManager){
        S.assetManager = assetManager;
    }
    
    private static InputManager inputManager;
    public static InputManager getInputManager(){
        return inputManager;
    }
    public static void setInputManager(InputManager inputManager){
        S.inputManager = inputManager;
    }
    
    private static RenderManager renderManager;
    public static RenderManager getRenderManager(){
        return renderManager;
    }
    public static void setRenderManager(RenderManager renderManager){
        S.renderManager = renderManager;
    }
    
    private static AppStateManager stateManager;
    public static AppStateManager getStateManager(){
        return stateManager;
    }
    public static void setStateManager(AppStateManager stateManager){
        S.stateManager = stateManager;
    }
    
    // Other:
    private static BulletAppState bulletAppState;
    public static BulletAppState getBulletAppState(){
        return bulletAppState;
    }
    public static void setBulletAppState(BulletAppState bulletAppState){
        S.bulletAppState = bulletAppState;
    }
    private static Camera camera;
    public static Camera getCamera(){
        return camera;
    }
    public static void setCamera(Camera camera){
        S.camera = camera;
    }
    
    private static String version;
    public static String getVersion(){
        return version;
    }
    public static void setVersion(String version){
        S.version = version;
    }
    
    private static Timer timer;
    public static Timer getTimer(){
        return timer;
    }
    public static void setTimer(Timer timer){
        S.timer = timer;
    }
}
