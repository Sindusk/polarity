package sin.weapons;

import com.jme3.collision.CollisionResult;
import sin.tools.T;
import sin.weapons.ProjectileManager.Projectile;

/**
 *
 * @author SinisteRing
 */
public class ActionParser {
    private static float ParseValue(String s){
        return Float.parseFloat(s);
    }
    public static void ParseUpdate(Projectile p){
        //String[] actions = p.getUpdate().split(":");
    }
    public static void ParseCollision(Projectile p, CollisionResult target){
        String[] actions = p.getCollision().split(":");
        int i = 0;
        while(i < actions.length){
            if(actions[i].contains("damage")){
                int begin = actions[i].indexOf("(")+1;
                int end = actions[i].indexOf(")");
                DamageManager.damage(target, ParseValue(actions[i].substring(begin, end)));
            }
            if(actions[i].contains("destroy")){
                p.destroy();
            }
            i++;
        }
    }
}
