package sin.hud;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.geometry.SinText;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class ContextMenu {
    private Node node = new Node("ContextMenu");
    private ArrayList<SinText> labels = new ArrayList(1);
    private ArrayList<Geometry> boxes = new ArrayList(1);
    private Geometry highlight;
    private ColorRGBA backColor;
    private ColorRGBA textColor;
    private float x_size;
    private float size;
    private String font;
    
    public ContextMenu(float size, Vector3f trans, String font, float x_size, ColorRGBA backColor, ColorRGBA textColor){
        this.size = size;
        this.font = font;
        this.backColor = backColor;
        this.textColor = textColor;
        this.x_size = x_size;
        highlight = CG.createBox(node, "highlight", new Vector3f(1, 1, 1), Vector3f.ZERO, new ColorRGBA(0, 1, 0, 0.25f));
        node.setLocalTranslation(trans);
    }
    
    public Node getNode(){
        return node;
    }
    public int getBoxIndex(String name){
        int i = 0;
        while(i < boxes.size()){
            if(boxes.get(i).getName().equals(name)){
                return i;
            }
            i++;
        }
        return -1;
    }
    public boolean isOption(String name){
        if(getBoxIndex(name) == -1){
            return false;
        }
        return true;
    }
    
    public void setLocalTranslation(Vector3f trans){
        node.setLocalTranslation(trans);
    }
    public void setData(String[][] data){
        int i = 0;
        node.detachAllChildren();
        labels = new ArrayList(1);
        boxes = new ArrayList(1);
        SinText label = null;
        Geometry box;
        while(i < data.length){
            label = CG.createSinText(node, size, new Vector3f(size, -size*i, 0), font, data[i][1], textColor, SinText.Alignment.Left);
            box = CG.createBox(node, data[i][0], new Vector3f(x_size, label.getLineHeight()*0.5f, 0),
                    new Vector3f(x_size, -label.getLineHeight()+(-size*i), 0), backColor);
            labels.add(label);
            boxes.add(box);
            i++;
        }
        highlight.setLocalScale(new Vector3f(x_size, label.getLineHeight()*0.5f, 0));
    }
    
    public void highlightBox(String name){
        int index = getBoxIndex(name);
        if(index != -1){
            node.attachChild(highlight);
            highlight.setLocalTranslation(boxes.get(index).getLocalTranslation());
        }
    }
    public void removeHighlight(){
        highlight.removeFromParent();
    }
    
    public void destroy(){
        node.detachAllChildren();
        node.removeFromParent();
    }
}
