package com.freddy.ruins7.instantmessagesystem.entity;

import java.io.Serializable;

/**
 * Created by ruins7 on 2016-10-10.
 */

public class Group implements Serializable{

    private static final Long serialVersionUID = 4L;
    private int gid;
    private String gname;

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }
}