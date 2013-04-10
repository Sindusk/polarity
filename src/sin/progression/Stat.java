package sin.progression;

import java.util.ArrayList;
import java.util.Arrays;
import sin.tools.T;

/**
 *
 * @author SinisteRing
 */
public class Stat {
    // Stat handles:
    public static final String HEALTH = "health";
    public static final String SHIELDS = "shields";
    public static final String DAMAGE = "damage";
    
    // Stat instance data:
    private String handle;
    private ArrayList<Float> values = new ArrayList(1);
    
    public Stat(String data){
        handle = T.getInnerHeader(data);
        ArrayList<String> strVals = T.getInnerArgs(data);
        T.log(strVals.toString());
        int i = 0;
        while(i < strVals.size()){
            values.add(Float.parseFloat(strVals.get(i)));
            i++;
        }
    }
    
    public String getHandle(){
        return handle;
    }
    public ArrayList<Float> getValues(){
        return values;
    }
}
