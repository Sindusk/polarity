package sin.player;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import sin.player.ability.AbilityManager.Ability;
import sin.player.ability.AbilityManager.Blink;
import sin.player.ability.AbilityManager.Infect;
import sin.player.ability.StatusManager;
import sin.animation.Models.PlayerModel;
import sin.hud.AbilityBar;
import sin.hud.BarManager;
import sin.hud.HUD;
import sin.netdata.DamageData;
import sin.netdata.ability.AbilityCooldownData;
import sin.netdata.player.PlayerData;
import sin.network.ClientNetwork;
import sin.network.ServerNetwork;
import sin.player.StatsManager.PlayerStats;
import sin.tools.C;
import sin.tools.S;
import sin.weapons.RecoilManager;
import sin.weapons.Weapons;
import sin.weapons.Weapons.Weapon;

/**
 *
 * @author SinisteRing
 */
public class Player {
    // Managers/Subclasses:
    private Ability[] ability = new Ability[4];
    private CharacterControl control;
    private PlayerStats stats;
    private PlayerModel model;
    private StatusManager status = new StatusManager(this);
    private Weapon[][] weapons = new Weapon[2][2];

    // Other Important Variables:
    private PlayerData data;
    private Vector3f locA = Vector3f.ZERO;
    private Vector3f locB = Vector3f.ZERO;
    private Quaternion rot = new Quaternion();
    private HostedConnection conn;
    private int set = 0;
    private int id;
    private float interp = 0;
    private boolean connected = false;

    public Player(){}

    public boolean isConnected(){
        return connected;
    }
    public int getID(){
        return id;
    }
    public CharacterControl getControl(){
        return control;
    }
    public HostedConnection getConnection(){
        return conn;
    }
    public PlayerData getData(){
        return data;
    }
    public StatusManager getStatus(){
        return status;
    }
    public Vector3f getLocation(){
        return locB;
    }
    
    public void setConnection(HostedConnection conn){
        this.conn = conn;
    }
    public void setFiring(boolean left, boolean firing){
        if(left){
            weapons[set][0].setFiring(firing);
        }else{
            weapons[set][1].setFiring(firing);
        }
    }
    public void setLocation(Vector3f loc, Quaternion rot){
        this.locA = this.locB.clone();
        this.locB = loc;
        this.rot = rot;
        this.interp = 0;
    }

    public void damage(int attacker, float damage){
        stats.damage(damage);
        ServerNetwork.broadcast(new DamageData(attacker, "player", id, damage));
        if(ClientNetwork.getID() == id){
            if(stats.getHealth() <= 0){
                kill();
            }
            HUD.updateLifeBars(stats);
        }
    }
    public void cast(int index, Ray ray){
        if(ability[index].getCooldown() <= 0){
            conn.send(new AbilityCooldownData(index, ability[index].getCooldownMax()));
        }
        ability[index].execute(id, ray);
    }
    
    public void kill(){
        stats.refresh();
        HUD.updateLifeBars(stats);
        control.setPhysicsLocation(new Vector3f(0, 10, 0));
        S.getCamera().lookAtDirection(new Vector3f(1, 0, 0), Vector3f.UNIT_Y);
    }
    public void reload(){
        weapons[set][0].reload();
        weapons[set][1].reload();
    }
    public void swapGuns(){
        weapons[set][0].disable();
        weapons[set][1].disable();
        if(set == 0){
            set = 1;
        }else if(set == 1){
            set = 0;
        }
        weapons[set][0].enable(Weapons.getNode());
        weapons[set][1].enable(Weapons.getNode());
    }

    public void update(float tpf){
        if(ClientNetwork.getID() == id){
            MovementManager.move(tpf);
            weapons[set][0].tick(tpf);
            weapons[set][1].tick(tpf);
            RecoilManager.decoil(tpf);
        }else{
            int i = 0;
            while(i < ability.length){
                if(ability[i] != null){
                    ability[i].update(tpf);
                }
                i++;
            }
            status.update(tpf);
            model.update(locA, locB, rot, tpf, interp);
            control.setPhysicsLocation(model.getLocation());
            interp += tpf*ClientNetwork.MOVE_INVERSE;
        }
    }
    public void create(PlayerData d){
        this.data = d;
        this.id = d.getID();
        weapons = C.parseWeapons(d.getWeapons());
        
        // Temporary
        stats = new PlayerStats(1000, 1000, 1000, 1000);
        ability[0] = new Blink(5, 150);
        ability[1] = new Infect(10, 100, 5, 3);
        // End Temporary
        
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 8f, 1);
        control = new CharacterControl(capsuleShape, 0.05f);
        control.setJumpSpeed(30);
        control.setFallSpeed(30);
        control.setGravity(70);
        control.setCollisionGroup(S.GROUP_PLAYER);
        S.getBulletAppState().getPhysicsSpace().add(control);
        
        if(ClientNetwork.getID() == id){
            weapons[0][0].enable(Weapons.getNode());
            weapons[0][1].enable(Weapons.getNode());
            AbilityBar.createIcons(ability);
            HUD.setBarMax(BarManager.BH.HEALTH, (int) stats.getMaxHealth());
            HUD.setBarMax(BarManager.BH.SHIELDS, (int) stats.getMaxShields());
            HUD.updateLifeBars(stats);
        }else{
            this.model = new PlayerModel(id, PlayerManager.getNode());
        }
        connected = true;
    }
    public void destroy(){
        model.destroy();
        connected = false;
    }
}
