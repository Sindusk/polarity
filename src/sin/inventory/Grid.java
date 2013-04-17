package sin.inventory;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.inventory.Items.ItemSlot;
import sin.tools.T;
import sin.tools.T.Vector2i;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class Grid {
    private float scale;
    private Vector2i size;
    private ArrayList<ItemSlot> slots;
    
    public Grid(Node node, float scale, Vector2i size){
        this.scale = scale;
        this.size = size;
        slots = new ArrayList(1);
        int x, y;
        int i = 0;
        while(i < size.x*size.y){
            x = i%size.x;
            y = i/size.x;
            slots.add(new ItemSlot(node, scale, new Vector2i(x, y), new Vector3f(((-size.x/2f)+x)+0.5f, ((size.y/2f)-y)-0.5f, -0.002f), new ColorRGBA(0, 0, 0.5f, 1)));
            i++;
        }
    }
    
    public int getIndex(int x, int y){
        return (y*size.x)+x;
    }
    public Vector3f getInsertPosition(int x, int y){
        int i = getIndex(x, y);
        return slots.get(i).getLocalTranslation().add(0, 0, 0.001f);
    }
}
