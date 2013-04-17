package sin.inventory;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.inventory.Items.Chestplate;
import sin.inventory.Items.Greaves;
import sin.inventory.Items.Helmet;
import sin.inventory.Items.Item;
import sin.progression.Stat;
import sin.tools.A;
import sin.tools.S;
import sin.tools.T;
import sin.tools.T.Vector2i;

/**
 *
 * @author SinisteRing
 */
public class InventoryScreen {
    private static Node node = new Node("InventoryScreenNode");
    private static Inventory inventory = new Inventory(200, new Vector2i(8, 7), new Vector3f(2, 0, 0));
    private static Equipment equipment = new Equipment(new Vector3f(-3, 0, 0));
    private static Item dragItem = null;
    private static boolean dragging = false;
    
    public static Node getNode(){
        return node;
    }
    
    public static void action(CollisionResult target){
        if(target == null){
            return;
        }
        Geometry tarGeo = target.getGeometry();
        ArrayList<String> args = T.getArgs(tarGeo.getName());
        if(inventory.isInventoryItem(tarGeo)){
            int x = Integer.parseInt(args.get(0));
            int y = Integer.parseInt(args.get(1));
            dragItem = inventory.getItem(x, y);
        }else if(equipment.isEquipmentItem(tarGeo)){
            int index = Integer.parseInt(args.get(0));
            dragItem = equipment.getItem(index);
        }
        dragging = true;
    }
    public static void unaction(){
        Vector2f mouseLoc = S.getInputManager().getCursorPosition();
        ArrayList<CollisionResult> results = A.getMouseTargets(mouseLoc, S.getCamera(), node);
        Geometry rGeo;
        Geometry slot = null;
        int i = 0;
        while(i < results.size()){
            rGeo = results.get(i).getGeometry();
            if(equipment.isEquipmentSlot(rGeo) || inventory.isInventorySlot(rGeo)){
                slot = results.get(i).getGeometry();
            }
            i++;
        }
        if(dragItem != null){
            ArrayList<String> data = T.getArgs(dragItem.getGeometry().getName());
            if(inventory.isInventoryItem(dragItem.getGeometry())){
                int ix = Integer.parseInt(data.get(0));
                int iy = Integer.parseInt(data.get(1));
                Item invItem = inventory.getItem(ix, iy);
                if(slot != null){
                    if(inventory.isInventorySlot(slot)){
                        data = T.getArgs(slot.getName());
                        int sx = Integer.parseInt(data.get(0));
                        int sy = Integer.parseInt(data.get(1));
                        Item swapItem = inventory.getItem(sx, sy);
                        inventory.setItem(sx, sy, invItem);
                        inventory.setItem(ix, iy, swapItem);
                        inventory.updateItem(sx, sy);
                    }else if(equipment.isEquipmentSlot(slot)){
                        data = T.getArgs(slot.getName());
                        int index = Integer.parseInt(data.get(0));
                        if(equipment.canEquip(index, invItem)){
                            Item equipItem = equipment.getItem(index);
                            inventory.setItem(ix, iy, equipItem);
                            equipment.setItem(index, invItem);
                        }
                    }
                }
                inventory.updateItem(ix, iy);
            }else if(equipment.isEquipmentItem(dragItem.getGeometry())){
                int index = Integer.parseInt(data.get(0));
                Item equipItem = equipment.getItem(index);
                if(slot != null){
                    if(inventory.isInventorySlot(slot)){
                        data = T.getArgs(slot.getName());
                        int sx = Integer.parseInt(data.get(0));
                        int sy = Integer.parseInt(data.get(1));
                        Item invItem = inventory.getItem(sx, sy);
                        if(equipment.canEquip(index, invItem) || invItem.isEmpty()){
                            inventory.setItem(sx, sy, equipItem);
                            equipment.setItem(index, invItem);
                            inventory.updateItem(sx, sy);
                        }
                    }else if(equipment.isEquipmentSlot(slot)){
                        data = T.getArgs(slot.getName());
                        int sindex = Integer.parseInt(data.get(0));
                        Item swapItem = equipment.getItem(sindex);
                        if(equipment.canEquip(index, swapItem)){
                            equipment.setItem(sindex, equipItem);
                            equipment.setItem(index, swapItem);
                            equipment.updateItem(sindex);
                        }
                    }
                }
                equipment.updateItem(index);
            }
            dragItem = null;
            dragging = false;
        }
    }
    
    public static void update(Vector2f mouseLoc){
        if(dragging && dragItem != null){
            Vector3f worldCoords = S.getCamera().getWorldCoordinates(mouseLoc, 0).mult(new Vector3f(10, 10, 0));
            worldCoords.subtractLocal(dragItem.getGeometry().getParent().getLocalTranslation().clone());
            dragItem.getGeometry().setLocalTranslation(worldCoords);
        }
    }
    
    public static void initialize(){
        node.attachChild(inventory.getNode());
        node.attachChild(equipment.getNode());
        ArrayList<Stat> stats = new ArrayList(1);
        stats.add(new Stat("damage[5]"));
        // Testing Items:
        inventory.addItem(new Helmet("helmet", (ArrayList<Stat>) stats.clone(), "helmet!", T.getItemPath("helmet")));
        inventory.addItem(new Chestplate("chestplate", (ArrayList<Stat>) stats.clone(), "chestplate!", T.getItemPath("chestplate")));
        inventory.addItem(new Chestplate("chainmail", (ArrayList<Stat>) stats.clone(), "chainmail!", T.getItemPath("chainmail")));
        inventory.addItem(new Greaves("greaves", (ArrayList<Stat>) stats.clone(), "greaves!", T.getItemPath("greaves")));
        int i = 0;
        while(i < 4){
            inventory.addItem(new Item("meow", (ArrayList<Stat>) stats.clone(), "meow2", T.getNeuroPath("core")));
            i++;
        }
    }
}
