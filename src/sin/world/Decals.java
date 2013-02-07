/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.world;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import sin.tools.T;

/**
 *
 * @author SinisteRing
 */
public class Decals{
    // Constant Variables:
    private static final int   DECAL_NUM = 150;
    private static final float DECAL_SIZE = 0.2f;

    // Instance Variables:
    private Node node = new Node();
    private int next = 0;
    private Geometry[] decal = new Geometry[DECAL_NUM];

    public Decals(){
        //rootNode.attachChild(node);
    }

    public Node getNode(){
        return node;
    }
    public void createDecal(Vector3f loc){
        decal[next].setLocalTranslation(loc);
        next++;
        if(next >= decal.length){
            next = 0;
        }
    }
    public void resetDecals(){
        int i = 0;
        while(i < decal.length){
            decal[i].setLocalTranslation(Vector3f.ZERO);
            i++;
        }
    }
    public void initialize(){
        int i = 0;
        while(i < decal.length){
            decal[i] = World.CG.createSphere(node, "decal", DECAL_SIZE, T.v3f(0, 0, 0), ColorRGBA.Black);
            i++;
        }
        //rootNode.attachChild(node);
    }
}
