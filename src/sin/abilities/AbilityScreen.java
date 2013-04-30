package sin.abilities;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import sin.tools.T;

/**
 *
 * @author SinisteRing
 */
public class AbilityScreen {
    private static Node node = new Node("AbilityScreenNode");
    private static AbilityMenu menu = new AbilityMenu(new Vector3f(4, 3, 0));
    
    public static Node getNode(){
        return node;
    }
    
    public static void initialize(){
        node.attachChild(menu.getNode());
        menu.addAbility(new Ability("Test Ability", T.getNeuroPath("core"), 1, null));
        menu.addAbility(new Ability("Ability 2", T.getNeuroPath("empty"), 1, null));
    }
}
