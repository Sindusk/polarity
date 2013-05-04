package sin.inventory;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.neuronet.Stat;
import sin.tools.T;
import sin.tools.T.Vector2i;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class Items {
    public static final float SLOT_SIZE = 0.48f;
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
        private String name;
        private ArrayList<Stat> stats;
        private String description;
        private String image;

        public Item(){
            this.name = "empty";
            this.image = T.getGraphicPath("default");
        }
        public Item(String name, ArrayList<Stat> stats, String description, String image){
            this.name = name;
            this.stats = stats;
            this.description = description;
            this.image = image;
        }
        
        public boolean isEmpty(){
            return name.equals("empty");
        }
        public Geometry getGeometry(){
            return geo;
        }
        public String getName(){
            return name;
        }
        
        public Geometry generateTile(Node node, String data, Vector3f trans){
            geo =  CG.createBox(node, name+"("+data+")", new Vector3f(ITEM_SIZE, ITEM_SIZE, 0),
                    trans, image, new Vector2f(1, 1));
            return geo;
        }
        public void update(Node node, String data, Vector3f trans){
            if(geo != null){
                geo.removeFromParent();
            }
            if(!isEmpty()){
                generateTile(node, data, trans);
            }
        }
    }
    public static class Equipment extends Item{
        public Equipment(String name, ArrayList<Stat> stats, String description, String image){
            super(name, stats, description, image);
        }
    }
    
    // Equipment Classes:
    public static class Helmet extends Equipment{
        private float headshotMult;
        
        public Helmet(String name, ArrayList<Stat> stats, String description, String image){
            super(name, stats, description, image);
        }
    }
    public static class Chestplate extends Equipment{
        private float critMult;
        
        public Chestplate(String name, ArrayList<Stat> stats, String description, String image){
            super(name, stats, description, image);
        }
    }
    public static class Greaves extends Equipment{
        private float jumpMult;
        
        public Greaves(String name, ArrayList<Stat> stats, String description, String image){
            super(name, stats, description, image);
        }
    }
}
