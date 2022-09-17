package com.findfriends.mycompany.findfriends.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AdsPref {

    private SharedPreferences pref;
    private final static String CLICK_NUMBER ="click_number_ads";

    public AdsPref(Context context){
        pref = context.getSharedPreferences("AdsPref",Context.MODE_PRIVATE);
    }

    public void setClickNumber(int i){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(CLICK_NUMBER,i);
        editor.apply();
    }

    public int getClickNumber(){
        return pref.getInt(CLICK_NUMBER,0);
    }
}
