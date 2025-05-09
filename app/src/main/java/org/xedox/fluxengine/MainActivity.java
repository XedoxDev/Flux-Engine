package org.xedox.fluxengine;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.xedox.fluxengine.dialog.NewProjectDialog;
import org.xedox.fluxengine.project.Project;
import org.xedox.fluxengine.project.ProjectManager;
import org.xedox.fluxengine.project.ProjectsAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView projectsRecycler;
    private FloatingActionButton fabCreate;
    private ProjectsAdapter projectsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupToolbar();
        setupProjectsRecycler();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        projectsRecycler = findViewById(R.id.projects);
        fabCreate = findViewById(R.id.create_project);

        fabCreate.setOnClickListener((v) -> NewProjectDialog.show(this, projectsAdapter));
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupProjectsRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        projectsRecycler.setLayoutManager(layoutManager);
        projectsAdapter = new ProjectsAdapter(this);
        projectsRecycler.setAdapter(projectsAdapter);
        projectsAdapter.setProjects(ProjectManager.getProjects());
        projectsAdapter.setOnProjectClickListener(
                (project) -> {
                    Intent i = new Intent(this, EditorActivity.class);
                    i.putExtra("project_name", project.getName());
                    startActivity(i);
                    finish();
                });
    }
}
