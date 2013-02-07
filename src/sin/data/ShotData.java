/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.data;

import com.jme3.audio.AudioNode;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.jme3.scene.Node;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class ShotData extends AbstractMessage {
    private int id;
    private int player;
    private float damage;
    public ShotData(){
        
    }
    public ShotData(int id, int player, float damage){
        this.id = id;
        this.player = player;
        this.damage = damage;
        this.setReliable(true);
    }
    public int getID(){
        return id;
    }
    public int getPlayer(){
        return player;
    }
    public float getDamage(){
        return damage;
    }
}
