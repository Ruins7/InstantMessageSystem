package com.freddy.ruins7.instantmessagesystem.entity;

import java.io.Serializable;

/**
 * Created by ruins7 on 2016-10-10.
 */

public class UserJoin implements Serializable{

    //user join group info

    private static final Long serialVersionUID = 2L;
    private int ujid;
    private int uid;
    private int gid;

    public int getUjid() {
        return ujid;
    }

    public void setUjid(int ujid) {
        this.ujid = ujid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }
}
