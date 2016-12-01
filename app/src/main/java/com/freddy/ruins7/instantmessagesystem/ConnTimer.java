package com.freddy.ruins7.instantmessagesystem;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ruins7 on 2016-11-03.
 */

public class ConnTimer extends Application {

    private static ConnTimer connTimer;
    private static Handler handler;
    private static Runnable runnable;
    private static int flag = -1;//防止logoff之后再次跳用计时器的flag

    private static String urlbase;
    private static String url;
    private static Activity contextin;
    private static Boolean connStatus = true;

    private ConnTimer() {

    }

    public static int getFlag() {
        return flag;
    }

    public static ConnTimer getConnTimerInstance(Activity context) {
        urlbase = context.getResources().getString(R.string.url);
        url = urlbase + "connserver.action";
        flag++;
        if (connTimer == null) {
            connTimer = new ConnTimer();
        }
        return connTimer;
    }

    //server connect 计时器, 传入Activity context，从子线程向主线程发送消息，更新ui
    public void timerForConnServer(final int time, Activity context) {
        //将传入的activity赋给全局context
        contextin = context;
        handler = new Handler();
        runnable = new Runnable() {

            @Override
            public void run() {
                handler.postDelayed(this, time);//计时器执行过一次之后再次调用该计时器所延迟的时间
                //要做的事情，这里再次调用此Runnable对象，以实现每秒实现一次的定时器操作
                //try to conn to server
                //Log.v("try", "try to conn server");
                //启动通信线程
                ConnTest conntest = new ConnTest();
                conntest.start();

            }
        };
        //启动计时器
        handler.postDelayed(runnable, time);//设置后第一次执行该计时器所延迟的时间
    }

    //停止计时器
    private void stopTime(Runnable runnable) {
        handler.removeCallbacks(runnable);
    }

    public static Boolean getConnStatus() {
        return connStatus;
    }

    // server 通信 继承Thread类
    private class ConnTest extends Thread {

        public void run() {
            connStatus = isConnByHttp();
            //子线程与主线程通信,更新ui
            if (connStatus == false) {
                contextin.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(contextin, "Fail to connect to server. Place check your network.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        //尝试连接server
        private boolean isConnByHttp() {

            boolean isConn = false;
            URL urll = null;
            HttpURLConnection conn = null;
            try {
                urll = new URL(url);
                conn = (HttpURLConnection) urll.openConnection();
                // conn.setHeader("Range","bytes="+"");
                conn.setConnectTimeout(1000 * 5);
                if (conn.getResponseCode() == 200) {
                    isConn = true;
                }
            } catch (MalformedURLException e) {

            } catch (IOException e) {

            } finally {
                conn.disconnect();
            }
            return isConn;
        }
    }
}
