package org.xedox.fluxengine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import org.xedox.engine.GameCore;
import org.xedox.engine.Scene;
import org.xedox.fluxengine.project.Project;
import org.xedox.fluxengine.project.ProjectManager;

public class GameLauncher extends AndroidApplication {
    private static final String TAG = "GameLauncher";
    private static final String PROJECT_NAME_EXTRA = "project_name";

    private Project project;
    private GameCore gameCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Initializing game launcher");

        try {
            initializeProject();
            initializeGameCore();
        } catch (IllegalArgumentException e) {
            handleError("Configuration error: " + e.getMessage(), e);
        } catch (Exception e) {
            handleError("Initialization failed: " + e.getMessage(), e);
        }
    }

    private void initializeProject() {
        Intent intent = getIntent();
        String projectName = intent.getStringExtra(PROJECT_NAME_EXTRA);

        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name not provided");
        }

        project = ProjectManager.loadProject(projectName);
        if (project == null) {
            throw new IllegalStateException("Failed to load project: " + projectName);
        }

        Log.i(TAG, "Loaded project: " + projectName);

        if (project.getSceneCount() == 0) {
            throw new IllegalStateException("Project has no scenes");
        }
        
        if (project.getMainScene() == null) {
            throw new IllegalStateException("Project has no main scene");
        }
    }

    private void initializeGameCore() {
        Scene startScene = project.getMainScene();
        String assetsPath = project.getAssetsPath();
        
        if (startScene == null) {
            throw new IllegalStateException("Start scene is null");
        }
        
        if (assetsPath == null) {
            assetsPath = "";
        }
        
        Log.d(TAG, String.format(
            "Starting game with:\n- Scene: %s\n- Assets path: %s",
            startScene.getName(),
            assetsPath
        ));

        gameCore = new GameCore(startScene, assetsPath);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(gameCore, config);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroying game launcher");
        
        if (gameCore != null) {
            try {
                gameCore.dispose();
            } catch (Exception e) {
                Log.e(TAG, "Error disposing game core", e);
            }
        }
    }

    private void handleError(String message, Throwable e) {
        Log.e(TAG, message, e);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }
}