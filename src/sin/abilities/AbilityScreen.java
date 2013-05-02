package sin.abilities;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.abilities.AbilityActions.AbilityAction;
import sin.abilities.AbilityActions.Damage;
import sin.abilities.AbilityActions.Stun;
import sin.tools.T;

/**
 *
 * @author SinisteRing
 */
public class AbilityScreen {
    private static Node node = new Node("AbilityScreenNode");
    private static AbilityMenu menu = new AbilityMenu(new Vector3f(4, 3, 0));
    private static AbilityBar bar = new AbilityBar(new Vector3f(-2, 3, 0));
    private static AbilityCreator creator = new AbilityCreator(new Vector3f(-3, -3, 0));
    
    public static Node getNode(){
        return node;
    }
    
    public static void initialize(){
        node.attachChild(bar.getNode());
        node.attachChild(creator.getNode());
        node.attachChild(menu.getNode());
        ArrayList<AbilityAction> a1 = new ArrayList(1);
        a1.add(new Damage(5));
        a1.add(new Stun(0.5f));
        ArrayList<AbilityAction> a2 = new ArrayList(1);
        a2.add(new Stun(0.1f));
        a2.add(new Damage(10));
        a2.add(new Stun(0.1f));
        menu.addAbility(new Ability("Test Ability", T.getNeuroPath("core"), 1, a1));
        menu.addAbility(new Ability("Ability 2", T.getNeuroPath("empty"), 1, a1));
        menu.addAbility(new Ability("Corner Ability", T.getNeuroPath("corner"), 1, a2));
        menu.addAbility(new Ability("Lockdown", T.getNeuroPath("locked"), 1, a2));
        bar.setAbility(0, T.getNeuroPath("core"));
        bar.setAbility(1, T.getNeuroPath("conn4way"));
        bar.setAbility(2, T.getNeuroPath("empty"));
        bar.setAbility(3, T.getNeuroPath("locked"));
        creator.setEntry(menu.getEntry(2));
    }
}
