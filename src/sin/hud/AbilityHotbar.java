package sin.hud;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import sin.player.ability.AbilityManager.Ability;
import sin.tools.T;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class AbilityHotbar {
    private static Node node = new Node("AbilityBarNode");
    private static Geometry bar;
    private static AbilityIcon[] icons = new AbilityIcon[4];
    
    private static class AbilityIcon{
        private static final float ICON_SIZE = 20f;
        private Node node = new Node("AbilityIcon");
        private Geometry icon;
        private Geometry coolGeo;
        private BitmapText coolText;
        private float x, y, cooldown, cooldownMax;
        private boolean cooling = false;
        
        public AbilityIcon(float x, float y, String name){
            this.x = x;
            this.y = y;
            icon = CG.createBox(node, "", new Vector3f(20, 20, 0), new Vector3f(x, y, 1), T.getIconPath(name.toLowerCase()), Vector2f.UNIT_XY);
            coolGeo = CG.createBox(node, "", new Vector3f(20, 20, 0), new Vector3f(x, y, 2), new ColorRGBA(0, 0, 0.5f, 0.4f));
            coolGeo.setLocalScale(1, 0, 1);
            coolText = CG.createText(node, 20, new Vector3f(x-15, y, 3), "OCRAStd", ColorRGBA.Orange);
            coolText.setBox(new Rectangle(-15, 28, 60, 40));
            coolText.setAlignment(Align.Center);
            coolText.setVerticalAlignment(VAlign.Center);
        }
        
        public boolean onCooldown(){
            return cooling;
        }
        public Node getNode(){
            return node;
        }
        
        public void setCooldown(float cooldown){
            this.cooldown = cooldown;
            this.cooldownMax = cooldown;
            cooling = true;
        }
        public void update(float tpf){
            cooldown -= tpf;
            float perc = cooldown/cooldownMax;
            coolGeo.setLocalTranslation(x, y+(perc*ICON_SIZE)-ICON_SIZE, 2);
            coolGeo.setLocalScale(1, perc, 1);
            coolText.setText(""+(int) FastMath.ceil(cooldown));
            if(cooldown <= 0){
                coolText.setText("");
                cooling = false;
            }
        }
    }
    
    public static void addCooldown(int id, float cooldown){
        icons[id].setCooldown(cooldown);
    }
    public static void createIcons(Ability[] abilities){
        int i = 0;
        while(i < 4){
            if(abilities[i] != null){
                icons[i] = new AbilityIcon(bar.getLocalTranslation().getX()-(60*(1.5f-(float)i)), bar.getLocalTranslation().getY(), abilities[i].getName());
                node.attachChild(icons[i].getNode());
            }
            i++;
        }
    }
    
    public static void update(float tpf){
        int i = 0;
        while(i < 4){
            if(icons[i] != null && icons[i].onCooldown()){
                icons[i].update(tpf);
            }
            i++;
        }
    }
    
    public static void initialize(float cx, float cy){
        bar = CG.createBox(node, "", new Vector3f(115, 25, 0), new Vector3f(cx, cy-(cy*0.58f), 0), new ColorRGBA(0, 0.5f, 0, 0.4f));
    }
    
    public static Node getNode(){
        return node;
    }
}
