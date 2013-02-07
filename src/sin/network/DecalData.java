/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.network;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class DecalData extends AbstractMessage {
    private Vector3f loc;
    public DecalData(){
        //
    }
    public DecalData(Vector3f loc){
        this.loc = loc;
    }
    public Vector3f getLocation(){
        return loc;
    }
}
