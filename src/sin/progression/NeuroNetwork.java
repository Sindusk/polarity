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
import sin.progression.Neuro.NeuroConnector;
import sin.progression.Neuro.NeuroCore;
import sin.progression.Neuro.NeuroEmpty;
import sin.progression.Neuro.NeuroLocked;
import sin.progression.Neuro.NeuroSource;
import sin.progression.Neuro.NeuroTemplate;
import sin.tools.N;
import sin.tools.S;
import sin.tools.T;
import sin.tools.T.Vector2i;
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
    private static NeuroTemplate[][] neuro = new NeuroTemplate[NEURO_SIZE][NEURO_SIZE];
    
    private static Geometry highlight;
    private static Geometry mark;
    private static Node node = new Node("NeuroNode");
    
    public static class CoreData{
        private ArrayList<String> data = new ArrayList(1);

        public ArrayList<String> getData(){
            return data;
        }

        private void add(ArrayList<String> otherData){
            ArrayList<String> dataArgs, pdArgs;
            String str;
            int i = 0;
            int n, k;
            while(i < otherData.size()){
                n = 0;
                str = otherData.get(i);
                while(n < data.size()){
                    // Combine alike properties.
                    if(T.getInnerHeader(data.get(n)).equals(T.getInnerHeader(str))){
                        dataArgs = T.getInnerArgs(data.get(n));
                        pdArgs = T.getInnerArgs(str);
                        str = T.getInnerHeader(str)+"[";
                        k = 0;
                        while(k < dataArgs.size() && k < pdArgs.size()){
                            str += Float.toString(Float.parseFloat(dataArgs.get(k)) + Float.parseFloat(pdArgs.get(k)));
                            k++;
                            if(k < dataArgs.size() && k < pdArgs.size()){
                                str += ";";
                            }
                        }
                        str += "]";
                        data.set(n, str);
                        break;
                    }
                    n++;
                }
                // Add if the property does not yet exist in data.
                if(n >= data.size()){
                    data.add(str);
                }
                i++;
            }
        }
        public void add(PulseData pd){
            add(pd.getData());
        }
        public void add(CoreData cd){
            add(cd.getData());
        }
    }
    public static class PulseData{
        private ArrayList<String> data;
        private boolean split = false;
        
        private void clearOuts(){
            int i = 0;
            while(i < data.size()){
                if(T.getHeader(data.get(i)).equals("outs")){
                    data.remove(data.get(i));
                }
                i++;
            }
        }
        public PulseData(ArrayList<String> data){
            this.data = (ArrayList<String>) data.clone();
            clearOuts();
        }
        
        public boolean needsSplit(){
            return split;
        }
        public ArrayList<String> getData(){
            return data;
        }
        
        public void setSplit(boolean split){
            this.split = split;
        }
        
        public void add(PulseData other){
            ArrayList<String> otherData = other.getData();
            String curData;
            int i = 0;
            int n;
            while(i < otherData.size()){
                n = 0;
                curData = otherData.get(i);
                while(n < data.size()){
                    // Combine alike properties
                    if(T.getInnerHeader(data.get(n)).equals(T.getInnerHeader(curData))){
                        data.add(curData);
                        otherData.remove(curData);
                        break;
                    }
                    n++;
                }
                i++;
            }
        }
    }
    public static class PulseHandler{
        private ArrayList<Pulse> pulses = new ArrayList(1);
        
        public static class Pulse{
            private Vector2i loc;
            private Vector2i dir;
            private PulseData data;
            
            public Pulse(Vector2i loc, Vector2i dir){
                this.data = new PulseData(N.getSourceData(neuro[loc.x][loc.y]));
                this.loc = loc.add(dir);
                this.dir = dir;
            }
            
            public Vector2i getDirection(){
                return dir;
            }
            
            private Vector2i canConnect(){
                ArrayList<Vector2i> outs = N.obtainOuts(neuro[loc.x][loc.y]);
                if(outs == null){
                    return null;
                }
                int i = 0;
                while(i < outs.size()){
                    if(outs.get(i).equalsInverted(dir)){
                        return outs.get(i);
                    }
                    i++;
                }
                return null;
            }
            public PulseData execute(){
                Vector2i in = canConnect();
                if(in != null){
                    ArrayList<Vector2i> outs = N.obtainOuts(neuro[loc.x][loc.y]);
                    if(outs == null){
                        return null;
                    }
                    outs.remove(in);
                    if(outs.size() == 1){
                        dir = outs.get(0);
                        if(neuro[loc.x][loc.y] instanceof NeuroConnector){
                            loc.addLocal(dir);
                            return execute();
                        }
                    }else if(outs.size() > 1){
                        data.setSplit(true);
                    }
                }else if(neuro[loc.x][loc.y] instanceof NeuroCore){
                    return data;
                }
                return null;
            }
        }
        
        public PulseHandler(int x, int y){
            ArrayList<Vector2i> outs = N.obtainOuts(neuro[x][y]);
            if(outs == null){
                T.log("PulseHandler (ERROR 1)");
                return;
            }
            int i = 0;
            while(i < outs.size()){
                pulses.add(new Pulse(new Vector2i(x, y), outs.get(i).clone()));
                i++;
            }
        }
        
        public CoreData execute(){
            CoreData data = new CoreData();
            PulseData temp;
            int i = 0;
            while(i < pulses.size()){
                temp = pulses.get(i).execute();
                if(temp != null){
                    if(temp.needsSplit()){
                        // SPLIT then send more pulses
                    }else{
                        data.add(temp);
                    }
                }
                i++;
            }
            return data;
        }
    }
    
    public static Node getNode(){
        return node;
    }
    
    private static void calculateNetwork(){
        CoreData data = new CoreData();
        int x = 0;
        int y;
        PulseHandler handler;
        while(x < NEURO_SIZE){
            y = 0;
            while(y < NEURO_SIZE){
                if(neuro[x][y] instanceof NeuroSource){
                    handler = new PulseHandler(x, y);
                    data.add(handler.execute());
                }
                y++;
            }
            x++;
        }
        stats.updateStat("Health", data.getData().toString());
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
        ArrayList<String> data = T.getArgs(mark.getName());
        int x = Integer.parseInt(data.get(0));
        int y = Integer.parseInt(data.get(1));
        if(name.equals("rotate")){
            T.log("Rotating: "+x+", "+y);
            neuro[x][y].rotate();
        }else if(name.equals("connector")){
            T.log("Adding connector: "+x+", "+y);
            neuro[x][y].destroy(); // Destroy old geometry.
            ArrayList<Vector2i> outs = new ArrayList(1);
            outs.add(new Vector2i(0, 1));
            outs.add(new Vector2i(0, -1));
            neuro[x][y] = new NeuroConnector(new Vector2i(x, y), outs);
        }else if(name.equals("corner")){
            T.log("Adding corner: "+x+", "+y);
            neuro[x][y].destroy();
            ArrayList<Vector2i> outs = new ArrayList(1);
            outs.add(new Vector2i(0, 1));
            outs.add(new Vector2i(-1, 0));
            neuro[x][y] = new NeuroConnector(new Vector2i(x, y), outs);
        }else if(name.equals("conn3way")){
            T.log("Adding 3-way: "+x+", "+y);
            neuro[x][y].destroy();
            ArrayList<Vector2i> outs = new ArrayList(1);
            outs.add(new Vector2i(0, 1));
            outs.add(new Vector2i(1, 0));
            outs.add(new Vector2i(-1, 0));
            neuro[x][y] = new NeuroConnector(new Vector2i(x, y), outs);
        }else if(name.equals("conn4way")){
            T.log("Adding 4-way: "+x+", "+y);
            neuro[x][y].destroy();
            ArrayList<Vector2i> outs = new ArrayList(1);
            outs.add(new Vector2i(0, 1));
            outs.add(new Vector2i(1, 0));
            outs.add(new Vector2i(0, -1));
            outs.add(new Vector2i(-1, 0));
            neuro[x][y] = new NeuroConnector(new Vector2i(x, y), outs);
        }else if(name.equals("source")){
            T.log("Adding source: "+x+", "+y);
            neuro[x][y].destroy();
            ArrayList<Vector2i> outs = new ArrayList(1);
            outs.add(new Vector2i(0, -1));
            ArrayList<String> source = new ArrayList(1);
            source.add("damage[1]");
            neuro[x][y] = new NeuroSource(new Vector2i(x, y), outs, source);
        }else if(name.equals("unlock")){
            T.log("Unlocking: "+x+", "+y);
            neuro[x][y].destroy();
            neuro[x][y] = new NeuroEmpty(new Vector2i(x, y));
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
            ArrayList<String> args = T.getArgs(name);
            int x = Integer.parseInt(args.get(0));
            int y = Integer.parseInt(args.get(1));
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
            ArrayList<String> args = T.getArgs(name);
            int x = Integer.parseInt(args.get(0));
            int y = Integer.parseInt(args.get(1));
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
                ArrayList<String> data = T.getArgs(name);
                int x = Integer.parseInt(data.get(0));
                int y = Integer.parseInt(data.get(1));
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
                    //neuro[n][(NEURO_SIZE-1)-i] = new NeuroTemplate(T.getHeader(data[n]), T.getArgs(data[0]));
                    neuro[n][(NEURO_SIZE-1)-i] = Neuro.createNeuro(new Vector2i(n, (NEURO_SIZE-1)-i), T.getHeader(data[n]), T.getArgs(data[0]));
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
                    neuro[x][y] = new NeuroCore(new Vector2i(x, y));
                }else if((x >= 3 && x <= 7) && (y >= 3 && y <= 7) &&
                        !(x == 3 && y == 3) && !(x == 3 && y == 7) &&
                        !(x == 7 && y == 3) && !(x == 7 && y == 7)){
                    neuro[x][y] = new NeuroEmpty(new Vector2i(x, y));
                }else{
                    neuro[x][y] = new NeuroLocked(new Vector2i(x, y));
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
        stats = new StatsDisplay(data.length, new Vector3f(x, y, 0), 0.22f, "Ethno26", -0.27f, 4.5f);
        stats.setData(data);
        node.attachChild(stats.getNode());
    }
    
    public static void initialize(){
        createNeuroMenu(4f, 0f);
        createNeuroStats(1.75f, 2.75f);
        node.attachChild(Neuro.getNode());
        Neuro.getNode().setLocalTranslation(GRID_OFFSET);
        
        // Create highlight and mark geometries:
        highlight = CG.createBox(node, "node(5,5)", new Vector3f(Neuro.NEURO_SCALE*0.5f, Neuro.NEURO_SCALE*0.5f, 0.02f),
                neuro[5][5].getGeometry().getLocalTranslation().add(GRID_OFFSET), new ColorRGBA(1, 1, 1, 0.20f));
        mark = CG.createBox(node, "node(5,5)", new Vector3f(Neuro.NEURO_SCALE*0.5f, Neuro.NEURO_SCALE*0.5f, 0.02f),
                neuro[5][5].getGeometry().getLocalTranslation().add(GRID_OFFSET), new ColorRGBA(1, 1, 1, 0.35f));
    }
}
