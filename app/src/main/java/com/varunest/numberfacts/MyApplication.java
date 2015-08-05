package com.varunest.numberfacts;

import android.app.Application;

/**
 * Created by Varun on 29/03/15.
 */
public class MyApplication extends Application {

    private static boolean activityVisible=false;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused (){
        activityVisible = false;
    }

}
