package sin.abilities;

import com.jme3.font.BitmapFont.VAlign;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.abilities.AbilityActions.AbilityAction;
import sin.abilities.AbilityActions.Damage;
import sin.abilities.AbilityActions.Stun;
import sin.geometry.SinText;
import sin.geometry.SinText.Alignment;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class AbilityCreator {
    private Node node = new Node("AbilityCreatorNode");
    private AbilityEntry entry;
    
    public AbilityCreator(Vector3f trans){
        node.setLocalTranslation(trans);
    }
    
    public Node getNode(){
        return node;
    }
    
    private void createAction(int index, AbilityAction action){
        CG.createBox(node, new Vector3f(3, 0.4f, 0), new Vector3f(0, 4-index, 0), ColorRGBA.Yellow);
        if(action instanceof Damage){
            Damage dmg = (Damage) action;
            SinText text = CG.createSinText(node, 0.3f, Vector3f.ZERO, "Batman26", "Damage: "+dmg.getDamage(), ColorRGBA.Blue, Alignment.Left);
            text.setLocalTranslation(-2, (4-index)+(text.getLineHeight()/2), 0);
        }else if(action instanceof Stun){
            Stun stun = (Stun) action;
            SinText text = CG.createSinText(node, 0.3f, Vector3f.ZERO, "Batman26", "Stun: "+stun.getDuration(), ColorRGBA.Blue, Alignment.Left);
            text.setLocalTranslation(-2, (4-index)+(text.getLineHeight()/2), 0);
        }
    }
    public void setEntry(AbilityEntry entry){
        this.entry = entry;
        ArrayList<AbilityAction> actions = entry.getAbility().getActions();
        if(actions == null){
            return;
        }
        int i = 0;
        while(i < actions.size()){
            createAction(i, actions.get(i));
            i++;
        }
    }
}
