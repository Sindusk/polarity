/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.tools;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 *
 * @author SinisteRing
 */
public class T {
    public static final Vector3f EMPTY_SPACE = new Vector3f(0, -50, 0);
    public static Vector3f v3f(float x, float y, float z){
        return new Vector3f(x, y, z);
    }
    public static Vector3f v3f(float x, float y){
        return new Vector3f(x, y, 0);
    }
    public static Vector2f v2f(float x, float y){
        return new Vector2f(x, y);
    }
}
