package com.disys.systemtask.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.disys.systemtask.model.User;

public class SessionSharPref {
    // User name (make variable public to access from outside)

    public static final String key_eid="key_eid";
    public static final String key_name="key_name";
    public static final String key_idbarahno="key_idbarahno";
    public static final String key_emailaddress="key_emailaddress";
    public static final String key_unifiednumber="key_unifiednumber";
    public static final String key_mobileno="key_mobileno";
    public static final String key_password="key_password";
    public static final String key_new_user="key_new_user";


    // Sharedpref file name
    private static final String PREFER_NAME = "DisysPref";

    private SharedPreferences pref;
    // Editor reference for Shared preferences
    private SharedPreferences.Editor editor;
    // Context
    private Context _context;
    // Shared pref mode
    private int PRIVATE_MODE = 0;

    public SessionSharPref(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveIsNew()
    {
        editor.putBoolean(key_new_user,false);
        editor.commit();
    }

    public boolean getIsNew()
    {
        return pref.getBoolean(key_new_user,true);
    }

    public void saveUserDate(User data)
    {
        editor.putString(key_name, data.getUserName());
        editor.putString(key_password, data.getPassword());

        editor.commit();
    }

    public User getUserData() {
        User data=new User();
        data.setUserName(pref.getString(key_name, null));
        data.setPassword(pref.getString(key_password, null));

        return data;
    }


    public void clearSession() {
        editor.clear();
        editor.commit();
    }




}


