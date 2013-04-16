package sin.inventory;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.inventory.Items.Chestplate;
import sin.inventory.Items.Greaves;
import sin.inventory.Items.Helmet;
import sin.inventory.Items.ItemSlot;
import sin.tools.T;

/**
 *
 * @author SinisteRing
 */
public class Equipment {
    private Node node = new Node("EquipmentNode");
    
    private ArrayList<ItemSlot> slots = new ArrayList(1);
    private Helmet helmet;
    private Chestplate chestplate;
    private Greaves greaves;
    
    public Equipment(Vector3f trans){
        node.setLocalTranslation(trans);
        slots.add(new ItemSlot(node, Items.SLOT_SIZE, 0, new Vector3f(0, 1.7f, -0.002f), ColorRGBA.Orange));
        slots.add(new ItemSlot(node, Items.SLOT_SIZE, 1, new Vector3f(0, 0f, -0.002f), ColorRGBA.Red));
        slots.add(new ItemSlot(node, Items.SLOT_SIZE, 2, new Vector3f(0, -1.5f, -0.002f), ColorRGBA.Magenta));
    }
    
    public boolean isSlot(Geometry geo){
        if(T.getHeader(geo.getName()).equals("equipslot")){
            return true;
        }
        return false;
    }
    public Node getNode(){
        return node;
    }
}
