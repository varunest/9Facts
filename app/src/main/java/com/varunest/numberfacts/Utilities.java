package com.varunest.numberfacts;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Varun on 23/03/15.
 */
public class Utilities {

    private static Utilities utilities;
    private static String tag = "DEBUG_UTILITIES";

    //a function to generate random numbers
    public static int randInt(int min, int max) {
        Random rand = new Random();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    /* Function to get singleton object */
    public static Utilities getSharedInstance(){
        if (utilities == null) {
            utilities = new Utilities();
            return utilities;
        } else {
            return utilities;
        }
    }

    /* function to check internet connection */
    public static boolean isNetworkAvailable(Context context) {
        if (((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null) {
            return true;
        } else {
            Toast.makeText(context, context.getString(R.string.no_internet_connection),Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
