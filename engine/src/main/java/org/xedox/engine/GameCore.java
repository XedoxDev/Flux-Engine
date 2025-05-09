package org.xedox.engine;

import android.util.Log;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Logger;

public class GameCore extends Game {
    private final Logger logger;
    public static String assetsPath;
    private Scene currentScene;
    public static InputMultiplexer multiplexer;

    public GameCore(Scene startScene, String assetsPath) {
        if (startScene == null) {
            throw new IllegalArgumentException("Start scene cannot be null");
        }
        
        this.logger = new Logger("GameCore", Logger.DEBUG);
        this.currentScene = startScene;
        GameCore.assetsPath = assetsPath != null ? 
            (assetsPath.endsWith("/") ? assetsPath : assetsPath + "/") : "";
            
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void create() {
        logger.debug("GameCore created with assets path: " + assetsPath);
        if (currentScene == null) {
            logger.error("Current scene is null during create()");
            return;
        }
        
        try {
            switchScene(currentScene);
            if (getScreen() != null) {
                logger.debug("Initial scene: " + getScreen().getClass().getSimpleName());
            }
        } catch (Exception e) {
            logger.error("Error during scene initialization", e);
        }
    }

    public synchronized void switchScene(Scene newScene) {
        if (newScene == null) {
            throw new IllegalArgumentException("New scene cannot be null");
        }

        try {
            Scene oldScene = currentScene;
            currentScene = newScene.clone();
            
            if (currentScene == null) {
                throw new IllegalStateException("Failed to clone scene");
            }
            
            setScreen(currentScene);
            currentScene.start();
            
            if (oldScene != null) {
                oldScene.hide();
                oldScene.dispose();
            }
        } catch (Exception e) {
            logger.error("Error switching scenes", e);
            throw new RuntimeException("Scene switch failed", e);
        }
    }

    @Override
    public void dispose() {
        logger.debug("Disposing GameCore");
        try {
            if (currentScene != null) {
                currentScene.dispose();
                currentScene = null;
            }
            if (multiplexer != null) {
                multiplexer.clear();
            }
        } catch (Exception e) {
            logger.error("Error during disposal", e);
        } finally {
            super.dispose();
        }
    }

    public String getAssetsPath() {
        return assetsPath != null ? assetsPath : "";
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}