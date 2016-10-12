package com.freddy.ruins7.instantmessagesystem.entity;

import java.io.Serializable;

/**
 * Created by ruins7 on 2016-10-10.
 */

public class User implements Serializable {

    private static final Long serialVersionUID = 1L;
    private int uid;
    private String username;
    private String password;


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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
