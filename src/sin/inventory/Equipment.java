package sin.inventory;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.inventory.Items.Chestplate;
import sin.inventory.Items.Greaves;
import sin.inventory.Items.Helmet;
import sin.inventory.Items.Item;
import sin.inventory.Items.ItemSlot;
import sin.tools.T;

/**
 *
 * @author SinisteRing
 */
public class Equipment {
    private Node node = new Node("EquipmentNode");
    
    private ArrayList<ItemSlot> slots = new ArrayList(1);
    private ArrayList<Class> itemClasses = new ArrayList(1);
    private ArrayList<Item> items = new ArrayList(1);
    
    public Equipment(Vector3f trans){
        node.setLocalTranslation(trans);
        itemClasses.add(Helmet.class);
        slots.add(new ItemSlot(node, Items.SLOT_SIZE, 0, new Vector3f(0, 1.7f, -0.002f), new ColorRGBA(1, 0.4f, 0, 1)));
        itemClasses.add(Chestplate.class);
        slots.add(new ItemSlot(node, Items.SLOT_SIZE, 1, new Vector3f(0, 0f, -0.002f), new ColorRGBA(1, 0.5f, 0, 1)));
        itemClasses.add(Greaves.class);
        slots.add(new ItemSlot(node, Items.SLOT_SIZE, 2, new Vector3f(0, -1.5f, -0.002f), new ColorRGBA(1, 0.6f, 0, 1)));
        int i = 0;
        while(i < itemClasses.size()){
            items.add(new Item());
            i++;
        }
    }
    
    public boolean isEquipmentItem(Geometry geo){
        int i = 0;
        while(i < items.size()){
            if(items.get(i).getGeometry() != null && items.get(i).getGeometry().equals(geo)){
                return true;
            }
            i++;
        }
        return false;
    }
    public boolean isEquipmentSlot(Geometry geo){
        if(T.getHeader(geo.getName()).equals("equipslot")){
            return true;
        }
        return false;
    }
    public boolean canEquip(int index, Item item){
        if(itemClasses.get(index).isInstance(item)){
            return true;
        }
        return false;
    }
    public void setItem(int index, Item item){
        items.set(index, item);
        updateItem(index);
    }
    public Item getItem(int index){
        return items.get(index);
    }
    public Node getNode(){
        return node;
    }
    
    public void updateItem(int index){
        items.get(index).update(node, Integer.toString(index), slots.get(index).getLocalTranslation().add(0, 0, 0.001f));
    }
}
