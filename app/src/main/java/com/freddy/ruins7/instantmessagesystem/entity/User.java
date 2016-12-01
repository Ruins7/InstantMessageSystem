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

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        User e = (User) o;
        return (this.getUid() == e.getUid());
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + getUid();
        return result;
    }


}
