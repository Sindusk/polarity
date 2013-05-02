package sin.abilities;

import com.jme3.math.Ray;
import java.util.ArrayList;
import sin.abilities.AbilityActions.AbilityAction;

/**
 *
 * @author SinisteRing
 */
public class Ability {
    private String name;
    private String icon;
    private ArrayList<AbilityAction> actions;
    private float cooldownMax;
    private float cooldown;
    private boolean cooling = false;

    public Ability(String name, String icon, float cooldown, ArrayList<AbilityAction> actions){
        this.name = name;
        this.icon = icon;
        this.cooldownMax = cooldown;
        this.cooldown = 0;
        this.actions = actions;
    }

    public String getName(){
        return name;
    }
    public String getIcon(){
        return icon;
    }
    public ArrayList<AbilityAction> getActions(){
        return (ArrayList<AbilityAction>) actions.clone();
    }
    public float getCooldownMax(){
        return cooldownMax;
    }
    public float getCooldown(){
        return cooldown;
    }

    public void casted(){
        cooldown = cooldownMax;
        cooling = true;
    }
    public void update(float tpf){
        cooldown -= tpf;
        if(cooldown <= 0){
            cooling = false;
        }
    }

    public void execute(int attacker, Ray ray){
        
    }
}
