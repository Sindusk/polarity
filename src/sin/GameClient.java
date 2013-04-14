package sin;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.BulletAppState.ThreadingType;
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
import sin.appstates.ClientCharState;
import sin.appstates.ClientGameState;
import sin.appstates.ClientMenuState;
import sin.input.ClientInputHandler;
import sin.network.ClientNetwork;
import sin.tools.C;
import sin.tools.S;
import sin.tools.T;
import sin.weapons.RecoilManager;
import sin.world.CG;

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
    private static GameClient app;
    
    // --- Global Constant Variables --- //
    private static final boolean MODE_DEBUG = true;          // Debug Mode
    private static final String CLIENT_VERSION = "DEV 0.10"; // Client Version (Important for client-server connections)
    private static final float BULLET_ACCURACY = 0.01f;      // Accuracy timer for bullet app state resets
    
    // App States:
    private static BulletAppState bulletAppState;       // Physics State.
    private static ClientCharState charState;           // Character State.
    private static ClientGameState gameState;           // Gameplay State.
    private static ClientMenuState menuState;           // Main Menu State.
    
    // Nodes:
    private Node root = new Node("Root");   // Node encompassing all visual elements.
    private Node gui = new Node("GUI");     // Node encompassing all GUI elements.
    
    // Getters for Nodes:
    public Node getRoot(){
        return root;
    }
    public Node getGUI(){
        return gui;
    }
    
    // Gameplay State Nodes:
    public Node getCollisionNode(){
        return gameState.getCollisionNode();
    }
    public Node getMiscNode(){
        return gameState.getMiscNode();
    }
    public Node getSingleNode(){
        return gameState.getSingleNode();
    }
    public Node getTerrain(){
        return gameState.getTerrainNode();
    }
    public Node getTracerNode(){
        return gameState.getTracerNode();
    }
    
    // Getters for States:
    public ClientCharState getCharState(){
        return charState;
    }
    public ClientGameState getGameState(){
        return gameState;
    }
    public ClientMenuState getMenuState(){
        return menuState;
    }
    public BulletAppState getBulletAppState(){
        return bulletAppState;
    }
    
    // Getters for helper classes:
    public AppSettings getSettings(){
        return settings;
    }
    public String getVersion(){
        return CLIENT_VERSION;
    }
    
    public void resetBulletAppState(){
        if(stateManager.hasState(bulletAppState)){
            stateManager.detach(bulletAppState);
        }
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setAccuracy(BULLET_ACCURACY);
        S.setBulletAppState(bulletAppState);
        CG.initialize(bulletAppState);
    }
    
    // Main:
    @Override
    public void start() {
        super.start();
    }
    public static void main(String[] args) throws IOException {
        // Tone down loggers severely:
        Logger.getLogger("com.jme3").setLevel(Level.WARNING);
        if(MODE_DEBUG){
            try{
                Logger.getLogger("polarity").addHandler(new FileHandler("PolarityLog.xml"));
                Logger.getLogger("com.jme3").addHandler(new FileHandler("JMELog.xml"));
            }catch (FileNotFoundException ex){
                T.log(ex);
            }
        }
        app = new GameClient();
        AppSettings set = new AppSettings(true);
        set.setResolution(1280, 720);
        set.setSamples(0);
        set.setVSync(false);
        set.setRenderer(AppSettings.LWJGL_OPENGL1);
        set.setTitle("Polarity Client");
        app.setSettings(set);
        app.start();
    }

    // Initialization:
    @Override
    public void initialize(){
        super.initialize();
        
        T.log("Initializing Viewport...");
        gui.setQueueBucket(Bucket.Gui);
        gui.setCullHint(CullHint.Never);
        viewPort.attachScene(root);
        guiViewPort.attachScene(gui);
        
        // Viewport Init:
        viewPort.setBackgroundColor(ColorRGBA.Black);
        setPauseOnLostFocus(false);
        
        // Initialize Tools & Classes:
        S.height = settings.getHeight();
        S.width = settings.getWidth();
        S.setAssetManager(assetManager);
        S.setCamera(cam);
        S.setInputManager(inputManager);
        S.setRenderManager(renderManager);
        S.setStateManager(stateManager);
        S.setTimer(timer);
        S.setVersion(CLIENT_VERSION);
        S.setViewPort(viewPort);
        
        C.gatherCharacterData();
        ClientInputHandler.initialize(app);
        ClientNetwork.initialize(app);
        RecoilManager.initialize();
        
        // Initialize App States:
        T.log("Creating Character State...");
        charState = new ClientCharState();
        T.log("Creating Game State...");
        gameState = new ClientGameState();
        T.log("Creating Menu State...");
        menuState = new ClientMenuState();
        // Attach App States:
        stateManager.attach(menuState);
        resetBulletAppState();
    }
    
    // Update:
    @Override
    public void update() {
        super.update(); // makes sure to execute AppTasks
        if (speed == 0 || paused) {
            return;
        }
        float tpf = timer.getTimePerFrame() * speed;
        
        // Update States:
        stateManager.update(tpf);
        
        // Update logical and geometric states:
        root.updateLogicalState(tpf);
        gui.updateLogicalState(tpf);
        root.updateGeometricState();
        gui.updateGeometricState();

        // Render display:
        stateManager.render(renderManager);
        renderManager.render(tpf, context.isRenderable());
        stateManager.postRender();
    }
    
    @Override
    public void destroy(){
        if(ClientNetwork.isConnected()){
            ClientNetwork.close();
        }
        super.destroy();
    }
}
