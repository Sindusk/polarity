package sin.inventory;

import com.jme3.math.ColorRGBA;
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
public class Items {
    public static final float SLOT_SIZE = 0.46f;
    public static final float SLOT_BUFFER = 0.02f;
    public static final float ITEM_SIZE = 0.39f;
    
    public static class ItemSlot{
        private Geometry geo;
        
        public ItemSlot(Node node, float scale, Vector2i loc, Vector3f trans, ColorRGBA color){
            geo = CG.createBox(node, "slot("+loc.x+","+loc.y+")", new Vector3f(scale, scale, 0), trans, color);
            node.attachChild(geo);
        }
        public ItemSlot(Node node, float scale, int index, Vector3f trans, ColorRGBA color){
            geo = CG.createBox(node, "equipslot("+index+")", new Vector3f(scale, scale, 0), trans, color);
            node.attachChild(geo);
        }
        
        public Vector3f getLocalTranslation(){
            return geo.getLocalTranslation();
        }
        
        public void destroy(){
            geo.removeFromParent();
        }
    }
    public static class Item{
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

        public void update(Node node, Vector3f trans){
            if(!isEmpty()){
                geo.removeFromParent();
                generateTile(node, trans);
            }
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
        public Vector2i getLocation(){
            return loc;
        }
        public String getName(){
            return name;
        }

        public Geometry generateTile(Node node, Vector3f trans){
            geo =  CG.createBox(node, name+"("+loc.x+","+loc.y+")", new Vector3f(ITEM_SIZE, ITEM_SIZE, 0),
                    trans, image, new Vector2f(1, 1));
            return geo;
        }
    }
    public static class Equipment extends Item{
        public Equipment(Vector2i loc, String name, ArrayList<Stat> stats, String description, String image){
            super(loc, name, stats, description, image);
        }
    }
    
    // Equipment Classes:
    public static class Helmet extends Equipment{
        private float headshotMult;
        
        public Helmet(Vector2i loc, String name, ArrayList<Stat> stats, String description, String image){
            super(loc, name, stats, description, image);
        }
    }
    public static class Chestplate extends Equipment{
        private float critMult;
        
        public Chestplate(Vector2i loc, String name, ArrayList<Stat> stats, String description, String image){
            super(loc, name, stats, description, image);
        }
    }
    public static class Greaves extends Equipment{
        private float jumpMult;
        
        public Greaves(Vector2i loc, String name, ArrayList<Stat> stats, String description, String image){
            super(loc, name, stats, description, image);
        }
    }
}
