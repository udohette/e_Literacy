package com.example.techflex_e_literacy.quiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.techflex_e_literacy.mainActivity.LoginActivity;

public class Shared {
    //to  create shared preference file
    SharedPreferences sharedPreferences;
    // to  edit the shared preference file
    SharedPreferences.Editor editor;
    // context pass the reference to  another class
    Context context;
    // mode should  be private for sharedpreference file
    int mode =0;
    // shared prference file name;
    String Filename = "sdfile";
    // Store the boolean value in  respect  to key id;
    String Data = "b";
    //create constructor to pass memory at runtime to  the sharedfile

    public Shared(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Filename,mode);
        editor = sharedPreferences.edit();
    }
    // for second time user
    public void secondTime(){
        editor.putBoolean(Data,true);
        editor.commit();
    }
    // for first  time user
    public  void firstTime(){
       if (!this.activate()){
           //if b  is false then  jump to  LoginActivity
           Intent intent = new Intent(context, TrialActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           context.startActivity(intent);
       }
    }
    //to  get  the default value as false
    private boolean activate(){
        return sharedPreferences.getBoolean(Data,false);
    }
}
