package sin.data;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author SinisteRing
 */
@Serializable
public class MoveData extends AbstractMessage {
  private int id;           //Client ID
  private Vector3f loc;     // Message Data
  private Quaternion rot;
  public MoveData() {}
  public MoveData(int ID, Vector3f location, Quaternion rotation){
      loc = location;
      rot = rotation;
      id = ID;
  }
  public int getID(){
      return id;
  }
  public Vector3f getLocation(){
      return loc;
  }
  public Quaternion getRotation(){
      return rot;
  }
}
