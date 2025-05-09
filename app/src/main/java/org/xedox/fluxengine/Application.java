package org.xedox.fluxengine;

import android.content.Context;
import android.os.Environment;
import java.io.File;

public class Application extends android.app.Application {

    public static Context appContext; 
    private static volatile String home;
    private static volatile String projectsPath;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        initPaths();
    }

    private static void initPaths() {
        home = appContext.getExternalFilesDir("home").getAbsolutePath();
        
        File projectsDir = new File(home, "Projects");
        if (!projectsDir.exists()) {
            projectsDir.mkdirs();
        }
        projectsPath = projectsDir.getAbsolutePath();
    }

    public static String getHome() {
        if (home == null) {
            initPaths(); 
        }
        return home;
    }

    public static String getProjectsPath() {
        if (projectsPath == null) {
            initPaths();  
        }
        return projectsPath;
    }
    
    public static int getScreenWidth() {
       return appContext.getResources().getDisplayMetrics().widthPixels;
    }
    
    public static int getScreenHeight() {
       return appContext.getResources().getDisplayMetrics().heightPixels;
    }
}