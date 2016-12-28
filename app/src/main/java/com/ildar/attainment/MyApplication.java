package com.ildar.attainment;

import android.app.Application;

/**
 * Created by ildar on 30.07.2016.
 */
public class MyApplication extends Application {
    private static boolean activityVisible;

    public static boolean isActivityVisible(){
        return activityVisible;
    }

    public static void activityResumed(){
        activityVisible = true;
    }

    public static void activityPaused(){
        activityVisible = false;
    }
}
