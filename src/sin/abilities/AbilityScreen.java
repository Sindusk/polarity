package sin.abilities;

import com.jme3.collision.CollisionResult;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.abilities.AbilityActions.AbilityAction;
import sin.abilities.AbilityActions.Blind;
import sin.abilities.AbilityActions.Damage;
import sin.abilities.AbilityActions.Slow;
import sin.abilities.AbilityActions.Stun;
import sin.geometry.SinText.Alignment;
import sin.tools.T;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class AbilityScreen {
    private static Node node = new Node("AbilityScreenNode");
    private static AbilityMenu menu = new AbilityMenu(new Vector3f(4, 1, 0));
    private static AbilityBar bar = new AbilityBar(new Vector3f(4, 3, 0));
    private static AbilityCreator creator = new AbilityCreator(new Vector3f(-3, -3, 0));
    
    public static Node getNode(){
        return node;
    }
    
    public static void action(CollisionResult target){
        if(target == null){
            return;
        }
        Geometry geo = target.getGeometry();
        int index = menu.getIndex(geo);
        if(index != -1){
            creator.setEntry(menu.getEntry(index));
        }
    }
    
    public static void initialize(){
        node.attachChild(bar.getNode());
        node.attachChild(creator.getNode());
        node.attachChild(menu.getNode());
        CG.createSinText(node, 0.3f, new Vector3f(-3, 3, 0), "Batman26", "Projectile Here", ColorRGBA.Green, Alignment.Center);
        ArrayList<AbilityAction> a1 = new ArrayList(1);
        a1.add(new Damage(150));
        ArrayList<AbilityAction> a2 = new ArrayList(1);
        a2.add(new Damage(15));
        a2.add(new Stun(0.5f));
        a2.add(new Slow(0.3f, 1.5f));
        a2.add(new Blind(0.3f));
        ArrayList<AbilityAction> a3 = new ArrayList(1);
        a3.add(new Damage(25));
        a3.add(new Stun(1));
        ArrayList<AbilityAction> a4 = new ArrayList(1);
        a4.add(new Slow(0.7f, 1f));
        a4.add(new Blind(1));
        menu.addAbility(new Ability("Big Damage", T.getNeuroPath("core"), 1, a1));
        menu.addAbility(new Ability("All Modifiers", T.getNeuroPath("empty"), 1, a2));
        menu.addAbility(new Ability("Damage & Stun", T.getNeuroPath("corner"), 1, a3));
        menu.addAbility(new Ability("Lockdown", T.getNeuroPath("locked"), 1, a4));
        bar.setAbility(0, T.getNeuroPath("core"));
        bar.setAbility(1, T.getNeuroPath("conn4way"));
        bar.setAbility(2, T.getNeuroPath("empty"));
        bar.setAbility(3, T.getNeuroPath("locked"));
        creator.setEntry(menu.getEntry(1));
    }
}
