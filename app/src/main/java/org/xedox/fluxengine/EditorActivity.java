package org.xedox.fluxengine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import org.xedox.engine.GameCore;
import org.xedox.engine.Scene;
import org.xedox.fluxeditor.FEditorFragment;
import org.xedox.fluxengine.dialog.DialogBuilder;
import org.xedox.fluxengine.project.Project;
import org.xedox.fluxengine.project.ProjectManager;

public class EditorActivity extends AppCompatActivity
        implements AndroidFragmentApplication.Callbacks {

    private static final String PROJECT_NAME_KEY = "project_name";
    private static final String PROJECT_KEY = "project";

    private Project project;
    private ImageButton backButton, startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        backButton = findViewById(R.id.back);
        startButton = findViewById(R.id.start);

        Intent intent = getIntent();
        String projectName = intent.getStringExtra(PROJECT_NAME_KEY);
        
        if (projectName == null || projectName.isEmpty()) {
            Toast.makeText(this, "Project name not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            project = ProjectManager.loadProject(projectName);
            if (project == null) {
                throw new RuntimeException("Failed to load project");
            }
            
            String assetsPath = project.getAssetsPath();
            GameCore.assetsPath = assetsPath != null ? 
                (assetsPath.endsWith("/") ? assetsPath : assetsPath + "/") : "";
                
            if (project.getMainScene() == null) {
                throw new RuntimeException("Project has no main scene");
            }
        } catch (Exception err) {
            err.printStackTrace();
            Toast.makeText(this, "Error loading project", Toast.LENGTH_SHORT).show();
            navigateBack();
            return;
        }

        backButton.setOnClickListener(v -> navigateBack());
        startButton.setOnClickListener(v -> run());

        if (getSupportFragmentManager().findFragmentById(R.id.editor) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.editor, FEditorFragment.newInstance())
                    .commit();
        }

        OnBackPressedCallback callback =
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateBack();
                    }
                };

        getOnBackPressedDispatcher().addCallback(this, callback);
        
        Scene mainScene = project.getMainScene();
    }

    private void navigateBack() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void exit() {
        navigateBack();
    }

    public void run() {
        if (project == null || project.getMainScene() == null) {
            Toast.makeText(this, "Project not properly loaded", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Intent i = new Intent(this, GameLauncher.class);
        i.putExtra("project_name", project.getName());
        startActivity(i);
    }
}