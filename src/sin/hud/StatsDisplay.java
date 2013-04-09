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
    
    public StatsDisplay(int num, Vector3f start, float size, String font, float increment, float space){
        this.start = start;
        int i = 0;
        while(i < num){
            labels.add(CG.createSinText(node, size, start.add(0, increment*i, 0), font, "Label:", ColorRGBA.Orange, Alignment.Left));
            values.add(CG.createSinText(node, size, start.add(space, increment*i, 0), font, "VAL", ColorRGBA.Orange, Alignment.Right));
            i++;
        }
    }
    
    public Node getNode(){
        return node;
    }
    
    public void setData(String[][] data){
        int i = 0;
        while(i < data.length){
            labels.get(i).setText(data[i][0]);
            values.get(i).setText(data[i][1]);
            i++;
        }
    }
    public void setStat(int index, String label, String value){
        labels.get(index).setText(label);
        values.get(index).setText(value);
    }
    
    public void updateStat(String label, String value){
        int i = 0;
        while(i < labels.size()){
            if(labels.get(i).getText().equals(label)){
                values.get(i).setText(value);
                return;
            }
            i++;
        }
        T.log("Error @ updateStat: Could not find label ("+label+")");
    }
}
