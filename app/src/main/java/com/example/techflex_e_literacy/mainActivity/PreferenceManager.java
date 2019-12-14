package com.example.techflex_e_literacy.mainActivity;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.techflex_e_literacy.R;

public class PreferenceManager {
    private Context context;
    private SharedPreferences sharedPreferences;

    public PreferenceManager(Context context){
        this.context = context;
        getSharedPrefernce();

    }
    public void getSharedPrefernce(){
         sharedPreferences = context.getSharedPreferences(context.getString(R.string.my_preference),context.MODE_PRIVATE);

    }
    public void writePrefence(){
       SharedPreferences.Editor editor = sharedPreferences.edit();
       editor.putString(context.getString(R.string.my_preference_key),"INIT_OK");
       editor.apply();
    }
    public boolean checkPrefences(){
        boolean status = false;
        if (sharedPreferences.getString(context.getString(R.string.my_preference_key),"null").equals("null")){
            status = false;
        }else {
            status = true;
        }
        return status;
    }
    public void clearPreference(){
        sharedPreferences.edit().clear().apply();
    }
}
