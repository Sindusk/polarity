package sin;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import sin.hud.HUD;
import sin.input.InputHandler;
import sin.network.Networking;
import sin.player.Char;
import sin.player.Player;
import sin.tools.T;
import sin.weapons.Recoil;
import sin.weapons.Weapons;
import sin.weapons.Weapons.RangedWeapon.AK47;
import sin.weapons.Weapons.RangedWeapon.LaserPistol;
import sin.weapons.Weapons.RangedWeapon.M4A1;
import sin.weapons.Weapons.RangedWeapon.Raygun;
import sin.world.Decals;
import sin.world.World;


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
    private static final boolean MODE_DEBUG = false;
    public static final String INPUT_MAPPING_EXIT = "SIMPLEAPP_Exit";
    public static final String INPUT_MAPPING_CAMERA_POS = "SIMPLEAPP_CameraPos";
    public static final String INPUT_MAPPING_MEMORY = "SIMPLEAPP_Memory";
    public static final String INPUT_MAPPING_HIDE_STATS = "SIMPLEAPP_HideStats";
    
    public static final String CLIENT_VERSION = "ALPHA 0.04";
    
    // Important System Variables:
    private static boolean CLIENT_KEYS_CLEARED = false;
    private static final Logger logger = Logger.getLogger(GameClient.class.getName());
    private static BulletAppState bulletAppState;   // Physics State.
    private static GameClient app;                  // The application itself (this).
    
    // Nodes:
    private static Node guiNode = new Node("Gui Node");     // Node encompassing all GUI elements.
    private static Node rootNode = new Node("Root Node");   // Node encompassing all visual elements.
    private static Node collisionNode = new Node();     // Node encompassing anything able to be shot [single, world, player].
    private static Node singleNode = new Node();        // Node encompassing single player testing (Static).
    private static Node worldNode = new Node();         // Node encompassing terrain and environment (Static).
    private static Node playerNode = new Node();        // Node encompassing player models (Kinematic).
    private static Node tracerNode = new Node();        // Node encompassing tracers, mainly for testing.
    
    // Custom Variables:
    private static InputHandler input = new InputHandler(); // Class for handling all forms of input.
    private static Networking network = new Networking();   // Class for controlling Networking.
    private static Player[] player = new Player[16];        // Array of networked players.
    private static Char character;                      // Character data for the current client.
    private static Recoil recoil = new Recoil();        // Class for controlling Camera movement (recoil/decoil).
    private static HUD hud = new HUD();                 // Class for controlling User Interface & HUD.
    private static Decals dcs = new Decals();           // Class for controlling Bullet Decals.
    
    public static Node getRoot(){
        return rootNode;
    }
    public static Node getGUI(){
        return guiNode;
    }
    public static Node getWorld(){
        return worldNode;
    }
    public static Node getPlayerNode(){
        return playerNode;
    }
    public static Node getCollisionNode(){
        return collisionNode;
    }
    public static Node getTracerNode(){
        return tracerNode;
    }
    public static Char getCharacter(){
        return character;
    }
    public static Player getPlayer(int index){
        return player[index];
    }
    public static HUD getHUD(){
        return hud;
    }
    public static Decals getDCS(){
        return dcs;
    }
    public static Recoil getRecoil(){
        return recoil;
    }
    public static Networking getNetwork(){
        return network;
    }
    public AppSettings getSettings(){
        return settings;
    }
    public static BulletAppState getBulletAppState(){
        return bulletAppState;
    }
    public static Logger getLogger(){
        return logger;
    }
    
    // Main:
    @Override
    public void start() {
        super.start();
    }
    public static void main(String[] args) throws IOException {
        Logger.getLogger("com.jme3").setLevel(Level.WARNING);
        if(MODE_DEBUG){
            try {
                logger.addHandler(new FileHandler("FPSlog.xml"));
                Logger.getLogger("com.jme3").addHandler(new FileHandler("FPSlog2.xml"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        input.initialize(app, context);
        
        // Initialize the World class and create a basic place:
        World.initialize(assetManager, bulletAppState);
        World.createSinglePlayerArea(singleNode);
        collisionNode.attachChild(singleNode);
        collisionNode.attachChild(worldNode);
        collisionNode.attachChild(playerNode);
        rootNode.attachChild(collisionNode);
        
        // Initialize new HUD & remove debug HUD elements:
        Networking.initialize(app);
        Recoil.initialize(app);
        Weapons.initialize(app);
        Player.initialize(app);
        Char.initialize(app);
        hud.initialize(app);
        dcs.initialize();
        rootNode.attachChild(dcs.getNode());
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
            player[i] = new Player(i, T.v3f(0, 10, 0));
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
