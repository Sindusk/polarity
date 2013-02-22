package sin.player;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.hud.BarManager.BH;
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
    
    private class CharStats{
            // Instance Variables:
            private float health;
            private float health_max;
            private float shields;
            private float shields_max;
            
            public CharStats(float health_max, float shields_max){
                this.health = health_max;
                this.health_max = health_max;
                this.shields = shields_max;
                this.shields_max = shields_max;
                app.getHUD().setBarMax(BH.HEALTH, (int) health_max);
                app.getHUD().setBarMax(BH.SHIELDS, (int) shields_max);
                app.getHUD().updateBar(BH.HEALTH, (int) FastMath.ceil(health));
                app.getHUD().updateBar(BH.SHIELDS, (int) FastMath.ceil(shields));
            }
            
            public void update(){
                app.getHUD().updateBar(BH.HEALTH, (int) FastMath.ceil(health));
                app.getHUD().updateBar(BH.SHIELDS, (int) FastMath.ceil(shields));
            }
            public void damage(float damage){
                if(shields > 0){
                    shields -= damage;
                    if(shields <= 0){
                        health += shields;
                        shields = 0;
                        if(health <= 0){
                            app.getCharacter().kill();
                        }
                    }
                }else{
                    health -= damage;
                    if(health <= 0){
                        app.getCharacter().kill();
                    }
                }
                update();
            }
            public void refresh(){
                health = health_max;
                shields = shields_max;
                update();
            }
        }

    // Instance Variables:
    private Weapon[][] weapons = new Weapon[2][2];
    private int set = 0;
    private CharStats charStats;
    private CharacterControl player;
    private Node charNode = new Node();

    public final void create(){
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 10f, 1);
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
        weapons[0][0].enable(charNode);
        weapons[0][1].enable(charNode);
        charStats = new CharStats(health, shields);
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
        return charNode;
    }
    public Weapon getWeapon(boolean left){
        if(left){
            return weapons[set][0];
        }else{
            return weapons[set][1];
        }
    }

    public void damage(float damage){
        charStats.damage(damage);
    }
    public void kill(){
        charStats.refresh();
        player.setPhysicsLocation(T.v3f(0, 10, 0));
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
        weapons[set][0].enable(charNode);
        weapons[set][1].enable(charNode);
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
