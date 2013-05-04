package sin.proficiencies;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import sin.geometry.SinText;
import sin.geometry.SinText.Alignment;
import sin.world.CG;

/**
 *
 * @author SinisteRing
 */
public class ProficienciesScreen {
    private static Node node = new Node("ProficienciesScreen");
    private static HashMap<Proficiency, Float> profs = new HashMap(1);
    
    private static enum Proficiency{
        FIRE("Fire"),
        ICE("Ice");
        
        private String handle;
        Proficiency(String handle){
            this.handle = handle;
        }
        public String getHandle(){
            return handle;
        }
    }
    
    public static Node getNode(){
        return node;
    }
    
    public static void writeNewProficiencies(BufferedWriter bw) throws IOException{
        for(Proficiency prof : Proficiency.values()){
            profs.put(prof, 0f);
            bw.write(prof.getHandle()+":0\n");
        }
        bw.close();
    }
    public static void readProficiencies(BufferedReader br) throws IOException{
        String line;
        String[] split;
        while(br.ready()){
            line = br.readLine();
            split = line.split(":");
            for(Proficiency prof : Proficiency.values()){
                if(split[0].equals(prof.getHandle())){
                    profs.put(prof, Float.parseFloat(split[1]));
                    break;
                }
            }
        }
    }
    
    public static void initialize(){
        final float inc = -0.7f;
        float value;
        float space = 0;
        for(Proficiency prof : Proficiency.values()){
            value = profs.get(prof);
            CG.createBox(node, new Vector3f(2, 0.3f, 0), new Vector3f(0, space, -0.001f), ColorRGBA.Blue);
            SinText text = CG.createSinText(node, 0.2f, Vector3f.ZERO, "Batman26", prof.getHandle()+": "+Math.round(value), ColorRGBA.Green, Alignment.Left);
            text.setLocalTranslation(new Vector3f(-1.5f, space+(text.getLineHeight()), 0));
            space += inc;
        }
    }
}
