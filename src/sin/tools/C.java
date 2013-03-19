package sin.tools;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import sin.weapons.Weapons.AK47;
import sin.weapons.Weapons.LaserPistol;
import sin.weapons.Weapons.M4A1;
import sin.weapons.Weapons.Raygun;
import sin.weapons.Weapons.RocketLauncher;
import sin.weapons.Weapons.Weapon;
import sin.world.CG;

/**
 * C (Character) - Tools used for the management of player/character-specific data.
 * @author SinisteRing
 */
public class C {
    private static final int NEURO_SIZE = 11;
    private static final float NEURO_SCALE = 0.7f;
    
    private static String[][] neuros = new String[NEURO_SIZE][NEURO_SIZE];
    
    public static Weapon parseWeapon(int j, String data){
        boolean left;
        if(j == 0){
            left = true;
        }else{
            left = false;
        }
        
        //String[] args = T.getArgs(data); [FOR USE LATER]
        // Modern:
        if(data.contains("M4A1")){
            return new M4A1(left);
        }else if(data.contains("AK47")){
            return new AK47(left);
        }
        // Energy:
        else if(data.contains("LaserPistol")){
            return new LaserPistol(left);
        }else if(data.contains("Raygun")){
            return new Raygun(left);
        }
        // Explosive:
        else if(data.contains("RocketLauncher")){
            return new RocketLauncher(left);
        }
        
        return null;
    }
    public static Weapon[][] parseWeapons(String s){
        Weapon[][] weapons = new Weapon[2][2];
        String[] sets = s.split("-");
        String[] data;
        int i = 0;
        int j;
        while(i < 2){
            j = 0;
            if(sets[i] != null){
                data = sets[i].split(":");
                while(j < data.length){
                    weapons[i][j] = parseWeapon(j, data[j]);
                    j++;
                }
            }
            i++;
        }
        return weapons;
    }
    
    private static String getNeuroMaterialPath(int x, int y){
        if(neuros[x][y].equals("x")){
            return T.getNeuroPath("locked");
        }else if(neuros[x][y].equals("i")){
            return T.getNeuroPath("empty");
        }else if(neuros[x][y].equals("c")){
            return T.getNeuroPath("core");
        }
        return T.getMaterialPath("default");
    }
    private static void readNeuroNet(BufferedReader br) throws IOException{
        String[] data;
        int i = 0;
        int n;
        while(i < NEURO_SIZE){
            if(br.ready()){
                data = br.readLine().split(":");
                n = 0;
                while(n < NEURO_SIZE){
                    neuros[i][n] = data[n];
                    n++;
                }
            }
            i++;
        }
    }
    private static void createNewNeuroNet(BufferedWriter bw) throws IOException{
        int x = 0;
        int y;
        while(x < NEURO_SIZE){
            y = 0;
            while(y < NEURO_SIZE){
                if(x == 5 && y == 5){
                    neuros[x][y] = "c";
                }else if((x >= 3 && x <= 7) && (y >= 3 && y <= 7) &&
                        !(x == 3 && y == 3) && !(x == 3 && y == 7) &&
                        !(x == 7 && y == 3) && !(x == 7 && y == 7)){
                    neuros[x][y] = "i";
                }else{
                    neuros[x][y] = "x";
                }
                bw.write(neuros[x][y]);
                y++;
                if(y != NEURO_SIZE){
                    bw.write(":");
                }
            }
            bw.write("\n");
            x++;
        }
    }
    public static Node createNeuroNode(){
        Node node = new Node("NeuroNode");
        int x = 0;
        int y;
        while(x < NEURO_SIZE){
            y = 0;
            while(y < NEURO_SIZE){
                CG.createBox(node, x+","+y+" - "+neuros[x][y], new Vector3f(NEURO_SCALE*0.5f, NEURO_SCALE*0.5f, 0.01f),
                        new Vector3f((x*NEURO_SCALE)-(NEURO_SCALE*5f), (y*NEURO_SCALE)-(NEURO_SCALE*5f), 0),
                        getNeuroMaterialPath(x, y), new Vector2f(1, 1));
                y++;
            }
            x++;
        }
        return node;
    }
    public static void gatherCharacterData(){
        try{
            File file = new File("neuronet.txt");
            if(!file.exists()){
                T.log("No NeuroNet found. Creating new file...");
                file.createNewFile();
                T.log(file.getAbsolutePath());
                BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
                createNewNeuroNet(bw);
                bw.close();
                T.log("Finished creating new NeuroNet!");
            }else{
                T.log("Found Player NeuroNet!");
                BufferedReader br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
                readNeuroNet(br);
                br.close();
                T.log("Finished reading NeuroNet!");
            }
        }catch(IOException e){
            T.log(e);
        }
    }
}
