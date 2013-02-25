package sin.netdata;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class SoundData extends AbstractMessage {
    private int id;
    private String sound;
    public SoundData() {}
    public SoundData(int id, String sound){
        this.id = id;
        this.sound = sound;
    }
    public int getID(){
        return id;
    }
    public String getSound(){
        return sound;
    }
}
