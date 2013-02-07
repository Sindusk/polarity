/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.network;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class WorldData extends AbstractMessage {
    private int[][] world;
    public WorldData(){
        //
    }
    public WorldData(int[][] world){
        this.world = world;
        this.setReliable(true);
    }
    public int[][] getWorld(){
        return world;
    }
}
