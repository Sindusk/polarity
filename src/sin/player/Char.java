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
 *
 * @author SinisteRing
 */
public class Char {
    private static GameClient app;
    
    public static final int MOVE_FORWARD = 0; public static final int MOVE_BACKWARD = 1;  // Variables for handling
    public static final int MOVE_LEFT = 2;    public static final int MOVE_RIGHT = 3;     // character movement indexes.
    public static final int MOVE_CROUCH = 4;   // For crouch detection.
    
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
                            GameClient.getCharacter().kill();
                        }
                    }
                }else{
                    health -= damage;
                    if(health <= 0){
                        GameClient.getCharacter().kill();
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
    // Constant Variables:
    private static final float MOVE_SPEED = 0.25f;
    private static final float CROUCH_PENALTY = 0.6f;

    // Instance Variables:
    private Weapon[][] weapons = new Weapon[2][2];
    private int set = 0;
    private CharStats charStats;
    private CharacterControl player;
    private Node charNode = new Node();
    // Move: 0 = forward, 1 = backward, 2 = left, 3 = right, 4 = crouch
    public boolean movement[] = new boolean[]{false, false, false, false, false};

    public final void create(){
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 10f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(30);
        player.setFallSpeed(30);
        player.setGravity(70);
        player.setPhysicsLocation(new Vector3f(0, 110, 0));
        GameClient.getBulletAppState().getPhysicsSpace().add(player);
    }
    public Char(Weapon a, Weapon b, Weapon c, Weapon d, float health, float shields){
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
    public void move(){
        // Initialize temporary variables:
        Vector3f dir;// = v3f(0, 0, 0);
        Vector3f wd = T.v3f(0, 0, 0);
        float x, z, angle;
        double rad;
        // Calculate forward/backward points:
        if(movement[MOVE_FORWARD] || movement[MOVE_BACKWARD]){
            dir = app.getCamera().getDirection();
            x = dir.getZ();
            z = dir.getX();
            angle = (float) Math.toDegrees(Math.atan2(x/2 - x, z/2 - z));
            if(angle < 0){
                angle += 360;
            }
            rad = Math.toRadians(angle);
            float xWard = (float) -Math.cos(rad) * MOVE_SPEED;
            float zWard = (float) -Math.sin(rad) * MOVE_SPEED;
            if(movement[MOVE_FORWARD]) {
                wd.addLocal(xWard, 0, zWard);
            }
            if(movement[MOVE_BACKWARD]) {
                wd.addLocal(-xWard, 0, -zWard);
            }
        }
        // Calculate sidestep points:
        if(movement[MOVE_LEFT] || movement[MOVE_RIGHT]){
            dir = app.getCamera().getLeft();
            x = dir.getZ();
            z = dir.getX();
            angle = (float) Math.toDegrees(Math.atan2(x/2 - x, z/2 - z));
            if(angle < 0){
                angle += 360;
            }
            rad = Math.toRadians(angle);
            float xSide = (float) -Math.cos(rad) * MOVE_SPEED;
            float zSide = (float) -Math.sin(rad) * MOVE_SPEED;
            if(movement[MOVE_LEFT]) {
                wd.addLocal(xSide, 0, zSide);
            }
            if(movement[MOVE_RIGHT]) {
                wd.addLocal(-xSide, 0, -zSide);
            }
        }

        // If moving at a diagonal, reduce movement.
        if((movement[MOVE_FORWARD] || movement[MOVE_BACKWARD])
                && (movement[MOVE_LEFT] || movement[MOVE_RIGHT])){
            wd.setX(wd.getX()/1.414f);
            wd.setZ(wd.getZ()/1.414f);
        }

        // If crouching, lower view.
        if(movement[MOVE_CROUCH]){
            app.getCamera().setLocation(player.getPhysicsLocation().add(T.v3f(0, -1.5f, 0)));
            wd.setX(wd.getX()*CROUCH_PENALTY);
            wd.setZ(wd.getZ()*CROUCH_PENALTY);
        }else{
            app.getCamera().setLocation(player.getPhysicsLocation());
        }

        player.setWalkDirection(wd);
        charNode.setLocalTranslation(app.getCamera().getLocation());
        //UpdateWeaponUI();
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
        this.move();
        weapons[set][0].tick(tpf);
        weapons[set][1].tick(tpf);
        RecoilManager.decoil(tpf);
    }
    
    public static void initialize(GameClient app){
        Char.app = app;
    }
}
