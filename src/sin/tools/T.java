package sin.tools;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import java.util.logging.Level;
import java.util.logging.Logger;
import sin.GameClient;

/**
 *
 * @author SinisteRing
 */
public class T {
    public static GameClient app;
    
    public static final Vector3f EMPTY_SPACE = new Vector3f(0, -50, 0);
    
    // HUD, Font, UI:
    public static BitmapFont getFont(String fnt){
        return app.getAssetManager().loadFont("Interface/Fonts/"+fnt+".fnt");
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
        Logger.getLogger("polarity").info(s);
    }
    public static void log(Throwable t){
        Logger.getLogger("polarity").log(Level.SEVERE, "{0}", t);
    }
    
    public static void initialize(GameClient app){
        T.app = app;
    }
}
