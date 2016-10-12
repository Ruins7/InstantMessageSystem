package com.freddy.ruins7.instantmessagesystem.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ruins7 on 2016-10-10.
 */

public class Message implements Serializable{

    private static final Long serialVersionUID = 3L;
    private int mid;
    private int uid;
    private Date time;
    private String content;
    private int gid;

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }
}
