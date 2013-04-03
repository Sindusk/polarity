package sin.progression;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import sin.tools.T;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class Neuro {
    public static final float NEURO_SCALE = 0.7f;
    private static final float NEURO_BUFFER = 0f;
    
    private static Node node = new Node("NeuroNode");
    private Geometry neuron;
    private NeuroType type;
    private String[] data;
    private String header;
    private String description;
    
    public enum NeuroType{
        CONNECTOR("connector"),
        CORE("core"),
        EMPTY("empty"),
        LOCKED("locked"),
        SOURCE("source");
        
        private final String type;
        
        NeuroType(String type){
            this.type = type;
        }
        String getType(){
            return type;
        }
        static NeuroType convert(String s){
            if(s.equals(CONNECTOR.type)){
                return NeuroType.CONNECTOR;
            }else if(s.equals(CORE.type)){
                return NeuroType.CORE;
            }else if(s.equals(LOCKED.type)){
                return NeuroType.LOCKED;
            }else if(s.equals(SOURCE.type)){
                return NeuroType.SOURCE;
            }
            return NeuroType.EMPTY;
        }
    }
    
    public Neuro(String type, String[] data){
        this.type = NeuroType.convert(type);
        this.data = data;
        header = updateHeader();
        description = updateDescription();
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
    public NeuroType getType(){
        return type;
    }
    public String getWritable(){
        String writable = type.getType()+"(";
        if(data != null){
            int i = 0;
            while(i < data.length){
                writable += data[i]+",";
                i++;
            }
        }
        writable += ")";
        return writable;
    }
    
    public void setType(NeuroType type){
        this.type = type;
        this.data = null;
        header = updateHeader();
        description = updateDescription();
    }
    public void setData(String[] data){
        this.data = data;
        header = updateHeader();
        description = updateDescription();
    }
    
    private String updateHeader(){
        if(type.equals(NeuroType.CONNECTOR)){
            return "Connector";
        }else if(type.equals(NeuroType.SOURCE)){
            return "Source (Damage)";
        }else if(type.equals(NeuroType.LOCKED)){
            return "Locked Node";
        }else if(type.equals(NeuroType.EMPTY)){
            return "Empty Node";
        }else if(type.equals(NeuroType.CORE)){
            return "Neuro Core";
        }
        T.log("Error @ updateHeader: No header found for ("+type.getType()+")");
        return "NULL";
    }
    private String updateDescription(){
        if(type.equals(NeuroType.CONNECTOR)){
            return "Connects nodes.";
        }else if(type.equals(NeuroType.SOURCE)){
            return "+1% Damage";
        }else if(type.equals(NeuroType.LOCKED)){
            return "Level up to unlock.";
        }else if(type.equals(NeuroType.EMPTY)){
            return "Fill with an\nobject to use.";
        }else if(type.equals(NeuroType.CORE)){
            return "Connect nodes to\nthe core to use.";
        }
        T.log("Error @ getNeuroDescription: No description found for ("+type.getType()+")");
        return "NULL";
    }
    public void updateGeometry(){
        neuron.setMaterial(T.getMaterial(T.getNeuroPath(type.getType())));
    }
    
    public void pulse(){
        // implement
    }
    
    public void createGeometry(int x, int y){
        neuron = CG.createBox(node, "node("+x+","+y+")", new Vector3f(NEURO_SCALE*0.5f, NEURO_SCALE*0.5f, 0f),
            new Vector3f((x*NEURO_SCALE)+(-NEURO_SCALE*5f)+((x-5)*NEURO_BUFFER), (y*NEURO_SCALE)+(-NEURO_SCALE*5f)+((y-5)*NEURO_BUFFER), 0),
            T.getNeuroPath(type.getType()), new Vector2f(1, 1));
    }
}
