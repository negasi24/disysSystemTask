package com.disys.systemtask.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.disys.systemtask.model.User;

import java.nio.charset.StandardCharsets;

public class SessionSharPref {
    // User name (make variable public to access from outside)


    public static final String key_name="key_name";
    public static final String key_consumerSec="key_consumerSec";
    public static final String key_consumerKey="key_consumerKey";

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
        editor.putString(key_name,encryptData( data.getUserName()));
        editor.putString(key_password,encryptData( data.getPassword()));

        editor.commit();
    }

    public void saveSecretCode(String s1,String s2)
    {
        editor.putString(key_consumerKey,encryptData(s1));
        editor.putString(key_consumerSec,encryptData(s2));

        editor.commit();
    }

    public String getConsumerKey()
    {
        return decriptData(pref.getString(key_consumerKey,""));
    }
    public String getConsumerSec()
    {
        return decriptData(pref.getString(key_consumerSec,""));
    }


    private String encryptData(String str)
    {
        byte[] data = str.getBytes(StandardCharsets.UTF_8);
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);

        return base64;
    }

    private String decriptData(String base64)
    {
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        String text = new String(data, StandardCharsets.UTF_8);

        return text;
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


