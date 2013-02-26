package sin.world;

import com.jme3.bullet.BulletAppState;
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
import java.util.ArrayList;
import java.util.HashMap;
import sin.GameClient;
import sin.netdata.GeometryData;
import sin.tools.T;

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
 * World - Used for geometry creation and world generation.
 * @author SinisteRing
 */
public class World {
    private static GameClient app;
    
    private static HashMap<Vector3f, String> world = new HashMap();
    private static ArrayList<GeometryData> map = new ArrayList();
    
    // Constant Variables:
    public static final float ZONE_WIDTH = 10;
    public static final float ZONE_HEIGHT = 5;
    public static final int ZONE_VARIATIONS = 3;
    public static final int ZONE_X_NUM = 10;
    public static final int ZONE_Y_NUM = 4;
    public static final int ZONE_Z_NUM = 10;
    
    public static BulletAppState getBulletAppState(){
        return app.getBulletAppState();
    }
    
    public static ArrayList<GeometryData> getMap(){
        return map;
    }
    
    public static GeometryData geoData(String type, Vector3f size, Vector3f trans, Quaternion rot, String tex, Vector2f scale, boolean phy){
        return new GeometryData(type, size, trans, rot, tex, scale, phy);
    }
    
    public static void generateHallway(){
        int i = 1;
        float height = 0;
        float rng;
        while(i < 10){
            rng = FastMath.nextRandomFloat();
            if(rng < 0.7f){ // Flat
                map.add(geoData("box", T.v3f(ZONE_WIDTH, 1f, ZONE_WIDTH), T.v3f(i*ZONE_WIDTH*2, height, 0), null, T.getMaterial("lava_rock"), T.v2f(1, 1), true));
            }else if(rng < 0.85f){ // Up
                height += ZONE_HEIGHT;
                map.add(geoData("box", T.v3f(ZONE_WIDTH, 1f, ZONE_WIDTH), T.v3f(i*ZONE_WIDTH*2, height-(ZONE_HEIGHT/2.0f), 0),
                        new Quaternion().fromAngleAxis(FastMath.PI/6, Vector3f.UNIT_Z), T.getMaterial("lava_rock"), T.v2f(1, 1), true));
            }else{
                height -= ZONE_HEIGHT;
                map.add(geoData("box", T.v3f(ZONE_WIDTH, 1f, ZONE_WIDTH), T.v3f(i*ZONE_WIDTH*2, height+(ZONE_HEIGHT/2.0f), 0),
                        new Quaternion().fromAngleAxis(-FastMath.PI/6, Vector3f.UNIT_Z), T.getMaterial("lava_rock"), T.v2f(1, 1), true));
            }
            i++;
        }
    }
    
    public static void generateWorldData(){
        map.add(geoData("box", T.v3f(ZONE_WIDTH, 1f, ZONE_WIDTH), T.v3f(0, 0, 0), null, T.getMaterial("lava_rock"), T.v2f(1, 1), true));
        map.add(geoData("box", T.v3f(1f, ZONE_HEIGHT, ZONE_WIDTH), T.v3f(-ZONE_WIDTH, ZONE_HEIGHT, 0), null, T.getMaterial("BC_Tex"), T.v2f(1, 1), true));
        map.add(geoData("box", T.v3f(ZONE_WIDTH, ZONE_HEIGHT, 1f), T.v3f(0, ZONE_HEIGHT, ZONE_WIDTH), null, T.getMaterial("BC_Tex"), T.v2f(1, 1), true));
        map.add(geoData("box", T.v3f(ZONE_WIDTH, ZONE_HEIGHT, 1f), T.v3f(0, ZONE_HEIGHT, -ZONE_WIDTH), null, T.getMaterial("BC_Tex"), T.v2f(1, 1), true));
        world.put(T.v3f(0, 0, 0), "a");
        generateHallway();
    }
    public static void createGeometry(GeometryData d){
        if(d.getType().equals("box")){
            if(d.getPhy()){
                CG.createPhyBox(app.getTerrain(), d);
            }else{
                CG.createBox(app.getTerrain(), d);
            }
        }
    }
    
    /*public static Node genZone(int var, int x, int y, int z){
        Node node = new Node();
        Geometry geo;
        float geoSize = ZONE_WIDTH/2;
        if(var == 0){
            CG.createPhyBox(node, "floor", T.v3f(geoSize, 1f, geoSize), T.v3f(0, 0, 0), "Textures/wall.png", T.v2f(1, 1));
            geo = CG.createPhyCylinder(node, "pillar", geoSize/5, geoSize, T.v3f(0, geoSize/2, 0), "Textures/wall.png", T.v2f(1, 1));
            geo.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.PI/2, Vector3f.UNIT_X));
        }else if(var == 1){
            CG.createPhyBox(node, "floor", T.v3f(geoSize, 1f, geoSize), T.v3f(0, 0, 0), "Textures/brick.png", T.v2f(1, 1));
            CG.createPhyBox(node, "flybox", T.v3f(geoSize/4, geoSize/4, geoSize/4), T.v3f(0, geoSize, 0), "Textures/brick.png", T.v2f(1, 1));
        }else if(var == 2){
            CG.createPhyBox(node, "floor", T.v3f(geoSize, 1f, geoSize), T.v3f(0, 0, 0), "Textures/BC_Tex.png", T.v2f(1, 1));
        }
        node.setLocalTranslation(x*ZONE_WIDTH, y*ZONE_HEIGHT, z*ZONE_WIDTH);
        return node;
    }*/
    
    public static void createSinglePlayerArea(Node node){
        node.setLocalTranslation(0, 100, 0);
        CG.createPhyBox(node, "floor", T.v3f(50, 0.1f, 50), T.v3f(0, 0, 0), T.getMaterial("lava_rock"), T.v2f(5, 5));
        CG.createPhyBox(node, "wall", T.v3f(50, 20, 0.1f), T.v3f(0, 20, -50), T.getMaterial("BC_Tex"), T.v2f(25, 10));
    }
    
    public static void initialize(GameClient app){
        World.app = app;
    }
}
