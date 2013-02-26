package sin.tools;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sin.GameClient;
import sin.weapons.AttackManager;
import sin.weapons.ProjectileManager;
import sin.weapons.ProjectileManager.Projectile;

/**
 *
 * @author SinisteRing
 */
public class T {
    public static GameClient app;
    
    public static final Vector3f EMPTY_SPACE = new Vector3f(0, -50, 0);
    
    private static HashMap<Projectile, Float> SpiralTimer = new HashMap();
    private static HashMap<Projectile, Vector3f> SpiralDirection = new HashMap();
    
    // Projectile Action Parsing:
    private static String[] getArgs(String s){
        return s.substring(s.indexOf("(")+1, s.indexOf(")")).split(":");
    }
    private static float getValueF(String s){
        return Float.parseFloat(s);
    }
    
    public static void InitializeUpdate(Projectile p){
        String[] actions = p.getUpdate().split(":");
        int i = 0;
        while(i < actions.length){
            if(actions[i].contains("spiral")){
                SpiralTimer.put(p, 0f);
                SpiralDirection.put(p, p.getDirection().clone());
            }
            i++;
        }
    }
    public static void ParseUpdate(Projectile p, float tpf){
        String[] actions = p.getUpdate().split(":");
        String[] args;
        int i = 0;
        while(i < actions.length){
            if(actions[i].contains("spiral")){
                args = getArgs(actions[i]);
                float timer = SpiralTimer.get(p);
                if(timer > getValueF(args[0])){
                    Vector3f dir = SpiralDirection.get(p).clone();
                    dir.multLocal(randFloat(-1, 1), randFloat(-1, 1), randFloat(-1, 1));
                    dir.normalizeLocal();
                    ProjectileManager.addNew(p.getLocation().clone(), dir, 20, 20, "", "damage(2.3):destroy");
                    SpiralDirection.put(p, dir);
                    timer = 0;
                }else{
                    timer += tpf;
                }
                SpiralTimer.put(p, timer);
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
        return app.getAssetManager().loadFont("Interface/Fonts/"+fnt+".fnt");
    }
    public static String getMaterial(String tex){
        return "Textures/Material/"+tex+".png";
    }
    
    // Geometry and Collisions:
    public static CollisionResult getClosestCollision(Ray ray){
        CollisionResults results = new CollisionResults();
        app.getCollisionNode().collideWith(ray, results);
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
    
    public static void initialize(GameClient app){
        T.app = app;
    }
}
