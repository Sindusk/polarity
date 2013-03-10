package sin.tools;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.HashMap;
import sin.player.Character;
import sin.player.PlayerManager;
import sin.player.PlayerManager.Player;
import sin.hud.FloatingTextManager;
import sin.netdata.AttackData;
import sin.netdata.CommandData;
import sin.netdata.DamageData;
import sin.netdata.DecalData;
import sin.netdata.ProjectileData;
import sin.network.ClientNetwork;
import sin.npc.NPCManager;
import sin.weapons.ProjectileManager;
import sin.weapons.ProjectileManager.Projectile;
import sin.world.DecalManager;

/**
 * A (Attack & Ability) - Holds tools used for the handling of attacks, abilities, and damage.
 * @author SinisteRing
 */
public class A {
    private static HashMap<Projectile, HashMap<String, Float>> UpdateMap = new HashMap();
    
    // Damage Functions:
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
            // Send Data:
            //S.getServer().broadcast(new DamageData(attacker, "player", id, damage));
            PlayerManager.getPlayer(id).damage(attacker, damage);
        }else if(data[0].equals("npc")){
            String type = data[1];
            int id = Integer.parseInt(data[2]);
            damage = modDamage(data[3], damage);
            // Send Data:
            S.getServer().broadcast(new DamageData(attacker, "npc:"+type, id, damage));
            NPCManager.damageNPC(id, type, damage);
        }else{
            DecalManager.create(target.getContactPoint());
            S.getServer().broadcast(new DecalData(target.getContactPoint()));
        }
    }
    public static void damageAoE(int attacker, CollisionResult target, float radius, float damage){
        int i = 0;
        Player[] players = PlayerManager.getPlayers();
        while(i < players.length){
            if(players[i] != null){
                float dist = players[i].getLocation().distance(target.getContactPoint());
                if(dist <= radius){
                    S.getServer().broadcast(new DamageData(attacker, "player", i, damage*(radius-dist)));
                }
            }
            i++;
        }
    }
    
    // Logan's Functions:
    private static Vector3f spiral(Projectile p, float t){
        Vector3f dirVec = p.getDirection(); //your perpendicular plane is x=0
        Vector3f camUp = p.getUp();
        Vector3f rotY = camUp.subtract(camUp.project(dirVec)).normalizeLocal();
        Vector3f rotX = dirVec.cross(rotY).normalizeLocal();
        float rotationAngle = FastMath.PI*t;
        Vector3f velocity = rotY.mult(FastMath.cos(rotationAngle)).addLocal(rotX.mult(FastMath.sin(rotationAngle)));
        return velocity;
    }
    public static int sign(float x){
        if (x != x) {
            throw new IllegalArgumentException("NaN");
        }
        if (x == 0) {
            return 0;
        }
        x *= Float.POSITIVE_INFINITY;
        if (x == Float.POSITIVE_INFINITY) {
            return +1;
        }else{
            return -1;
        }
     }
    
    // Parsing Helpers:
    private static String[] getArgs(String s){
        return s.substring(s.indexOf("(")+1, s.indexOf(")")).split(",");
    }
    private static float getValueF(String s){
        return Float.parseFloat(s);
    }
    
    // Attack Parsing:
    public static void initializeUpdate(Projectile p){
        String[] actions = p.getUpdate().split(":");
        String[] args;
        UpdateMap.put(p, new HashMap());
        int i = 0;
        while(i < actions.length){
            if(actions[i].contains("spiral")){
                args = getArgs(actions[i]);
                UpdateMap.get(p).put(i+"spiral.timer", 0f);
                UpdateMap.get(p).put(i+"spiral.rot", getValueF(args[0]));
            }
            i++;
        }
    }
    public static void parseCollision(int attacker, String collision, CollisionResult target){
        String[] actions = collision.split(":");
        String[] args;
        int i = 0;
        while(i < actions.length){
            if(actions[i].contains("damage")){
                args = getArgs(actions[i]);
                A.damage(attacker, target, getValueF(args[0]));
            }else if(actions[i].contains("aoe")){
                args = getArgs(actions[i]);
                A.damageAoE(attacker, target, getValueF(args[0]), getValueF(args[1]));
            }
            i++;
        }
    }
    public static void parseCollision(Projectile p, CollisionResult target){
        parseCollision(p.getOwner(), p.getCollision(), target);
        if(p.getCollision().contains("destroy")){
            p.destroy();
            S.getServer().broadcast(new CommandData("projectile:destroy:"+p.getID()));
        }
    }
    public static void parseUpdate(Projectile p, float tpf){
        if(p.getUpdate().isEmpty()){
            return;
        }
        String[] actions = p.getUpdate().split(":");
        String[] args;
        int i = 0;
        while(i < actions.length){
            if(actions[i].contains("spiral")){
                args = getArgs(actions[i]);
                HashMap<String, Float> meow = UpdateMap.get(p);
                float timer = 0;
                if(meow.get(i+"spiral.timer") != null){
                    timer = meow.get(i+"spiral.timer");
                }
                if(timer > getValueF(args[1])){
                    float rot = UpdateMap.get(p).get(i+"spiral.rot");
                    ProjectileManager.addNew(p.getOwner(), p.getLocation().clone(), spiral(p, rot), new Vector3f(0, 0, 0), 20, 20, "", "damage(2.3):destroy");
                    S.getServer().broadcast(new ProjectileData(p.getOwner(), p.getLocation().clone(), spiral(p, rot), new Vector3f(0, 0, 0), 20, 20, "", "damage(2.3):destroy"));
                    rot += getValueF(args[2]);
                    UpdateMap.get(p).put(i+"spiral.rot", rot);
                    timer = 0;
                }else{
                    timer += tpf;
                }
                UpdateMap.get(p).put(i+"spiral.timer", timer);
            }
            i++;
        }
    }
    
    public static void handleCommand(String command){
        String[] action = command.split(":");
        if(action[0].equals("player")){
            if(action[1].equals("destroy")){
                PlayerManager.getPlayer(Integer.parseInt(action[2])).destroy();
            }
        }else if(action[0].equals("projectile")){
            if(action[1].equals("destroy")){
                ProjectileManager.getProjectile(Integer.parseInt(action[2])).destroy();
            }
        }else if(action[0].equals("teleport")){
            String[] data = action[1].split(",");
            float x = Float.parseFloat(data[0]);
            float y = Float.parseFloat(data[1])+4;
            float z = Float.parseFloat(data[2]);
            Character.getControl().setPhysicsLocation(new Vector3f(x, y, z));
        }
    }
    public static void handleDamage(DamageData m){
        if(m.getTarget() == ClientNetwork.getID()){
            Character.damage(m.getDamage());
        }else{
            String args[] = m.getType().split(":");
            if(args[0].equals("player")){
                FloatingTextManager.add(PlayerManager.getPlayer(m.getTarget()).getLocation().add(new Vector3f(0, 4, 0)), Character.getLocation(), m.getDamage());
            }else if(args[0].equals("npc")){
                FloatingTextManager.add(NPCManager.getNPC(args[1], m.getTarget()).getLocation().add(new Vector3f(0, 4, 0)), Character.getLocation(), m.getDamage());
            }else{
                T.log("Invalid Damage Data!");
            }
        }
    }
    
    // Attack Handlers:
    public static void rayAttack(AttackData d){
        CollisionResult target = getClosestCollisionByRange(d.getRay(), S.getCollisionNode(), d.getAttacker(), d.getRange());
        if(target != null){
            parseCollision(d.getAttacker(), d.getCollision(), target);
        }
    }
    
    // Status Handlers:
    public static void applyPoison(CollisionResult target, float time, float dps){
        String[] data = getPartData(target);
        if(data[0].equals("player")){
            int id = Integer.parseInt(data[1]);
            PlayerManager.getPlayer(id).getStatus().poison(time, dps);
        }
    }
    
    // Helper Functions:
    public static CollisionResults getCollisions(Ray ray){
        CollisionResults results = new CollisionResults();
        S.getCollisionNode().collideWith(ray, results);
        return results;
    }
    public static CollisionResult getClosestCollision(Ray ray, Node node, int attacker){
        CollisionResults results = getCollisions(ray);
        String[] data;
        int i = 0;
        while(i < results.size()){
            data = getPartData(results.getCollision(i));
            if(data[0] != null && data[0].equals("player")){
                if(data[1] != null && Integer.parseInt(data[1]) != attacker){
                    return results.getCollision(i);
                }
            }else{
                return results.getCollision(i);
            }
            i++;
        }
        return null;
    }
    public static CollisionResult getClosestCollisionByRange(Ray ray, Node node, int attacker, float range){
        CollisionResult result = getClosestCollision(ray, node, attacker);
        if(result != null && ray.getOrigin().distance(result.getContactPoint()) <= range){
            return result;
        }else{
            return null;
        }
    }
    public static float getDistance(Vector3f player, Vector3f target){
        return target.distance(player);
    }
    public static String[] getPartData(CollisionResult target){
        return target.getGeometry().getParent().getName().split(":");
    }
}
