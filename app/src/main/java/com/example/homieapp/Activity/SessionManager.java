package com.example.homieapp.Activity;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    public static final  String SESSION_USERSESSION = "userLoginSession";
    public static final  String SESSION_REMEMBERME = "rememberMe";

    //User session
    private static final  String IS_LOGIN = "IsLoggedIn";

    public static final  String KEY_FULLNAME = "full_name";
    public static final  String KEY_USERNAME = "username";
    public static final  String KEY_PHONENO = "phone_no";
    public static final  String KEY_EMAIL = "email";
    public static final  String KEY_PASSWORD = "password";
    public static final  String KEY_ADDRESS = "address";

    //Remember me
    private static final  String IS_REMEMBERME = "isRememberMe";
    public static final  String KEY_SESSIONPHONENO = "phone_no";
    public static final  String KEY_SESSIONPASSWORD = "password";

    public SessionManager(Context _context, String sessionName) {
        context = _context;
        usersSession = context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = usersSession.edit();

    }

    //User Login Session
    public void createLoginSession(String full_name, String username, String email, String phone_no, String password, String address){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_FULLNAME,full_name);
        editor.putString(KEY_USERNAME,username);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_PHONENO,phone_no);
        editor.putString(KEY_PASSWORD,password);
        editor.putString(KEY_ADDRESS,address);
        editor.commit();
    }
    public HashMap<String, String > getUserDetailFromSession(){
        HashMap<String, String> userData = new HashMap<String,String>();
        userData.put(KEY_FULLNAME, usersSession.getString(KEY_FULLNAME, null));
        userData.put(KEY_USERNAME, usersSession.getString(KEY_USERNAME, null));
        userData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));
        userData.put(KEY_PHONENO, usersSession.getString(KEY_PHONENO, null));
        userData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD, null));
        userData.put(KEY_ADDRESS, usersSession.getString(KEY_ADDRESS, null));
        return userData;
    }
    public boolean checkLogin(){
        return usersSession.getBoolean(IS_LOGIN, false);
    }
    public void logoutUserFromSession(){
        editor.clear();
        editor.commit();
    }

    //Remember me session functions
    public void createRememberMeSession(String phone_no, String password){
        editor.putBoolean(IS_REMEMBERME,true);
        editor.putString(KEY_SESSIONPHONENO,phone_no);
        editor.putString(KEY_SESSIONPASSWORD,password);
        editor.commit();
    }
    public HashMap<String, String > getRememberMeDetailFromSession(){
        HashMap<String, String> userData = new HashMap<String,String>();
        userData.put(KEY_SESSIONPHONENO, usersSession.getString(KEY_SESSIONPHONENO, null));
        userData.put(KEY_SESSIONPASSWORD, usersSession.getString(KEY_SESSIONPASSWORD, null));
        return userData;
    }
    public boolean checkRememberMe(){
        return usersSession.getBoolean(IS_REMEMBERME, false);
    }

}
