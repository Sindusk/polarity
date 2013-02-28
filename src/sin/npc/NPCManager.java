package sin.npc;

import com.jme3.scene.Node;
import sin.GameClient;

/**
 * NPCManager - Manages all NPC's.
 * @author SinisteRing
 */
public class NPCManager {
    private static GameClient app;
    
    public static class NPCTemplate{
        private Node node;
    }
    
    public static void initialize(GameClient app){
        NPCManager.app = app;
    }
}
