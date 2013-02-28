package sin.animation;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import java.util.HashMap;
import sin.GameClient;
import sin.tools.T;
import sin.world.CG;
import sin.world.World;

/**
 *
 * @author SinisteRing
 */
public class Models {
    public static GameClient app;
    
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
        public Node getPart(String name){
            return parts.get(name);
        }
        
        public void addPart(Node part, String name, Vector3f trans){
            part.setLocalTranslation(trans);
            node.attachChild(part);
            parts.put(name, part);
            part.setName(handle+":"+name);
        }
    }
    public static class PlayerModel extends ModelTemplate{
        // Instance Variables:
        private RigidBodyControl rbc;

        public static Node genHead(){
            Node part = new Node();
            CG.createSphere(part, "", .7f, T.v3f(0, .5f, 0), T.getMaterial("wall"), Sphere.TextureMode.Polar);
            CG.createSphere(part, "", .5f, T.v3f(0, .1f, 0), T.getMaterial("BC_Tex"), Sphere.TextureMode.Projected);
            return part;
        }
        public static Node genTorso(){
            Node part = new Node();
            CG.createBox(part, "", T.v3f(1.2f, .6f, .4f), T.v3f(0, .75f, 0), T.getMaterial("BC_Tex"), T.v2f(1, 1));
            CG.createBox(part, "", T.v3f(.95f, .4f, .35f), T.v3f(0, -.2f, 0), T.getMaterial("BC_Tex"), T.v2f(1, 1));
            float radius = .75f;
            Vector3f trans = T.v3f(-.6f, .85f, 0);
            CG.createSphere(part, "", radius, trans, T.getMaterial("BC_Tex"), Sphere.TextureMode.Projected);
            trans.setX(trans.getX()*-1);
            CG.createSphere(part, "", radius, trans, T.getMaterial("BC_Tex"), Sphere.TextureMode.Projected);
            radius = .5f;
            trans = T.v3f(.3f, -.7f, -.1f);
            CG.createSphere(part, "", radius, trans, T.getMaterial("BC_Tex"), Sphere.TextureMode.Projected);
            trans.setX(trans.getX()*-1);
            CG.createSphere(part, "", radius, trans, T.getMaterial("BC_Tex"), Sphere.TextureMode.Projected);
            return part;
        }
        public static Node genArm(){
            Node part = new Node();
            CG.createSphere(part, "", .65f, T.v3f(0,0,0), T.getMaterial("BC_Tex"), Sphere.TextureMode.Projected);
            Quaternion facing = new Quaternion().fromAngleAxis(-FastMath.PI/2.3f, Vector3f.UNIT_X);
            Geometry g = CG.createCylinder(part, "", .45f, 1.7f, T.v3f(0, -.95f, -.2f), T.getMaterial("BC_Tex"), T.v2f(1, 1));
            g.setLocalRotation(facing);
            CG.createSphere(part, "", .5f, T.v3f(0, -1.6f, -.35f), T.getMaterial("BC_Tex"), Sphere.TextureMode.Projected);
            CG.createCylinder(part, "", .42f, 1.7f, T.v3f(0, -1.65f, .55f), T.getMaterial("BC_Tex"), T.v2f(1, 1));
            CG.createBox(part, "", T.v3f(.5f, .5f, .5f), T.v3f(0, -1.65f, 1.5f), T.getMaterial("BC_Tex"), T.v2f(1, 1));
            return part;
        }
        public static Node genLeg(){
            Node part = new Node();
            CG.createSphere(part, "", .5f, T.v3f(0, 0, 0), T.getMaterial("BC_Tex"), Sphere.TextureMode.Projected);
            Quaternion facing = new Quaternion().fromAngleAxis(FastMath.PI/2.3f, Vector3f.UNIT_X);
            Geometry g = CG.createCylinder(part, "", .45f, 1.2f, T.v3f(0, -.5f, .1f), T.getMaterial("BC_Tex"), T.v2f(1, 1));
            g.setLocalRotation(facing);
            CG.createSphere(part, "", .5f, T.v3f(0, -1.2f, .2f), T.getMaterial("BC_Tex"), Sphere.TextureMode.Projected);
            facing = new Quaternion().fromAngleAxis(FastMath.PI/1.8f, Vector3f.UNIT_X);
            g = CG.createCylinder(part, "", .45f, 1.2f, T.v3f(0, -1.8f, .1f), T.getMaterial("BC_Tex"), T.v2f(1, 1));
            g.setLocalRotation(facing);
            CG.createSphere(part, "", .5f, T.v3f(0, -2.3f, 0), T.getMaterial("BC_Tex"), Sphere.TextureMode.Projected);
            CG.createBox(part, "", T.v3f(.4f, .2f, 1), T.v3f(0, -3f, .3f), T.getMaterial("BC_Tex"), T.v2f(1, 1));
            return part;
        }

        public PlayerModel(Node node, int id){
            super(node, "player:"+id);
            this.addPart(genHead(), "head", T.v3f(0, 1.75f, 0));
            this.addPart(genTorso(), "torso", Vector3f.ZERO);
            this.addPart(genArm(), "arm.left", T.v3f(-1.2f, 1.1f, 0));
            this.addPart(genArm(), "arm.right", T.v3f(1.2f, 1.1f, 0));
            this.addPart(genLeg(), "leg.left", T.v3f(-0.5f, -1, 0));
            this.addPart(genLeg(), "leg.right", T.v3f(0.5f, -1, 0));
        }
        
        public void update(Vector3f loc, Quaternion rot){
            float[] angles = new float[3];
            rot.toAngles(angles);
            this.getNode().setLocalTranslation(loc);
            this.getNode().setLocalRotation(new Quaternion().fromAngles(0, angles[1], 0));
            parts.get("head").setLocalRotation(new Quaternion().fromAngles(angles[0], 0, 0));
            parts.get("arm.left").setLocalRotation(new Quaternion().fromAngles(angles[0], 0, 0));
            parts.get("arm.right").setLocalRotation(new Quaternion().fromAngles(angles[0], 0, 0));
        }
        public void create(){
            CollisionShape cs = CollisionShapeFactory.createMeshShape(this.getNode());
            rbc = new RigidBodyControl(cs, 1);
            rbc.setKinematic(true);
            this.getNode().addControl(rbc);
            //app.getBulletAppState().getPhysicsSpace().add(rbc);
        }
        public void destroy(){
            this.getNode().removeControl(rbc);
            this.getNode().removeFromParent();
            World.getBulletAppState().getPhysicsSpace().remove(rbc);
        }
    }
    
    public static void initialize(GameClient app){
        Models.app = app;
    }
}
