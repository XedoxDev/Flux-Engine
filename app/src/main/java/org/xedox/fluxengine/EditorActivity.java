package org.xedox.fluxengine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import org.xedox.engine.GameCore;
import org.xedox.engine.Scene;
import org.xedox.fluxeditor.FEditorFragment;
import org.xedox.fluxengine.bars.SpinnerView;
import org.xedox.fluxengine.bars.TransformSelectorView;
import org.xedox.fluxengine.dialog.NewSceneDialog;
import org.xedox.fluxengine.project.Project;
import org.xedox.fluxengine.project.ProjectManager;
import org.xedox.fluxengine.utils.SpinnerAdapter;
import java.util.ArrayList;
import java.util.List;

public class EditorActivity extends AppCompatActivity
        implements AndroidFragmentApplication.Callbacks {
    private static final String TAG = "EditorActivity";
    private static final String PROJECT_NAME_KEY = "project_name";

    private Project project;
    private SpinnerView scenesSelector;
    private FEditorFragment editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        
        String projectName = getIntent().getStringExtra(PROJECT_NAME_KEY);
        if (projectName == null || projectName.isEmpty()) {
            showErrorAndFinish("Project name not provided");
            return;
        }

        try {
            project = ProjectManager.loadProject(projectName);
            if (project == null) {
                throw new RuntimeException("Failed to load project");
            }

            GameCore.assetsPath = project.getAssetsPath() != null 
                ? (project.getAssetsPath().endsWith("/") 
                    ? project.getAssetsPath() 
                    : project.getAssetsPath() + "/")
                : "";

            if (project.getMainScene() == null) {
                throw new RuntimeException("Project has no main scene");
            }
        } catch (Exception err) {
            showErrorAndFinish("Error loading project: " + err.getMessage());
            return;
        }

        initUIComponents();

        setupEditorFragment();
    }

    private void showErrorAndFinish(String message) {
        Log.e(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initUIComponents() {
        ImageButton backButton = findViewById(R.id.back);
        ImageButton startButton = findViewById(R.id.start);
        TransformSelectorView transformSelector = findViewById(R.id.transform_selector);
        scenesSelector = findViewById(R.id.scenes_selector);
        
        updateScenesList();

        scenesSelector.setOnItemClickListener((spinner, pos, item) -> {
            if (item.getTitle().equals(getString(R.string.create_scene))) {
                NewSceneDialog.show(this, scenesSelector.adapter, project);
            } else {
                switchToScene(item.getTitle());
                spinner.performDefaultItemClick(pos, item);
            }
        });

        backButton.setOnClickListener(v -> exit());

        startButton.setOnClickListener(v -> {
            try {
                project.saveScenes();
                Intent intent = new Intent(this, GameLauncher.class);
                intent.putExtra(PROJECT_NAME_KEY, project.getName());
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error saving scenes: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });

        transformSelector.addItem(
            new TransformSelectorView.Item("position", R.drawable.transform_position));

        getOnBackPressedDispatcher().addCallback(this, 
            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    exit();
                }
            });
    }

    private void setupEditorFragment() {
        try {
            editor = FEditorFragment.newInstance(project.getMainScene());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.editor, editor);
            transaction.commitNow();
        } catch (Exception e) {
            Log.e(TAG, "Error setting up editor fragment", e);
            showErrorAndFinish("Failed to initialize editor");
        }
    }

    private void updateScenesList() {
        List<SpinnerAdapter.Item> newItems = new ArrayList<>();
        for (int i = 0; i < project.getSceneCount(); i++) {
            newItems.add(new SpinnerAdapter.Item(project.getScene(i).getName()));
        }
        newItems.add(new SpinnerAdapter.Item(getString(R.string.create_scene)));
        scenesSelector.adapter.setItems(newItems);
    }

    private void switchToScene(String sceneName) {
        Scene scene = project.getSceneByName(sceneName);
        if (scene != null && editor != null) {
            editor.setScene(scene);
        } else {
            Toast.makeText(this, "Scene not found or editor not initialized", 
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void exit() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}