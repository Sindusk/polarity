package sin.abilities;

import com.jme3.font.BitmapFont.VAlign;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.abilities.AbilityActions.AbilityAction;
import sin.abilities.AbilityActions.Blind;
import sin.abilities.AbilityActions.Damage;
import sin.abilities.AbilityActions.Slow;
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
        CG.createBox(node, new Vector3f(3, 0.22f, 0), new Vector3f(0, 4-(index/2f), 0), ColorRGBA.Yellow);
        SinText text = CG.createSinText(node, 0.2f, Vector3f.ZERO, "Batman26", "Action", ColorRGBA.Blue, Alignment.Left);
        if(action instanceof Blind){
            Blind blind = (Blind) action;
            text.setText("Blind: "+blind.getDuration()+" seconds");
        }else if(action instanceof Damage){
            Damage dmg = (Damage) action;
            text.setText("Damage: "+dmg.getDamage());
        }else if(action instanceof Slow){
            Slow slow = (Slow) action;
            text.setText("Slow: "+Math.round(slow.getPercentage()*100)+"% for "+slow.getDuration()+" seconds.");
        }else if(action instanceof Stun){
            Stun stun = (Stun) action;
            text.setText("Stun: "+stun.getDuration()+" seconds");
        }
        text.setLocalTranslation(-2.5f, (4-index/2f)+(text.getLineHeight()/2), 0);
    }
    public void setEntry(AbilityEntry entry){
        this.entry = entry;
        node.detachAllChildren();
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
