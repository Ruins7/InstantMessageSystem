package com.freddy.ruins7.instantmessagesystem;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.freddy.ruins7.instantmessagesystem.entity.Group;
import com.freddy.ruins7.instantmessagesystem.entity.Message;
import com.freddy.ruins7.instantmessagesystem.entity.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by ruins7 on 2016-10-11.
 */

public class CertainGroupActivity extends AppCompatActivity {

    private static String messContentStr;
    private static TextView groupid;
    private static TextView status;
    private static TextView messContent;
    private static Button sendMess;
    private static TextView allMemberse;
    private static Button quit;
    private static Button backToAllGroups;
    private static RelativeLayout messview;
    private static ScrollView scroll1;
    private static ScrollView scroll2;
    private static TextView newmesstv;

    private static User loginuser;
    private static List<User> allusers;
    private static List<Message> allmesses;
    private static int groupidvalue;
    private static int numofmess = 0;
    private static Message message;
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
    private static Date curDate;
    private static String strTime = "";
    private List<Group> allgroups;
    private List<Group> joinedgroups;
    private static Intent intent_ivtime;
    private static Bundle bundle;
    private static List<Cookie> cookies;

    private static HttpClient client;
    private static HttpPost request;
    private static HttpEntity entity;
    private static HttpResponse response;

    private static String urlbase;
    private static String url;
    private static String url2;
    private static String url3;

