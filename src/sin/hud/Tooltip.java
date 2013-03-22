package sin.hud;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class Tooltip {
    private Node node;
    private Geometry background;
    private BitmapText header;
    private BitmapText text;
    private Vector3f size;
    private boolean visible = false;
    
    public Tooltip(Vector3f size, Vector3f trans, ColorRGBA backColor, ColorRGBA textColor){
        this.size = size;
        node = new Node("Tooltip");
        background = CG.createBox(node, "TTBackground", size, trans, backColor);
        header = CG.createText(node, 18, new Vector3f(-size.x+5, size.y-5, 1), "Batman26", ColorRGBA.Blue);
        text = CG.createText(node, 14, new Vector3f(-size.x+5, size.y-40, 1), "OCRAStd", textColor);
    }
    
    public boolean isVisible(){
        return visible;
    }
    
    public BitmapText getHeader(){
        return header;
    }
    public void setHeader(String str){
        header.setText(str);
    }
    public void setText(String str){
        text.setText(str);
    }
    public void setVisible(Node parent, boolean visible){
        if(visible){
            parent.attachChild(node);
        }else{
            parent.detachChild(node);
        }
        this.visible = visible;
    }
    
    public void updateLocation(Vector2f mouseLoc){
        node.setLocalTranslation(mouseLoc.x+size.x+20, mouseLoc.y-size.y-20, 0);
    }
}
