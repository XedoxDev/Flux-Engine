package org.xedox.fluxeditor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import org.xedox.engine.Scene;

public class FEditorFragment extends AndroidFragmentApplication {
    private Scene scene;
    private EditorApplication editorApp;

    public static FEditorFragment newInstance(Scene scene) {
        FEditorFragment fragment = new FEditorFragment();
        fragment.setScene(scene);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        config.useWakelock = true;

        editorApp = new EditorApplication();
        if (scene != null) {
            editorApp.setScene(scene); 
        }
        return initializeForView(editorApp, config);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        if (editorApp != null) {
            editorApp.setScene(scene); 
        }
    }
}