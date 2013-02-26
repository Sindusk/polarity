package sin.world;

import com.jme3.bullet.BulletAppState;
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
    
    private static HashMap<Vector3f, String> world = new HashMap();
    private static ArrayList<GeometryData> map = new ArrayList();
    
    // Constant Variables:
    private static final float WALL_WIDTH = 0.1f;
    private static final float ZONE_WIDTH = 10;
    private static final float ZONE_HEIGHT = 5;
    private static final float ZONE_RAMP_LENGTH = FastMath.sqrt(FastMath.sqr(ZONE_WIDTH)+FastMath.sqr(ZONE_HEIGHT));
    
    public static BulletAppState getBulletAppState(){
        return app.getBulletAppState();
    }
    
    public static ArrayList<GeometryData> getMap(){
        return map;
    }
    
    public static GeometryData geoData(String type, Vector3f size, Vector3f trans, Quaternion rot, String tex, Vector2f scale, boolean phy){
        return new GeometryData(type, size, trans, rot, tex, scale, phy);
    }
    
    public static void generateHallway(){
        int i = 1;
        float height = 0;
        float rng;
        while(i < 10){
            rng = FastMath.nextRandomFloat();
            if(rng < 0.50f){ // Flat
                map.add(geoData("box", T.v3f(ZONE_WIDTH, WALL_WIDTH, ZONE_WIDTH), T.v3f(i*ZONE_WIDTH*2, height, 0), null, T.getMaterial("lava_rock"), T.v2f(1, 1), true));
            }else if(rng < 0.75f){ // Up
                height += ZONE_HEIGHT;
                map.add(geoData("box", T.v3f(ZONE_RAMP_LENGTH, WALL_WIDTH, ZONE_RAMP_LENGTH), T.v3f(i*ZONE_WIDTH*2, height-(ZONE_HEIGHT/2.0f), 0),
                        new Quaternion().fromAngleAxis(FastMath.PI/6, Vector3f.UNIT_Z), T.getMaterial("lava_rock"), T.v2f(1, 1), true));
            }else{
                height -= ZONE_HEIGHT;
                map.add(geoData("box", T.v3f(ZONE_WIDTH, WALL_WIDTH, ZONE_WIDTH), T.v3f(i*ZONE_WIDTH*2, height+(ZONE_HEIGHT/2.0f), 0),
                        new Quaternion().fromAngleAxis(-FastMath.PI/16, Vector3f.UNIT_Z), T.getMaterial("lava_rock"), T.v2f(1, 1), true));
            }
            i++;
        }
    }
    
    public static void generateWorldData(){
        map.add(geoData("box", T.v3f(ZONE_WIDTH, .1f, ZONE_WIDTH), T.v3f(0, 0, 0), null, T.getMaterial("lava_rock"), T.v2f(1, 1), true));
        map.add(geoData("box", T.v3f(.1f, ZONE_HEIGHT, ZONE_WIDTH), T.v3f(-ZONE_WIDTH, ZONE_HEIGHT, 0), null, T.getMaterial("BC_Tex"), T.v2f(1, 1), true));
        map.add(geoData("box", T.v3f(ZONE_WIDTH, ZONE_HEIGHT, .1f), T.v3f(0, ZONE_HEIGHT, ZONE_WIDTH), null, T.getMaterial("BC_Tex"), T.v2f(1, 1), true));
        map.add(geoData("box", T.v3f(ZONE_WIDTH, ZONE_HEIGHT, .1f), T.v3f(0, ZONE_HEIGHT, -ZONE_WIDTH), null, T.getMaterial("BC_Tex"), T.v2f(1, 1), true));
        world.put(T.v3f(0, 0, 0), "a");
        generateHallway();
    }
    public static void createGeometry(GeometryData d){
        if(d.getType().equals("box")){
            if(d.getPhy()){
                CG.createPhyBox(app.getTerrain(), d);
            }else{
                CG.createBox(app.getTerrain(), d);
            }
        }
    }
    
    /*public static Node genZone(int var, int x, int y, int z){
        Node node = new Node();
        Geometry geo;
        float geoSize = ZONE_WIDTH/2;
        if(var == 0){
            CG.createPhyBox(node, "floor", T.v3f(geoSize, 1f, geoSize), T.v3f(0, 0, 0), "Textures/wall.png", T.v2f(1, 1));
            geo = CG.createPhyCylinder(node, "pillar", geoSize/5, geoSize, T.v3f(0, geoSize/2, 0), "Textures/wall.png", T.v2f(1, 1));
            geo.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.PI/2, Vector3f.UNIT_X));
        }else if(var == 1){
            CG.createPhyBox(node, "floor", T.v3f(geoSize, 1f, geoSize), T.v3f(0, 0, 0), "Textures/brick.png", T.v2f(1, 1));
            CG.createPhyBox(node, "flybox", T.v3f(geoSize/4, geoSize/4, geoSize/4), T.v3f(0, geoSize, 0), "Textures/brick.png", T.v2f(1, 1));
        }else if(var == 2){
            CG.createPhyBox(node, "floor", T.v3f(geoSize, 1f, geoSize), T.v3f(0, 0, 0), "Textures/BC_Tex.png", T.v2f(1, 1));
        }
        node.setLocalTranslation(x*ZONE_WIDTH, y*ZONE_HEIGHT, z*ZONE_WIDTH);
        return node;
    }*/
    public static void clear(){
        app.getTerrain().detachAllChildren();
    }
    
    public static void createSinglePlayerArea(Node node){
        node.setLocalTranslation(0, 100, 0);
        CG.createPhyBox(node, "floor", T.v3f(50, 0.1f, 50), T.v3f(0, 0, 0), T.getMaterial("lava_rock"), T.v2f(5, 5));
        CG.createPhyBox(node, "wall", T.v3f(50, 20, 0.1f), T.v3f(0, 20, -50), T.getMaterial("BC_Tex"), T.v2f(25, 10));
    }
    
    public static void initialize(GameClient app){
        World.app = app;
    }
}
