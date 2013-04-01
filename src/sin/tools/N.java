package sin.tools;

import java.util.ArrayList;

/**
 * Neuro (N) - Utilities for NeuroNetwork.
 * @author SinisteRing
 */
public class N {
    public static final String CONNECTOR = "connector";
    public static final String CORE = "core";
    public static final String EMPTY = "empty";
    public static final String LOCKED = "locked";
    public static final String SOURCE = "source";
    
    private static String[] createOption(String handle, String label){
        String[] option = new String[2];
        option[0] = handle;
        option[1] = label;
        return option;
    }
    public static ArrayList<String[]> getNeuroOptions(String data){
        ArrayList<String[]> options = new ArrayList(1);
        String header = T.getHeader(data);
        if(header.equals(LOCKED)){
            options.add(createOption("unlock", "Unlock Node"));
        }else if(header.equals(EMPTY)){
            options.add(createOption("source", "Create Source"));
            options.add(createOption("connector", "Create Connector"));
        }
        options.add(createOption("cancel", "Cancel"));
        return options;
    }
}
