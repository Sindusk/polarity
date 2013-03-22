package sin.hud;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.geometry.SinText;
import sin.geometry.SinText.Alignment;
import sin.tools.T;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class StatsDisplay {
    private Node node = new Node("Stats");
    private ArrayList<SinText> labels = new ArrayList(1);
    private ArrayList<SinText> values = new ArrayList(1);
    private Vector3f start;
    
    public StatsDisplay(int num, Vector3f start, float size, float increment, float space){
        this.start = start;
        int i = 0;
        while(i < num){
            labels.add(CG.createSinText(node, size, start.add(0, increment*i, 0), "OCRAStd", "Label:", ColorRGBA.Orange, Alignment.Left));
            values.add(CG.createSinText(node, size, start.add(space, increment*i, 0), "OCRAStd", "VAL", ColorRGBA.Orange, Alignment.Right));
            T.log("meow"+start.toString());
            i++;
        }
    }
    
    public Node getNode(){
        return node;
    }
    
    public void setStat(int index, String label, String value){
        labels.get(index).setText(label);
        values.get(index).setText(value);
    }
}
