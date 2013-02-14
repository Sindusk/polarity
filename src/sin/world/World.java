/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sin.world;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture.WrapMode;
import sin.GameClient;

/**
Copyright (c) 2003-2011 jMonkeyEngine
All rights reserved.
 
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 
Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
 
Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
 
Neither the name of 'jMonkeyEngine' nor the names of its contributors 
may be used to endorse or promote products derived from this software 
without specific prior written permission.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * World Generation
 * @author SinisteRing
 */
public class World {
    private static GameClient app;
    
    // Constant Variables:
    public static final float ZONE_SIZE = 50;
    public static final int ZONE_VARIATIONS = 3;
    public static final int ZONE_X_NUM = 10;
    public static final int ZONE_Z_NUM = 10;
    
    //private static AssetManager assetManager;
    //private static BulletAppState bulletAppState;
    
    private static Vector3f v3f(float x, float y, float z){
        return new Vector3f(x, y, z);
    }
    private static Vector2f v2f(float x, float y){
        return new Vector2f(x, y);
    }
    
    // Create Geometry class:
    public static class CG{
        private static Material getMaterial(ColorRGBA color){
            Material m = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            m.setColor("Color", color);
            if(color.getAlpha() < 1){
                m.setTransparent(true);
                m.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            }
            return m;
        }
        private static Material getMaterial(String tex){
            Material m = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            m.setTexture("ColorMap", app.getAssetManager().loadTexture(tex));
            m.getTextureParam("ColorMap").getTextureValue().setWrap(WrapMode.Repeat);
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
            Box b = new Box(trans, size.getX(), size.getY(), size.getZ());
            b.scaleTextureCoordinates(scale);
            Geometry g = new Geometry(name, b);
            Material m = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            m.setTexture("ColorMap", app.getAssetManager().loadTexture(tex));
            m.getTextureParam("ColorMap").getTextureValue().setWrap(WrapMode.Repeat);
            g.setMaterial(m);
            if(node != null) {
                node.attachChild(g);
            }
            return g;
        }
        public static Geometry createPhyBox(Node node, String name, Vector3f size, Vector3f trans, String tex, Vector2f scale){
            Geometry g = createBox(node, name, size, trans, tex, scale);
            CollisionShape cs = CollisionShapeFactory.createMeshShape(g);
            RigidBodyControl rbc = new RigidBodyControl(cs, 0);
            rbc.setKinematic(true);
            g.addControl(rbc);
            GameClient.getBulletAppState().getPhysicsSpace().add(rbc);
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
            GameClient.getBulletAppState().getPhysicsSpace().add(rbc);
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
    }
    
    public static Node genZone(int var, int x, int z){
        Node node = new Node();
        Geometry geo;
        float geoSize = ZONE_SIZE/2;
        if(var == 0){
            CG.createPhyBox(node, "floor", v3f(geoSize, .1f, geoSize), v3f(0, 0, 0), "Textures/wall.png", v2f(1, 1));
            geo = CG.createPhyCylinder(node, "pillar", geoSize/5, geoSize, v3f(0, geoSize/2, 0), "Textures/wall.png", v2f(1, 1));
            geo.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.PI/2, Vector3f.UNIT_X));
        }else if(var == 1){
            CG.createPhyBox(node, "floor", v3f(geoSize, .1f, geoSize), v3f(0, 0, 0), "Textures/brick.png", v2f(1, 1));
            CG.createPhyBox(node, "flybox", v3f(geoSize/4, geoSize/4, geoSize/4), v3f(0, geoSize, 0), "Textures/brick.png", v2f(1, 1));
        }else if(var == 2){
            CG.createPhyBox(node, "floor", v3f(geoSize, .1f, geoSize), v3f(0, 0, 0), "Textures/BC_Tex.png", v2f(1, 1));
        }
        node.setLocalTranslation(x*ZONE_SIZE, 0, z*ZONE_SIZE);
        return node;
    }
    
    public static void initialize(GameClient app){
        World.app = app;
        //World.assetManager = assetManager;
        //World.bulletAppState = bulletAppState;
    }
    public static void createSinglePlayerArea(Node node){
        node.setLocalTranslation(0, 100, 0);
        CG.createPhyBox(node, "floor", v3f(50, 0.1f, 50), v3f(0, 0, 0), "Textures/BC_Tex.png", v2f(5, 5));
        CG.createPhyBox(node, "wall", v3f(50, 20, 0.1f), v3f(0, 20, -50), "Textures/brick.png", v2f(50, 20));
    }
    public static void create(int[][] world, Node node){
        int x = 0;
        int z;
        while(x < world.length){
            z = 0;
            while(z < world[x].length){
                if(!(x == world.length/2 && z == world[x].length/2)) {
                    node.attachChild(genZone(world[x][z], x-(world.length/2), z-(world[x].length/2)));
                }else{
                    CG.createPhyBox(node, "Start", v3f(World.ZONE_SIZE/2, 5, World.ZONE_SIZE/2), v3f(0, 0, 0), "Textures/BC_Tex.png", v2f(1, 1));
                }
                z++;
            }
            x++;
        }
    }
}
