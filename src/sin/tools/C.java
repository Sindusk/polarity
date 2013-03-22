package sin.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import sin.progression.NeuroNetwork;
import sin.weapons.Weapons.AK47;
import sin.weapons.Weapons.LaserPistol;
import sin.weapons.Weapons.M4A1;
import sin.weapons.Weapons.Raygun;
import sin.weapons.Weapons.RocketLauncher;
import sin.weapons.Weapons.Weapon;

/**
 * C (Character) - Tools used for the management of player/character-specific data.
 * @author SinisteRing
 */
public class C {
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
    
    public static void gatherCharacterData(){
        try{
            File file = new File("neuronet.txt");
            if(!file.exists()){
                T.log("No NeuroNet found. Creating new file...");
                file.createNewFile();
                T.log(file.getAbsolutePath());
                BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
                NeuroNetwork.writeNewNeuroNet(bw);
                bw.close();
                T.log("Finished creating new NeuroNet!");
            }else{
                T.log("Found Player NeuroNet!");
                BufferedReader br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
                NeuroNetwork.readNeuroNet(br);
                br.close();
                T.log("Finished reading NeuroNet!");
            }
        }catch(IOException e){
            T.log(e);
        }
    }
}
