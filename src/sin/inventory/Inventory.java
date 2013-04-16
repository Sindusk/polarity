package sin.inventory;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.inventory.Items.Item;
import sin.tools.T;
import sin.tools.T.Vector2i;
import sin.world.CG;

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
        i = 0;
        while(i < maxSize){
            x = i%gridSize.x;
            y = i/(gridSize.y+1);
            items.add(new Item(new Vector2i(x, y)));
            i++;
        }
    }
    
    private int getIndex(int x, int y){
        return (y*gridSize.x)+x;
    }
    
    public boolean isItem(String name){
        int i = 0;
        while(i < items.size()){
            if(items.get(i) != null && items.get(i).getName().equals(name)){
                return true;
            }
            i++;
        }
        return false;
    }
    public boolean isSlot(Geometry geo){
        if(T.getHeader(geo.getName()).equals("slot")){
            return true;
        }
        return false;
    }
    public Item getItem(int x, int y){
        return items.get((y*gridSize.x)+x);
    }
    public Node getNode(){
        return node;
    }
    
    public void updateItem(int x, int y){
        int i = getIndex(x, y);
        items.get(i).update(node, grid.getInsertPosition(x, y));
    }
    
    public void addItem(Item item){
        int x, y;
        int i = 0;
        while(i < maxSize){
            x = i%gridSize.x;
            y = i/(gridSize.y+1);
            if(items.get(i) == null || items.get(i).isEmpty()){
                items.set(i, item);
                items.get(i).setLocation(new Vector2i(x, y));
                items.get(i).generateTile(node, grid.getInsertPosition(x, y));
                return;
            }
            i++;
        }
    }
    public void swapItem(Vector2i from, Vector2i to){
        int fromLoc = (from.y*gridSize.x)+from.x;
        int toLoc = (to.y*gridSize.x)+to.x;
        Item temp = items.set(fromLoc, items.get(toLoc));
        Vector2i tempLoc = items.get(fromLoc).getLocation().clone();
        items.get(fromLoc).setLocation(temp.getLocation().clone());
        temp.setLocation(tempLoc);
        items.set(toLoc, temp);
    }
}
