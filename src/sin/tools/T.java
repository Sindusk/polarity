package sin.tools;

import com.jme3.font.BitmapFont;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * T (Tools) - Provides miscellaneous tools for various functions.
 * @author SinisteRing
 */
public class T {
    public static final Vector3f EMPTY_SPACE = new Vector3f(0, -50, 0);
    public static final float ROOT_HALF = 1.0f/FastMath.sqrt(2);
    
    public static class Vector2i{
        public int x;
        public int y;
        
        public Vector2i(int x, int y){
            this.x = x;
            this.y = y;
        }
        
        public void addLocal(Vector2i other){
            this.x += other.x;
            this.y += other.y;
        }
        public Vector2i add(Vector2i other){
            return new Vector2i(x+other.x, y+other.y);
        }
        
        public boolean equalsInverted(Vector2i other){
            if(other.x != -this.x){
                return false;
            }
            if(other.y != -this.y){
                return false;
            }
            return true;
        }
        public boolean equals(Vector2i other){
            if(this == other){
                return true;
            }
            if(other.x != this.x){
                return false;
            }
            if(other.y != this.y){
                return false;
            }
            return true;
        }
        
        @Override
        public Vector2i clone(){
            return new Vector2i(x, y);
        }
        @Override
        public String toString(){
            return "("+x+", "+y+")";
        }
    }
    
    // Parsing Assist:
    public static ArrayList<String> getArgs(String s){
        if(s.contains("(") && s.contains(")")){
            return new ArrayList<String>(Arrays.asList(s.substring(s.indexOf("(")+1, s.indexOf(")")).split(",")));
        }
        return new ArrayList<String>(Arrays.asList("".split(",")));
    }
    public static ArrayList<String> getInnerArgs(String s){
        if(s.contains("[") && s.contains("]")){
            return new ArrayList<String>(Arrays.asList(s.substring(s.indexOf("[")+1, s.indexOf("]")).split(";")));
        }
        return new ArrayList<String>(Arrays.asList("".split(";")));
    }
    public static String getHeader(String s){
        if(s.contains("(")){
            return s.substring(0, s.indexOf("("));
        }
        return s;
    }
    
    // Asset Management:
    public static BitmapFont getFont(String fnt){
        return S.getAssetManager().loadFont("Interface/Fonts/"+fnt+".fnt");
    }
    public static Material getMaterial(ColorRGBA color){
        Material m = new Material(S.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", color);
        if(color.getAlpha() < 1){
            m.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        }
        return m;
    }
    public static Material getMaterial(String tex){
        Material m = new Material(S.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        m.setTexture("ColorMap", S.getAssetManager().loadTexture(tex));
        m.getTextureParam("ColorMap").getTextureValue().setWrap(Texture.WrapMode.Repeat);
        return m;
    }
    public static String getGraphicPath(String tex){
        return "Interface/Graphic/"+tex+".png";
    }
    public static String getIconPath(String tex){
        return "Interface/Icons/"+tex+".png";
    }
    public static String getMaterialPath(String tex){
        return "Textures/Material/"+tex+".png";
    }
    public static String getNeuroPath(String tex){
        return "Textures/Neuros/"+tex+".png";
    }
    
    // Key Mappings:
    public static void createMapping(ActionListener listener, String name, KeyTrigger trigger){
        S.getInputManager().addMapping(name, trigger);
        S.getInputManager().addListener(listener, name);
    }
    public static void createMapping(ActionListener listener, String name, MouseButtonTrigger trigger){
        S.getInputManager().addMapping(name, trigger);
        S.getInputManager().addListener(listener, name);
    }
    public static void createMapping(AnalogListener listener, String name, MouseAxisTrigger trigger){
        S.getInputManager().addMapping(name, trigger);
        S.getInputManager().addListener(listener, name);
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
        System.out.println("[POLARITY] "+s);
    }
    public static void log(float f){
        System.out.println("[POLARITY] "+f);
    }
    public static void log(Throwable t){
        Logger.getLogger("polarity").log(Level.SEVERE, "{0}", t);
    }
}
