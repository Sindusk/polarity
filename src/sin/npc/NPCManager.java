package sin.npc;

import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.scene.Node;
import java.util.HashMap;
import sin.animation.Models.EntityModel;
import sin.netdata.npc.GruntData;
import sin.netdata.npc.EntityData;
import sin.netdata.npc.EntityDeathData;
import sin.netdata.npc.OrganismData;
import sin.tools.S;
import sin.tools.T;

/**
 * NPCManager - Manages all NPC's.
 * @author SinisteRing
 */
public class NPCManager {
    private static HashMap<Integer, Grunt> grunts = new HashMap();
    private static Node node = new Node("NPCNode");
    private static int NUM_GRUNTS = 0;
    
    // Inner Classes:
    public static abstract class Entity{
        private int id;
        private EntityModel model;
        private Vector3f location;
        private String type;
        
        public Entity(int id, Node node, Vector3f location, String type){
            this.id = id;
            this.location = location;
            this.type = type;
            this.model = new EntityModel(id, node, type);
            this.model.setLocalTranslation(location);
        }
        
        public int getID(){
            return id;
        }
        public Vector3f getLocation(){
            return location;
        }
        public String getType(){
            return type;
        }
        
        public void destroy(){
            this.model.destroy();
            if(S.getServer() != null){
                S.getServer().broadcast(new EntityDeathData(id, type));
            }
        }
    }
    public static abstract class Organism extends Entity{
        private float health;
        private float maxHealth;
        
        public Organism(int id, Node node, Vector3f location, String type, float health, float maxHealth){
            super(id, node, location, type);
            this.health = health;
            this.maxHealth = maxHealth;
        }
        
        public void damage(float damage){
            health -= damage;
            if(health <= 0){
                this.destroy();
            }
        }
        
        public float getMaxHealth(){
            return maxHealth;
        }
        public float getHealth(){
            return health;
        }
    }
    public static class Grunt extends Organism{
        private static final String name = "grunt";
        public Grunt(int id, Node node, Vector3f location, float health, float maxHealth){
            super(id, node, location, name, health, maxHealth);
        }
        public static String getName(){
            return name;
        }
    }
    // End Inner Classes
    
    public static Node getNode(){
        return node;
    }
    public static Entity getNPC(String type, int id){
        if(type.equals("grunt")){
            return grunts.get(id);
        }
        return null;
    }
    
    public static void damageNPC(int id, String type, float damage){
        if(type.equals(Grunt.getName())){
            grunts.get(id).damage(damage);
        }
    }
    public static void destroyNPC(EntityDeathData d){
        if(d.getType().equals(Grunt.getName())){
            grunts.get(d.getID()).destroy();
        }
    }
    
    public static void sendData(HostedConnection conn){
        Grunt g;
        int i = 0;
        while(i < NUM_GRUNTS){
            g = grunts.get(i);
            conn.send(new GruntData(i, g.getType(), g.getLocation(), g.getHealth(), g.getMaxHealth()));
            i++;
        }
    }
    public static void addGrunt(GruntData d){
        grunts.put(d.getID(), new Grunt(d.getID(), node, d.getLocation(), d.getHealth(), d.getMaxHealth()));
    }
    
    private static void createNewNPC(int id, String type, Vector3f loc){
        if(type.equals("grunt")){
            if(id == -1){
                grunts.put(NUM_GRUNTS, new Grunt(NUM_GRUNTS, node, loc, 500, 500));
                NUM_GRUNTS++;
            }else{
                grunts.put(id, new Grunt(id, node, loc, 500, 500));
            }
        }else{
            T.log("Unknown NPC type: "+type);
        }
    }
    public static void add(int id, String type, Vector3f loc){
        createNewNPC(id, type, loc);
    }
    public static void add(EntityData m){
        if(m instanceof OrganismData){
            if(m instanceof GruntData){
                addGrunt((GruntData) m);
            }
        }
    }
    public static void addNew(String type, Vector3f loc){
        createNewNPC(-1, type, loc);
    }
}
