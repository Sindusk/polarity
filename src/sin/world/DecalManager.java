package sin.world;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.HashMap;

/**
 *
 * @author SinisteRing
 */
public class DecalManager{
    // Constant Variables:
    private static final float DECAL_SIZE = 0.2f;

    // Instance Variables:
    private static Node node = new Node("DecalNode");
    private static Integer numDecals = 0;
    private static HashMap<Integer, Decal> decals = new HashMap();
    
    private static class Decal{
        private Geometry decal;
        
        public Decal(){}
        
        public void create(Vector3f loc){
            if(decal == null){
                decal = CG.createSphere(node, "decal", DECAL_SIZE, loc, ColorRGBA.Black);
            }else{
                decal.setLocalTranslation(loc);
            }
            node.attachChild(decal);
        }
        public void destroy(){
            decal.removeFromParent();
        }
    }

    public static Node getNode(){
        return node;
    }
    public static void create(Vector3f loc){
        Decal d = new Decal();
        d.create(loc);
        decals.put(numDecals, d);
        numDecals++;
        if(numDecals > 200){
            clear();
        }
    }
    public static void clear(){
        int i = 0;
        while(i < numDecals){
            decals.get(i).destroy();
            i++;
        }
        numDecals = 0;
    }
}
