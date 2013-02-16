package sin.weapons;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.tools.T;
import sin.world.World.CG;

/**
 * Tracer Manager - Used mainly for testing, handles the creation and management of tracers.
 * @author SinisteRing
 */
public class TracerManager {
    /** Constant Variables: **/
    private static final float TRACER_WIDTH = 0.25f;
    /** End Constants **/
    
    private static GameClient app;
    
    private static Node node = new Node("Tracers");
    private static Tracer[] tracers = new Tracer[250];
    private static boolean enabled = false;
    
    private static class Tracer{
        private Geometry tracer;
        private boolean used = false;
        
        public Tracer(){
            //
        }
        
        public boolean inUse(){
            return used;
        }
        
        public void create(Vector3f start, Vector3f end){
            tracer = CG.createLine(node, "", TRACER_WIDTH, start, end, ColorRGBA.Blue);
        }
        public void destroy(){
            if(tracer != null && node.hasChild(tracer)){
                node.detachChild(tracer);
            }
        }
    }
    
    public static int findEmptyTracer(){
        int i = 0;
        while(i < tracers.length){
            if(tracers[i] == null || !tracers[i].inUse()){
                return i;
            }
            i++;
        }
        return -1;
    }
    public static void add(Vector3f start, Vector3f end){
        int i = findEmptyTracer();
        if(i != -1){
            if(tracers[i] == null){
                tracers[i] = new Tracer();
            }
            tracers[i].create(start, end);
        }else{
            T.log("Tracers are overloaded!");
        }
    }
    public static void add(Ray ray, float range){
        Vector3f end = ray.getDirection().mult(range).add(ray.getOrigin());
        add(ray.getOrigin(), end);
    }
    public static void clear(){
        int i = 0;
        while(i < tracers.length){
            if(tracers[i] == null){
                break;
            }
            tracers[i].destroy();
            i++;
        }
    }
    public static void toggle(){
        if(enabled){
            app.getRoot().detachChild(node);
            enabled = false;
        }else{
            app.getRoot().attachChild(node);
            enabled = true;
        }
    }
    
    public static void initialize(GameClient app){
        TracerManager.app = app;
    }
}
