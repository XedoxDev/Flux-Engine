package org.xedox.fluxengine.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import org.xedox.fluxengine.R;
import org.xedox.fluxengine.project.ProjectManager;
import org.xedox.fluxengine.project.ProjectsAdapter;

import static org.xedox.fluxengine.dialog.DialogBuilder.EXIT;
import static org.xedox.fluxengine.dialog.DialogBuilder.RETURN;

public class NewProjectDialog {

    public static void show(@NonNull Context context, ProjectsAdapter adapter) {
        DialogBuilder builder = new DialogBuilder(context);
        builder.setView(R.layout.new_project_dialog);
        builder.setTitle(R.string.create_project);
        
        TextInputEditText inputField = builder.findViewById(R.id.input_field);
        TextView errorMessage = builder.findViewById(R.id.error_message);

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> EXIT);

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            String projectName = inputField.getText().toString().trim();

            if (TextUtils.isEmpty(projectName)) {
                showError(errorMessage, inputField, R.string.project_name_cannot_be_empty);
                return RETURN;
            }

            if (ProjectManager.projectExists(projectName)) {
                showError(errorMessage, inputField, R.string.project_already_exists);
                return RETURN;
            }

            try {
                adapter.addProject(ProjectManager.create(context, projectName));
                return EXIT;
            } catch (Exception err) {
                String errorText = context.getString(R.string.project_create_exception) + ": " +
                        (err.getLocalizedMessage() != null ? 
                        err.getLocalizedMessage() : "Unknown error");
                showError(errorMessage, inputField, errorText);
                return RETURN;
            }
        });

        builder.create().show();
    }

    private static void showError(
            @NonNull TextView errorView, 
            @NonNull TextInputEditText inputField, 
            int errorResId) {
        showError(errorView, inputField, errorView.getContext().getString(errorResId));
    }

    private static void showError(
            @NonNull TextView errorView, 
            @NonNull TextInputEditText inputField, 
            String errorText) {
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(errorText);
        inputField.requestFocus();
        inputField.selectAll();
    }
}