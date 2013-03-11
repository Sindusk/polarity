package sin.hud;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import sin.player.ability.AbilityManager.Ability;
import sin.tools.T;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class AbilityBar {
    private static Node node = new Node("AbilityBarNode");
    private static Geometry bar;
    private static AbilityIcon[] icons = new AbilityIcon[4];
    
    private static class AbilityIcon{
        private Node node = new Node("AbilityIcon");
        private Geometry icon;
        
        public AbilityIcon(float x, float y, String name){
            icon = CG.createBox(node, "", new Vector3f(20, 20, 0), new Vector3f(x, y, 1), T.getIconPath(name.toLowerCase()), Vector2f.UNIT_XY);
        }
        
        public Node getNode(){
            return node;
        }
    }
    
    public static void createIcons(Ability[] abilities){
        int i = 0;
        while(i < 4){
            if(abilities[i] != null){
                icons[i] = new AbilityIcon(bar.getLocalTranslation().getX()-(60*(1.5f-(float)i)), bar.getLocalTranslation().getY(), abilities[i].getName());
                node.attachChild(icons[i].getNode());
            }
            i++;
        }
    }
    public static void initialize(float cx, float cy){
        bar = CG.createBox(node, "", new Vector3f(115, 25, 0), new Vector3f(cx, cy-(cy*0.58f), 0), new ColorRGBA(0, 0.5f, 0, 0.4f));
    }
    
    public static Node getNode(){
        return node;
    }
}
