package sin.inventory;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
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
    private ArrayList<Geometry> slots;
    private ArrayList<Item> items;
    
    public Inventory(int maxSize, Vector2i gridSize, Vector3f trans){
        this.maxSize = maxSize;
        this.gridSize = gridSize;
        node.setLocalTranslation(trans);
        items = new ArrayList(maxSize);
        slots = new ArrayList(gridSize.x*gridSize.y);
        int x, y;
        int i = 0;
        while(i < gridSize.x*gridSize.y){
            x = i%gridSize.x;
            y = i/(gridSize.y+1);
            slots.add(CG.createBox(node, "slot("+x+","+y+")", new Vector3f(Item.SLOT_SIZE, Item.SLOT_SIZE, 0),
                    new Vector3f(x, -y, -0.01f), new ColorRGBA(0, 0, 0.5f, 1)));
            i++;
        }
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
    public Item getItem(int x, int y){
        return items.get((y*gridSize.x)+x);
    }
    public Node getNode(){
        return node;
    }
    
    public void addItem(Item item){
        int x, y;
        int i = 0;
        while(i < maxSize){
            x = i%gridSize.x;
            y = i/(gridSize.y+1);
            if(items.size() <= i){
                items.add(new Item(new Vector2i(x, y)));
            }
            if(items.get(i).isEmpty()){
                items.set(i, item);
                items.get(i).setLocation(new Vector2i(x, y));
                items.get(i).generateTile(node, new Vector3f(x, -y, 0.01f));
                return;
            }
            i++;
        }
    }
}
