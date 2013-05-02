package sin.abilities;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import sin.geometry.SinText;
import sin.geometry.SinText.Alignment;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class AbilityEntry {
    private Node node = new Node("AbilityEntryNode");
    private Ability ability;
    
    public AbilityEntry(Ability abil){
        this.ability = abil;
        CG.createBox(node, "abilityEntry", new Vector3f(2.6f, 0.4f, -0.001f), Vector3f.ZERO, ColorRGBA.Blue);
        CG.createBox(node, new Vector3f(0.3f, 0.3f, 0), new Vector3f(-2, 0, 0), abil.getIcon());
        SinText text = CG.createSinText(node, 0.3f, Vector3f.ZERO, "Batman26", abil.getName(), ColorRGBA.Orange, Alignment.Left);
        text.setLocalTranslation(new Vector3f(-1.3f, text.getLineHeight(), 0));
    }
    
    public Node getNode(){
        return node;
    }
    public Ability getAbility(){
        return ability;
    }
    public void setLocalTranslation(Vector3f trans){
        node.setLocalTranslation(trans);
    }
}
