package org.xedox.fluxengine.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.textfield.TextInputEditText;

import org.xedox.engine.Scene;
import org.xedox.fluxengine.R;

import static org.xedox.fluxengine.dialog.DialogBuilder.EXIT;
import static org.xedox.fluxengine.dialog.DialogBuilder.RETURN;
import org.xedox.fluxengine.project.Project;
import org.xedox.fluxengine.utils.SpinnerAdapter;

public class NewSceneDialog {

    public static void show(@NonNull Context context, SpinnerAdapter adapter, Project project) {
        DialogBuilder builder = new DialogBuilder(context);
        builder.setView(R.layout.new_scene_dialog);
        builder.setTitle(R.string.create_scene);

        TextInputEditText inputField = builder.findViewById(R.id.input_field);
        TextView errorMessage = builder.findViewById(R.id.error_message);

        builder.setNegativeButton(
                R.string.cancel,
                (dialog, which) -> {
                    return EXIT;
                });

        builder.setPositiveButton(
                R.string.create,
                (dialog, which) -> {
                    String sceneName = inputField.getText().toString().trim();

                    if (TextUtils.isEmpty(sceneName)) {
                        showError(errorMessage, inputField, R.string.scene_name_cannot_be_empty);
                        return RETURN;
                    }

                    if (project.containsScene(sceneName)) {
                        showError(errorMessage, inputField, R.string.scene_already_exists);
                        return RETURN;
                    }
                    try {
                        Scene newScene =
                                new Scene(
                                        sceneName,
                                        project.getIntProperty("default_width"),
                                        project.getIntProperty("default_height"));
                        project.addScene(newScene);
                        adapter.add(
                                adapter.getItemCount() - 1,
                                new SpinnerAdapter.Item(newScene.getName()));
                    } catch (Exception err) {
                        showError(errorMessage, inputField, err.toString());
                        err.printStackTrace();
                        return RETURN;
                    }

                    return EXIT;
                });

        builder.create().show();
    }

    private static void showError(
            @NonNull TextView errorView,
            @NonNull TextInputEditText inputField,
            @StringRes int errorResId) {
        showError(errorView, inputField, errorView.getContext().getString(errorResId));
    }

    private static void showError(
            @NonNull TextView errorView,
            @NonNull TextInputEditText inputField,
            @NonNull String errorText) {
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(errorText);
        inputField.requestFocus();
        inputField.selectAll();
    }
}
