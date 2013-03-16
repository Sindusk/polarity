package sin;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sin.appstates.ServerGameState;
import sin.appstates.ServerMenuState;
import sin.input.ServerInputHandler;
import sin.network.ServerNetwork;
import sin.tools.S;
import sin.tools.T;
import sin.world.*;

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
 * Game Server
 * @author SinisteRing
 */
public class GameServer extends Application{
    private static GameServer app;
    
    // Global Constant Variables:
    private static final String SERVER_VERSION = "DEV 0.08";
    private static final float BULLET_ACCURACY = 0.01f;      // Accuracy timer for bullet app state resets
    
    // App States:
    private BulletAppState bulletAppState;
    private ServerGameState gameState;
    private ServerMenuState menuState;
    
    // Nodes:
    private Node root = new Node("Root");
    private Node gui = new Node("GUI");
    
    // Getters for Nodes:
    public Node getGUI(){
        return gui;
    }
    public Node getRoot(){
        return root;
    }
    
    // Getters for AppStates:
    public BulletAppState getBulletAppState(){
        return bulletAppState;
    }
    public ServerGameState getGameState(){
        return gameState;
    }
    public ServerMenuState getMenuState(){
        return menuState;
    }
    public String getVersion(){
        return SERVER_VERSION;
    }
    public Node getWorld(){
        return menuState.getWorld();
    }
    
    public void resetBulletAppState(){
        if(stateManager.hasState(bulletAppState)){
            stateManager.detach(bulletAppState);
        }
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setAccuracy(BULLET_ACCURACY);
        S.setBulletAppState(bulletAppState);
        CG.initialize(bulletAppState);
    }
    
    @Override
    public void start(){
        super.start();
    }
    public static void main(String[] args) throws IOException {
        Logger.getLogger("com.jme3").setLevel(Level.WARNING);
        app = new GameServer();
        AppSettings set = new AppSettings(true);
        set.setResolution(600, 400);
        set.setSamples(0);
        set.setVSync(false);
        set.setRenderer(AppSettings.LWJGL_OPENGL1);
        set.setTitle("Polarity Server");
        app.setSettings(set);
        app.start();
    }
    
    @Override
    public void initialize(){
        super.initialize();
        
        // Initialize Root and GUI:
        gui.setQueueBucket(Bucket.Gui);
        gui.setCullHint(CullHint.Never);
        viewPort.attachScene(root);
        guiViewPort.attachScene(gui);
        
        // Viewport Init:
        viewPort.setBackgroundColor(ColorRGBA.Black);
        setPauseOnLostFocus(false);
        
        // Initialize Tools:
        S.setAssetManager(assetManager);
        S.setCamera(cam);
        S.setInputManager(inputManager);
        
        ServerInputHandler.initialize(app);
        ServerNetwork.initialize(app);
        
        // Initialize App States:
        gameState = new ServerGameState();
        menuState = new ServerMenuState();
        
        // Attach App States:
        stateManager.attach(menuState);
        resetBulletAppState();
    }

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
        ServerNetwork.stop();
        super.destroy();
    }
}
