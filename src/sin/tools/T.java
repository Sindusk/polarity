package sin.tools;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sin.GameClient;
import sin.weapons.AttackManager;
import sin.weapons.ProjectileManager;
import sin.weapons.ProjectileManager.Projectile;

/**
 * Tools - Provides miscellaneous tools for various functions.
 * @author SinisteRing
 */
public class T {
    //public static GameClient app;
    public static AssetManager assetManager;
    
    public static final Vector3f EMPTY_SPACE = new Vector3f(0, -50, 0);
    
    private static HashMap<Projectile, HashMap<String, Float>> UpdateMap = new HashMap();
    
    private static Vector3f spiral(Projectile p, float t){
        Vector3f dirVec = p.getDirection(); //your perpendicular plane is x=0
        Vector3f camUp = p.getUp();
        Vector3f rotY = camUp.subtract(camUp.project(dirVec)).normalizeLocal();
        Vector3f rotX = dirVec.cross(rotY).normalizeLocal();
        float rotationAngle = FastMath.PI*t;
        Vector3f velocity = rotY.mult(FastMath.cos(rotationAngle)).addLocal(rotX.mult(FastMath.sin(rotationAngle)));
        return velocity;
    }
    
    // Projectile Action Parsing:
    private static String[] getArgs(String s){
        return s.substring(s.indexOf("(")+1, s.indexOf(")")).split(",");
    }
    private static float getValueF(String s){
        return Float.parseFloat(s);
    }
    
    public static void InitializeUpdate(Projectile p){
        String[] actions = p.getUpdate().split(":");
        String[] args;
        UpdateMap.put(p, new HashMap());
        int i = 0;
        while(i < actions.length){
            if(actions[i].contains("spiral")){
                args = getArgs(actions[i]);
                UpdateMap.get(p).put(i+"spiral.timer", 0f);
                UpdateMap.get(p).put(i+"spiral.rot", getValueF(args[0]));
            }
            i++;
        }
    }
    public static void ParseUpdate(Projectile p, float tpf){
        if(p.getUpdate().isEmpty()){
            return;
        }
        String[] actions = p.getUpdate().split(":");
        String[] args;
        int i = 0;
        while(i < actions.length){
            if(actions[i].contains("spiral")){
                args = getArgs(actions[i]);
                HashMap<String, Float> meow = UpdateMap.get(p);
                float timer = 0;
                if(meow.get(i+"spiral.timer") != null){
                    timer = meow.get(i+"spiral.timer");
                }
                if(timer > getValueF(args[1])){
                    float rot = UpdateMap.get(p).get(i+"spiral.rot");
                    ProjectileManager.addNew(p.getLocation().clone(), spiral(p, rot), T.v3f(0, 0, 0), 20, 20, "", "damage(2.3):destroy");
                    rot += getValueF(args[2]);
                    UpdateMap.get(p).put(i+"spiral.rot", rot);
                    timer = 0;
                }else{
                    timer += tpf;
                }
                UpdateMap.get(p).put(i+"spiral.timer", timer);
            }
            i++;
        }
    }
    public static void ParseCollision(String collision, CollisionResult target){
        String[] actions = collision.split(":");
        String[] args;
        int i = 0;
        while(i < actions.length){
            if(actions[i].contains("damage")){
                args = getArgs(actions[i]);
                AttackManager.damage(target, getValueF(args[0]));
            }else if(actions[i].contains("aoe")){
                args = getArgs(actions[i]);
                AttackManager.damageAoE(target, getValueF(args[0]), getValueF(args[1]));
            }
            i++;
        }
    }
    public static void ParseCollision(Projectile p, CollisionResult target){
        ParseCollision(p.getCollision(), target);
        if(p.getCollision().contains("destroy")){
            p.destroy();
        }
    }
    
    // Asset Management:
    public static BitmapFont getFont(String fnt){
        return assetManager.loadFont("Interface/Fonts/"+fnt+".fnt");
    }
    public static Material getMaterial(ColorRGBA color){
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", color);
        if(color.getAlpha() < 1){
            m.setTransparent(true);
            m.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        }
        return m;
    }
    public static Material getMaterial(String tex){
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setTexture("ColorMap", assetManager.loadTexture(tex));
        m.getTextureParam("ColorMap").getTextureValue().setWrap(Texture.WrapMode.Repeat);
        return m;
    }
    public static String getMaterialPath(String tex){
        return "Textures/Material/"+tex+".png";
    }
    
    // Geometry and Collisions:
    public static CollisionResult getClosestCollision(Ray ray, Node node){
        CollisionResults results = new CollisionResults();
        node.collideWith(ray, results);
        if(results.size() > 0){
            return results.getClosestCollision();
        }else{
            return null;
        }
    }
    
    // Random numbers:
    public static float randFloat(float min, float max){
        return (FastMath.nextRandomFloat()+min)*(max-min);
    }
    
    // Vectors and gamespace:
    public static Vector3f v3f(float x, float y, float z){
        return new Vector3f(x, y, z);
    }
    public static Vector3f v3f(float x, float y){
        return new Vector3f(x, y, 0);
    }
    public static void addv3f(Vector3f source, Vector3f additive){
        source.setX(source.getX() + additive.getX());
        source.setY(source.getY() + additive.getY());
        source.setZ(source.getZ() + additive.getZ());
    }
    public static Vector2f v2f(float x, float y){
        return new Vector2f(x, y);
    }
    
    // Logging
    public static void log(String s){
        //String ss = "[POLARITY] "+s;
        //Logger.getLogger("polarity").info(ss);
        System.out.println("[POLARITY] "+s);
    }
    public static void log(Throwable t){
        Logger.getLogger("polarity").log(Level.SEVERE, "{0}", t);
    }
    
    public static void initialize(AssetManager assetManager){
        T.assetManager = assetManager;
    }
}
