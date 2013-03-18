package sin.character;

import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.animation.Models;
import sin.tools.S;
import sin.tools.T;
import sin.world.CG;

/**
 * CharacterScreen - Handles the Character Screen and all data associated with it.
 * @author SinisteRing
 */
public class CharacterScreen {
    private static GameClient app;
    
    // Char Side:
    private static Camera charCamera;
    private static ViewPort charView;
    private static Node charNode;
    
    // Menu Side:
    private static Camera menuCamera;
    private static ViewPort menuView;
    private static Node menuNode;
    
    public static void handleClick(){
        Vector2f loc = S.getInputManager().getCursorPosition();
        Camera c;
        Node n;
        if(loc.x >= S.width*0.5f){
            c = menuCamera;
            n = menuNode;
        }else{
            c = charCamera;
            n = charNode;
        }
        T.log("cursor at "+loc.x+", "+loc.y);
        Vector3f click3d = c.getWorldCoordinates(new Vector2f(loc.x, loc.y), 0f).clone();
        Vector3f dir = c.getWorldCoordinates(new Vector2f(loc.x, loc.y), 1f).subtractLocal(click3d).normalizeLocal();
        Ray ray = new Ray(click3d, dir);
        CollisionResults results = new CollisionResults();
        n.collideWith(ray, results);
        if(results.getClosestCollision() != null){
            T.log("collision with "+results.getClosestCollision().getGeometry().getName());
        }
    }
    
    private static void buildMenu(){
        CG.createBox(menuNode, "weapons", new Vector3f(3.5f, 0.4f, 0.01f), new Vector3f(0, 4, 0), T.getMaterialPath("wall"), new Vector2f(1, 1));
        CG.createBox(menuNode, "equipment", new Vector3f(3.5f, 0.4f, 0.01f), new Vector3f(0, 2, 0), T.getMaterialPath("brick"), new Vector2f(1, 1));
        CG.createBox(menuNode, "abilities", new Vector3f(3.5f, 0.4f, 0.01f), new Vector3f(0, 0, 0), T.getMaterialPath("wall"), new Vector2f(1, 1));
        CG.createBox(menuNode, "neuronet", new Vector3f(3.5f, 0.4f, 0.01f), new Vector3f(0, -2, 0), T.getMaterialPath("brick"), new Vector2f(1, 1));
        CG.createBox(menuNode, "proficiencies", new Vector3f(3.5f, 0.4f, 0.01f), new Vector3f(0, -4, 0), T.getMaterialPath("wall"), new Vector2f(1, 1));
    }
    public static void initialize(GameClient app, Node root){
        CharacterScreen.app = app;
        
        // Initialize:
        charCamera = app.getCamera().clone();
        charCamera.setFrustumPerspective(60, 1, 1, 40);
        charCamera.setViewPort(0f, 0.5f, 0f, 1f);
        charCamera.setLocation(new Vector3f(0, 0, -10));
        charView = app.getRenderManager().createMainView("Char", charCamera);
        charView.setClearFlags(true, true, true);
        charView.setBackgroundColor(ColorRGBA.DarkGray);
        charNode = new Node("Char_Node");
        root.attachChild(charNode);
        charView.attachScene(charNode);
        charCamera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        menuCamera = app.getCamera().clone();
        menuCamera.setFrustumPerspective(60, 1, 1, 40);
        menuCamera.setViewPort(0.5f, 1f, 0f, 1f);
        menuCamera.setLocation(new Vector3f(0, 0, -10));
        menuView = app.getRenderManager().createMainView("Menu", menuCamera);
        menuView.setClearFlags(true, true, true);
        menuView.setBackgroundColor(ColorRGBA.Black);
        menuNode = new Node("Menu_Node");
        root.attachChild(menuNode);
        menuView.attachScene(menuNode);
        menuCamera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        buildMenu();
        
        Models.PlayerModel model = new Models.PlayerModel(0, charNode);
        model.getNode().rotate(0, 180*FastMath.DEG_TO_RAD, 0);
    }
}
