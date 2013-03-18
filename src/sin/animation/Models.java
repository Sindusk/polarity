package sin.animation;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import java.util.HashMap;
import sin.tools.T;
import sin.world.CG;

/**
 * Models - Creates and manages all Model classes.
 * @author SinisteRing
 */
public class Models {
    public static abstract class ModelTemplate{
        protected HashMap<String, Node> parts = new HashMap();
        private Node node = new Node();
        private String handle;
        
        public ModelTemplate(Node node, String handle){
            node.attachChild(this.node);
            this.handle = handle;
        }
        
        public Node getNode(){
            return node;
        }
        public String getHandle(){
            return handle;
        }
        public Vector3f getLocation(){
            return node.getLocalTranslation();
        }
        public Node getPart(String name){
            return parts.get(name);
        }
        
        public void setLocalTranslation(Vector3f trans){
            node.setLocalTranslation(trans);
        }
        public void addPart(Node part, String name, Vector3f trans){
            part.setLocalTranslation(trans);
            node.attachChild(part);
            parts.put(name, part);
            part.setName(handle+":"+name);
        }
        
        public void destroy(){
            this.getNode().removeFromParent();
        }
    }
    public static class PlayerModel extends ModelTemplate{
        public static Node genHead(){
            Node part = new Node();
            CG.createSphere(part, "head", .7f, T.v3f(0, .5f, 0), T.getMaterialPath("wall"), Sphere.TextureMode.Polar);
            CG.createSphere(part, "neck", .5f, T.v3f(0, .1f, 0), T.getMaterialPath("BC_Tex"), Sphere.TextureMode.Projected);
            return part;
        }
        public static Node genTorso(){
            Node part = new Node();
            CG.createBox(part, "upper_torso", T.v3f(1.2f, .6f, .4f), T.v3f(0, .75f, 0), T.getMaterialPath("BC_Tex"), T.v2f(1, 1));
            CG.createBox(part, "lower_torso", T.v3f(.95f, .4f, .35f), T.v3f(0, -.2f, 0), T.getMaterialPath("BC_Tex"), T.v2f(1, 1));
            float radius = .75f;
            Vector3f trans = T.v3f(-.6f, .85f, 0);
            CG.createSphere(part, "left_pec", radius, trans, T.getMaterialPath("BC_Tex"), Sphere.TextureMode.Projected);
            trans.setX(trans.getX()*-1);
            CG.createSphere(part, "right_pec", radius, trans, T.getMaterialPath("BC_Tex"), Sphere.TextureMode.Projected);
            radius = .5f;
            trans = T.v3f(.3f, -.7f, -.1f);
            CG.createSphere(part, "left_hip", radius, trans, T.getMaterialPath("BC_Tex"), Sphere.TextureMode.Projected);
            trans.setX(trans.getX()*-1);
            CG.createSphere(part, "right_hip", radius, trans, T.getMaterialPath("BC_Tex"), Sphere.TextureMode.Projected);
            return part;
        }
        public static Node genArm(){
            Node part = new Node();
            CG.createSphere(part, "shoulder", .65f, T.v3f(0,0,0), T.getMaterialPath("BC_Tex"), Sphere.TextureMode.Projected);
            Quaternion facing = new Quaternion().fromAngleAxis(-FastMath.PI/2.3f, Vector3f.UNIT_X);
            Geometry g = CG.createCylinder(part, "bicep", .45f, 1.7f, T.v3f(0, -.95f, -.2f), T.getMaterialPath("BC_Tex"), T.v2f(1, 1));
            g.setLocalRotation(facing);
            CG.createSphere(part, "elbow", .5f, T.v3f(0, -1.6f, -.35f), T.getMaterialPath("BC_Tex"), Sphere.TextureMode.Projected);
            CG.createCylinder(part, "arm", .42f, 1.7f, T.v3f(0, -1.65f, .55f), T.getMaterialPath("BC_Tex"), T.v2f(1, 1));
            CG.createBox(part, "hand", T.v3f(.5f, .5f, .5f), T.v3f(0, -1.65f, 1.5f), T.getMaterialPath("BC_Tex"), T.v2f(1, 1));
            return part;
        }
        public static Node genLeg(){
            Node part = new Node();
            CG.createSphere(part, "calf", .5f, T.v3f(0, 0, 0), T.getMaterialPath("BC_Tex"), Sphere.TextureMode.Projected);
            Quaternion facing = new Quaternion().fromAngleAxis(FastMath.PI/2.3f, Vector3f.UNIT_X);
            Geometry g = CG.createCylinder(part, "", .45f, 1.2f, T.v3f(0, -.5f, .1f), T.getMaterialPath("BC_Tex"), T.v2f(1, 1));
            g.setLocalRotation(facing);
            CG.createSphere(part, "knee", .5f, T.v3f(0, -1.2f, .2f), T.getMaterialPath("BC_Tex"), Sphere.TextureMode.Projected);
            facing = new Quaternion().fromAngleAxis(FastMath.PI/1.8f, Vector3f.UNIT_X);
            g = CG.createCylinder(part, "leg", .45f, 1.2f, T.v3f(0, -1.8f, .1f), T.getMaterialPath("BC_Tex"), T.v2f(1, 1));
            g.setLocalRotation(facing);
            CG.createSphere(part, "ankle", .5f, T.v3f(0, -2.3f, 0), T.getMaterialPath("BC_Tex"), Sphere.TextureMode.Projected);
            CG.createBox(part, "foot", T.v3f(.4f, .2f, 1), T.v3f(0, -3f, .3f), T.getMaterialPath("BC_Tex"), T.v2f(1, 1));
            return part;
        }
        
