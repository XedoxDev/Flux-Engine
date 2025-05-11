package org.xedox.engine;

import android.util.Log;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameCore extends Game {
    private Scene currentScene;

    public static String assetsPath;
    public static InputMultiplexer multiplexer;
    public static boolean init = false;

    public GameCore(Scene startScene, String assetsPath) {
        if (startScene == null) {
            throw new IllegalArgumentException("Start scene cannot be null");
        }

        GameCore.assetsPath =
                assetsPath != null
                        ? (assetsPath.endsWith("/") ? assetsPath : assetsPath + "/")
                        : "";
        this.currentScene = startScene;
        init = true;
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.valueOf("#000000"));
        super.render();
    }

    @Override
    public void create() {
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        try {
            switchScene(currentScene);
        } catch (Exception e) {
            currentScene = new Scene("fallback", 800, 600);
            setScreen(currentScene);
        }
    }

    public synchronized void switchScene(Scene newScene) {
        if (newScene == null) {
            throw new IllegalArgumentException("New scene cannot be null");
        }

        try {
            Scene oldScene = currentScene;

            currentScene = newScene.clone();
            currentScene.start();
            setScreen(currentScene);

            if (oldScene != null) {
                try {
                    oldScene.hide();
                    oldScene.dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            currentScene = new Scene("fallback", 800, 600);
            setScreen(currentScene);
        }
    }

    @Override
    public void dispose() {
        try {
            if (multiplexer != null) {
                multiplexer.clear();
            }
            if (currentScene != null) {
                currentScene.dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            super.dispose();
        }
        init = false;
    }

    public static String getAssetsPath() {
        return assetsPath != null ? assetsPath : "";
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
