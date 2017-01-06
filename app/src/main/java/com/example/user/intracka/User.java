package com.example.user.intracka;

/**
 * Created by user on 1/4/2017.
 */

public class User {
    String usr_id, site_id, username,password;

    public User(){};

    public User(String usr_id, String site_id, String username, String password) {
        this.usr_id = usr_id;
        this.site_id = site_id;
        this.username = username;
        this.password = password;
    }

    public String getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(String usr_id) {
        this.usr_id = usr_id;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
