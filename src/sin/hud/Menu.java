package sin.hud;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
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
public class Menu {
    private Node node;
    private Node gui;
    private ArrayList<SinText> labels = new ArrayList(1);
    private ArrayList<Geometry> buttons = new ArrayList(1);
    private float x, y;
    
    public Menu(float x, float y){
        this.x = x;
        this.y = y;
        node = new Node("Menu");
        node.setLocalTranslation(new Vector3f(x, y, 0));
        gui = new Node("MenuGUI");
    }
    
    public boolean isButton(String name){
        int i = 0;
        while(i < buttons.size()){
            if(name.equals(buttons.get(i).getName())){
                return true;
            }
            i++;
        }
        return false;
    }
    public boolean isLabel(String name){
        if(name.equals("BitmapFont")){
            return true;
        }
        return false;
    }
    public boolean isElement(String name){
        return isButton(name) || isLabel(name);
    }
    public Node getNode(){
        return node;
    }
    public Node getGUI(){
        return gui;
    }
    
    public void addLabel(float size, Vector3f trans, String font, String text, ColorRGBA color, Alignment align){
        SinText label = CG.createSinText(node, size, trans, font, text, color, align);
        labels.add(label);
    }
    public void addButton(String name, Vector3f size, Vector3f trans, String graphic){
        buttons.add(CG.createBox(node, name, size, trans, T.getGraphicPath(graphic), new Vector2f(1, 1)));
    }
    public void destroy(){
        node.removeFromParent();
    }
}
