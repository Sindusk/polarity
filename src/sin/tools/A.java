package sin.tools;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.network.Server;
import sin.netdata.DamageData;
import sin.netdata.DecalData;
import sin.network.Networking;
import sin.world.DecalManager;

/**
 *
 * @author SinisteRing
 */
public class A {
    public static Server server;
    
    public static float modDamage(String part, float dmg){
        if(part.equals("head")){
            dmg *= 1.5f;
        }
        return dmg;
    }
    public static void damage(int attacker, CollisionResult target, float damage){
        String data[] = getPartData(target);
        if(data == null){
            return;
        }
        if(data[0].equals("player")){
            int id = Integer.parseInt(data[1]);
            damage = modDamage(data[2], damage);
            server.broadcast(new DamageData(attacker, id, damage));
            //HUD.addFloatingText(PlayerManager.getPlayer(id).getLocation().clone().addLocal(T.v3f(0, 4, 0)), Character.getLocation(), damage);
            //Networking.send(new DamageData(Networking.getID(), id, damage));
        }else{
            DecalManager.create(target.getContactPoint());
            Networking.send(new DecalData(target.getContactPoint()));
        }
    }
    public static void damageAoE(int attacker, CollisionResult target, float radius, float damage){
        DecalManager.create(target.getContactPoint());
        Networking.send(new DecalData(target.getContactPoint()));
    }
    
    public static float getDistance(Vector3f player, Vector3f target){
        return target.distance(player);
    }
    public static String[] getPartData(CollisionResult target){
        return target.getGeometry().getParent().getName().split(":");
    }
    public static int getHitbox(String name){
        if(name.contains("head")) {
            return 0;
        }else if(name.contains("torso")) {
            return 1;
        }else if(name.contains("arm")) {
            return 2;
        }else if(name.contains("leg")) {
            return 3;
        }
        return -1;
    }
    
    public static void initialize(Server server){
        A.server = server;
    }
}
