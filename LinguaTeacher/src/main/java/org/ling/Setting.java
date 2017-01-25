package org.ling;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Dmitry on 01.03.2016.
 */
public class Setting {
    public static final String APP_PREFERENCES = "mysettings";

    private static  SharedPreferences mSettings;
    private static Context c;

    public static SharedPreferences init(Context con){
        c = con;
        return mSettings = c.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static void setBool(String key, Boolean value) {

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean( key, value);
        editor.apply();
    }

    public static boolean getBool(String key) {
        return  mSettings.getBoolean( key,  false );
    }



    public static long getLong( String key){
        return  mSettings.getLong(key, 0);
    }

    public static void setLong(String key, long value){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static int getInt( String key){
        return  mSettings.getInt(key, 0);
    }

    public static void setInt(String key, int value){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static float getFuildFloat( String key){
        Log.d("mysettings", "get setting  for " + key);
        return  mSettings.getFloat(key, 0);
    }

    public static void setFuild(String key, float value){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putFloat(key, value);
        editor.apply();
        Log.d("mysettings", "add setting  for " + key + " v =  " + value);
    }


    public static String getString( String key){
        return  mSettings.getString(key,"");
    }

    public static void setString(String key, String value){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
