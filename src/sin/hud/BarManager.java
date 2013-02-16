package sin.hud;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.HashMap;
import sin.GameClient;
import sin.tools.T;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class BarManager {
    private static GameClient app;
    
    private static DynamicBar[] bars = new DynamicBar[5];
    private static HashMap<BH, Integer> handles = new HashMap();
    
    public static enum BH{
        HEALTH, SHIELDS, AMMO_LEFT, AMMO_RIGHT
    }
    public static class DynamicBar{
        // Constant Variables:
        private static final float BORDER_WIDTH = 2;

        // Instance Variables:
        private boolean used = false;
        private int type;
        private Vector2f loc;
        private Vector3f fillLoc;
        private float width, height;
        private Node bar = new Node();
        private Geometry fill;
        private ColorRGBA color;
        private BitmapText text;
        private int max;
        private float textSize;
        private boolean showMax;

        public DynamicBar(){
            //
        }
        
        public boolean isUsed(){
            return used;
        }

        public void setMax(int max){
            this.max = max;
        }
        public void update(int value){
            //this.value = value;
            float perc = (float) value/max;
            Vector3f newLoc = fillLoc.clone();
            if(type == 0){
                fill.setLocalScale(perc, 1, 1);
                newLoc.addLocal(-(width-(perc*width))/2, 0, 0);
            }else if(type == 1){
                fill.setLocalScale(1, perc, 1);
                newLoc.addLocal(0, -(height-(perc*height))/2, 0);
                text.setBox(new Rectangle(0, height+((Integer.toString(value).length()-3)*textSize/2), width, 15));
            }
            fill.setLocalTranslation(newLoc);
            if(showMax){
                text.setText(value+"/"+max);
            }else{
                text.setText(Integer.toString(value));
            }
        }
        public void create(Node node, int type, Vector2f loc, float width, float height, float textSize,
                ColorRGBA color, int max, boolean showMax){
            this.type = type;
            this.loc = loc;
            this.width = width;
            this.height = height;
            this.color = color;
            this.max = max;
            this.textSize = textSize;
            this.showMax = showMax;
            
            Vector2f topLeft = T.v2f(loc.getX()-(width/2), loc.getY()-(height/2));
            float tlx = topLeft.getX();
            float tly = topLeft.getY();

            ColorRGBA b_color = ColorRGBA.Black;
            // Create Borders:
            World.CG.createLine(bar, "", BORDER_WIDTH, T.v3f(tlx, tly), T.v3f(tlx+width, tly), b_color);
            World.CG.createLine(bar, "", BORDER_WIDTH, T.v3f(tlx, tly), T.v3f(tlx, tly+height), b_color);
            World.CG.createLine(bar, "", BORDER_WIDTH, T.v3f(tlx+width, tly+height), T.v3f(tlx, tly+height), b_color);
            World.CG.createLine(bar, "", BORDER_WIDTH, T.v3f(tlx+width, tly+height), T.v3f(tlx+width, tly), b_color);

            fillLoc = T.v3f(topLeft.getX()+(width/2), topLeft.getY()+(height/2), 1);
            //fill = GenerateFill(width/2, height/2, color);
            fill = World.CG.createBox(bar, "", T.v3f(width/2, height/2), T.v3f(0, 0, 0), color);
            fill.setLocalTranslation(fillLoc);
            bar.attachChild(fill);

            //BitmapFont fnt = assetManager.loadFont("Interface/Fonts/OCRAStd.fnt");
            text = new BitmapText(T.getFont("OCRAStd"), false);
            if(type == 0){
                text.setBox(new Rectangle(0, height, width, height-textSize+3));
            }else if(type == 1){
                //text.setBox(new Rectangle(0, height, width, 15));
                text.setBox(new Rectangle(0, height+((Integer.toString(max).length()-3)*textSize/2), width, 15));
            }
            text.setAlignment(BitmapFont.Align.Center);
            text.setVerticalAlignment(BitmapFont.VAlign.Center);
            text.setSize(textSize);
            text.setColor(ColorRGBA.White);
            if(showMax){
                text.setText(max+"/"+max);
            }else{
                text.setText(Integer.toString(max));
            }
            text.move(topLeft.getX(), topLeft.getY(), 2);
            bar.attachChild(text);
            used = true;

            node.attachChild(bar);
        }
        public void destroy(){
            bar.detachAllChildren();
            used = false;
        }
    }
    
    public static DynamicBar getBar(BH handle){
        return bars[handles.get(handle)];
    }
    public static void setBarMax(BH handle, int value){
        bars[handles.get(handle)].setMax(value);
    }
    public static void updateBar(BH handle, int value){
        bars[handles.get(handle)].update(value);
    }
    
    private static int findEmptyBar(){
        int i = 0;
        while(i < bars.length){
            if(bars[i] == null || !bars[i].isUsed()){
                return i;
            }
            i++;
        }
        return -1;
    }
    public static void add(Node node, BH handle, int type, Vector2f loc, float width, float height, float textSize,
                ColorRGBA color, int max, boolean showMax){
        int i = findEmptyBar();
        if(i != -1){
            if(bars[i] == null){
                bars[i] = new DynamicBar();
            }
            bars[i].create(node, type, loc, width, height, textSize, color, max, showMax);
            handles.put(handle, i);
        }
    }
    
    public static void initialize(GameClient app){
        BarManager.app = app;
    }
}
