package com.mobile16.progetto.showdown.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mobile16.progetto.showdown.model.User;

/**
 * This class maintains session data across the app using the SharedPreferences.
 * We store a boolean flag isLoggedIn in shared preferences to check the login status.
 */
public class SessionManager implements ISessionManager{

    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_NAME = "userName";
    private static final String USER_ID = "userId";
    private static final String USER_PASSWORD = "userPassword";

    @SuppressWarnings("all")
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    @Override
    public void setUserLogin(User currentUser) {
        editor.putBoolean(KEY_IS_LOGGEDIN, true);

        editor.putString(USER_EMAIL, currentUser.getMail());
        editor.putString(USER_PASSWORD, currentUser.getPassword());
        if(currentUser.isUsernameSet()){
            editor.putString(USER_NAME, currentUser.getUsername());
        }
        if(currentUser.isUserIdSet()){
            editor.putInt(USER_ID, currentUser.getUserId());
        }

        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    @Override
    public void userLogout(){
        editor.putBoolean(KEY_IS_LOGGEDIN, false);
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    @Override
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    @Override
    public User getLoggedUser(){
        return new User(pref.getString(USER_EMAIL, ""), pref.getString(USER_PASSWORD, ""),
                pref.getString(USER_NAME, null), pref.getInt(USER_ID, -1));
    }
}