    private static Handler handler;
    private static Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.certaingroup_activity);

        urlbase = this.getResources().getString(R.string.url);
        url = urlbase + "sendmess.action";
        url2 = urlbase + "quitgroup.action";
        url3 = urlbase + "findallgroups.action";

        groupid = (TextView) findViewById(R.id.groupid);
        status = (TextView) findViewById(R.id.onegroupstatus);
        messContent = (TextView) findViewById(R.id.messagecontent);
        sendMess = (Button) findViewById(R.id.sendmessage);
        allMemberse = (TextView) findViewById(R.id.allmembers);
        quit = (Button) findViewById(R.id.quitgroup);
        backToAllGroups = (Button) findViewById(R.id.backtoallgroups);
        messview = (RelativeLayout) findViewById(R.id.messview);

        Send sendmess = new Send();
        QuitGroup quitGroup = new QuitGroup();
        BackToAllGroups backtogroups = new BackToAllGroups();

        sendMess.setOnClickListener(sendmess);
        quit.setOnClickListener(quitGroup);
        backToAllGroups.setOnClickListener(backtogroups);

        //ScrollView 嵌套的用法
        scroll1 = (ScrollView) findViewById(R.id.scroll1);
        scroll2 = (ScrollView) findViewById(R.id.scroll2);

        scroll1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                findViewById(R.id.scroll2).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        scroll2.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        //接收上一个页面传过来的参数，group的id,userID
        loginuser = new User();
        loginuser = (User) getIntent().getSerializableExtra("loginuser");
        allusers = (ArrayList<User>) getIntent().getSerializableExtra("allusers");
        allmesses = (ArrayList<Message>) getIntent().getSerializableExtra("allmesses");
        bundle = this.getIntent().getExtras();
        groupidvalue = (int) bundle.getInt("groupid");
        groupid.setText(String.valueOf(groupidvalue));

        //设置mess的数量，为发送消息时的动态位置做准备
        if (allmesses != null) {
            numofmess = allmesses.size();
        }

        //连接server，并显示连接状态(需要计时器)
        handler = new Handler();
        runnable = new Runnable() {

            @Override
            public void run() {
                handler.postDelayed(this, 5000);//计时器执行过一次之后再次调用该计时器所延迟的时间
                //要做的事情，这里再次调用此Runnable对象，以实现每秒实现一次的定时器操作
                Boolean serverStatus = ConnTimer.getConnStatus();
                if (serverStatus == true) {
                    status.setText("UP" + "(" + loginuser.getUsername() + ")");
                    status.setTextColor(Color.parseColor("#4F8300"));
                } else {
                    status.setText("DOWN" + "(" + loginuser.getUsername() + ")");
                    status.setTextColor(Color.parseColor("#B92A0C"));
                }
            }
        };
        //启动计时器
        handler.postDelayed(runnable, 1000);//设置后第一次执行该计时器所延迟的时间

        //查询该group的消息记录messages，按时间排列(id)，并且显示在页面上(需要计时器)
        if (allmesses != null) {
            for (int i = 0; i < allmesses.size(); i++) {
                TextView tv = new TextView(this);
                tv.setText("UserID(" + allmesses.get(i).getUid() + "):  " + allmesses.get(i).getContent() + "\n (" + allmesses.get(i).getTime() + ")");
                tv.setHeight(300);
                tv.setWidth(1000);
                tv.setPadding(20, 10, 50, 10);
                tv.setTextColor(Color.parseColor("#000000"));
                tv.setTextSize(16);
                tv.setBackgroundColor(Color.parseColor("#ECECEC"));
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(20, 100 + (350 * i), 50, 50);
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//与父容器的左侧对齐
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);//与父容器的上侧对齐

                tv.setLayoutParams(lp);
                messview.addView(tv);
            }
            Toast.makeText(CertainGroupActivity.this, "You have " + allmesses.size() + " messages in this group.", Toast.LENGTH_SHORT).show();
        }

        //查询该group内的所有user，并显示在页面上
        String allmemebersname = "";
        Set<User> hs = new HashSet<User>(allusers);
        for (User user : hs) {
            allmemebersname += user.getUsername() + "(UserID: " + user.getUid() + ")" + ", ";
        }
        allMemberse.setText(allmemebersname);
    }

    //send message
    class Send implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            messContentStr = messContent.getText().toString();
            if (!messContentStr.trim().contentEquals("")) {
                //show on activity, and send, write into DB
                //发送到server
                message = new Message();
                message.setUid(loginuser.getUid());
                curDate = new Date(System.currentTimeMillis());//获取当前时间
                strTime = formatter.format(curDate);
                message.setTime(strTime);
                message.setContent(messContentStr);
                message.setGid(groupidvalue);
                //启动通信线程
                SendMessConn sendMessConn = new SendMessConn();
                sendMessConn.start();

                //将新消息显示在页面上
                newmesstv = new TextView(CertainGroupActivity.this);
                newmesstv.setText("UserID(" + loginuser.getUid() + "):  " + messContentStr + "\n (" + strTime + ")");
                newmesstv.setHeight(300);
                newmesstv.setWidth(1000);
                newmesstv.setPadding(20, 10, 50, 10);
                newmesstv.setTextColor(Color.parseColor("#000000"));
                newmesstv.setTextSize(16);
                newmesstv.setBackgroundColor(Color.parseColor("#ECECEC"));

                //设置textview样式
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(20, 100 + (350 * numofmess), 50, 50);
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//与父容器的左侧对齐
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);//与父容器的上侧对齐

                newmesstv.setLayoutParams(lp);
                messview.addView(newmesstv);
                //发送成功后清空消息控件
                messContent.setText("");
                Toast.makeText(CertainGroupActivity.this, "You have a new message.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CertainGroupActivity.this, "Message can not be empty.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //send message 与 server 通信 继承Thread类
    class SendMessConn extends Thread {

        private String statusMessage = null;
        @Override
        public void run() {
            // 初始化消息循环队列，需要在Handler创建之前
            Looper.prepare();
            client = new DefaultHttpClient();
            HttpPost request;
            try {
                request = new HttpPost(new URI(url));
                //json 封装
                JSONObject json = new JSONObject();
                json.put("uid", message.getUid());
                json.put("time", message.getTime());
                json.put("content", message.getContent());
                json.put("gid", message.getGid());
                //从全局变量获取cookie
                CookieApplication appCookie = ((CookieApplication) getApplication());
                cookies = appCookie.getCookie();
                StringEntity se = new StringEntity(json.toString(), "utf-8");
                request.setEntity(se);
                //set http header cookie信息
                request.setHeader("cookie", "JSESSIONID=" + cookies.get(0).getValue());
                try {
                    response = client.execute(request);
                } catch (Exception e) {
                    statusMessage = "something wrong with network";
                }
                int statecode = response.getStatusLine().getStatusCode();
                //Log.v("connect status", String.valueOf(statecode));
                if (statecode == 200) { //请求成功
                    entity = response.getEntity();
                    if (entity != null) {
                        String out = EntityUtils.toString(entity, "UTF-8");
                        //Log.i("result from server", out);
                        if (out.equals("sendfail")) {
                            statusMessage = "Fail to send";
                        } else {
                            //发送成功提示
                            //修改numofmess, ++表示textview的append
                            numofmess++;
                        }
                    } else if (entity == null) {
                        //未请求到返回数据
                        statusMessage = "There is no return information from server";
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
            if(statusMessage != null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CertainGroupActivity.this, statusMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    //quit group
    class QuitGroup implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //delete 该用户的该group记录, 跳转
            //启动通信线程
            QuitGroupConn quitGroupConn = new QuitGroupConn();
            quitGroupConn.start();
            Toast.makeText(CertainGroupActivity.this, "quit group successfully", Toast.LENGTH_SHORT).show();
        }
    }

    //quit group 与 server 通信 继承Thread类
    class QuitGroupConn extends Thread {

        private String statusMessage = null;
        @Override
        public void run() {
            // 初始化消息循环队列，需要在Handler创建之前
            Looper.prepare();
            client = new DefaultHttpClient();
            HttpPost request;
            try {
                request = new HttpPost(new URI(url2));
                //json 封装
                JSONObject json = new JSONObject();
                json.put("uid", loginuser.getUid());
                json.put("gid", groupidvalue);
                //从全局变量获取cookie
                CookieApplication appCookie = ((CookieApplication) getApplication());
                cookies = appCookie.getCookie();
                StringEntity se = new StringEntity(json.toString(), "utf-8");
                request.setEntity(se);
                //set http header cookie信息
                request.setHeader("cookie", "JSESSIONID=" + cookies.get(0).getValue());
                try {
                    response = client.execute(request);
                } catch (Exception e) {
                    statusMessage = "something wrong with network";
                }
                int statecode = response.getStatusLine().getStatusCode();
                //Log.v("connect status", String.valueOf(statecode));
                if (statecode == 200) { //请求成功
                    entity = response.getEntity();
                    if (entity != null) {
                        String out = EntityUtils.toString(entity, "UTF-8");
                        //Log.i("result from server", out);
                        if (out.equals("failtoquit")) {
                            statusMessage = "Fail to quit";
                        } else {
                            //json解析
                            JSONArray jsonArray = new JSONArray(out);
                            JSONObject jsonuser = jsonArray.getJSONObject(0);
                            JSONArray jsonallgroups = jsonArray.getJSONArray(1);
                            JSONArray jsonjoinedgroups = jsonArray.getJSONArray(2);
                            JSONArray jsonallgroups2 = (JSONArray) jsonallgroups.get(0);
                            JSONArray jsonjoinedgroups2 = (JSONArray) jsonjoinedgroups.get(0);
                            //loginuser
                            loginuser.setUsername(jsonuser.getString("username"));
                            loginuser.setUid(jsonuser.getInt("uid"));
                            //all groups
                            allgroups = new ArrayList<Group>();
                            for (int i = 0; i < jsonallgroups2.length(); i++) {
                                Group g = new Group();
                                g.setGid(jsonallgroups2.getJSONObject(i).getInt("gid"));
                                g.setGname(jsonallgroups2.getJSONObject(i).getString("gname"));
                                allgroups.add(g);
                            }
                            //all joined groups
                            joinedgroups = new ArrayList<Group>();
                            for (int i = 0; i < jsonjoinedgroups2.length(); i++) {
                                Group uj = new Group();
                                uj.setGid(jsonjoinedgroups2.getJSONObject(i).getInt("gid"));
                                uj.setGname(jsonjoinedgroups2.getJSONObject(i).getString("gname"));
                                joinedgroups.add(uj);
                            }
                            //赋值(对象传递)
                            intent_ivtime = new Intent();
                            intent_ivtime.putExtra("allgroups", (Serializable) allgroups);
                            intent_ivtime.putExtra("alljoingroups", (Serializable) joinedgroups);
                            intent_ivtime.putExtra("loginuser", (Serializable) loginuser);
                            //跳转，将数据发送到下一个页面
                            intent_ivtime.setClass(CertainGroupActivity.this, AllGroupsActivity.class);
                            CertainGroupActivity.this.startActivity(intent_ivtime);
                            //跳转之后停止检查server状态的计时器
                            stopCheckServerStauts(runnable);
                        }
                    } else if (entity == null) {
                        //未请求到返回数据
                        statusMessage = "There is no return information from server";
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
            if(statusMessage != null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CertainGroupActivity.this, statusMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    //回到所有groups页面
    class BackToAllGroups implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //启动通信线程
            BackGroupConn backconn = new BackGroupConn();
            backconn.start();
        }
    }

    //backtogroup 与 server 通信 继承Thread类
    class BackGroupConn extends Thread {

        private String statusMessage = null;
        @Override
        public void run() {
            // 初始化消息循环队列，需要在Handler创建之前
            Looper.prepare();
            client = new DefaultHttpClient();
            HttpPost request;
            try {
                request = new HttpPost(new URI(url3));
                JSONObject json = new JSONObject();
                json.put("username", loginuser.getUsername());
                json.put("uid", loginuser.getUid());
                //从全局变量获取cookie
                CookieApplication appCookie = ((CookieApplication) getApplication());
                cookies = appCookie.getCookie();
                StringEntity se = new StringEntity(json.toString(), "utf-8");
                request.setEntity(se);
                //set http header cookie信息
                request.setHeader("cookie", "JSESSIONID=" + cookies.get(0).getValue());
                try {
                    response = client.execute(request);
                } catch (Exception e) {
                    statusMessage = "something wrong with network";
                }
                int statecode = response.getStatusLine().getStatusCode();
                //Log.v("connect status", String.valueOf(statecode));
                if (statecode == 200) { //请求成功
                    entity = response.getEntity();
                    if (entity != null) {
                        String out = EntityUtils.toString(entity, "UTF-8");
                        //Log.i("result from server", out);
                        if (out.trim().equals("groupempty")) {
                            statusMessage = "Fail to back";
                        } else {
                            //json解析
                            JSONArray jsonArray = new JSONArray(out);
                            JSONObject jsonuser = jsonArray.getJSONObject(0);
                            JSONArray jsonallgroups = jsonArray.getJSONArray(1);
                            JSONArray jsonjoinedgroups = jsonArray.getJSONArray(2);
                            JSONArray jsonallgroups2 = (JSONArray) jsonallgroups.get(0);
                            JSONArray jsonjoinedgroups2 = (JSONArray) jsonjoinedgroups.get(0);
                            //loginuser
                            loginuser.setUsername(jsonuser.getString("username"));
                            loginuser.setUid(jsonuser.getInt("uid"));
                            //all groups
                            allgroups = new ArrayList<Group>();
                            for (int i = 0; i < jsonallgroups2.length(); i++) {
                                Group g = new Group();
                                g.setGid(jsonallgroups2.getJSONObject(i).getInt("gid"));
                                g.setGname(jsonallgroups2.getJSONObject(i).getString("gname"));
                                allgroups.add(g);
                            }
                            //all joined groups
                            joinedgroups = new ArrayList<Group>();
                            for (int i = 0; i < jsonjoinedgroups2.length(); i++) {
                                Group uj = new Group();
                                uj.setGid(jsonjoinedgroups2.getJSONObject(i).getInt("gid"));
                                uj.setGname(jsonjoinedgroups2.getJSONObject(i).getString("gname"));
                                joinedgroups.add(uj);
                            }
                            //赋值(对象传递)
                            intent_ivtime = new Intent();
                            intent_ivtime.putExtra("allgroups", (Serializable) allgroups);
                            intent_ivtime.putExtra("alljoingroups", (Serializable) joinedgroups);
                            intent_ivtime.putExtra("loginuser", (Serializable) loginuser);
                            //跳转，将数据发送到下一个页面
                            intent_ivtime.setClass(CertainGroupActivity.this, AllGroupsActivity.class);
                            CertainGroupActivity.this.startActivity(intent_ivtime);
                            //跳转之后停止检查server状态的计时器
                            stopCheckServerStauts(runnable);
                        }

                    } else if (entity == null) {
                        //未请求到返回数据
                        statusMessage = "There is no return information from server";
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
            if(statusMessage != null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CertainGroupActivity.this, statusMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    //停止计时器
    private void stopCheckServerStauts(Runnable runnable) {
        handler.removeCallbacks(runnable);
    }
}
