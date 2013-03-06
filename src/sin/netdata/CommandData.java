package sin.netdata;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class CommandData extends AbstractMessage {
    private String command;
    public CommandData() {}
    public CommandData(String command){
        this.command = command;
        this.setReliable(true);
    }
    public String getCommand(){
        return command;
    }
}
