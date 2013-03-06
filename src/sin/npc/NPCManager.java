package sin.npc;

import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.scene.Node;
import java.util.HashMap;
import sin.animation.Models.NPCModel;
import sin.netdata.NPCData;
import sin.tools.S;
import sin.tools.T;

/**
 * NPCManager - Manages all NPC's.
 * @author SinisteRing
 */
public class NPCManager {
    private static HashMap<Integer, NPCTemplate> NPC = new HashMap();
    private static Node node = new Node("NPCNode");
    private static int NUM_NPC = 0;
    
    // Inner Classes:
    public static abstract class NPCTemplate{
        private NPCModel model;
        private Vector3f location;
        private String type;
        
        public NPCTemplate(Node node, Vector3f location, String type, int id){
            this.location = location;
            this.type = type;
            this.model = new NPCModel(node, type, id);
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
        public Grunt(Node node, Vector3f location, int id){
            super(node, location, "grunt", id);
        }
    }
    // End Inner Classes
    
    public static Node getNode(){
        return node;
    }
    
    public static void sendData(HostedConnection conn){
        int i = 0;
        while(i < NUM_NPC){
            conn.send(new NPCData(i, NPC.get(i).getType(), NPC.get(i).getLocation()));
            i++;
        }
    }
    
    private static NPCTemplate createNewNPC(int id, String type, Vector3f loc){
        if(type.equals("grunt")){
            return new Grunt(node, loc, id);
        }
        T.log("Unknown NPC type: "+type);
        return null;
    }
    public static boolean add(int id, String type, Vector3f loc){
        if(NPC.get(id) != null){
            return false;
        }
        NPC.put(id, createNewNPC(id, type, loc));
        if(id > NUM_NPC){
            NUM_NPC = id;
        }
        return true;
    }
    public static int addNew(String type, Vector3f loc){
        NPC.put(NUM_NPC, createNewNPC(NUM_NPC, type, loc));
        NUM_NPC++;
        return NUM_NPC-1;
    }
}
