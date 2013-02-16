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
import sin.hud.BarManager.BarHandle;
import sin.hud.BarManager.DynamicBar;
import sin.tools.T;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class HUD {
    private static GameClient app;
    
    private class FloatingText{
        // Constant Variables:
        private static final float SIZE = 1;
        private static final float TIME = 0.5f;

        // Instance Variables:
        private BitmapText text;
        private boolean used;
        private float timer;

        public FloatingText(){
            text = new BitmapText(T.getFont("OCRAStd"));
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
    private static final float CROSSHAIR_WIDTH = 3.2f;
    private static final int FTEXT_NUM = 50;

    // Index Holders:
    public static final int HEALTH = 0;
    public static final int SHIELD = 1;
    public static final int AMMO_LEFT = 2;
    public static final int AMMO_RIGHT = 3;

    // Instance Variables:
    private static Node node = new Node("GUI");      // Node used to attach/detach GUI and HUD elements.
    private float cx, cy;
    //private DynamicBar[] bar = new DynamicBar[4];
    private FloatingText[] texts = new FloatingText[FTEXT_NUM];
    public BitmapText ping;
    private Geometry[] crosshair = new Geometry[4];

    public HUD(){
        //
    }

    public static Node getGUI(){
        return node;
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

    public void setBarMax(BarHandle handle, int value){
        BarManager.setBarMax(handle, value);
    }
    public void updateBar(BarHandle handle, int value){
        BarManager.updateBar(handle, value);
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
    public void initialize(GameClient app, Node root){
        HUD.app = app;
        root.attachChild(node);
        
        // Get the center coordinates for the screen:
        this.cx = app.getSettings().getWidth()/2;
        this.cy = app.getSettings().getHeight()/2;

        // Create crosshairs:
        createCrosshairs(CROSSHAIR_LENGTH, CROSSHAIR_OFFSET, CROSSHAIR_WIDTH);

        // Create dynamic bar UI elements:
        //bar[HEALTH] = new DynamicBar(0, T.v2f(cx, 30), 200, 40, 25, ColorRGBA.Red, 100, true);
        //bar[HEALTH].create(node);
        //bar[SHIELD] = new DynamicBar(0, T.v2f(cx, 90), 200, 40, 25, ColorRGBA.Blue, 100, true);
        //bar[SHIELD].create(node);
        //bar[AMMO_LEFT] = new DynamicBar(1, T.v2f(cx-130, 60), 30, 100, 30, ColorRGBA.Orange, 30, false);
        //bar[AMMO_LEFT].create(node);
        //bar[AMMO_RIGHT] = new DynamicBar(1, T.v2f(cx+130, 60), 30, 100, 30, ColorRGBA.Orange, 30, false);
        BarManager.add(node, BarHandle.HEALTH, 0, T.v2f(cx, 30), 200, 40, 25, ColorRGBA.Red, 100, true);
        BarManager.add(node, BarHandle.SHIELDS, 0, T.v2f(cx, 90), 200, 40, 25, ColorRGBA.Blue, 100, true);
        BarManager.add(node, BarHandle.AMMO_LEFT, 1, T.v2f(cx-130, 60), 30, 100, 30, ColorRGBA.Orange, 30, false);
        BarManager.add(node, BarHandle.AMMO_RIGHT, 1, T.v2f(cx+130, 60), 30, 100, 30, ColorRGBA.Orange, 30, false);
        //bar[AMMO_RIGHT].create(node);

        // Initialize ping display:
        ping = new BitmapText(T.getFont("Tele-Marines"));
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
