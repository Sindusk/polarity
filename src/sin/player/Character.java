package sin.player;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.player.StatsManager.PlayerStats;
import sin.hud.BarManager;
import sin.hud.BarManager.BH;
import sin.hud.HUD;
import sin.tools.T;
import sin.weapons.RecoilManager;
import sin.weapons.Weapons.RangedReloadWeapon;
import sin.weapons.Weapons.Weapon;

/**
 * Character - Used for the creation and controlling of the player character.
 * @author SinisteRing
 */
public class Character {
    private static GameClient app;
    
    // Instance Variables:
    private static PlayerStats stats;
    private static Weapon[][] weapons = new Weapon[2][2];
    private static int set = 0;
    private static CharacterControl control;
    private static Node node = new Node();
    
    public static void setFiring(boolean left, boolean firing){
        if(left) {
            weapons[set][0].setFiring(firing);
        }
        else {
            weapons[set][1].setFiring(firing);
        }
    }
    public static Vector3f getLocation(){
        return control.getPhysicsLocation();
    }
    public static CharacterControl getControl(){
        return control;
    }
    public static Node getNode(){
        return node;
    }
    public static Weapon getWeapon(boolean left){
        if(left){
            return weapons[set][0];
        }else{
            return weapons[set][1];
        }
    }
    
    public static void damage(float damage){
        if(stats.damage(damage)){
            kill();
        }else{
            HUD.updateBar(BH.HEALTH, (int) FastMath.ceil(stats.getHealth()));
            HUD.updateBar(BH.SHIELDS, (int) FastMath.ceil(stats.getShields()));
        }
    }
    public static void kill(){
        stats.refresh();
        HUD.updateBar(BH.HEALTH, (int) FastMath.ceil(stats.getHealth()));
        HUD.updateBar(BH.SHIELDS, (int) FastMath.ceil(stats.getShields()));
        control.setPhysicsLocation(T.v3f(0, 10, 0));
        app.getCamera().lookAtDirection(T.v3f(1, 0, 0), Vector3f.UNIT_Y);
    }
    public static void reload(){
        if(weapons[set][0] instanceof RangedReloadWeapon){
            RangedReloadWeapon e = (RangedReloadWeapon) weapons[set][0];
            e.reload();
        }
        if(weapons[set][1] instanceof RangedReloadWeapon){
            RangedReloadWeapon e = (RangedReloadWeapon) weapons[set][1];
            e.reload();
        }
    }
    public static void swapGuns(){
        weapons[set][0].disable();
        weapons[set][1].disable();
        if(set == 0){
            set = 1;
        }else if(set == 1){
            set = 0;
        }
        weapons[set][0].enable(node);
        weapons[set][1].enable(node);
    }
    
    public static void update(float tpf){
        MovementManager.move(tpf);
        weapons[set][0].tick(tpf);
        weapons[set][1].tick(tpf);
        RecoilManager.decoil(tpf);
    }
    public static void create(Weapon a, Weapon b, Weapon c, Weapon d, float health, float shields){
        weapons[0][0] = a;
        weapons[0][1] = b;
        weapons[1][0] = c;
        weapons[1][1] = d;
        weapons[0][0].enable(node);
        weapons[0][1].enable(node);
        
        // Stats
        stats = new PlayerStats(health, health, shields, shields);
        HUD.setBarMax(BarManager.BH.HEALTH, (int) health);
        HUD.setBarMax(BarManager.BH.SHIELDS, (int) health);
        HUD.updateBar(BarManager.BH.HEALTH, (int) FastMath.ceil(health));
        HUD.updateBar(BarManager.BH.SHIELDS, (int) FastMath.ceil(shields));
        
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 8f, 1);
        control = new CharacterControl(capsuleShape, 0.05f);
        control.setJumpSpeed(30);
        control.setFallSpeed(30);
        control.setGravity(70);
        control.setPhysicsLocation(new Vector3f(0, 110, 0));
        app.getBulletAppState().getPhysicsSpace().add(control);
    }
    
    public static void initialize(GameClient app){
        Character.app = app;
    }
}
