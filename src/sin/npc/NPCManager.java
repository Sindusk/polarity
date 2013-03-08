package sin.npc;

import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.scene.Node;
import java.util.HashMap;
import sin.animation.Models.NPCModel;
import sin.netdata.NPCData;
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
    public static abstract class NPCTemplate{
        private NPCModel model;
        private Vector3f location;
        private String type;
        
        public NPCTemplate(int id, Node node, Vector3f location, String type){
            this.location = location;
            this.type = type;
            this.model = new NPCModel(id, node, type);
            this.model.setLocalTranslation(location);
        }
        
        public Vector3f getLocation(){
            return location;
        }
        public String getType(){
            return type;
        }
    }
    public static class Grunt extends NPCTemplate{
        public Grunt(int id, Node node, Vector3f location){
            super(id, node, location, "grunt");
        }
    }
    // End Inner Classes
    
    public static Node getNode(){
        return node;
    }
    public static NPCTemplate getNPC(String type, int id){
        if(type.equals("grunt")){
            return grunts.get(id);
        }
        return null;
    }
    
    public static void sendData(HostedConnection conn){
        int i = 0;
        while(i < NUM_GRUNTS){
            conn.send(new NPCData(i, grunts.get(i).getType(), grunts.get(i).getLocation()));
            i++;
        }
    }
    
    private static void createNewNPC(int id, String type, Vector3f loc){
        if(type.equals("grunt")){
            if(id == -1){
                grunts.put(id, new Grunt(NUM_GRUNTS, node, loc));
            }else{
                grunts.put(id, new Grunt(id, node, loc));
            }
        }else{
            T.log("Unknown NPC type: "+type);
        }
    }
    public static void add(int id, String type, Vector3f loc){
        createNewNPC(id, type, loc);
    }
    public static void addNew(String type, Vector3f loc){
        createNewNPC(-1, type, loc);
    }
}