        public PlayerModel(int id, Node node){
            super(node, "player:"+id);
            this.addPart(genHead(), "head", T.v3f(0, 1.75f, 0));
            this.addPart(genTorso(), "torso", Vector3f.ZERO);
            this.addPart(genArm(), "arm.left", T.v3f(-1.2f, 1.1f, 0));
            this.addPart(genArm(), "arm.right", T.v3f(1.2f, 1.1f, 0));
            this.addPart(genLeg(), "leg.left", T.v3f(-0.5f, -1, 0));
            this.addPart(genLeg(), "leg.right", T.v3f(0.5f, -1, 0));
        }
        
        public void update(Vector3f locA, Vector3f locB, Quaternion rot, float tpf, float interp){
            float[] angles = new float[3];
            rot.toAngles(angles);
            this.getNode().setLocalTranslation(locA.clone().interpolate(locB, interp));
            this.getNode().setLocalRotation(new Quaternion().fromAngles(0, angles[1], 0));
            parts.get("head").setLocalRotation(new Quaternion().fromAngles(angles[0], 0, 0));
            parts.get("arm.left").setLocalRotation(new Quaternion().fromAngles(angles[0], 0, 0));
            parts.get("arm.right").setLocalRotation(new Quaternion().fromAngles(angles[0], 0, 0));
        }
    }
    public static class EntityModel extends ModelTemplate{
        public static Node genHead(){
            Node part = new Node();
            CG.createBox(part, "", new Vector3f(0.8f, 0.8f, 1.8f), Vector3f.ZERO, T.getMaterialPath("BC_Tex"), new Vector2f(1, 1));
            return part;
        }
        public static Node genBody(){
            Node part = new Node();
            CG.createBox(part, "", new Vector3f(1, 3, 1), Vector3f.ZERO, T.getMaterialPath("brick"), new Vector2f(1, 1));
            return part;
        }
        
        public EntityModel(int id, Node node, String type){
            super(node, "npc:"+type+":"+id);
            this.addPart(genHead(), "head", new Vector3f(0, 3, 0));
            this.addPart(genBody(), "body", Vector3f.ZERO);
        }
    }
}
