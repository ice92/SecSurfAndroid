package com.ftunram.secsurf.toolkit;

/**
 * Created by Ice on 2/13/2016.
 */
public class User {
    private String username,password;
    public void setUsername(String username){
        this.username=username;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public String getUsername(){
       return username;
    }
    public String getPassword(){
        return password;
    }
}
