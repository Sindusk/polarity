package sin.world;

import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import sin.GameClient;
import sin.netdata.GeometryData;
import sin.tools.T;

/**
 * World - Used for geometry creation and world generation.
 * @author SinisteRing
 */
public class World {
    private static GameClient app;
    
    // Constant Variables:
    private static final float WW = 0.1f;   // Wall Width
    private static final float ZS = 10;     // Zone Size
    private static final int HALL_LENGTH_MIN = 20;
    private static final int HALL_LENGTH_MAX = 25;
    private static final int HALL_MAX_RADIUS = 35;
    private static final int HALL_SPREAD = 5;
    private static final int HALL_WIDTH = 2;
    
    private static HashMap<Vector3f, String> world = new HashMap();
    private static ArrayList<GeometryData> map = new ArrayList();
    private static ArrayList<Material> mats = new ArrayList();
    private static boolean wireframe = false;
    
    public static BulletAppState getBulletAppState(){
        return app.getBulletAppState();
    }
    
    public static ArrayList<GeometryData> getMap(){
        return map;
    }
    
    public static void addMaterial(Material m){
        mats.add(m);
    }
    public static void toggleWireframe(){
        int i = 0;
        wireframe = !wireframe;
        while(i < mats.size()){
            mats.get(i).getAdditionalRenderState().setWireframe(wireframe);
            i++;
        }
    }
    
    public static GeometryData geoData(String type, Vector3f size, Vector3f trans, Quaternion rot, String tex, Vector2f scale, boolean phy){
        return new GeometryData(type, size, trans, rot, tex, scale, phy);
    }
    public static GeometryData geoFloor(float x, float y, float z, float xl, float zl, String tex, Vector2f scale, boolean phy){
        return geoData("box", T.v3f(xl*ZS/2.0f, WW, zl*ZS/2.0f), T.v3f(x*ZS, y*ZS, z*ZS), null, tex, scale, phy);
    }
    public static GeometryData geoWall(float x, float y, float z, float xl, float zl, String tex, Vector2f scale, boolean phy){
        if(xl == 0){
            xl = WW;
        }else{
            xl *= ZS/2;
        }
        if(zl == 0){
            zl = WW;
        }else{
            zl *= ZS/2;
        }
        return geoData("box", T.v3f(xl, ZS/2, zl), T.v3f(x*ZS, (y*ZS)+(ZS/2), z*ZS), null, tex, scale, phy);
    }
    
