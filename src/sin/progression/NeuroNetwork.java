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
import java.util.ArrayList;
import sin.character.CharacterScreen;
import sin.geometry.SinText.Alignment;
import sin.hud.ContextMenu;
import sin.hud.Menu;
import sin.hud.StatsDisplay;
import sin.hud.Tooltip;
import sin.progression.Neuro.NeuroType;
import sin.tools.N;
import sin.tools.S;
import sin.tools.T;
import sin.world.CG;

/**
 * NeuroNetwork - Class managing the Neuro Network progression system.
 * @author SinisteRing
 */
public class NeuroNetwork {
    private static final int NEURO_SIZE = 11;
    private static final Vector3f GRID_OFFSET = new Vector3f(-3, 0, 0);
    
    private static Tooltip tooltip = new Tooltip(new Vector3f(100, 50, 1), new Vector3f(0, 0, 0), new ColorRGBA(0.6f, 0.6f, 0.6f, 1), ColorRGBA.Black);
    private static ContextMenu contextMenu = new ContextMenu(0.235f, Vector3f.ZERO, "Batman26", 1.6f, new ColorRGBA(23/255f, 92/255f, 12/255f, 1), ColorRGBA.Cyan);
    private static Menu neuroMenu;
    private static StatsDisplay stats;
    private static Neuro[][] neuro = new Neuro[NEURO_SIZE][NEURO_SIZE];
    
    private static Geometry highlight;
    private static Geometry mark;
    private static Node node = new Node("NeuroNode");
    
    public static Node getNode(){
        return node;
    }
    
    private static void calculateNetwork(){
        int x = 0;
        int y;
        while(x < NEURO_SIZE){
            y = 0;
            while(y < NEURO_SIZE){
                neuro[x][y].pulse();
                y++;
            }
            x++;
        }
    }
    
    public static void markNeuron(int x, int y){
        mark.setName(neuro[x][y].getGeometry().getName());
        mark.setLocalTranslation(neuro[x][y].getGeometry().getLocalTranslation().add(GRID_OFFSET));
    }
    public static void highlightNeuron(int x, int y){
        highlight.setName(neuro[x][y].getGeometry().getName());
        highlight.setLocalTranslation(neuro[x][y].getGeometry().getLocalTranslation().add(GRID_OFFSET));
        if(!node.hasChild(highlight)){
            node.attachChild(highlight);
        }
    }
    public static void updateTooltip(Vector2f mouseLoc, int x, int y){
        tooltip.updateLocation(mouseLoc);
        tooltip.setHeader(neuro[x][y].getHeader());
        tooltip.setText(neuro[x][y].getDescription());
    }
    
