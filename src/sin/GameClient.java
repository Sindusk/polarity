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
import sin.appstates.GameplayState;
import sin.appstates.MenuState;
import sin.hud.HUD;
import sin.input.InputHandler;
import sin.network.Networking;
import sin.player.Char;
import sin.player.Player;
import sin.tools.T;
import sin.weapons.Recoil;
import sin.weapons.Weapons;
import sin.world.Decals;

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
    private static final boolean MODE_DEBUG = false;            // Debug Mode
    public static final String CLIENT_VERSION = "ALPHA 0.05";   // Client Version (Important for client-server connections)
    
    // Important System Variables:
    private static final Logger logger = Logger.getLogger(GameClient.class.getName());
    private static GameClient app;                  // The application itself (this).
    // App States:
    private static BulletAppState bulletAppState;   // Physics State.
    private static GameplayState gameplayState;     // Gameplay State.
    private static MenuState menuState;             // Main Menu State.
    
    // Nodes:
    private Node rootNode = new Node("Root Node");   // Node encompassing all visual elements.
    private Node guiNode = new Node("Gui Node");     // Node encompassing all GUI elements.
    
    // Custom Variables:
    private static InputHandler input = new InputHandler(); // Class for handling all forms of input.
    private static Networking network = new Networking();   // Class for controlling Networking.
    private static Recoil recoil = new Recoil();            // Class for controlling Camera movement (recoil/decoil).
    
    // Getters for Nodes:
    public Node getRoot(){
        return rootNode;
    }
    public static Node getWorld(){
        return gameplayState.getWorld();
    }
    public Node getGUI(){
        return guiNode;
    }
    public static Node getCollisionNode(){
        return gameplayState.getCollisionNode();
    }
    public static Node getMiscNode(){
        return gameplayState.getMiscNode();
    }
    public static Node getSingleNode(){
        return gameplayState.getSingleNode();
    }
    public static Node getPlayerNode(){
        return gameplayState.getPlayerNode();
    }
    public static Node getTerrain(){
        return gameplayState.getTerrainNode();
    }
    public static Node getTracerNode(){
        return gameplayState.getTracerNode();
    }
    
    // Getters for States:
    public static GameplayState getGameplayState(){
        return gameplayState;
    }
    
    public static HUD getHUD(){
        return gameplayState.getHUD();
    }
    public static Char getCharacter(){
        return gameplayState.getCharacter();
    }
    public static Player getPlayer(int index){
        return gameplayState.getPlayer(index);
    }
    public static Decals getDCS(){
        return gameplayState.getDCS();
    }
    public static Recoil getRecoil(){
        return recoil;
    }
    public static Networking getNetwork(){
        return network;
    }
    public static InputHandler getInputHandler(){
        return input;
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
        set.setTitle("Polarity");
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
        
        // Initialize keybinds & Tools.
        T.initialize(app);
        input = new InputHandler();
        input.initialize(app, context);
        
        // Initialize new HUD & remove debug HUD elements:
        Networking.initialize(app);
        Recoil.initialize(app);
        Weapons.initialize(app);
        Player.initialize(app);
        Char.initialize(app);
        viewPort.setBackgroundColor(ColorRGBA.Black);
        setPauseOnLostFocus(false);
        
        // Initialize App States:
        bulletAppState = new BulletAppState();  
        gameplayState = new GameplayState();
        menuState = new MenuState();
        // Attach App States:
        stateManager.attach(bulletAppState);
        stateManager.attach(menuState);
        
        bulletAppState.getPhysicsSpace().setAccuracy(0.01f);
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
        
        // Update logical and geometric states:
        rootNode.updateLogicalState(tpf);
        guiNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
        guiNode.updateGeometricState();

        // Render display:
        stateManager.render(renderManager);
        renderManager.render(tpf, context.isRenderable());
        stateManager.postRender();
    }
    
    @Override
    public void destroy(){
        if(Networking.isConnected()){
            Networking.client.close();
        }
        super.destroy();
    }
}
