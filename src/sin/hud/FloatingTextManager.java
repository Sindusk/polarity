package sin.hud;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import java.util.ArrayList;
import sin.tools.T;

/**
 *
 * @author SinisteRing
 */
public class FloatingTextManager {
    // Constant Variables:
    private static final float SIZE = 1;
    private static final float TIME = 0.5f;
    
    private static ArrayList<FloatingText> text = new ArrayList(1);
    private static Node node = new Node("FloatingTextNode");
    
    public static class FloatingText{
        // Instance Variables:
        private BitmapText text;
        private boolean used;
        private float timer;

        public FloatingText(){
            text = new BitmapText(T.getFont("OCRAStd"));
            text.setColor(ColorRGBA.Magenta);
            text.setSize(SIZE);
            text.setQueueBucket(RenderQueue.Bucket.Transparent);
        }
        
        public boolean isUsed(){
            return used;
        }

        public void update(float tpf){
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
            node.attachChild(text);
            timer = TIME;
            used = true;
        }
        private void destroy(){
            timer = 0;
            used = false;
            text.removeFromParent();
        }
    }
    
    public static Node getNode(){
        return node;
    }
    
    public static void update(float tpf){
        int i = 0;
        while(i < text.size()){
            if(text.get(i) != null && text.get(i).isUsed()){
                text.get(i).update(tpf);
            }
            i++;
        }
    }
    public static void add(Vector3f loc, Vector3f lookAt, float value){
        int i = 0;
        while(i < text.size()){
            if(text.get(i) == null){
                text.add(i, new FloatingText());
                text.get(i).create(loc, lookAt, value);
                return;
            }else if(!text.get(i).isUsed()){
                text.get(i).create(loc, lookAt, value);
                return;
            }
            i++;
        }
        text.add(new FloatingText());
        text.get(i).create(loc, lookAt, value);
    }
}
