package sin.inventory;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.progression.Stat;
import sin.tools.T;
import sin.tools.T.Vector2i;

/**
 *
 * @author SinisteRing
 */
public class InventoryScreen {
    private static Node node = new Node("InventoryScreenNode");
    private static Inventory inventory = new Inventory(new Vector2i(5, 5), new Vector3f(-3, 2, 0));
    
    public static Node getNode(){
        return node;
    }
    
    private static void inventoryAction(String name){
        ArrayList<String> args = T.getArgs(name);
        int x = Integer.parseInt(args.get(0));
        int y = Integer.parseInt(args.get(1));
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
    
    public static void initialize(){
        node.attachChild(inventory.getNode());
        ArrayList<Stat> stats = new ArrayList(1);
        stats.add(new Stat("damage[5]"));
        int i = 0;
        while(i < 8){
            inventory.addItem(new Item(null, "meow", stats, "meow2", T.getNeuroPath("core")));
            i++;
        }
    }
}
