package sin.tools;

import java.util.ArrayList;
import sin.progression.Neuro.NeuroConnector;
import sin.progression.Neuro.NeuroEmpty;
import sin.progression.Neuro.NeuroLocked;
import sin.progression.Neuro.NeuroSource;
import sin.progression.Neuro.NeuroTemplate;
import sin.progression.Neuro.NeuroWithOutlets;
import sin.tools.T.Vector2i;

/**
 * Neuro (N) - Utilities for NeuroNetwork.
 * @author SinisteRing
 */
public class N {
    public static final int NEURO_SIZE = 11;
    
    private static String[] createOption(String handle, String label){
        String[] option = new String[2];
        option[0] = handle;
        option[1] = label;
        return option;
    }
    public static ArrayList<String[]> getNeuroOptions(NeuroTemplate neuro){
        ArrayList<String[]> options = new ArrayList(1);
        if(neuro instanceof NeuroConnector){
            options.add(createOption("rot_clock", "Rotate Clock"));
            options.add(createOption("rot_counter", "Rotate Counter"));
        }else if(neuro instanceof NeuroLocked){
            options.add(createOption("unlock", "Unlock Node"));
        }else if(neuro instanceof NeuroEmpty){
            options.add(createOption("source", "Create Source"));
            options.add(createOption("connector", "Create Connector"));
            options.add(createOption("corner", "Create Corner"));
            options.add(createOption("conn3way", "Create 3-Way"));
            options.add(createOption("conn4way", "Create 4-Way"));
        }else if(neuro instanceof NeuroSource){
            options.add(createOption("source_health", "Add +30 Health"));
            options.add(createOption("rot_clock", "Rotate Clock"));
            options.add(createOption("rot_counter", "Rotate Counter"));
        }
        options.add(createOption("cancel", "Cancel"));
        return options;
    }
    
    public static ArrayList<String> getSourceData(NeuroTemplate neuro){
        if(neuro instanceof NeuroSource){
            NeuroSource source = (NeuroSource) neuro;
            return source.getData();
        }else{
            return null;
        }
    }
    
    public static ArrayList<Vector2i> obtainOuts(NeuroTemplate neuro){
        if(neuro instanceof NeuroWithOutlets){
            NeuroWithOutlets n = (NeuroWithOutlets) neuro;
            return (ArrayList<Vector2i>) n.getOuts().clone();
        }
        return null;
    }
    public static ArrayList<Vector2i> parseOuts(ArrayList<String> args){
        ArrayList<Vector2i> outs = new ArrayList(1);
        ArrayList<String> innerArgs;
        int x, y;
        int i = 0;
        int n;
        while(i < args.size()){
            if(T.getInnerHeader(args.get(i)).equals("outs")){
                innerArgs = T.getInnerArgs(args.get(i));
                n = 1;
                while(n < args.size()){
                    x = Integer.parseInt(args.get(i-1));
                    y = Integer.parseInt(args.get(i));
                    outs.add(new Vector2i(x, y));
                    n+=2;
                }
                args.remove(i);
                break;
            }
            i++;
        }
        return outs;
    }
    
    public static boolean withinBounds(Vector2i loc){
        return (loc.x >= 0 && loc.x < NEURO_SIZE) && (loc.y >= 0 && loc.y < NEURO_SIZE);
    }
}
