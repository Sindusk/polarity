package sin.progression;

import com.jme3.collision.CollisionResult;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import sin.character.CharacterScreen;
import sin.geometry.SinText.Alignment;
import sin.hud.Menu;
import sin.hud.StatsDisplay;
import sin.hud.Tooltip;
import sin.tools.S;
import sin.tools.T;
import sin.world.CG;

/**
 * NeuroNetwork - Class managing the Neuro Network progression system.
 * @author SinisteRing
 */
public class NeuroNetwork {
    private static final int NEURO_SIZE = 11;
    private static final float NEURO_SCALE = 0.7f;
    private static final float NEURO_BUFFER = 0f;
    
    private static Menu neuroMenu;
    private static String[][] neuros = new String[NEURO_SIZE][NEURO_SIZE];
    private static Geometry[][] neuron = new Geometry[NEURO_SIZE][NEURO_SIZE];
    private static Geometry highlight;
    private static Geometry mark;
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
    
    public static void markNeuron(int x, int y){
        mark.setName(x+","+y);
        mark.setLocalTranslation(neuron[x][y].getLocalTranslation());
    }
    public static void highlightNeuron(int x, int y){
        highlight.setName(x+","+y);
        highlight.setLocalTranslation(neuron[x][y].getLocalTranslation());
    }
    public static void updateTooltip(Vector2f mouseLoc, Tooltip t, int x, int y){
        t.updateLocation(mouseLoc);
        t.setHeader(getNeuroHeader(x, y));
        t.setText(getNeuroDescription(x, y));
    }
    
    private static void menuAction(String name){
        String[] data = mark.getName().split(",");
        int x = Integer.parseInt(data[0]);
        int y = Integer.parseInt(data[1]);
        if(name.equals("unlock")){
            if(neuros[x][y].equals("x")){
                T.log("Unlocking neuro "+x+", "+y);
                neuros[x][y] = "i";
                neuron[x][y].setMaterial(T.getMaterial(getNeuroMaterialPath(x, y)));
            }else{
                T.log("Neuro is not locked.");
            }
        }
    }
    public static void action(CollisionResult target){
        if(target == null){
            return;
        }
        String name = target.getGeometry().getName();
        if(neuroMenu.isButton(name)){
            menuAction(name);
        }else if(!neuroMenu.isElement(name)){
            String[] data = target.getGeometry().getName().split(",");
            int x = Integer.parseInt(data[0]);
            int y = Integer.parseInt(data[1]);
            NeuroNetwork.markNeuron(x, y);
        }
    }
    public static void update(Vector2f mouseLoc, Tooltip tooltip){
        CollisionResult target = CharacterScreen.getMouseTarget(mouseLoc, S.getCamera(), node);
        if(target != null && !neuroMenu.isElement(target.getGeometry().getName())){
            String[] data = target.getGeometry().getName().split(",");
            int x = Integer.parseInt(data[0]);
            int y = Integer.parseInt(data[1]);
            if(tooltip.isVisible()){
                NeuroNetwork.updateTooltip(mouseLoc, tooltip, x, y);
                NeuroNetwork.highlightNeuron(x, y);
            }else{
                tooltip.setVisible(CharacterScreen.getGUI(), true);
            }
        }else{
            if(tooltip.isVisible()){
                tooltip.setVisible(CharacterScreen.getGUI(), false);
            }
        }
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
    public static void writeNewNeuroNet(BufferedWriter bw) throws IOException{
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
    
    private static void createNeuroMenu(float x_offset){
        neuroMenu = new Menu(x_offset, 0);
        // Header Text:
        neuroMenu.addLabel(0.4f, new Vector3f(0, 3.75f, 0), "Batman26", "Neuro Network", ColorRGBA.Green, Alignment.Center);
        // Stats:
        StatsDisplay stats = new StatsDisplay(15, new Vector3f(x_offset-2.5f, 2.75f, 0), 0.2f, -0.25f, 3f);
        stats.setStat(0, "Health:", "1,000");
        stats.setStat(1, "Shields:", "1,000");
        node.attachChild(stats.getNode());
        //neuroMenu.addLabel(0.2f, new Vector3f(-2.5f, 2.75f, 0), "OCRAStd", "A wild stat appears!", ColorRGBA.Cyan, Alignment.Left);
        //neuroMenu.addLabel(0.2f, new Vector3f(-2.5f, 2.5f, 0), "OCRAStd", "A new and separate stat!", ColorRGBA.Orange, Alignment.Left);
        node.attachChild(neuroMenu.getNode());
        CharacterScreen.getGUI().attachChild(neuroMenu.getGUI());
    }
    private static void createNeuroGrid(float x_offset){
        int x = 0;
        int y;
        while(x < NEURO_SIZE){
            y = 0;
            while(y < NEURO_SIZE){
                neuron[x][y] = CG.createBox(node, x+","+y, new Vector3f(NEURO_SCALE*0.5f, NEURO_SCALE*0.5f, 0.01f),
                        new Vector3f((x*NEURO_SCALE)+(-NEURO_SCALE*5f)+((x-5)*NEURO_BUFFER)+x_offset, (y*NEURO_SCALE)+(-NEURO_SCALE*5f)+((y-5)*NEURO_BUFFER), 0),
                        getNeuroMaterialPath(x, y), new Vector2f(1, 1));
                y++;
            }
            x++;
        }
    }
    public static void initialize(){
        node = new Node("NeuroNode");
        createNeuroGrid(-3f);
        createNeuroMenu(4f);
        
        // Create highlight and mark geometries:
        highlight = CG.createBox(node, "5,5", new Vector3f(NEURO_SCALE*0.5f, NEURO_SCALE*0.5f, 0.02f),
                neuron[5][5].getLocalTranslation(), new ColorRGBA(1, 1, 1, 0.25f));
        mark = CG.createBox(node, "5,5", new Vector3f(NEURO_SCALE*0.5f, NEURO_SCALE*0.5f, 0.02f),
                neuron[5][5].getLocalTranslation(), new ColorRGBA(1, 1, 1, 0.5f));
    }
}