    public static void generateWall(Vector3f start, float xi, float zi, int spaces, boolean left){
        float x = start.getX()+xi;
        float z = start.getZ()+zi;
        float xl = 0;
        float zl = 0;
        Vector3f loc;
        int i = 0;
        float mod;
        if(left){
            x -= xi;
            z -= zi;
            mod = -1;
        }else{
            mod = 1;
        }
        while(i < spaces){
            loc = T.v3f(x+(i*xi), start.getY(), z+(i*zi));
            if(world.get(loc) != null && world.get(loc).contains("h")){
                if(left && (xl != 0 || zl != 0)){
                    xl -= xi;
                    zl -= zi;
                    i--;
                }
                break;
            }
            xl += xi;
            zl += zi;
            i++;
        }
        if(xl == 0 && zl == 0){
            return;
        }
        if(i < spaces){
            loc = T.v3f(x+(i*xi), start.getY(), z+(i*zi));
            generateWall(loc, xi, zi, spaces-i, left);
        }
        loc = T.v3f(x+(i*xi)-(xl/2), start.getY(), z+(i*zi)-(zl/2));
        map.add(geoWall(loc.getX()-((xi-zi)*mod/2), loc.getY(), loc.getZ()-((zi-xi)*mod/2), xl, zl, T.getMaterialPath("BC_Tex"), T.v2f(Math.max(FastMath.abs(xl), FastMath.abs(zl)), 1), true));
    }
    public static void generateHallway(Vector3f start, float xi, float zi){
        Vector3f loc;
        float x = start.getX()+xi;
        float z = start.getZ()+zi;
        float w, rng, dist;
        int i = 0;
        int spread = 0;
        int iMax = FastMath.nextRandomInt(HALL_LENGTH_MIN, HALL_LENGTH_MAX);
        boolean b = false;
        float xs = 0;
        float zs = 0;
        // Make sure both xi and zi have an absolute value of 1:
        if(FastMath.abs(xi) > 1 || FastMath.abs(zi) > 1){
            if(xi != 0){
                xi /= FastMath.abs(xi);
            }
            if(zi != 0){
                zi /= FastMath.abs(zi);
            }
        }
        while(i <= iMax){
            // Initialize loc to the next step:
            loc = T.v3f(x, start.getY(), z);
            // Check each of the spaces in this step for hallway:
            w = -HALL_WIDTH+1;
            while(w <= HALL_WIDTH-1){
                if(world.get(loc.add(w*zi, 0, w*xi)) != null && world.get(loc.add(w*zi, 0, w*xi)).contains("h")){
                    //T.log("loc "+loc+" broke it");
                    b = true;
                    break;
                }
                w++;
            }
            if(b){
                break;
            }
            w--;
            while(w >= -HALL_WIDTH+1){
                //T.log("putting h in "+loc.add(w*zi, 0, w*xi));
                world.put(loc.add(w*zi, 0, w*xi), "h");
                w--;
            }
            // Check distance & random to see if more hallways should be created:
            dist = loc.distance(Vector3f.ZERO);
            rng = FastMath.nextRandomFloat();
            if(dist < HALL_MAX_RADIUS && spread > HALL_SPREAD && i != iMax){
                if(rng < 0.13f){
                    generateHallway(loc, zi*HALL_WIDTH, xi*HALL_WIDTH);
                    spread = 0;
                }else if(rng < 0.26f){
                    generateHallway(loc, -zi*HALL_WIDTH, -xi*HALL_WIDTH);
                    spread = 0;
                }else if(rng < 0.13f){
                    generateHallway(loc, zi*HALL_WIDTH, xi*HALL_WIDTH);
                    generateHallway(loc, -zi*HALL_WIDTH, -xi*HALL_WIDTH);
                    spread = 0;
                }
            }
            xs += xi;
            zs += zi;
            x += xi;
            z += zi;
            spread++;
            i++;
        }
        
        // Return if there's no hallway to generate (0 in size):
        if(Math.max(FastMath.abs(xs), FastMath.abs(zs)) < 1){
            return;
        }
        
        // Generate the front wall:
        loc = T.v3f(x+(zi*HALL_WIDTH), start.getY(), z+(xi*HALL_WIDTH));
        generateWall(loc, -zi, -xi, HALL_WIDTH*2-1, false);
        generateWall(loc, -xi, -zi, i, false);
        generateWall(loc.add(-zi*(HALL_WIDTH*2), 0, -xi*(HALL_WIDTH*2)), -xi, -zi, i, true);
        
        // Generate the actual floor:
        float xloc = x-((xs+xi)/2);
        float zloc = z-((zs+zi)/2);
        if(xs == 0){
            xs = HALL_WIDTH*2-1;
        }
        if(zs == 0){
            zs = HALL_WIDTH*2-1;
        }
        map.add(geoFloor(xloc, start.getY(), zloc, xs, zs, T.getMaterialPath("lava_rock"), T.v2f(zs, xs), true));
    }
    public static void generateStart(){
        map.add(geoFloor(0, 0, 0, 5, 5, T.getMaterialPath("brick"), T.v2f(5, 5), true));
        int x = -2;
        int z;
        while(x <= 2){
            z = -2;
            while(z <= 2){
                world.put(T.v3f(x, 0, z), "h");
                z++;
            }
            x++;
        }
    }
    
    public static void generateWorldData(){
        Vector3f start = T.v3f(2, 0, 0);
        float xi = 1;
        float zi = 0;
        generateStart();
        generateHallway(start, xi, zi);
    }
    public static void createGeometry(Node node, GeometryData d){
        if(d.getType().equals("box")){
            if(d.getPhy()){
                CG.createPhyBox(node, d);
            }else{
                CG.createBox(node, d);
            }
        }
    }
    
    public static void clear(){
        app.getTerrain().detachAllChildren();
    }
    
    public static void createSinglePlayerArea(Node node){
        node.setLocalTranslation(0, 100, 0);
        CG.createPhyBox(node, "floor", T.v3f(50, 0.1f, 50), T.v3f(0, -1, 0), T.getMaterialPath("lava_rock"), T.v2f(5, 5));
        CG.createPhyBox(node, "wall", T.v3f(30, 20, 0.1f), T.v3f(0, 20, -60), T.getMaterialPath("BC_Tex"), T.v2f(15, 10));
        //CG.createPhyBox(node, "platform", T.v3f(3, 3, 3), T.v3f(-50, 0, 0), ColorRGBA.Black);
        CG.createPhyBox(node, "savior", T.v3f(10, 0.1f, 10), T.v3f(0, -101, 0), ColorRGBA.Yellow);
    }
    
    public static void initialize(GameClient app){
        World.app = app;
    }
}
