package org.xedox.fluxengine.project;

import android.content.Context;
import android.util.Log;
import com.badlogic.gdx.math.Vector2;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.xedox.engine.objects.GameObject;
import org.xedox.engine.Scene;
import org.xedox.engine.objects.TextureObject;
import org.xedox.fluxengine.Application;
import org.xedox.fluxengine.io.Assets;
import org.xedox.fluxengine.io.FileX;
import org.xedox.fluxengine.io.IFile;

import java.io.IOException;
import java.util.*;

public class ProjectManager {
    public static final String TAG = "ProjectManager";
    public static final String ASSETS_DIR = "Assets";
    public static final String SCENES_DIR = "Scenes";
    public static final String ICON_FILE = "icon.png";
    public static final String PROPERTIES_FILE = "project.properties";
    public static final String PROPERTIES_ASSET = "base_project.properties";
    public static final String DEFAULT_SCENE_NAME = "main";

    public static Project create(Context context, String name) {
        return create(context, name, new HashMap<>());
    }

    public static Project create(
            Context context, String name, Map<String, String> customProperties) {
        try {
            IFile projectDir = new FileX(Application.getProjectsPath(), name);
            createProjectStructure(projectDir);
            prepareProjectProperties(context, projectDir, name, customProperties);
            return createMainScene(new Project(name));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Project createMainScene(Project project) {
        int width = Integer.parseInt(project.getProperty("default_width"));
        int height = Integer.parseInt(project.getProperty("default_height"));
        Scene main = new Scene("main", width, height);
        TextureObject my_object = new TextureObject("my_object");
        my_object.setScriptPath("script.lua");
        my_object.setTexturePath("eye.png");
        my_object.setSize(new Vector2(100, 100));
        main.add(my_object);
        try {
            Assets.from(Application.appContext)
                    .asset("script.lua")
                    .toPath(project.getAssetsPath())
                    .toFileName("script.lua")
                    .copy();

            Assets.from(Application.appContext)
                    .asset("eye.png")
                    .toPath(project.getAssetsPath())
                    .toFileName("eye.png")
                    .copy();
            project.addScene(main);
        } catch (Exception err) {
            err.printStackTrace();
        }

        return project;
    }

    private static void createProjectStructure(IFile projectDir) throws IOException {
        IFile assetsDir = new FileX(projectDir, ASSETS_DIR);
        IFile scenesDir = new FileX(projectDir, SCENES_DIR);
        assetsDir.mkdirs();
        scenesDir.mkdirs();
    }

    private static void prepareProjectProperties(
            Context context,
            IFile projectDir,
            String projectName,
            Map<String, String> customProperties)
            throws IOException {
        IFile propsFile = new FileX(projectDir, PROPERTIES_FILE);
        String defaultProps = Assets.from(context).asset(PROPERTIES_ASSET).read();
        String mergedProps = mergeProperties(defaultProps, projectName, customProperties);
        propsFile.write(mergedProps);
    }

    private static String mergeProperties(
            String defaultProps, String projectName, Map<String, String> customProperties) {
        String result = defaultProps.replace("${project_name}", projectName);
        result = result.replace("${create_time}", getTime());
        if (customProperties != null && !customProperties.isEmpty()) {
            StringBuilder sb = new StringBuilder(result);
            for (Map.Entry<String, String> entry : customProperties.entrySet()) {
                sb.append("\n").append(entry.getKey()).append("=").append(entry.getValue());
            }
            result = sb.toString();
        }

        return result;
    }

    public static String getTime() {
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = time.format(formatter);
        return formattedTime;
    }

    public static boolean projectExists(String name) {
        if (name.equals(null)) return false;
        IFile projectDir = new FileX(Application.getProjectsPath(), name);
        return projectDir.exists();
    }

    public static List<Project> getProjects() {
        List<Project> projects = new ArrayList<>();
        File projectsPath = new File(Application.getProjectsPath());

        if (!projectsPath.exists()) {
            return projects;
        }

        File[] files = projectsPath.listFiles();
        if (files == null) {
            return projects;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                projects.add(new Project(file.getName()));
            }
        }
        return projects;
    }

    public static Project loadProject(String name) {
        Project project = new Project(name);
        return project;
    }
}