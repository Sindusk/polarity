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
public class PingData extends AbstractMessage {
    public PingData(){}
}
