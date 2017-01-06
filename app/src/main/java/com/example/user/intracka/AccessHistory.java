package com.example.user.intracka;

/**
 * Created by user on 1/5/2017.
 */

public class AccessHistory {
    String usr_id,login_date,login_time,login_loc,logout_date,logout_time,logout_loc,device_imei_num1,duration;

    public AccessHistory(String usr_id, String login_date, String login_time, String login_loc, String device_imei_num) {
        this.usr_id = usr_id;
        this.login_date = login_date;
        this.login_time = login_time;
        this.login_loc = login_loc;
        this.device_imei_num1 = device_imei_num;
    }

    public String getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(String usr_id) {
        this.usr_id = usr_id;
    }

    public String getLogin_date() {
        return login_date;
    }

    public void setLogin_date(String login_date) {
        this.login_date = login_date;
    }

    public String getLogin_time() {
        return login_time;
    }

    public void setLogin_time(String login_time) {
        this.login_time = login_time;
    }

    public String getLogin_loc() {
        return login_loc;
    }

    public void setLogin_loc(String login_loc) {
        this.login_loc = login_loc;
    }

    public String getLogout_date() {
        return logout_date;
    }

    public void setLogout_date(String logout_date) {
        this.logout_date = logout_date;
    }

    public String getLogout_time() {
        return logout_time;
    }

    public void setLogout_time(String logout_time) {
        this.logout_time = logout_time;
    }

    public String getLogout_loc() {
        return logout_loc;
    }

    public void setLogout_loc(String logout_loc) {
        this.logout_loc = logout_loc;
    }

    public String getDevice_imei_num1() {
        return device_imei_num1;
    }

    public void setDevice_imei_num1(String device_imei_num1) {
        this.device_imei_num1 = device_imei_num1;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
