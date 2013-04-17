package sin.inventory;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.inventory.Items.Item;
import sin.tools.T;
import sin.tools.T.Vector2i;

/**
 *
 * @author SinisteRing
 */
public class Inventory {
    private Node node = new Node("InventoryNode");
    private int maxSize;
    private Vector2i gridSize;
    private Grid grid;
    private ArrayList<Item> items;
    
    public Inventory(int maxSize, Vector2i gridSize, Vector3f trans){
        this.maxSize = maxSize;
        this.gridSize = gridSize;
        node.setLocalTranslation(trans);
        items = new ArrayList(maxSize);
        grid = new Grid(node, Items.SLOT_SIZE, gridSize);
        int x, y;
        int i = 0;
        while(i < maxSize){
            x = i%gridSize.x;
            y = i/gridSize.x;
            items.add(new Item());
            items.get(i).update(node, x+","+y, trans);
            i++;
        }
    }
    
    private int getIndex(int x, int y){
        return (y*gridSize.x)+x;
    }
    
    public boolean isInventoryItem(Geometry geo){
        int i = 0;
        while(i < items.size()){
            if(items.get(i).getGeometry() != null && items.get(i).getGeometry().equals(geo)){
                return true;
            }
            i++;
        }
        return false;
    }
    public boolean isInventorySlot(Geometry geo){
        if(T.getHeader(geo.getName()).equals("slot")){
            return true;
        }
        return false;
    }
    public void setItem(int x, int y, Item item){
        items.set(getIndex(x, y), item);
    }
    public Item getItem(int x, int y){
        return items.get(getIndex(x, y));
    }
    public Node getNode(){
        return node;
    }
    
    public void updateItem(int x, int y){
        int i = getIndex(x, y);
        items.get(i).update(node, x+","+y, grid.getInsertPosition(x, y));
    }
    
    public void addItem(Item item){
        int x, y;
        int i = 0;
        while(i < maxSize){
            x = i%gridSize.x;
            y = i/gridSize.x;
            if(items.get(i) == null || items.get(i).isEmpty()){
                items.set(i, item);
                items.get(i).generateTile(node, x+","+y, grid.getInsertPosition(x, y));
                return;
            }
            i++;
        }
    }
}
