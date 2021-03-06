package sin.hud;

import sin.world.FloatingTextManager;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import sin.GameClient;
import sin.hud.BarManager.BH;
import sin.network.ClientNetwork;
import sin.player.PlayerManager;
import sin.player.StatsManager.PlayerStats;
import sin.tools.T;
import sin.weapons.RecoilManager;
import sin.world.CG;

/**
 * HUD (Heads Up Display) - Controls GUI elements while in-game.
 * @author SinisteRing
 */
public class HUD {
    private static GameClient app;
    
    // Constant Variables:
    private static final float CROSSHAIR_LENGTH = 16;
    private static final float CROSSHAIR_OFFSET = 5;
    private static final float CROSSHAIR_WIDTH = 3.2f;

    // Instance Variables:
    private static Node node = new Node("GUI");      // Node used to attach/detach GUI and HUD elements.
    private static float cx, cy;
    private static BitmapText ping;
    private static BitmapText loc;
    private static BitmapText fps;
    private static BitmapText[] weaponText = new BitmapText[2];
    private static Geometry[] crosshair = new Geometry[4];

    public static Node getGUI(){
        return node;
    }
    public static BitmapText getPing(){
        return ping;
    }
    public static BitmapText getWeaponText(boolean left){
        if(left){
            return weaponText[0];
        }else{
            return weaponText[1];
        }
    }
    
    private static void createCrosshairs(float length, float offset, float width){
        crosshair[0] = CG.createLine(node, "", width, T.v3f(cx-(length+offset), cy), T.v3f(cx-offset, cy), ColorRGBA.Red);
        crosshair[1] = CG.createLine(node, "", width, T.v3f(cx, cy-(length+offset)), T.v3f(cx, cy-offset), ColorRGBA.Red);
        crosshair[2] = CG.createLine(node, "", width, T.v3f(cx+(length+offset), cy), T.v3f(cx+offset, cy), ColorRGBA.Red);
        crosshair[3] = CG.createLine(node, "", width, T.v3f(cx, cy+(length+offset)), T.v3f(cx, cy+offset), ColorRGBA.Red);
    }
    public static void updateCrosshairs(){
        float mod = RecoilManager.getSpreadMod();
        crosshair[0].setLocalTranslation(T.v3f(-mod, 0));
        crosshair[1].setLocalTranslation(T.v3f(0, -mod));
        crosshair[2].setLocalTranslation(T.v3f(mod, 0));
        crosshair[3].setLocalTranslation(T.v3f(0, mod));
    }
    
    public static void setBarMax(BH handle, int value){
        BarManager.setBarMax(handle, value);
    }
    public static void updateBar(BH handle, int value){
        BarManager.updateBar(handle, value);
    }
    public static void updateLifeBars(PlayerStats stats){
        updateBar(BH.HEALTH, (int) FastMath.ceil(stats.getHealth()));
        updateBar(BH.SHIELDS, (int) FastMath.ceil(stats.getShields()));
    }
    
    public static void showCrosshairs(boolean show){
        int i = 0;
        while(i < crosshair.length){
            if(show){
                node.attachChild(crosshair[i]);
            }else{
                node.detachChild(crosshair[i]);
            }
            i++;
        }
    }
    
    public static void update(float tpf){
        AbilityHotbar.update(tpf);
        FloatingTextManager.update(tpf);
        Vector3f ploc = PlayerManager.getPlayer(ClientNetwork.getID()).getControl().getPhysicsLocation();
        Vector3f pdir = app.getCamera().getDirection();
        String compass;
        if(FastMath.abs(pdir.getX()) > FastMath.abs(pdir.getZ())){
            if(pdir.getX() > 0){
                compass = "North";
            }else{
                compass = "South";
            }
        }else{
            if(pdir.getZ() > 0){
                compass = "East";
            }else{
                compass = "West";
            }
        }
        loc.setText("X: "+String.format("%5.0f", ploc.getX())+"\nY: "+String.format("%5.0f", ploc.getY())+"\nZ: "+String.format("%5.0f", ploc.getZ())+"\nFacing: "+compass);
        fps.setText("FPS: " + String.format("%5.0f", 1/tpf));
        RecoilManager.updateCrosshairs();
    }
    public static void clear(){
        BarManager.clear();
        node.detachAllChildren();
    }
    
    public static void initialize(GameClient app, Node root){
        HUD.app = app;
        root.attachChild(node);
        
        // Get the center coordinates for the screen:
        cx = app.getSettings().getWidth()/2;
        cy = app.getSettings().getHeight()/2;

        // Create crosshairs:
        createCrosshairs(CROSSHAIR_LENGTH, CROSSHAIR_OFFSET, CROSSHAIR_WIDTH);

        // Create dynamic bar UI elements:
        BarManager.add(node, BH.HEALTH, 0, new Vector2f(cx, 30), 200, 40, 25, ColorRGBA.Red, 100, true);
        BarManager.add(node, BH.SHIELDS, 0, new Vector2f(cx, 90), 200, 40, 25, ColorRGBA.Blue, 100, true);
        BarManager.add(node, BH.AMMO_LEFT, 1, new Vector2f(cx-130, 60), 30, 100, 30, ColorRGBA.Orange, 30, false);
        BarManager.add(node, BH.AMMO_RIGHT, 1, new Vector2f(cx+130, 60), 30, 100, 30, ColorRGBA.Orange, 30, false);
        
        // Create ability bar:
        AbilityHotbar.initialize(cx, cy);
        node.attachChild(AbilityHotbar.getNode());

        // Initialize ping display:
        ping = CG.createText(node, 16, new Vector3f(20, cy*2-20, 0), "Tele-Marines", ColorRGBA.Green);
        ping.setText("Not Connected");
        loc = CG.createText(node, 14, new Vector3f(20, cy*2-40, 0), "Batman26", ColorRGBA.Cyan);
        fps = CG.createText(node, 16, new Vector3f(20, cy*2-100, 0), "Batman26", ColorRGBA.Red);
        
        // Initialize weapon text display:
        int i = 0;
        while(i < weaponText.length){
            weaponText[i] = new BitmapText(T.getFont("Batman26"));
            weaponText[i].setColor(ColorRGBA.Gray);
            weaponText[i].setSize(25);
            if(i == 0){
                weaponText[i].setLocalTranslation(new Vector3f(cx-(cx*0.6f), 30, 0));
            }else{
                weaponText[i].setLocalTranslation(new Vector3f(cx+(cx*0.5f), 30, 0));
            }
            weaponText[i].setText("meow!");
            node.attachChild(weaponText[i]);
            i++;
        }
    }
}
