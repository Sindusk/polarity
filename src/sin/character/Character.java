package sin.character;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import sin.GameClient;
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
    private Weapon[][] weapons = new Weapon[2][2];
    private int set = 0;
    private CharacterControl player;
    private Node node = new Node();
    
    public final void create(){
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 8f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(30);
        player.setFallSpeed(30);
        player.setGravity(70);
        player.setPhysicsLocation(new Vector3f(0, 110, 0));
        app.getBulletAppState().getPhysicsSpace().add(player);
    }
    public Character(Weapon a, Weapon b, Weapon c, Weapon d, float health, float shields){
        weapons[0][0] = a;
        weapons[0][1] = b;
        weapons[1][0] = c;
        weapons[1][1] = d;
        weapons[0][0].enable(node);
        weapons[0][1].enable(node);
        StatsManager.createCharacter(health, shields);
        this.create();
    }
    
    public void setFiring(boolean left, boolean firing){
        if(left) {
            weapons[set][0].setFiring(firing);
        }
        else {
            weapons[set][1].setFiring(firing);
        }
    }
    public Vector3f getLocation(){
        return player.getPhysicsLocation();
    }
    public CharacterControl getPlayer(){
        return player;
    }
    public Node getNode(){
        return node;
    }
    public Weapon getWeapon(boolean left){
        if(left){
            return weapons[set][0];
        }else{
            return weapons[set][1];
        }
    }
    
    public void damage(float damage){
        StatsManager.damage(damage);
    }
    public void kill(){
        StatsManager.refreshCharacter();
        player.setPhysicsLocation(T.v3f(0, 10, 0));
        app.getCamera().lookAtDirection(T.v3f(1, 0, 0), Vector3f.UNIT_Y);
    }
    public void reload(){
        if(weapons[set][0] instanceof RangedReloadWeapon){
            RangedReloadWeapon e = (RangedReloadWeapon) weapons[set][0];
            e.reload();
        }
        if(weapons[set][1] instanceof RangedReloadWeapon){
            RangedReloadWeapon e = (RangedReloadWeapon) weapons[set][1];
            e.reload();
        }
    }
    public void swapGuns(){
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
    
    public void update(float tpf){
        MovementManager.move();
        weapons[set][0].tick(tpf);
        weapons[set][1].tick(tpf);
        RecoilManager.decoil(tpf);
    }
    
    public static void initialize(GameClient app){
        Character.app = app;
    }
}
