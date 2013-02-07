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
public class ConnectData extends AbstractMessage {
    private int id;
    private String version;
    public ConnectData() {}
    public ConnectData(String ver){
        version = ver;
        this.setReliable(true);
    }
    public ConnectData(int id){
        this.id = id;
    }
    public int getID(){
        return id;
    }
    public String GetVersion(){
        return version;
    }
}
