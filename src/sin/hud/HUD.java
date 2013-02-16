/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.hud;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.tools.T;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class HUD {
    private static GameClient app;
    
    private class Font{
        public Font(){
            //
        }
        public BitmapFont getFont(String fnt){
            return app.getAssetManager().loadFont("Interface/Fonts/"+fnt+".fnt");
        }
    }
    private class DynamicBar{
        // Constant Variables:
        private static final float BORDER_WIDTH = 2;

        // Instance Variables:
        private int type;
        private Vector2f loc;
        private Vector3f fillLoc;
        private float width, height;
        private Node bar = new Node();
        //private Geometry[] border = new Geometry[4];
        private Geometry fill;
        private ColorRGBA color;
        private BitmapText text;
        private int max;
        //private int value;
        private float textSize;
        private boolean showMax;

        public DynamicBar(int type, Vector2f loc, float width, float height, float textSize,
                ColorRGBA color, int max, boolean showMax){
            this.type = type;
            this.loc = loc;
            this.width = width;
            this.height = height;
            this.color = color;
            this.max = max;
            //this.value = max;
            this.textSize = textSize;
            this.showMax = showMax;
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
        public void create(){
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
            text = new BitmapText(font.getFont("OCRAStd"), false);
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

            node.attachChild(bar);
        }
        public void destroy(){
            //
        }
    }
    private class FloatingText{
        // Constant Variables:
        private static final float SIZE = 1;
        private static final float TIME = 0.5f;

        // Instance Variables:
        private BitmapText text;
        private boolean used;
        private float timer;

        public FloatingText(){
            text = new BitmapText(font.getFont("OCRAStd"));
            //text.setLocalTranslation(loc);
            //text.setText(Float.toString(value));
            text.setColor(ColorRGBA.Magenta);
            text.setSize(SIZE);
            text.setQueueBucket(RenderQueue.Bucket.Transparent);
            GameClient.getMiscNode().attachChild(text);
            used = false;
            timer = 0;
        }

        public void addTime(float tpf){
            timer -= tpf;
            if(timer <= 0){
                this.destroy();
            }else{
                text.setLocalTranslation(text.getLocalTranslation().addLocal(T.v3f(0, tpf*2, 0)));
            }
        }
        public void create(Vector3f loc, Vector3f lookAt, float value){
            text.setLocalTranslation(loc);
            text.setText(Integer.toString((int) FastMath.floor(value)));
            text.lookAt(lookAt, Vector3f.UNIT_Y);
            timer = TIME;
            used = true;
        }
        private void destroy(){
            timer = 0;
            used = false;
            text.setLocalTranslation(T.EMPTY_SPACE);
        }
    }

    // Constant Variables:
    private static final float CROSSHAIR_LENGTH = 18;
    private static final float CROSSHAIR_OFFSET = 5;
    private static final float CROSSHAIR_WIDTH = 4;
    private static final int FTEXT_NUM = 50;

    // Index Holders:
    public static final int HEALTH = 0;
    public static final int SHIELD = 1;
    public static final int AMMO_LEFT = 2;
    public static final int AMMO_RIGHT = 3;

    // Instance Variables:
    private Node node = new Node("GUI");      // Node used to attach/detach GUI and HUD elements.
    private float cx, cy;
    private Font font = new Font();
    private DynamicBar[] bar = new DynamicBar[4];
    private FloatingText[] texts = new FloatingText[FTEXT_NUM];
    public BitmapText ping;
    private Geometry[] crosshair = new Geometry[4];

    public HUD(){
        //
    }

    private void createCrosshairs(float length, float offset, float width){
        crosshair[0] = World.CG.createLine(node, "", width, T.v3f(cx-(length+offset), cy), T.v3f(cx-offset, cy), ColorRGBA.Red);
        crosshair[1] = World.CG.createLine(node, "", width, T.v3f(cx, cy-(length+offset)), T.v3f(cx, cy-offset), ColorRGBA.Red);
        crosshair[2] = World.CG.createLine(node, "", width, T.v3f(cx+(length+offset), cy), T.v3f(cx+offset, cy), ColorRGBA.Red);
        crosshair[3] = World.CG.createLine(node, "", width, T.v3f(cx, cy+(length+offset)), T.v3f(cx, cy+offset), ColorRGBA.Red);
    }
    private void updateCrosshairs(){
        float mod = GameClient.getRecoil().getSpreadMod();
        crosshair[0].setLocalTranslation(T.v3f(-mod, 0));
        crosshair[1].setLocalTranslation(T.v3f(0, -mod));
        crosshair[2].setLocalTranslation(T.v3f(mod, 0));
        crosshair[3].setLocalTranslation(T.v3f(0, mod));
    }
    public void addFloatingText(Vector3f loc, Vector3f lookAt, float damage){
        int i = 0;
        while(i < texts.length){
            if(!texts[i].used){
                texts[i].create(loc, lookAt, damage);
                return;
            }
            i++;
        }
    }

    public void setBarMax(int index, int value){
        bar[index].setMax(value);
    }
    public void updateBar(int index, int value){
        bar[index].update(value);
    }

    public void update(float tpf){
        int i = 0;
        while(i < texts.length){
            if(texts[i].used){
                texts[i].addTime(tpf);
            }
            i++;
        }
        if(GameClient.getRecoil().getSpreadMod() != 0){
            updateCrosshairs();
        }
    }
    public void initialize(GameClient app, Node node){
        HUD.app = app;
        node.attachChild(this.node);
        
        // Get the center coordinates for the screen:
        this.cx = app.getSettings().getWidth()/2;
        this.cy = app.getSettings().getHeight()/2;

        // Create crosshairs:
        createCrosshairs(CROSSHAIR_LENGTH, CROSSHAIR_OFFSET, CROSSHAIR_WIDTH);

        // Create dynamic bar UI elements:
        bar[HEALTH] = new DynamicBar(0, T.v2f(cx, 30), 200, 40, 25, ColorRGBA.Red, 100, true);
        bar[HEALTH].create();
        bar[SHIELD] = new DynamicBar(0, T.v2f(cx, 90), 200, 40, 25, ColorRGBA.Blue, 100, true);
        bar[SHIELD].create();
        bar[AMMO_LEFT] = new DynamicBar(1, T.v2f(cx-130, 60), 30, 100, 30, ColorRGBA.Orange, 30, false);
        bar[AMMO_LEFT].create();
        bar[AMMO_RIGHT] = new DynamicBar(1, T.v2f(cx+130, 60), 30, 100, 30, ColorRGBA.Orange, 30, false);
        bar[AMMO_RIGHT].create();

        // Initialize ping display:
        ping = new BitmapText(font.getFont("Tele-Marines"));
        ping.setColor(ColorRGBA.Green);
        ping.setSize(16);
        ping.setLocalTranslation(T.v3f(20, cy*2-20));
        ping.setText("Not Connected");
        node.attachChild(ping);

        // Initialize floating texts:
        int i = 0;
        while(i < texts.length){
            texts[i] = new FloatingText();
            i++;
        }
    }
}
