package sin.tools;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import sin.player.PlayerManager;
import sin.world.FloatingTextManager;
import sin.netdata.AttackData;
import sin.netdata.CommandData;
import sin.netdata.DamageData;
import sin.netdata.DecalData;
import sin.netdata.ProjectileData;
import sin.network.ClientNetwork;
import sin.network.ServerNetwork;
import sin.npc.NPCManager;
import sin.player.Player;
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
            // Damage Player:
            PlayerManager.getPlayer(id).damage(attacker, damage);
        }else if(data[0].equals("npc")){
            String type = data[1];
            int id = Integer.parseInt(data[2]);
            damage = modDamage(data[3], damage);
            // Damage NPC:
            ServerNetwork.broadcast(new DamageData(attacker, "npc:"+type, id, damage));
            NPCManager.damageNPC(id, type, damage);
        }else{
            DecalManager.create(target.getContactPoint());
            ServerNetwork.broadcast(new DecalData(target.getContactPoint()));
        }
    }
    public static void damageAoE(int attacker, CollisionResult target, float radius, float damage){
        int i = 0;
        Player[] players = PlayerManager.getPlayers();
        while(i < players.length){
            if(players[i] != null){
                float dist = players[i].getLocation().distance(target.getContactPoint());
                if(dist <= radius){
                    ServerNetwork.broadcast(new DamageData(attacker, "player", i, damage*(radius-dist)));
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
    
    // Attack Parsing:
    public static void initializeUpdate(Projectile p){
        String[] actions = p.getUpdate().split(":");
        ArrayList<String> args;
        UpdateMap.put(p, new HashMap());
        int i = 0;
        while(i < actions.length){
            if(actions[i].contains("spiral")){
                args = T.getArgs(actions[i]);
                UpdateMap.get(p).put(i+"spiral.timer", 0f);
                UpdateMap.get(p).put(i+"spiral.rot", Float.parseFloat(args.get(0)));
            }
            i++;
        }
    }
    public static void parseCollision(int attacker, String collision, CollisionResult target){
        String[] actions = collision.split(":");
        ArrayList<String> args;
        int i = 0;
        while(i < actions.length){
            if(actions[i].contains("damage")){
                args = T.getArgs(actions[i]);
                A.damage(attacker, target, Float.parseFloat(args.get(0)));
            }else if(actions[i].contains("aoe")){
                args = T.getArgs(actions[i]);
                A.damageAoE(attacker, target, Float.parseFloat(args.get(0)), Float.parseFloat(args.get(1)));
            }
            i++;
        }
    }
    public static void parseCollision(Projectile p, CollisionResult target){
        parseCollision(p.getOwner(), p.getCollision(), target);
        if(p.getCollision().contains("destroy")){
            p.destroy();
            ServerNetwork.broadcast(new CommandData("projectile:destroy:"+p.getID()));
        }
    }
    public static void parseUpdate(Projectile p, float tpf){
        if(p.getUpdate().isEmpty()){
            return;
        }
        String[] actions = p.getUpdate().split(":");
        ArrayList<String> args;
        int i = 0;
        while(i < actions.length){
            if(actions[i].contains("spiral")){
                args = T.getArgs(actions[i]);
                HashMap<String, Float> meow = UpdateMap.get(p);
                float timer = 0;
                if(meow.get(i+"spiral.timer") != null){
                    timer = meow.get(i+"spiral.timer");
                }
                if(timer > Float.parseFloat(args.get(1))){
                    float rot = UpdateMap.get(p).get(i+"spiral.rot");
                    ProjectileManager.addNew(p.getOwner(), p.getLocation().clone(), spiral(p, rot), new Vector3f(0, 0, 0), 20, 20, "", "damage(2.3):destroy");
                    ServerNetwork.broadcast(new ProjectileData(p.getOwner(), p.getLocation().clone(), spiral(p, rot), new Vector3f(0, 0, 0), 20, 20, "", "damage(2.3):destroy"));
                    rot += Float.parseFloat(args.get(2));
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
            PlayerManager.getPlayer(ClientNetwork.getID()).getControl().setPhysicsLocation(new Vector3f(x, y, z));
        }
    }
    public static void handleDamage(DamageData m){
        if(m.getTarget() == ClientNetwork.getID()){
            PlayerManager.getPlayer(ClientNetwork.getID()).damage(m.getAttacker(), m.getDamage());
        }else{
            String args[] = m.getType().split(":");
            if(args[0].equals("player")){
                FloatingTextManager.add(PlayerManager.getPlayer(m.getTarget()).getLocation().add(new Vector3f(0, 4, 0)), S.getCamera().getLocation(), m.getDamage());
            }else if(args[0].equals("npc")){
                FloatingTextManager.add(NPCManager.getNPC(args[1], m.getTarget()).getLocation().add(new Vector3f(0, 6, 0)), S.getCamera().getLocation(), m.getDamage());
            }else{
                T.log("Invalid Damage Data!");
            }
        }
    }
    
    // Attack Handlers:
    public static void rayAttack(AttackData d){
        CollisionResult target = getClosestCollisionByRange(S.getCollisionNode(), d.getRay(), d.getAttacker(), d.getRange());
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
    
    // Collision Assistance:
    public static ArrayList<CollisionResult> getCollisions(Node node, Ray ray){
        CollisionResults results = new CollisionResults();
        node.collideWith(ray, results);
        ArrayList<CollisionResult> list = new ArrayList(1);
        int i = 0;
        while(i < results.size()){
            if(!results.getCollision(i).getGeometry().getName().equals("BitmapFont")){
                list.add(results.getCollision(i));
            }
            i++;
        }
        return list;
    }
    public static CollisionResult getClosestCollision(Node node, Ray ray){
        CollisionResults results = new CollisionResults();
        node.collideWith(ray, results);
        String name;
        int i = 0;
        while(i < results.size()){
            name = results.getCollision(i).getGeometry().getName();
            if(!name.equals("BitmapFont")){
                return results.getCollision(i);
            }
            i++;
        }
        return null;
    }
    public static CollisionResult getClosestCollisionNotPlayer(Node node, Ray ray, int player){
        CollisionResults results = new CollisionResults();
        node.collideWith(ray, results);
        String[] data;
        int i = 0;
        while(i < results.size()){
            data = getPartData(results.getCollision(i));
            if(data[0] != null && data[0].equals("player")){
                if(data[1] != null && Integer.parseInt(data[1]) != player){
                    return results.getCollision(i);
                }
            }else{
                return results.getCollision(i);
            }
            i++;
        }
        return null;
    }
    public static CollisionResult getClosestCollisionByRange(Node node, Ray ray, int player, float range){
        CollisionResult result = getClosestCollisionNotPlayer(node, ray, player);
        if(result != null && ray.getOrigin().distance(result.getContactPoint()) <= range){
            return result;
        }else{
            return null;
        }
    }
    
    // Mouse Collision:
    private static Vector3f getWorldDir(Vector3f loc, Vector2f mouseLoc, Camera cam){
        return cam.getWorldCoordinates(mouseLoc, 1f).subtract(loc).normalize();
    }
    private static Vector3f getWorldLoc(Vector2f mouseLoc, Camera cam){
        return cam.getWorldCoordinates(mouseLoc, 0f).clone();
    }
    public static ArrayList<CollisionResult> getMouseTargets(Vector2f mouseLoc, Camera cam, Node node){
        Vector3f loc = getWorldLoc(mouseLoc, cam);
        Vector3f dir = getWorldDir(loc, mouseLoc, cam);
        return getCollisions(node, new Ray(loc, dir));
    }
    public static CollisionResult getMouseTarget(Vector2f mouseLoc, Camera cam, Node node){
        Vector3f loc = getWorldLoc(mouseLoc, cam);
        Vector3f dir = getWorldDir(loc, mouseLoc, cam);
        return getClosestCollision(node, new Ray(loc, dir));
    }
    
    // Helper Functions:
    public static float getDistance(Vector3f player, Vector3f target){
        return target.distance(player);
    }
    public static String[] getPartData(CollisionResult target){
        return target.getGeometry().getParent().getName().split(":");
    }
}
