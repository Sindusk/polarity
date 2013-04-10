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
    private ArrayList<String> handles = new ArrayList(1);
    private ArrayList<SinText> labels = new ArrayList(1);
    private ArrayList<SinText> values = new ArrayList(1);
    private ArrayList<String> base = new ArrayList(1);
    private Vector3f start;
    private float size;
    private String font;
    private float increment;
    private float space;
    
    public StatsDisplay(Vector3f start, float size, String font, float increment, float space){
        this.start = start;
        this.size = size;
        this.font = font;
        this.increment = increment;
        this.space = space;
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
    
    public void addStat(String handle, String label, String value){
        handles.add(handle);
        labels.add(CG.createSinText(node, size, start.add(0, increment*labels.size(), 0), font, label, ColorRGBA.Orange, Alignment.Left));
        values.add(CG.createSinText(node, size, start.add(space, increment*values.size(), 0), font, value, ColorRGBA.Orange, Alignment.Right));
        base.add(value);
    }
    
    public void updateStat(String handle, String value){
        int i = 0;
        while(i < handles.size()){
            if(handles.get(i).equals(handle)){
                values.get(i).setText(value);
                return;
            }
            i++;
        }
        T.log("Error @ updateStat: Could not find handle ("+handle+")");
    }
    
    public void reset(){
        int i = 0;
        while(i < values.size()){
            values.get(i).setText(base.get(i));
            i++;
        }
    }
}
