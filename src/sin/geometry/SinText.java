package sin.geometry;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author SinisteRing
 */
public class SinText extends BitmapText{
    public static enum Alignment{
        Left, Right, Center;
    }
    private Node node = new Node();
    private Alignment align = Alignment.Left;
    
    protected final void attach(){
        node.attachChild(this);
    }
    public SinText(BitmapFont font){
        super(font);
        this.attach();
    }
    
    // Getters & Setters:
    public Node getNode(){
        return node;
    }
    
    // Custom Functions:
    public void setAlignment(Alignment align){
        if(align == Alignment.Left){
            super.setLocalTranslation(new Vector3f(0, -this.getLineHeight()*0.5f, 0));
        }else if(align == Alignment.Right){
            super.setLocalTranslation(-this.getLineWidth(), -this.getLineHeight()*0.5f, 0);
        }else if(align == Alignment.Center){
            super.setLocalTranslation(-this.getLineWidth()*0.5f, -this.getLineHeight()*0.5f, 0);
        }
        this.align = align;
    }
    
    // Overriden Functions:
    @Override
    public void setText(String text){
        super.setText(text);
        setAlignment(align);
    }
    @Override
    public void setLocalTranslation(Vector3f trans){
        node.setLocalTranslation(trans);
    }
}
