package sin.inventory;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.progression.Stat;
import sin.tools.T;
import sin.tools.T.Vector2i;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class Item {
    public static final float SLOT_SIZE = 0.48f;
    public static final float ITEM_SIZE = 0.45f;
    
    private Geometry geo;
    private Vector2i loc;
    private String name;
    private ArrayList<Stat> stats;
    private String description;
    private String image;
    
    public Item(Vector2i loc){
        this.name = "empty";
        this.loc = loc;
        this.image = T.getGraphicPath("default");
    }
    public Item(Vector2i loc, String name, ArrayList<Stat> stats, String description, String image){
        this.loc = loc;
        this.name = name;
        this.stats = stats;
        this.description = description;
        this.image = image;
    }
    
    public boolean isEmpty(){
        return name.equals("empty");
    }
    public void setLocation(Vector2i loc){
        this.loc = loc;
    }
    public Geometry getGeometry(){
        return geo;
    }
    public String getName(){
        return name;
    }
    
    public Geometry generateTile(Node node, Vector3f trans){
        geo =  CG.createBox(node, name+"("+loc.x+","+loc.y+")", new Vector3f(ITEM_SIZE, ITEM_SIZE, 0), trans, image, new Vector2f(1, 1));
        return geo;
    }
}
