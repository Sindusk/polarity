package sin.inventory;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.progression.Stat;
import sin.tools.S;
import sin.tools.T;
import sin.tools.T.Vector2i;

/**
 *
 * @author SinisteRing
 */
public class InventoryScreen {
    private static Node node = new Node("InventoryScreenNode");
    private static Inventory inventory = new Inventory(100, new Vector2i(7, 6), new Vector3f(-3, 2, 0));
    private static Geometry dragTarget = null;
    private static boolean dragging = false;
    
    public static Node getNode(){
        return node;
    }
    
    private static void beginDrag(int x, int y){
        dragTarget = inventory.getItem(x, y).getGeometry();
        dragging = true;
    }
    private static void inventoryAction(String name){
        ArrayList<String> args = T.getArgs(name);
        int x = Integer.parseInt(args.get(0));
        int y = Integer.parseInt(args.get(1));
        T.log(x+","+y);
        beginDrag(x, y);
    }
    public static void action(CollisionResult target){
        if(target == null){
            return;
        }
        String name = target.getGeometry().getName();
        if(inventory.isItem(T.getHeader(name))){
            inventoryAction(name);
        }
    }
    public static void unaction(){
        Vector2f mouseLoc = S.getInputManager().getCursorPosition();
        T.log(mouseLoc.toString());
        dragging = false;
    }
    
    public static void update(Vector2f mouseLoc){
        if(dragging){
            Vector3f worldCoords = S.getCamera().getWorldCoordinates(mouseLoc, 0).mult(new Vector3f(10, 10, 0));
            worldCoords.subtractLocal(inventory.getNode().getLocalTranslation().clone());
            dragTarget.setLocalTranslation(worldCoords);
        }
    }
    
    public static void initialize(){
        node.attachChild(inventory.getNode());
        ArrayList<Stat> stats = new ArrayList(1);
        stats.add(new Stat("damage[5]"));
        int i = 0;
        while(i < 19){
            inventory.addItem(new Item(null, "meow", (ArrayList<Stat>) stats.clone(), "meow2", T.getNeuroPath("core")));
            i++;
        }
    }
}
