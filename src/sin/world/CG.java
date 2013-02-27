package sin.world;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.GImpactCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.collision.shapes.SimplexCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import sin.GameClient;
import sin.netdata.GeometryData;

/**
 * CG (Create Geometry) - Used to create geometries of all types without multiple lines of code.
 * @author SinisteRing
 */
public class CG {
    private static GameClient app;
    
    private static Material getMaterial(ColorRGBA color){
            Material m = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            m.setColor("Color", color);
            if(color.getAlpha() < 1){
                m.setTransparent(true);
                m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            }
            return m;
        }
    private static Material getMaterial(String tex){
        Material m = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        m.setTexture("ColorMap", app.getAssetManager().loadTexture(tex));
        m.getTextureParam("ColorMap").getTextureValue().setWrap(Texture.WrapMode.Repeat);
        return m;
    }

    // Boxes:
    public static Geometry createBox(Node node, String name, Vector3f size, Vector3f trans, ColorRGBA color){
        Box b = new Box(trans, size.getX(), size.getY(), size.getZ());
        Geometry g = new Geometry(name, b);
        Material m = getMaterial(color);
        g.setMaterial(m);
        if(node != null) {
            node.attachChild(g);
        }
        return g;
    }
    public static Geometry createBox(Node node, String name, Vector3f size, Vector3f trans, String tex, Vector2f scale){
        Box b = new Box(Vector3f.ZERO, size.getX(), size.getY(), size.getZ());
        b.scaleTextureCoordinates(scale);
        Geometry g = new Geometry(name, b);
        Material m = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        m.setTexture("ColorMap", app.getAssetManager().loadTexture(tex));
        m.getTextureParam("ColorMap").getTextureValue().setWrap(Texture.WrapMode.Repeat);
        g.setMaterial(m);
        g.setLocalTranslation(trans);
        if(node != null) {
            node.attachChild(g);
        }
        return g;
    }
    public static Geometry createBox(Node node, GeometryData d){
        Geometry g = createBox(node, "", d.getSize(), d.getTrans(), d.getTex(), d.getScale());
        if(d.getRot() != null){
            g.rotate(d.getRot());
        }
        return g;
    }
    public static Geometry createPhyBox(Node node, String name, Vector3f size, Vector3f trans, String tex, Vector2f scale){
        Geometry g = createBox(node, name, size, trans, tex, scale);
        CollisionShape cs = CollisionShapeFactory.createMeshShape(g);
        RigidBodyControl rbc = new RigidBodyControl(cs, 0);
        rbc.setKinematic(true);
        g.addControl(rbc);
        app.getBulletAppState().getPhysicsSpace().add(rbc);
        return g;
    }
    public static Geometry createPhyBox(Node node, String name, Vector3f size, Vector3f trans, ColorRGBA color){
        Geometry g = createBox(node, name, size, trans, color);
        CollisionShape cs = CollisionShapeFactory.createMeshShape(g);
        RigidBodyControl rbc = new RigidBodyControl(cs, 0);
        rbc.setKinematic(true);
        g.addControl(rbc);
        app.getBulletAppState().getPhysicsSpace().add(rbc);
        return g;
    }
    public static Geometry createPhyBox(Node node, GeometryData d){
        Geometry g = createPhyBox(node, "", d.getSize(), d.getTrans(), d.getTex(), d.getScale());
        if(d.getRot() != null){
            g.rotate(d.getRot());
        }
        return g;
    }

    // Cylinders:
    public static Geometry createCylinder(Node node, String name, float radius, float length, Vector3f trans, ColorRGBA color, Vector2f scale){
        Cylinder b = new Cylinder(16, 16, radius, length, true);
        b.scaleTextureCoordinates(scale);
        Geometry g = new Geometry(name, b);
        Material m = getMaterial(color);
        g.setMaterial(m);
        g.setLocalTranslation(trans);
        if(node != null) {
            node.attachChild(g);
        }
        return g;
    }
    public static Geometry createCylinder(Node node, String name, float radius, float length, Vector3f trans, String tex, Vector2f scale){
        Cylinder b = new Cylinder(16, 16, radius, length, true);
        b.scaleTextureCoordinates(scale);
        Geometry g = new Geometry(name, b);
        Material m = getMaterial(tex);
        g.setMaterial(m);
        g.setLocalTranslation(trans);
        if(node != null) {
            node.attachChild(g);
        }
        return g;
    }
    public static Geometry createPhyCylinder(Node node, String name, float radius, float length, Vector3f trans, String tex, Vector2f scale){
        Geometry g = createCylinder(node, name, radius, length, trans, tex, scale);
        CollisionShape cs = CollisionShapeFactory.createMeshShape(g);
        RigidBodyControl rbc = new RigidBodyControl(cs, 0);
        rbc.setKinematic(true);
        g.addControl(rbc);
        app.getBulletAppState().getPhysicsSpace().add(rbc);
        return g;
    }

    // Lines:
    public static Geometry createLine(Node node, String name, float width, Vector3f start, Vector3f stop, ColorRGBA color){
        Line b = new Line(start, stop);
        b.setLineWidth(width);
        Geometry g = new Geometry(name, b);
        Material m = getMaterial(color);
        g.setMaterial(m);
        if(node != null) {
            node.attachChild(g);
        }
        return g;
    }
    
    public static Geometry createQuad(Node node, String name, float width, float height, Vector3f trans, ColorRGBA color){
        Quad b = new Quad(width, height);
        Geometry g = new Geometry(name, b);
        Material m = getMaterial(color);
        g.setMaterial(m);
        if(node != null){
            node.attachChild(g);
        }
        return g;
    }
    
    // Spheres:
    public static Geometry createSphere(Node node, String name, float radius, Vector3f trans, ColorRGBA color){
        Sphere b = new Sphere(16, 16, radius);
        Geometry g = new Geometry(name, b);
        Material m = getMaterial(color);
        g.setMaterial(m);
        g.setLocalTranslation(trans);
        if(node != null) {
            node.attachChild(g);
        }
        return g;
    }
    public static Geometry createSphere(Node node, String name, float radius, Vector3f trans, String tex, Sphere.TextureMode mode){
        Sphere b = new Sphere(16, 16, radius);
        b.setTextureMode(mode);
        Geometry g = new Geometry(name, b);
        Material m = getMaterial(tex);
        g.setMaterial(m);
        g.setLocalTranslation(trans);
        if(node != null) {
            node.attachChild(g);
        }
        return g;
    }
    
    public static void initialize(GameClient app){
        CG.app = app;
    }
}
