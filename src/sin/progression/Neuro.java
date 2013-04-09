package sin.progression;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.tools.N;
import sin.tools.T;
import sin.tools.T.Vector2i;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class Neuro {
    public static final float NEURO_SCALE = 0.7f;
    private static final float NEURO_BUFFER = 0f;
    private static Node node = new Node("NeuroNode");
    
    public static Node getNode(){
        return node;
    }
    
    public static abstract class NeuroTemplate{
        private Vector2i loc;
        private Geometry neuron;
        protected String header;
        protected String description;
        
        public NeuroTemplate(Vector2i loc){
            this.loc = loc;
        }
        
        public static Node getNode(){
            return node;
        }
        
        public Geometry getGeometry(){
            return neuron;
        }
        public String getHeader(){
            return header;
        }
        public String getDescription(){
            return description;
        }
        public abstract String getWritable();
        
        public void rotate(){
            float deg = -FastMath.DEG_TO_RAD*90;
            neuron.rotate(0, 0, deg);
        }
        
        protected void createGeometry(String handle){
            neuron = CG.createBox(node, "node("+loc.x+","+loc.y+")", new Vector3f(NEURO_SCALE*0.5f, NEURO_SCALE*0.5f, 0f),
                new Vector3f((loc.x*NEURO_SCALE)+(-NEURO_SCALE*5f)+((loc.x-5)*NEURO_BUFFER), (loc.y*NEURO_SCALE)+(-NEURO_SCALE*5f)+((loc.y-5)*NEURO_BUFFER), 0),
                T.getNeuroPath(handle), new Vector2f(1, 1));
            node.attachChild(neuron);
        }
        public void destroy(){
            neuron.removeFromParent();
        }
    }
    public static abstract class NeuroWithOutlets extends NeuroTemplate{
        protected ArrayList<Vector2i> outlets = new ArrayList(1);
        
        public NeuroWithOutlets(Vector2i loc, ArrayList<Vector2i> outs){
            super(loc);
            this.outlets = outs;
        }
        
        public ArrayList<Vector2i> getOuts(){
            return outlets;
        }
        public String getWritable(){
            String writable = "outs[";
            int i = 0;
            while(i < outlets.size()){
                writable += outlets.get(i).x+";"+outlets.get(i).y;
                i++;
                if(i < outlets.size()){
                    writable += ";";
                }
            }
            writable += "]";
            return writable;
        }
        
        @Override
        public void rotate(){
            super.rotate();
            Vector2i temp;
            int i = 0;
            while(i < outlets.size()){
                temp = outlets.get(i);
                outlets.set(i, new Vector2i(temp.y, -temp.x));
                i++;
            }
        }
    }
    // Classes:
    public static class NeuroConnector extends NeuroWithOutlets{
        public static final String HANDLE = "connector";
        
        private void determineGeometry(){
            if(outlets.size() <= 2){
                if(outlets.get(0).equalsInverted(outlets.get(1))){
                    createGeometry("connector");
                }else{
                    createGeometry("corner");
                }
            }else if(outlets.size() <= 3){
                createGeometry("conn3way"); // still needs rotation
            }else if(outlets.size() <= 4){
                createGeometry("conn4way"); // still needs rotation
            }
        }
        public NeuroConnector(Vector2i loc, ArrayList<Vector2i> outs){
            super(loc, outs);
            this.header = "Connector";
            this.description = "Connects nodes.";
            determineGeometry();
        }
        
        @Override
        public String getWritable(){
            return HANDLE+"("+super.getWritable()+")";
        }
    }
    public static class NeuroCore extends NeuroTemplate{
        public static final String HANDLE = "core";
        
        public NeuroCore(Vector2i loc){
            super(loc);
            this.header = "Neuro Core";
            this.description = "Connect nodes to\nthe core to use.";
            createGeometry(HANDLE);
        }
        
        public String getWritable(){
            return HANDLE+"()";
        }
    }
    public static class NeuroEmpty extends NeuroTemplate{
        public static final String HANDLE = "empty";
        
        public NeuroEmpty(Vector2i loc){
            super(loc);
            this.header = "Empty Node";
            this.description = "Fill with an\nobject to use.";
            createGeometry(HANDLE);
        }
        
        public String getWritable(){
            return HANDLE+"()";
        }
    }
    public static class NeuroLocked extends NeuroTemplate{
        public static final String HANDLE = "locked";
        
        public NeuroLocked(Vector2i loc){
            super(loc);
            this.header = "Locked Node";
            this.description = "Level up to unlock.";
            createGeometry(HANDLE);
        }
        
        public String getWritable(){
            return HANDLE+"()";
        }
    }
    public static class NeuroSource extends NeuroWithOutlets{
        public static final String HANDLE = "source";
        private ArrayList<String> data;
        
        public NeuroSource(Vector2i loc, ArrayList<Vector2i> outs, ArrayList<String> data){
            super(loc, outs);
            this.data = data;
            this.header = "Source";
            this.description = "Provides bonuses \nthrough connections.\n\n+1% damage";
            createGeometry(HANDLE);
        }
        
        public ArrayList<String> getData(){
            return data;
        }
        
        @Override
        public String getWritable(){
            String writable = HANDLE+"("+super.getWritable()+",";
            int i = 0;
            while(i < data.size()){
                writable += data.get(i);
                i++;
                if(i < data.size()){
                    writable += ",";
                }
            }
            return writable;
        }
    }
    
    public static NeuroTemplate createNeuro(Vector2i loc, String type, ArrayList<String> args){
        if(type.equals(NeuroConnector.HANDLE)){
            ArrayList<Vector2i> outs = N.parseOuts(args);
            return new NeuroConnector(loc, outs);
        }else if(type.equals(NeuroCore.HANDLE)){
            return new NeuroCore(loc);
        }else if(type.equals(NeuroEmpty.HANDLE)){
            return new NeuroEmpty(loc);
        }else if(type.equals(NeuroLocked.HANDLE)){
            return new NeuroLocked(loc);
        }else if(type.equals(NeuroSource.HANDLE)){
            ArrayList<Vector2i> outs = N.parseOuts(args);
            return new NeuroSource(loc, outs, args);
        }
        return new NeuroLocked(loc);
    }
}
