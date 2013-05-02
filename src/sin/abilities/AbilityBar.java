package sin.abilities;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class AbilityBar {
    private Node node = new Node("AbilityBar");
    private ArrayList<Geometry> entries = new ArrayList(1);
    
    public AbilityBar(Vector3f trans){
        node.setLocalTranslation(trans);
        CG.createBox(node, new Vector3f(2.6f, 0.6f, 0), Vector3f.ZERO, ColorRGBA.Green);
        int i = 0;
        while(i < 4){
            entries.add(null);
            i++;
        }
    }
    
    public Node getNode(){
        return node;
    }
    
    public void setAbility(int index, String icon){
        Geometry geo = CG.createBox(node, "equipped("+Integer.toString(index)+")", new Vector3f(0.5f, 0.5f, 0), new Vector3f(-2f+(index*1.25f), 0, 0), icon);
        if(entries.get(index) != null){
            node.detachChild(entries.get(index));
        }
        entries.set(index, geo);
        node.attachChild(geo);
    }
}
