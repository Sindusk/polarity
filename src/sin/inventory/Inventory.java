package sin.inventory;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import sin.tools.T.Vector2i;

/**
 *
 * @author SinisteRing
 */
public class Inventory {
    private Node node = new Node("InventoryNode");
    private Vector2i size;
    private Item[][] items;
    
    public Inventory(Vector2i size, Vector3f trans){
        this.size = size;
        node.setLocalTranslation(trans);
        items = new Item[size.x][size.y];
    }
    
    public boolean isItem(String name){
        int x = 0;
        int y;
        while(x < items.length){
            y = 0;
            while(y < items[x].length){
                if(items[x][y] != null && items[x][y].getName().equals(name)){
                    return true;
                }
                y++;
            }
            x++;
        }
        return false;
    }
    public Node getNode(){
        return node;
    }
    
    public void addItem(Item item){
        int x = 0;
        int y;
        while(x < items.length){
            y = 0;
            while(y < items[x].length){
                if(items[x][y] == null){
                    items[x][y] = item;
                    items[x][y].setLocation(new Vector2i(x, y));
                    items[x][y].generateTile(node, new Vector3f(x, -y, 0));
                    return;
                }
                y++;
            }
            x++;
        }
    }
}
