package sin.world;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import sin.netdata.GeometryData;
import sin.tools.T;
import sin.tools.Tile;
import sin.tools.Tile.Type;

/**
 * World - Used for geometry creation and world generation.
 * @author SinisteRing
 */
public class World {
    //private static GameClient app;
    
    // Constant Variables:
    private static final float WW = 0.1f;   // Wall Width
    private static final float ZS = 10;     // Zone Size
    private static final int HALL_LENGTH_MIN = 20;
    private static final int HALL_LENGTH_MAX = 25;
    private static final int HALL_MAX_RADIUS = 25;
    private static final int HALL_SPREAD = 5;
    private static final int HALL_WIDTH = 2;
    
    private static HashMap<Vector3f, String> world = new HashMap();
    private static ArrayList<GeometryData> map = new ArrayList();
    private static ArrayList<Material> mats = new ArrayList();
    private static ArrayList<Hallway> hallways = new ArrayList();
    private static ArrayList<Wall> walls = new ArrayList();
    private static boolean wireframe = false;
    
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
    public static GeometryData geoFloor(Vector3f center, float xl, float zl, String tex, Vector2f scale, boolean phy){
        return geoData("box", new Vector3f(xl*ZS/2.0f, WW, zl*ZS/2.0f), center.mult(ZS), null, tex, scale, phy);
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
    
    private static class Wall{
        private Vector3f[] ends = new Vector3f[2];
        
        public Wall(Vector3f start, float xi, float zi, int spaces, boolean left){
            float x = start.getX();
            float z = start.getZ();
            float xl = 0;
            float zl = 0;
            Vector3f loc;
            int i = 0;
            float mod;
            ends[0] = start.clone();
            if(left){
                x -= xi;
                z -= zi;
                mod = -1;
            }else{
                mod = 1;
            }
            while(i < spaces){
                loc = new Vector3f(x+(i*xi), start.getY(), z+(i*zi));
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
            loc = T.v3f(x+(i*xi)-(xl/2), start.getY(), z+(i*zi)-(zl/2));
            map.add(geoWall(loc.getX()-((xi-zi)*mod/2), loc.getY(), loc.getZ()-((zi-xi)*mod/2), xl, zl, T.getMaterialPath("BC_Tex"), T.v2f(Math.max(FastMath.abs(xl), FastMath.abs(zl)), 1), true));
        }
    }
    private static class Hallway{
        Vector3f[] corners = new Vector3f[4];
        Vector3f center;
        GeometryData floor;
        
        private static class HallData{
            public Vector3f start;
            public float xi;
            public float zi;
            public HallData(Vector3f start, float xi, float zi){
                this.start = start;
                this.xi = xi;
                this.zi = zi;
            }
        }
        
        public Hallway(Vector3f start, float xi, float zi){
            float x = start.getX()+xi;
            float z = start.getZ()+zi;
            float rng, dist;
            int i = 0;
            int spread = 0;
            int iMax = FastMath.nextRandomInt(HALL_LENGTH_MIN, HALL_LENGTH_MAX);
            boolean b = false;
            // Make sure both xi and zi have an absolute value of 1:
            xi = T.sign(xi);
            zi = T.sign(zi);
            // Assign the first 2 corners:
            corners[0] = new Vector3f((zi*(HALL_WIDTH-1))+x, start.getY(), (xi*(HALL_WIDTH-1))+z); // Bottom Left
            corners[1] = new Vector3f((-zi*(HALL_WIDTH-1))+x, start.getY(), (-xi*(HALL_WIDTH-1))+z); // Bottom Right
            Vector3f left = corners[0].clone();
            Vector3f right = corners[1].clone();
            ArrayList<HallData> d = new ArrayList();
            while(i <= iMax){
                if(world.get(left) != null && world.get(left).contains("h")){
                    b = true;
                }
                if(world.get(right) != null && world.get(right).contains("h")){
                    b = true;
                }
                // Check each of the spaces in this step for hallway:
                if(b){
                    right.addLocal(-xi, 0, -zi);
                    left.addLocal(-xi, 0, -zi);
                    break;
                }
                world.put(left.clone(), "h");
                world.put(right.add(zi, 0, xi), "h");
                world.put(right.clone(), "h");
                // Check distance & random to see if more hallways should be created:
                dist = left.distance(Vector3f.ZERO);
                rng = FastMath.nextRandomFloat();
                if(dist < HALL_MAX_RADIUS && spread > HALL_SPREAD && i < iMax){
                    if(rng < 0.13f){
                        d.add(new HallData(left.clone(), zi, xi));
                        spread = 0;
                    }else if(rng < 0.26f){
                        d.add(new HallData(right.clone(), -zi, -xi));
                        spread = 0;
                    }else if(rng < 0.33f){
                        d.add(new HallData(left.clone(), zi, xi));
                        d.add(new HallData(right.clone(), -zi, -xi));
                        spread = 0;
                    }
                }
                x += xi;
                z += zi;
                spread++;
                i++;
                left.addLocal(xi, 0, zi);
                right.addLocal(xi, 0, zi);
            }
            corners[2] = right.clone(); // Top Right
            corners[3] = left.clone();  // Top Left
            
            // Generate hallways:
            int j = 0;
            while(j < d.size()){
                generateHallway(d.get(j).start, d.get(j).xi, d.get(j).zi);
                j++;
            }

            // Return if there's no hallway to generate (0 in size):
            float xs = FastMath.abs(corners[1].getX()-corners[3].getX())+1;
            float zs = FastMath.abs(corners[1].getZ()-corners[3].getZ())+1;
            if(Math.max(FastMath.abs(xs), FastMath.abs(zs)) < 1){
                return;
            }

            // Generate the front wall:
            generateWall(left.add(xi, 0, zi), -zi, -xi, HALL_WIDTH*2-1, false);
            generateWall(left.add(zi, 0, xi), -xi, -zi, i+1, false);
            generateWall(right.add(-zi, 0, -xi), -xi, -zi, i+1, true);

            // Generate the actual floor:
            float xloc = (corners[3].getX()+corners[1].getX())*0.5f;
            float zloc = (corners[3].getZ()+corners[1].getZ())*0.5f;
            center = new Vector3f(xloc, start.getY(), zloc);
            floor = geoFloor(center, xs, zs, T.getMaterialPath("lava_rock"), T.v2f(zs, xs), true);
            map.add(floor);
        }
    }
    
    public static void generateWall(Vector3f start, float xi, float zi, int spaces, boolean left){
        walls.add(new Wall(start, xi, zi, spaces, left));
    }
    public static void generateHallway(Vector3f start, float xi, float zi){
        hallways.add(new Hallway(start, xi, zi));
    }
    public static void generateStart(){
        map.add(geoFloor(new Vector3f(0, 0, 0), 5, 5, T.getMaterialPath("brick"), T.v2f(5, 5), true));
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
    
    public static void createSinglePlayerArea(Node node){
        node.setLocalTranslation(0, 100, 0);
        CG.createPhyBox(node, "floor", T.v3f(50, 0.1f, 50), T.v3f(0, -1, 0), T.getMaterialPath("lava_rock"), T.v2f(5, 5));
        CG.createPhyBox(node, "wall", T.v3f(30, 20, 0.1f), T.v3f(0, 20, -60), T.getMaterialPath("BC_Tex"), T.v2f(15, 10));
        CG.createPhyBox(node, "savior", T.v3f(10, 0.1f, 10), T.v3f(0, -101, 0), ColorRGBA.Yellow);
        Tile t = new Tile(new Vector3f(3, 3, 3), new Vector3f(10, 5, 0), Type.Grass, 1, node);
    }
}
