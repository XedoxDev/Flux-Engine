package org.xedox.fluxengine.project;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;
import org.xedox.engine.Scene;
import org.xedox.engine.objects.GameObject;
import org.xedox.engine.KryoEnvironment;
import org.xedox.engine.objects.TextureObject;
import org.xedox.fluxengine.Application;
import org.xedox.fluxengine.dialog.DialogBuilder;
import org.xedox.fluxengine.io.FileX;
import org.xedox.fluxengine.io.IFile;
import static org.xedox.fluxengine.project.ProjectManager.*;

public class Project {
    private final String name;
    private final String path;
    private final List<Scene> scenes = new ArrayList<>();
    private final Properties projectProperties = new Properties();
    private Drawable icon;

    public Project(@NonNull String name) {
        this.path = new File(Application.getProjectsPath(), name).getAbsolutePath();
        this.name = Objects.requireNonNull(name, "Project name cannot be null");
        try {
            loadIcon();
            loadProjectProperties();
            loadScenes();
        } catch (Throwable e) {
            System.err.println("Failed to initialize project: " + name);
            e.printStackTrace();
        }
    }

    private void loadProjectProperties() throws IOException {
        File propertiesFile = new File(path, PROPERTIES_FILE);
        if (!propertiesFile.exists()) {
            throw new FileNotFoundException("Project properties file not found: " + propertiesFile);
        }
        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            projectProperties.load(fis);
        }
    }

    public void saveProjectProperties() throws IOException {
        File propertiesFile = new File(path, PROPERTIES_FILE);
        try (FileOutputStream fos = new FileOutputStream(propertiesFile)) {
            projectProperties.store(fos, "Project configuration");
        }
    }

    public String getProperty(String key) {
        return projectProperties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return projectProperties.getProperty(key, defaultValue);
    }
    
    public int getIntProperty(String key) {
        return Integer.parseInt(projectProperties.getProperty(key));
    }

    public void setProperty(String key, String value) {
        projectProperties.setProperty(key, value);
    }

    private void loadScenes() {
        scenes.clear();
        File scenesDir = new File(path, SCENES_DIR);
        File[] sceneFiles = scenesDir.listFiles((dir, name) -> name.endsWith(".dat"));
        if (sceneFiles == null) return;

        for (File file : sceneFiles) {
            try {
                byte[] data = Files.readAllBytes(file.toPath());
                Scene baseScene = KryoEnvironment.deserialize(data, Scene.class);
                if (baseScene instanceof Scene) {
                    scenes.add(baseScene);
                } else {
                    Scene scene =
                            new Scene(
                                    baseScene.getName(),
                                    baseScene.getWidth(),
                                    baseScene.getHeight());
                    scene.addAll(baseScene.getObjects());
                    scenes.add(scene);
                }
            } catch (Exception e) {
                System.err.println("Failed to load scene from: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    public void saveScene(Scene scene) throws IOException {
        File scenesDir = new File(path, SCENES_DIR);
        if (!scenesDir.exists() && !scenesDir.mkdirs()) {
            throw new IOException("Failed to create scenes directory");
        }

        String sceneFileName = scene.getName() + ".dat";
        IFile sceneFile = new FileX(scenesDir, sceneFileName);

        byte[] data = KryoEnvironment.serialize(scene);
        sceneFile.write(data);
    }

    public void saveScene(String scene) throws IOException {
        saveScene(getSceneByName(scene));
    }

    public void saveScenes() throws IOException {
        File scenesDir = new File(path, SCENES_DIR);
        if (!scenesDir.exists() && !scenesDir.mkdir()) {
            throw new IOException("Failed to create scenes directory");
        }
        for (IFile file : new FileX(scenesDir.getAbsolutePath()).ifiles()) {
            if (!file.isDir()) {
                file.remove();
            }
        }
        for (Scene scene : scenes) {
            String sceneFileName = scene.getName() + ".dat";
            IFile sceneFile = new FileX(scenesDir, sceneFileName);
            sceneFile.write(KryoEnvironment.serialize(scene));
        }
    }

    private void loadIcon() {
        File iconFile = new File(path, ICON_FILE);
        this.icon = iconFile.exists() ? Drawable.createFromPath(iconFile.getAbsolutePath()) : null;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void addScene(@NonNull Scene scene) throws IOException {
        Objects.requireNonNull(scene, "Scene cannot be null");

        if (getSceneByName(scene.getName()) != null) {
            throw new IllegalArgumentException(
                    "Scene with name '" + scene.getName() + "' already exists");
        }

        scenes.add(scene);
        saveScene(scene);
    }

    public void addScene(int index, @NonNull Scene scene) throws IOException {
        Objects.requireNonNull(scene, "Scene cannot be null");

        if (getSceneByName(scene.getName()) != null) {
            throw new IllegalArgumentException(
                    "Scene with name '" + scene.getName() + "' already exists");
        }

        scenes.add(index, scene);
        saveScene(scene);
    }

    public boolean removeScene(Scene scene) {
        return scenes.remove(scene);
    }

    public Scene removeScene(int index) {
        return scenes.remove(index);
    }

    public Scene getScene(int index) {
        return scenes.get(index);
    }

    @Nullable
    public Scene getSceneByName(String name) {
        for (Scene scene : scenes) {
            if (name.equals(scene.getName())) {
                return scene;
            }
        }
        return null;
    }

    public int getSceneCount() {
        return scenes.size();
    }

    public boolean containsScene(Scene scene) {
        return scenes.contains(scene);
    }

    public int indexOfScene(Scene scene) {
        return scenes.indexOf(scene);
    }

    public void clearScenes() {
        scenes.clear();
    }

    public void saveProject() throws IOException {
        saveProjectProperties();
        saveScenes();
    }

    public void deleteProject() {
        File projectDir = new File(path);
        try {
            IFile.deleteDir(projectDir.getAbsolutePath());
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    @Nullable
    public Drawable getIcon() {
        return icon;
    }

    @NonNull
    public List<Scene> getScenes() {
        return new ArrayList<>(scenes);
    }

    public String getAssetsPath() {
        return path + File.separator + ASSETS_DIR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return path.equals(project.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    public Scene getMainScene() {
        String mainSceneName = getProperty("main_scene", DEFAULT_SCENE_NAME);
        Scene scene = getSceneByName(mainSceneName);
        if (scene == null && !scenes.isEmpty()) {
            scene = scenes.get(0);
        }
        return scene;
    }
    
    public boolean containsScene(String name) {
        for(Scene scene : scenes) {
        	if(scene.getName().equals(name)) return true;
        }
        return false;
    }
}
