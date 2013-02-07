/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.data;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class ErrorData extends AbstractMessage {
    private int id = -1;
    private String error;
    public boolean disconnect = false;
    public ErrorData() {}
    public ErrorData(int ID, String err, boolean dc){
        id = ID;
        error = err;
        disconnect = dc;
        this.setReliable(true);
    }
    public int getID(){
        return id;
    }
    public String getError(){
        return error;
    }
}
