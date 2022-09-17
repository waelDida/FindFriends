package com.findfriends.mycompany.findfriends.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class GdprPref {

    private SharedPreferences pref;
    private static final String STATUS = "gdpr_status";
    private static final String PREF_NAME="gdprPref";

    public GdprPref(Context context){
        pref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
    }

    public void setStatus(int status){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(STATUS,status);
        editor.apply();
    }

    public int getStatus(){
        return pref.getInt(STATUS,0);
    }
}
