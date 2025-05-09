package org.xedox.fluxeditor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

public class FEditorFragment extends AndroidFragmentApplication {
    
    public static FEditorFragment newInstance() {
        FEditorFragment fragment = new FEditorFragment();
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true; 
        config.useWakelock = true; 
        
        EditorApplication editorApp = new EditorApplication();
        return initializeForView(editorApp, config);
    }
}