package com.ildar.attainment;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ildar on 17.07.2016.
 */
public class PreferenceHelper {
    public static final String SPLASH_IS_INVISIBLE = "splash_is_invisible";

    private static PreferenceHelper instance;

    private Context context;

    private SharedPreferences sharedPreferences;

    private PreferenceHelper(){
    }

    public static PreferenceHelper getInstance(){
        if (instance == null) {
            instance = new PreferenceHelper();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply(); //изменеия вступают в силу
    }

    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }
}
