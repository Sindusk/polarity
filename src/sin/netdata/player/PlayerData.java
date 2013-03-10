package sin.netdata.player;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class PlayerData extends AbstractMessage {
    private int id;
    private String weapons;
    public PlayerData() {}
    public PlayerData(int id, String weapons){
        this.id = id;
        this.weapons = weapons;
    }
    public int getID(){
        return id;
    }
    public String getWeapons(){
        return weapons;
    }
}
