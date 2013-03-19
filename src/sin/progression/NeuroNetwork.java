package sin.progression;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import sin.tools.T;
import sin.world.CG;

/**
 * NeuroNetwork - Class managing the Neuro Network progression system.
 * @author SinisteRing
 */
public class NeuroNetwork {
    private static final int NEURO_SIZE = 11;
    private static final float NEURO_SCALE = 0.7f;
    private static final float NEURO_BUFFER = 0.05f;
    private static final float X_SHIFT = -3f;
    
    private static String[][] neuros = new String[NEURO_SIZE][NEURO_SIZE];
    private static Geometry[][] neuron = new Geometry[NEURO_SIZE][NEURO_SIZE];
    private static Node node;
    
    public static Node getNode(){
        return node;
    }
    
    private static String getNeuroMaterialPath(int x, int y){
        if(neuros[x][y].equals("x")){
            return T.getNeuroPath("locked");
        }else if(neuros[x][y].equals("i")){
            return T.getNeuroPath("empty");
        }else if(neuros[x][y].equals("c")){
            return T.getNeuroPath("core");
        }
        return T.getMaterialPath("default");
    }
    public static String getNeuroDescription(int x, int y){
        if(neuros[x][y].equals("x")){
            return "Level up to unlock.";
        }else if(neuros[x][y].equals("i")){
            return "Fill with an\nobject to use.";
        }else if(neuros[x][y].equals("c")){
            return "Connect nodes to\nthe core to use.";
        }
        T.log("Error @ getNeuroDescription!");
        return "NULL";
    }
    public static String getNeuroHeader(int x, int y){
        if(neuros[x][y].equals("x")){
            return "Locked Node";
        }else if(neuros[x][y].equals("i")){
            return "Empty Node";
        }else if(neuros[x][y].equals("c")){
            return "Neuro Core";
        }
        T.log("Error! No proper string found for neuros["+x+"]["+y+"]! ("+neuros[x][y]+")");
        return "NULL";
    }
    
    public static void highlightNeuron(int x, int y){
        //neuron[x][y].getMaterial().getAdditionalRenderState().setWireframe(true);
    }
    
    public static void readNeuroNet(BufferedReader br) throws IOException{
        String[] data;
        int i = 0;
        int n;
        while(i < NEURO_SIZE){
            if(br.ready()){
                data = br.readLine().split(":");
                n = 0;
                while(n < NEURO_SIZE){
                    neuros[n][(NEURO_SIZE-1)-i] = data[n];
                    n++;
                }
            }
            i++;
        }
    }
    public static void createNewNeuroNet(BufferedWriter bw) throws IOException{
        int x = 0;
        int y;
        while(x < NEURO_SIZE){
            y = 0;
            while(y < NEURO_SIZE){
                if(x == 5 && y == 5){
                    neuros[x][y] = "c";
                }else if((x >= 3 && x <= 7) && (y >= 3 && y <= 7) &&
                        !(x == 3 && y == 3) && !(x == 3 && y == 7) &&
                        !(x == 7 && y == 3) && !(x == 7 && y == 7)){
                    neuros[x][y] = "i";
                }else{
                    neuros[x][y] = "x";
                }
                bw.write(neuros[x][y]);
                y++;
                if(y != NEURO_SIZE){
                    bw.write(":");
                }
            }
            bw.write("\n");
            x++;
        }
    }
    public static void initialize(){
        node = new Node("NeuroNode");
        int x = 0;
        int y;
        while(x < NEURO_SIZE){
            y = 0;
            while(y < NEURO_SIZE){
                neuron[x][y] = CG.createBox(node, x+","+y, new Vector3f(NEURO_SCALE*0.5f, NEURO_SCALE*0.5f, 0.01f),
                        new Vector3f((x*NEURO_SCALE)+(-NEURO_SCALE*5f)+((x-5)*NEURO_BUFFER)+(X_SHIFT), (y*NEURO_SCALE)+(-NEURO_SCALE*5f)+((y-5)*NEURO_BUFFER), 0),
                        getNeuroMaterialPath(x, y), new Vector2f(1, 1));
                y++;
            }
            x++;
        }
    }
}
