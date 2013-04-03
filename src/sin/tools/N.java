package sin.tools;

import java.util.ArrayList;
import sin.progression.Neuro;
import sin.progression.Neuro.NeuroType;

/**
 * Neuro (N) - Utilities for NeuroNetwork.
 * @author SinisteRing
 */
public class N {
    private static String[] createOption(String handle, String label){
        String[] option = new String[2];
        option[0] = handle;
        option[1] = label;
        return option;
    }
    public static ArrayList<String[]> getNeuroOptions(Neuro neuro){
        ArrayList<String[]> options = new ArrayList(1);
        NeuroType type = neuro.getType();
        if(type.equals(NeuroType.LOCKED)){
            options.add(createOption("unlock", "Unlock Node"));
        }else if(type.equals(NeuroType.EMPTY)){
            options.add(createOption("source", "Create Source"));
            options.add(createOption("connector", "Create Connector"));
        }
        options.add(createOption("cancel", "Cancel"));
        return options;
    }
}
