package sin.character;

import com.jme3.collision.CollisionResult;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import sin.animation.Models;
import sin.inventory.InventoryScreen;
import sin.progression.NeuroNetworkScreen;
import sin.tools.A;
import sin.tools.S;
import sin.tools.T;
import sin.world.CG;

/**
 * CharacterScreen - Handles the Character Screen and all data associated with it.
 * @author SinisteRing
 */
public class CharacterScreen {
    private static final float split = 2f/5f;
    private static boolean rotating = false;
    private static String view;
    private static Node node = new Node("CharacterScreenNode");
    private static Node gui = new Node("CharacterScreenGUI");
    
    // Char Side:
    private static Camera leftCamera;
    private static ViewPort leftView;
    private static Node leftNode;
    
    // Menu Side:
    private static Camera rightCamera;
    private static ViewPort rightView;
    private static Node rightNode;
    
    public static Node getNode(){
        return node;
    }
    public static Node getGUI(){
        return gui;
    }
    
    private static Vector3f getWorldDir(Vector3f loc, Vector2f mouseLoc, Camera cam){
        return cam.getWorldCoordinates(mouseLoc, 1f).subtract(loc).normalize();
    }
    private static Vector3f getWorldLoc(Vector2f mouseLoc, Camera cam){
        return cam.getWorldCoordinates(mouseLoc, 0f).clone();
    }
    private static void hideView(ViewPort view, Node scene){
        S.getRenderManager().removeMainView(view);
        node.detachChild(scene);
    }
    
    private static void setRotating(boolean rot){
        T.log("rotation");
        rotating = rot;
    }
    
    public static CollisionResult getMouseTarget(Vector2f mouseLoc, Camera cam, Node node){
        Vector3f loc = getWorldLoc(mouseLoc, cam);
        Vector3f dir = getWorldDir(loc, mouseLoc, cam);
        return A.getClosestCollision(node, new Ray(loc, dir));
    }
    private static void menuAction(CollisionResult target){
        if(target == null){
            return;
        }
        String action = target.getGeometry().getName();
        if(action.equals("inventory")){
            hideView(rightView, rightNode);
            hideView(leftView, leftNode);
            view = "inventory";
            InventoryScreen.initialize();
            node.attachChild(InventoryScreen.getNode());
            S.getViewPort().setBackgroundColor(ColorRGBA.DarkGray);
        }else if(action.equals("neuronet ")){ //BROKE IT TO PREVENT BEING STUPID
            hideView(rightView, rightNode);
            hideView(leftView, leftNode);
            view = "neuronet";
            NeuroNetworkScreen.initialize();
            node.attachChild(NeuroNetworkScreen.getNode());
            S.getViewPort().setBackgroundColor(ColorRGBA.DarkGray);
        }
    }
    public static void handleRightClick(){
        Vector2f mouseLoc = S.getInputManager().getCursorPosition();
        if(view.equals("neuronet")){
            NeuroNetworkScreen.handleRightClick(mouseLoc);
        }
    }
    public static void handleClick(){
        Vector2f mouseLoc = S.getInputManager().getCursorPosition();
        if(view.equals("main")){
            if(mouseLoc.x >= S.width*split){
                menuAction(getMouseTarget(mouseLoc, rightCamera, rightNode));
            }else{
                setRotating(true);
            }
        }else if(view.equals("inventory")){
            InventoryScreen.action(getMouseTarget(mouseLoc, S.getCamera(), node));
        }else if(view.equals("neuronet")){
            NeuroNetworkScreen.action(getMouseTarget(mouseLoc, S.getCamera(), node));
        }
    }
    
    public static void update(){
        Vector2f mouseLoc = S.getInputManager().getCursorPosition();
        if(view.equals("neuronet")){
            NeuroNetworkScreen.update(mouseLoc);
        }
    }
    
    private static void buildChar(){
        Models.PlayerModel model = new Models.PlayerModel(0, leftNode);
        model.getNode().rotate(0, 30*FastMath.DEG_TO_RAD, 0);
    }
    private static void buildMenu(){
        rightNode.detachAllChildren();
        CG.createBox(rightNode, "inventory", new Vector3f(3.5f, 0.4f, 0f), new Vector3f(0, 3, 0), T.getGraphicPath("loadout"), new Vector2f(1, 1));
        CG.createBox(rightNode, "abilities", new Vector3f(3.5f, 0.4f, 0f), new Vector3f(0, 1, 0), T.getMaterialPath("lava_rock"), new Vector2f(1, 1));
        CG.createBox(rightNode, "neuronet", new Vector3f(3.5f, 0.4f, 0f), new Vector3f(0, -1, 0), T.getGraphicPath("neuronet"), new Vector2f(1, 1));
        CG.createBox(rightNode, "proficiencies", new Vector3f(3.5f, 0.4f, 0f), new Vector3f(0, -3, 0), T.getMaterialPath("BC_Tex"), new Vector2f(1, 1));
    }
    
    public static void initialize(){
        // Initialize Char View:
        leftCamera = S.getCamera().clone();
        leftCamera.setFrustumPerspective(60, 1-split, 1, 40);
        leftCamera.setViewPort(0f, split, 0f, 1f);
        leftCamera.setLocation(new Vector3f(0, 0, 10));
        leftView = S.getRenderManager().createMainView("Char", leftCamera);
        leftView.setClearFlags(true, true, true);
        leftView.setBackgroundColor(ColorRGBA.DarkGray);
        leftNode = new Node("Char_Node");
        node.attachChild(leftNode);
        leftView.attachScene(leftNode);
        leftCamera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        // Initialize Menu View:
        rightCamera = S.getCamera().clone();
        rightCamera.setFrustumPerspective(60, 1, 1, 40);
        rightCamera.setViewPort(split, 1f, 0f, 1f);
        rightCamera.setLocation(new Vector3f(0, 0, 10));
        rightView = S.getRenderManager().createMainView("Menu", rightCamera);
        rightView.setClearFlags(true, true, true);
        rightView.setBackgroundColor(ColorRGBA.Black);
        rightNode = new Node("Menu_Node");
        node.attachChild(rightNode);
        rightView.attachScene(rightNode);
        rightCamera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        buildChar();
        buildMenu();
        
        view = "main";
    }
}
