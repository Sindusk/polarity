package sin;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext.Type;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import sin.hud.HUD;
import sin.network.Networking;
import sin.player.Char;
import sin.player.Player;
import sin.tools.T;
import sin.weapons.Weapons;
import sin.weapons.Weapons.RangedWeapon.AK47;
import sin.weapons.Weapons.RangedWeapon.LaserPistol;
import sin.weapons.Weapons.RangedWeapon.M4A1;
import sin.weapons.Weapons.RangedWeapon.Raygun;
import sin.world.World;
import sin.world.World.CG;


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

 * Game Client
 * @author SinisteRing
 */

public class GameClient extends Application{

    // --- Global Constant Variables --- //
    // Network Variables:
    private Node guiNode = new Node("Gui Node");
    private Node rootNode = new Node("Root Node");
    public static final String INPUT_MAPPING_EXIT = "SIMPLEAPP_Exit";
    public static final String INPUT_MAPPING_CAMERA_POS = "SIMPLEAPP_CameraPos";
    public static final String INPUT_MAPPING_MEMORY = "SIMPLEAPP_Memory";
    public static final String INPUT_MAPPING_HIDE_STATS = "SIMPLEAPP_HideStats";
    
    public static final String CLIENT_VERSION = "ALPHA 0.04";
    
    // Important System Variables:
    private static boolean CLIENT_KEYS_CLEARED = false;
    public static final Logger logger = Logger.getLogger(GameClient.class.getName());
    public static BulletAppState bulletAppState;    // Physics State.
    private static GameClient app;                  // The application itself (this).
    
    // Nodes:
    public static Node collisionNode = new Node();      // Node encompassing anything able to be shot [single, world, player].
    private static Node singleNode = new Node();        // Node encompassing single player testing (Static).
    private static Node worldNode = new Node();         // Node encompassing terrain and environment (Static).
    public static Node playerNode = new Node();         // Node encompassing player models (Kinematic).
    public static Node tracerNode = new Node();         // Node encompassing tracers, mainly for testing.
    
    // Custom Variables:
    public static Char character;                     // Character data for the current client.
    public static Player[] players = new Player[16];        // Array of networked players.
    private static Networking network = new Networking();   // Class for controlling Networking.
    public Recoil recoil = new Recoil();               // Class for controlling Camera movement (recoil/decoil).
    public HUD hud = new HUD();                         // Class for controlling User Interface & HUD.
    public DecalSystem dcs = new DecalSystem();         // Class for controlling Bullet Decals.
    private InputHandler input = new InputHandler();    // Class for handling all forms of input.
    
    // --- Bullet Decals --- //
    public class DecalSystem{
        // Constant Variables:
        private static final int   DECAL_NUM = 150;
        private static final float DECAL_SIZE = 0.2f;
        
        // Instance Variables:
        private Node node = new Node();
        private int next = 0;
        private Geometry[] decal = new Geometry[DECAL_NUM];
        
