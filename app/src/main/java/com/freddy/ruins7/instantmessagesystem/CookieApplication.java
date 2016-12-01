package com.freddy.ruins7.instantmessagesystem;

import android.app.Application;
import org.apache.http.cookie.Cookie;

import java.net.CookieStore;
import java.util.List;

/**
 * Created by ruins7 on 2016-11-01.
 */

public class CookieApplication extends Application{

    private List<Cookie> cookies;


    public List<Cookie> getCookie(){
        return cookies;
    }
    public void setCookie(List<Cookie> cks){
        cookies = cks;
    }
}