    private static void contextAction(String name){
        String[] data = T.getArgs(mark.getName());
        int x = Integer.parseInt(data[0]);
        int y = Integer.parseInt(data[1]);
        if(name.equals("connector")){
            T.log("Adding connector: "+x+", "+y);
            neuro[x][y].setType(NeuroType.CONNECTOR);
            neuro[x][y].updateGeometry();
        }else if(name.equals("source")){
            T.log("Adding source: "+x+", "+y);
            neuro[x][y].setType(NeuroType.SOURCE);
            neuro[x][y].updateGeometry();
        }else if(name.equals("unlock")){
            T.log("Unlocking: "+x+", "+y);
            neuro[x][y].setType(NeuroType.EMPTY);
            neuro[x][y].updateGeometry();
        }
        calculateNetwork();
        contextMenu.destroy();
    }
    public static void handleRightClick(Vector2f mouseLoc){
        CollisionResult target = CharacterScreen.getMouseTarget(mouseLoc, S.getCamera(), node);
        if(target == null){
            return;
        }
        String name = target.getGeometry().getName();
        if(T.getHeader(name).equals("node")){
            String[] args = T.getArgs(name);
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            markNeuron(x, y);
            ArrayList<String[]> data = N.getNeuroOptions(neuro[x][y]);
            contextMenu.setData(data);
            contextMenu.setLocalTranslation(target.getContactPoint().add(0, 0, 0.01f));
            node.attachChild(contextMenu.getNode());
        }
    }
    public static void action(CollisionResult target){
        if(target == null){
            return;
        }
        String name = target.getGeometry().getName();
        if(contextMenu.isOption(name)){
            contextAction(name);
        }else if(T.getHeader(name).equals("node")) {
            String[] args = T.getArgs(name);
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            NeuroNetwork.markNeuron(x, y);
            contextMenu.destroy();
        }
    }
    public static void update(Vector2f mouseLoc){
        CollisionResult target = CharacterScreen.getMouseTarget(mouseLoc, S.getCamera(), node);
        String name;
        if(target != null){
            name = target.getGeometry().getName();
            if(contextMenu.isOption(name)){
                contextMenu.highlightBox(name);
                tooltip.setVisible(CharacterScreen.getGUI(), false);
                highlight.removeFromParent();
            }else if(T.getHeader(name).equals("node")){
                String[] data = T.getArgs(name);
                int x = Integer.parseInt(data[0]);
                int y = Integer.parseInt(data[1]);
                if(tooltip.isVisible()){
                    updateTooltip(mouseLoc, x, y);
                    highlightNeuron(x, y);
                }else{
                    tooltip.setVisible(CharacterScreen.getGUI(), true);
                }
                contextMenu.removeHighlight();
            }
        }else{
            if(tooltip.isVisible()){
                tooltip.setVisible(CharacterScreen.getGUI(), false);
                highlight.removeFromParent();
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
                    neuro[n][(NEURO_SIZE-1)-i] = new Neuro(T.getHeader(data[n]), T.getArgs(data[n]));
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
                    neuro[x][y].setType(NeuroType.CORE);
                }else if((x >= 3 && x <= 7) && (y >= 3 && y <= 7) &&
                        !(x == 3 && y == 3) && !(x == 3 && y == 7) &&
                        !(x == 7 && y == 3) && !(x == 7 && y == 7)){
                    neuro[x][y].setType(NeuroType.EMPTY);
                }else{
                    neuro[x][y].setType(NeuroType.LOCKED);
                }
                bw.write(neuro[x][y].getWritable());
                y++;
                if(y != NEURO_SIZE){
                    bw.write(":");
                }
            }
            bw.write("\n");
            x++;
        }
    }
    
    private static void createNeuroGrid(){
        int x = 0;
        int y;
        while(x < NEURO_SIZE){
            y = 0;
            while(y < NEURO_SIZE){
                neuro[x][y].createGeometry(x, y);
                y++;
            }
            x++;
        }
    }
    private static void createNeuroMenu(float x, float y){
        neuroMenu = new Menu(x, y);
        // Header Text:
        neuroMenu.addLabel(0.4f, new Vector3f(0, 3.75f, 0), "Batman26", "Neuro Network", ColorRGBA.Green, Alignment.Center);
        node.attachChild(neuroMenu.getNode());
    }
    private static void createNeuroStats(float x, float y){
        String[][] data = {
            {"Health", "1,000"},
            {"Shields", "1,000"},
            {"Damage Increase", "+0%"},
            {"Jump Height", "+0%"},
            {"Critical Chance", "0%"},
            {"Critical Damage", "1.50x"}};
        stats = new StatsDisplay(data.length, new Vector3f(x, y, 0), 0.22f, "Batman26", -0.27f, 3.5f);
        stats.setData(data);
        node.attachChild(stats.getNode());
    }
    
    public static void initialize(){
        createNeuroGrid();
        createNeuroMenu(4f, 0f);
        createNeuroStats(2.25f, 2.75f);
        node.attachChild(Neuro.getNode());
        Neuro.getNode().setLocalTranslation(GRID_OFFSET);
        
        // Create highlight and mark geometries:
        highlight = CG.createBox(node, "node(5,5)", new Vector3f(Neuro.NEURO_SCALE*0.5f, Neuro.NEURO_SCALE*0.5f, 0.02f),
                neuro[5][5].getGeometry().getLocalTranslation().add(GRID_OFFSET), new ColorRGBA(1, 1, 1, 0.20f));
        mark = CG.createBox(node, "node(5,5)", new Vector3f(Neuro.NEURO_SCALE*0.5f, Neuro.NEURO_SCALE*0.5f, 0.02f),
                neuro[5][5].getGeometry().getLocalTranslation().add(GRID_OFFSET), new ColorRGBA(1, 1, 1, 0.35f));
    }
}
