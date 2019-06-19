package com.example.rauber.lixotec.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SystemPreferences {

    Context context;
    SharedPreferences sharedPreferences;
    private int userId;
    private String userEmail;
    private String userName;
    private boolean alreadyLogged;
    private int userType;

    public void removePreferences(){
        sharedPreferences.edit().clear().commit();
    }

    public boolean getAlreadyLogged(){
        alreadyLogged = sharedPreferences.getBoolean("logged", false);
        return alreadyLogged;
    }

    public void setAlreadyLogged(boolean logged){
        this.alreadyLogged = logged;
        sharedPreferences.edit().putBoolean("logged", alreadyLogged).apply();
    }

    public int getUserId(){
        userId = sharedPreferences.getInt("userId", 0);
        return userId;
    }

    public void setUserUd(int userId){
        this.userId = userId;
        sharedPreferences.edit().putInt("userId",userId).apply();
    }

    public int getUserType(){
        userType = sharedPreferences.getInt("userType", 0);
        return userType;
    }

    public void setUserType(int userType){
        this.userType = userType;
        sharedPreferences.edit().putInt("userType",userType).apply();
    }

    public String getUserEmail(){
        userEmail = sharedPreferences.getString("userEmail","user@user.user");
        return userEmail;
    }

    public void setUserEmail(String userEmAIL){
        this.userEmail = userEmAIL;
        sharedPreferences.edit().putString("userEmail",userEmail).apply();
    }

    public String getName(){
        userName = sharedPreferences.getString("userName","Indefinido");
        return userName;
    }

    public void setUserName(String userName){
        userName = userName;
        sharedPreferences.edit().putString("userName",userName).apply();
    }

    public SystemPreferences(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
    }

}
