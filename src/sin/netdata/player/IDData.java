/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.netdata.player;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class IDData extends AbstractMessage {
    private int id = -1;
    private boolean confirm = false;
    public IDData() {}
    public IDData(int ID, boolean confirmed){
        id = ID;
        confirm = confirmed;
        this.setReliable(true);
    }
    public int getID(){
        return id;
    }
    public boolean getConfirmed(){
        return confirm;
    }
}