        public DecalSystem(){
            //rootNode.attachChild(node);
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
                decal[i] = CG.createSphere(node, "decal", DECAL_SIZE, T.v3f(0, 0, 0), ColorRGBA.Black);
                i++;
            }
            //rootNode.attachChild(node);
        }
    }
    // --- Recoil & Spread --- //
    public class Recoil{
        // Constant Variables:
        private static final float RECOIL_SENSITIVITY = 1;
        private static final float RECOIL_UP_INC = FastMath.PI*0.0001f;
        private static final float RECOIL_LEFT_INC = FastMath.PI*0.0003f;
        private static final float DECOIL_UP_PERC_MULT = 3;
        private static final float DECOIL_LEFT_PERC_MULT = 3;
        
        // Index Holders:
        private static final int UP = 0;
        private static final int UP_TOTAL = 1;
        private static final int LEFT = 2;
        private static final int LEFT_TOTAL = 3;
        // Recoil: 0 = up, 1 = up-total, 2 = left, 3 = left-total
        public float recoil[] = new float[]{0, 0, 0, 0};
        
        public void rotateCamera(float value, float sensitivity, Vector3f axis){
            Matrix3f mat = new Matrix3f();
            mat.fromAngleNormalAxis(sensitivity * value, axis);

            Vector3f up = cam.getUp();
            Vector3f left = cam.getLeft();
            Vector3f dir = cam.getDirection();
            Quaternion quat = new Quaternion();
            quat.lookAt(dir, up);

            mat.mult(up, up);
            mat.mult(left, left);
            mat.mult(dir, dir);

            Quaternion q = new Quaternion();
            q.fromAxes(left, up, dir);
            q.normalizeLocal();
            
            float angleY = dir.angleBetween(Vector3f.UNIT_Y);
            float angleYDegree = angleY * 180 / FastMath.PI ;
            if(angleYDegree>=5 && angleYDegree<=175 && up.y>=0) {
                cam.setAxes(q);
            }
        }
        
        public void RecoilUp(float mod){
            cam.getRotation().multLocal(new Quaternion().fromAngleAxis(-RECOIL_UP_INC*mod, Vector3f.UNIT_X));
            
            // Update variables:
            recoil[UP] += RECOIL_UP_INC*mod;
            recoil[UP_TOTAL] = recoil[UP];
        }
        public void RecoilLeft(float mod){
            rotateCamera(RECOIL_LEFT_INC*mod, RECOIL_SENSITIVITY, Vector3f.UNIT_Y);
            
            // Update variables:
            recoil[LEFT] += RECOIL_LEFT_INC*mod;
            recoil[LEFT_TOTAL] = recoil[LEFT];
        }
        public void DecoilUp(float mod){
            cam.getRotation().multLocal(new Quaternion().fromAngleAxis(-mod, Vector3f.UNIT_X));
        }
        public void DecoilLeft(float mod){
            rotateCamera(mod, RECOIL_SENSITIVITY, Vector3f.UNIT_Y);
        }
        
        public Recoil(){
            //
        }
        
        public float getSpreadMod(){
            return (FastMath.abs(recoil[UP])+FastMath.abs(recoil[LEFT]))*100;
        }
        public float getRecoil(boolean up){
            if(up) {
                return recoil[UP];
            }
            else {
                return recoil[LEFT];
            }
        }
        public void recoil(float up, float left){
            RecoilUp(up);
            RecoilLeft(left);
        }
        public void decoil(float tpf){
            if(recoil[UP] != 0){
                float decoil_up = RECOIL_UP_INC*tpf;
                float decoil_up_perc = recoil[UP_TOTAL]*DECOIL_UP_PERC_MULT*tpf;
                if(recoil[UP] < 0){
                    decoil_up *= -1;
                    //decoil_up_perc *= -1;
                }
                decoil_up += decoil_up_perc;
                if(FastMath.abs(recoil[UP]) < FastMath.abs(decoil_up)){
                    decoil_up = recoil[UP];
                    recoil[UP] = 0;
                }else{
                    recoil[UP] -= decoil_up;
                }
                DecoilUp(-decoil_up);
            }
            
            if(recoil[LEFT] != 0){
                float decoil_left = RECOIL_LEFT_INC*tpf;
                float decoil_left_perc = recoil[LEFT_TOTAL]*DECOIL_LEFT_PERC_MULT*tpf;
                if(recoil[LEFT] < 0){
                    decoil_left *= -1;
                    //decoil_left_perc *= -1;
                }
                decoil_left += decoil_left_perc;
                if(FastMath.abs(recoil[LEFT]) < FastMath.abs(decoil_left)){
                    decoil_left = recoil[LEFT];
                    recoil[LEFT] = 0;
                }else{
                    recoil[LEFT] -= decoil_left;
                }
                DecoilLeft(-decoil_left);
            }
        }
    }
    
    // --- Input Handling --- //
    private class InputHandler implements ActionListener, AnalogListener{
        // Constant Variables:
        public static final float MOUSE_SENSITIVITY = 1;
        public void onAction(String bind, boolean down, float tpf) {
            // Movement:
            if(bind.equals("Move_Left")){
                character.movement[Char.MOVE_LEFT] = down;
            }else if(bind.equals("Move_Right")){
                character.movement[Char.MOVE_RIGHT] = down;
            }else if(bind.equals("Move_Forward")){
                character.movement[Char.MOVE_FORWARD] = down;
            }else if(bind.equals("Move_Backward")){
                character.movement[Char.MOVE_BACKWARD] = down;
            }else if(bind.equals("Move_Crouch")){
                character.movement[Char.MOVE_CROUCH] = down;
            }else if(bind.equals("Move_Jump") && down){
                character.getPlayer().jump();
            }
            // Actions:
            else if(bind.equals("Trigger_Right")){
                character.setFiring(false, down);
            }else if(bind.equals("Trigger_Left")){
                character.setFiring(true, down);
            }else if(bind.equals("Trigger_Reload")){
                if(down) {
                    character.reload();
                }
            }
            if(down){
                // Weapon Swapping:
                if(bind.equals("Swap_1")){
                    character.swapGuns();
                }
                // Miscellaneous:
                else if(bind.equals("Server_Connect")){
                    // Networking:
                    network.connect();
                }else if(bind.equals("Misc_Key_1")){
                    if(rootNode.hasChild(tracerNode)) {
                        rootNode.detachChild(tracerNode);
                    }
                    else {
                        rootNode.attachChild(tracerNode);
                    }
                }else if(bind.equals("Misc_Key_2")){
                    tracerNode.detachAllChildren();
                    dcs.resetDecals();
                }else if(bind.equals("Misc_Key_3")){
                    players[4].create();
                    players[4].move(T.v3f(0, 105, -45), Quaternion.ZERO);
                }else if(bind.equals("Misc_Key_4")){
                    //hud.bar[0].update(30);
                }else if(bind.equals("Exit")){
                    stop();
                }
            }
        }

        public void onAnalog(String name, float value, float tpf) {
            // Camera:
            if (name.equals("Cam_Left")){
                recoil.rotateCamera(value, MOUSE_SENSITIVITY, Vector3f.UNIT_Y);
            }else if (name.equals("Cam_Right")){
                recoil.rotateCamera(-value, MOUSE_SENSITIVITY, Vector3f.UNIT_Y);
            }else if (name.equals("Cam_Up")){
                recoil.rotateCamera(-value, MOUSE_SENSITIVITY, cam.getLeft());
            }else if (name.equals("Cam_Down")){
                recoil.rotateCamera(value, MOUSE_SENSITIVITY, cam.getLeft());
            }
        }
        
        private void createMapping(String name, KeyTrigger trigger){
            inputManager.addMapping(name, trigger);
            inputManager.addListener(this, name);
        }
        private void createMapping(String name, MouseButtonTrigger trigger){
            inputManager.addMapping(name, trigger);
            inputManager.addListener(this, name);
        }
        private void createMapping(String name, MouseAxisTrigger trigger){
            inputManager.addMapping(name, trigger);
            inputManager.addListener(this, name);
        }
        public void initialize(){
            // Camera:
            createMapping("Cam_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
            createMapping("Cam_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false));
            createMapping("Cam_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
            createMapping("Cam_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
            // Movement:
            createMapping("Move_Left", new KeyTrigger(KeyInput.KEY_A));
            createMapping("Move_Right", new KeyTrigger(KeyInput.KEY_D));
            createMapping("Move_Forward", new KeyTrigger(KeyInput.KEY_W));
            createMapping("Move_Backward", new KeyTrigger(KeyInput.KEY_S));
            createMapping("Move_Crouch", new KeyTrigger(KeyInput.KEY_LCONTROL));
            createMapping("Move_Jump", new KeyTrigger(KeyInput.KEY_SPACE));
            // Actions:
            createMapping("Trigger_Left", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
            createMapping("Trigger_Right", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
            createMapping("Trigger_Reload", new KeyTrigger(KeyInput.KEY_R));
            // Weapon Swapping:
            createMapping("Swap_1", new KeyTrigger(KeyInput.KEY_Q));
            // Miscellaneous:
            createMapping("Server_Connect", new KeyTrigger(KeyInput.KEY_C));
            createMapping("Game_Menu", new KeyTrigger(KeyInput.KEY_ESCAPE));
            createMapping("Misc_Key_1", new KeyTrigger(KeyInput.KEY_V));
            createMapping("Misc_Key_2", new KeyTrigger(KeyInput.KEY_B));
            createMapping("Misc_Key_3", new KeyTrigger(KeyInput.KEY_O));
            createMapping("Misc_Key_4", new KeyTrigger(KeyInput.KEY_I));
            // Menu/Swapping:
            if(context.getType() == Type.Display){
                createMapping("Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
            }
        }
    }
    public Node getRoot(){
        return rootNode;
    }
    public Node getGUI(){
        return guiNode;
    }
    public Node getWorld(){
        return worldNode;
    }
    public Recoil getRecoil(){
        return recoil;
    }
    public AppSettings getSettings(){
        return settings;
    }
    public BulletAppState getBulletAppState(){
        return bulletAppState;
    }
    
    // Main:
    @Override
    public void start() {
        super.start();
    }
    public static void main(String[] args) throws IOException {
        Logger.getLogger("com.jme3").setLevel(Level.WARNING);
        try {
            logger.addHandler(new FileHandler("FPSlog.xml"));
            Logger.getLogger("com.jme3").addHandler(new FileHandler("FPSlog2.xml"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        app = new GameClient();
        AppSettings set = new AppSettings(true);
        set.setResolution(1280, 720);
        set.setSamples(0);
        set.setVSync(false);
        set.setRenderer(AppSettings.LWJGL_OPENGL1);
        set.setTitle("Sin's FPS Game");
        app.setSettings(set);
        app.start();
        
    }

    // Initialization:
    @Override
    public void initialize(){
        super.initialize();

        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        viewPort.attachScene(rootNode);
        guiViewPort.attachScene(guiNode);
        
        // Tune logger down to warnings and worse:
        Logger.getLogger("com.jme3").setLevel(Level.WARNING);
        
        // Initialize physics and attach to state manager:
        bulletAppState = new BulletAppState();  
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setAccuracy(0.01f);
        
        // Initialize keybinds.
        input.initialize();
        
        // Initialize the World class and create a basic place:
        World.initialize(assetManager, bulletAppState);
        World.createSinglePlayerArea(singleNode);
        collisionNode.attachChild(singleNode);
        collisionNode.attachChild(worldNode);
        collisionNode.attachChild(playerNode);
        rootNode.attachChild(collisionNode);
        
        // Initialize new HUD & remove debug HUD elements:
        Networking.initialize(app);
        Weapons.initialize(app);
        Player.initialize(app);
        Char.initialize(app);
        hud.initialize(app);
        dcs.initialize();
        rootNode.attachChild(dcs.node);
        viewPort.setBackgroundColor(ColorRGBA.Black);
        setPauseOnLostFocus(false);
        //setDisplayFps(false);
        
        // Create the player character:
        character = new Char(
                new M4A1(true), new LaserPistol(false),
                new Raygun(true), new AK47(false), 100, 100);
        
        // Create all the player characters.
        int i = 0;
        while(i < 16){
            players[i] = new Player(i, T.v3f(0, 10, 0));
            i++;
        }
    }
    
    // Update:
    @Override
    public void update() {
        super.update(); // makes sure to execute AppTasks
        if (speed == 0 || paused) {
            return;
        }

        float tpf = timer.getTimePerFrame() * speed;
        // update states
        stateManager.update(tpf);

        // Clear client keys which are buggy and annoying:
        if(!CLIENT_KEYS_CLEARED) {
            ClearClientKeys();
        }
        
        // Update audio listeners:
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
        
        // Update character location & hud:
        character.update(tpf);
        hud.update(tpf);
        
        // Update network if connected:
        if(Networking.isConnected()) {
            Networking.update(tpf);
        }
        rootNode.updateLogicalState(tpf);
        guiNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
        guiNode.updateGeometricState();

        // render states
        stateManager.render(renderManager);
        renderManager.render(tpf, context.isRenderable());
            // Render:
        
        stateManager.postRender();
    }

    // Actions/Input
    private void ClearClientKeys(){
        //if(inputManager.hasMapping("FLYCAM_ZoomIn")){
  //          inputManager.deleteMapping("FLYCAM_ZoomIn");
  //          inputManager.deleteMapping("FLYCAM_ZoomOut");
  //          inputManager.deleteMapping("FLYCAM_StrafeLeft");
  //          inputManager.deleteMapping("FLYCAM_StrafeRight");
  //          inputManager.deleteMapping("FLYCAM_Forward");
 //           inputManager.deleteMapping("FLYCAM_Backward");
//            inputManager.deleteMapping(DebugKeysAppState.INPUT_MAPPING_CAMERA_POS);
//            inputManager.deleteMapping(DebugKeysAppState.INPUT_MAPPING_MEMORY);
            inputManager.setCursorVisible(false);
            CLIENT_KEYS_CLEARED = true;
       // }
    }
    
    @Override
    public void destroy(){
        if(Networking.isConnected()){
            Networking.client.close();
        }
        super.destroy();
    }
}
