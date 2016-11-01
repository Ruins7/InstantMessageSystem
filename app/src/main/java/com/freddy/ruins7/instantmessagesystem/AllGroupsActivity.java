package com.freddy.ruins7.instantmessagesystem;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.freddy.ruins7.instantmessagesystem.entity.Group;
import com.freddy.ruins7.instantmessagesystem.entity.Message;
import com.freddy.ruins7.instantmessagesystem.entity.User;
import com.freddy.ruins7.instantmessagesystem.entity.UserJoin;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruins7 on 2016-10-10.
 */

public class AllGroupsActivity extends AppCompatActivity {

    private static List<Group> allgroups;
    private static List<Group> alljoingroups;
    private static User loginuser;

    private static Button logoff;
    private static TextView status;

    private static TextView groupid1;
    private static TextView groupname1;
    private static TextView groupstatus1;
    private static Button groupjoin1;

    private static TextView groupid2;
    private static TextView groupname2;
    private static TextView groupstatus2;
    private static Button groupjoin2;

    private static TextView groupid3;
    private static TextView groupname3;
    private static TextView groupstatus3;
    private static Button groupjoin3;

    private static TextView groupid4;
    private static TextView groupname4;
    private static TextView groupstatus4;
    private static Button groupjoin4;

    private static HttpClient client;
    private static HttpPost request;
    private static HttpEntity entity;
    private static HttpResponse response;

    private static Group group1;
    private static Group group2;
    private static Group group3;
    private static Group group4;
    private static UserJoin uj;

    private static List<User> allusers;
    private static List<Message> allmesses;

    private static Intent intent_ivtime;
    private static Bundle bundle;

    //private static String url = "http://192.168.191.1:8080/InstantMessageServer/joingroup.action";//ip address may change
    private static String url = "http://192.168.0.43:8080/InstantMessageServer/joingroup.action";//ip address may change

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allgroups_activity);

        //TODO 连接server，并显示连接状态(需要计时器)

        //接收登录界面传来的参数
        loginuser = (User) getIntent().getSerializableExtra("loginuser");
        allgroups = (ArrayList<Group>) getIntent().getSerializableExtra("allgroups");
        alljoingroups = (ArrayList<Group>) getIntent().getSerializableExtra("alljoingroups");

        //页面的控件
        status = (TextView) findViewById(R.id.status);
        logoff = (Button) findViewById(R.id.logff);

        groupid1 = (TextView) findViewById(R.id.groupid1);
        groupname1 = (TextView) findViewById(R.id.groupname1);
        groupstatus1 = (TextView) findViewById(R.id.groupstatus1);
        groupjoin1 = (Button) findViewById(R.id.groupjoin1);

        groupid2 = (TextView) findViewById(R.id.groupid2);
        groupname2 = (TextView) findViewById(R.id.groupname2);
        groupstatus2 = (TextView) findViewById(R.id.groupstatus2);
        groupjoin2 = (Button) findViewById(R.id.groupjoin2);

        groupid3 = (TextView) findViewById(R.id.groupid3);
        groupname3 = (TextView) findViewById(R.id.groupname3);
        groupstatus3 = (TextView) findViewById(R.id.groupstatus3);
        groupjoin3 = (Button) findViewById(R.id.groupjoin3);

        groupid4 = (TextView) findViewById(R.id.groupid4);
        groupname4 = (TextView) findViewById(R.id.groupname4);
        groupstatus4 = (TextView) findViewById(R.id.groupstatus4);
        groupjoin4 = (Button) findViewById(R.id.groupjoin4);

        //初始化时所有的group状态都是 not joined,按妞都是join
        groupstatus1.setText("Not Joined");
        groupstatus2.setText("Not Joined");
        groupstatus3.setText("Not Joined");
        groupstatus4.setText("Not Joined");

        groupjoin1.setText("Join");
        groupjoin2.setText("Join");
        groupjoin3.setText("Join");
        groupjoin4.setText("Join");

        Logoff logofffunction = new Logoff();
        JoinGroup joinGroup = new JoinGroup();

        //能登录成功肯定是连接成功，所以是up
        status.setText("UP" + "(" + loginuser.getUsername() + ")");
        status.setTextColor(Color.parseColor("#4F8300"));
        logoff.setOnClickListener(logofffunction);
        groupjoin1.setOnClickListener(joinGroup);
        groupjoin2.setOnClickListener(joinGroup);
        groupjoin3.setOnClickListener(joinGroup);
        groupjoin4.setOnClickListener(joinGroup);

        //给页面控件赋值
        setValues(loginuser, allgroups, alljoingroups);

    }

    //给groups控件赋值
    private void setValues(User loginuser, List<Group> allgroups, List<Group> alljoingroups) {

        group1 = new Group();
        group2 = new Group();
        group3 = new Group();
        group4 = new Group();
        groupid1.setText(String.valueOf(allgroups.get(0).getGid()));
        groupname1.setText(allgroups.get(0).getGname());
        group1.setGid(allgroups.get(0).getGid());
        group1.setGname(allgroups.get(0).getGname());
        groupid2.setText(String.valueOf(allgroups.get(1).getGid()));
        groupname2.setText(allgroups.get(1).getGname());
        group2.setGid(allgroups.get(1).getGid());
        group2.setGname(allgroups.get(1).getGname());
        groupid3.setText(String.valueOf(allgroups.get(2).getGid()));
        groupname3.setText(allgroups.get(2).getGname());
        group3.setGid(allgroups.get(2).getGid());
        group3.setGname(allgroups.get(2).getGname());
        groupid4.setText(String.valueOf(allgroups.get(3).getGid()));
        groupname4.setText(allgroups.get(3).getGname());
        group4.setGid(allgroups.get(3).getGid());
        group4.setGname(allgroups.get(3).getGname());

        for (int i = 0; i < alljoingroups.size(); i++) {
            if (alljoingroups.get(i).getGid() == 1) {
                groupstatus1.setText("Joined");
                groupjoin1.setText("Enter");
            } else if (alljoingroups.get(i).getGid() == 2) {
                groupstatus2.setText("Joined");
                groupjoin2.setText("Enter");
            } else if (alljoingroups.get(i).getGid() == 3) {
                groupstatus3.setText("Joined");
                groupjoin3.setText("Enter");
            } else if (alljoingroups.get(i).getGid() == 4) {
                groupstatus4.setText("Joined");
                groupjoin4.setText("Enter");
            }
        }
    }

    //logoff
    class Logoff implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent_ivtime = new Intent();
            intent_ivtime.setClass(AllGroupsActivity.this, MainActivity.class);
            AllGroupsActivity.this.startActivity(intent_ivtime);
        }
    }

    //join or enter group
    class JoinGroup implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO 获得group的id,userID，写入server db,然后跳转
            String groupaction = ((Button) v).getText().toString();
            int groupid = ((Button) v).getId();

            //获取点击的groupid
            uj = new UserJoin();
            uj.setUid(loginuser.getUid());
            if (groupid == R.id.groupjoin1) {
                uj.setGid(group1.getGid());
            } else if (groupid == R.id.groupjoin2) {
                uj.setGid(group2.getGid());
            } else if (groupid == R.id.groupjoin3) {
                uj.setGid(group3.getGid());
            } else if (groupid == R.id.groupjoin4) {
                uj.setGid(group4.getGid());
            }

            if (groupaction.equals("Join")) {//加入
                //启动通信线程
                JoininConn joininConn = new JoininConn();
                joininConn.start();

            } else if (groupaction.equals("Enter")) {//进入
                //直接请求该group下的信息
                GetGroupConn getGroupConn = new GetGroupConn();
                getGroupConn.start();
            }
        }
    }

    //join group 与 server 通信 继承Thread类
    class JoininConn extends Thread {

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
                json.put("gid", uj.getGid());
                json.put("uid", uj.getUid());

                //从全局变量获取cookie
                CookieApplication appCookie = ((CookieApplication) getApplication());
                List<Cookie> cookies = appCookie.getCookie();

                StringEntity se = new StringEntity(json.toString(), "utf-8");
                request.setEntity(se);
                //set http header cookie信息
                request.setHeader("cookie", "JSESSIONID=" + cookies.get(0).getValue());
                //TODO 网络连接出现问题到时候需要提示
                try {
                    response = client.execute(request);
                } catch (Exception e) {
                    Log.v("tag", "something wrong with network111111");
                }

                int statecode = response.getStatusLine().getStatusCode();
                Log.v("connect status", String.valueOf(statecode));
                if (statecode == 200) { //请求成功
                    entity = response.getEntity();
                    if (entity != null) {
                        String out = EntityUtils.toString(entity, "UTF-8");
                        Log.i("result from server", out);
                        if (out.trim().equals("failtojoin")) {
                            //TODO join失败
                        } else {
                            //json解析
                            JSONArray jsonArray = new JSONArray(out);

                            JSONArray jsonusers = jsonArray.getJSONArray(0);
                            JSONArray jsonmesses = jsonArray.getJSONArray(1);

                            //all groups
                            allusers = new ArrayList<User>();
                            for (int i = 0; i < jsonusers.length(); i++) {
                                User u = new User();
                                u.setUid(jsonusers.getJSONObject(i).getInt("uid"));
                                u.setUsername(jsonusers.getJSONObject(i).getString("username"));
                                allusers.add(u);
                            }

                            //all joined groups
                            allmesses = new ArrayList<Message>();
                            for (int i = 0; i < jsonmesses.length(); i++) {
                                Message m = new Message();
                                m.setMid(jsonmesses.getJSONObject(i).getInt("mid"));
                                m.setGid(jsonmesses.getJSONObject(i).getInt("gid"));
                                m.setUid(jsonmesses.getJSONObject(i).getInt("uid"));
                                m.setTime(jsonmesses.getJSONObject(i).getString("time"));
                                m.setContent(jsonmesses.getJSONObject(i).getString("content"));
                                allmesses.add(m);
                            }

                            //赋值(对象传递)
                            bundle = new Bundle();
                            intent_ivtime = new Intent();
                            intent_ivtime.putExtra("allusers", (Serializable) allusers);
                            intent_ivtime.putExtra("allmesses", (Serializable) allmesses);
                            intent_ivtime.putExtra("loginuser", loginuser);
                            bundle.putInt("groupid", uj.getGid());
                            intent_ivtime.putExtras(bundle);
                            //跳转，将数据发送到下一个页面
                            intent_ivtime.setClass(AllGroupsActivity.this, CertainGroupActivity.class);
                            AllGroupsActivity.this.startActivity(intent_ivtime);

                        }

                    } else if (entity == null) {
                        //未请求到返回数据
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("tag", e.toString());
            }
        }
    }

    //enter group 与 server 通信 继承Thread类
    class GetGroupConn extends Thread {

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
                json.put("username", loginuser.getUsername());
                json.put("uid", loginuser.getUid());
                json.put("gid", uj.getGid());

                //从全局变量获取cookie
                CookieApplication appCookie = ((CookieApplication) getApplication());
                List<Cookie> cookies = appCookie.getCookie();

                StringEntity se = new StringEntity(json.toString(), "utf-8");
                request.setEntity(se);
                //set http header cookie信息
                request.setHeader("cookie", "JSESSIONID=" + cookies.get(0).getValue());

                //TODO 网络连接出现问题到时候需要提示
                try {
                    response = client.execute(request);
                } catch (Exception e) {
                    Log.v("tag", "something wrong with network111111");
                }

                int statecode = response.getStatusLine().getStatusCode();
                Log.v("connect status", String.valueOf(statecode));
                if (statecode == 200) { //请求成功
                    entity = response.getEntity();
                    if (entity != null) {
                        String out = EntityUtils.toString(entity, "UTF-8");
                        Log.i("result from server", out);
                        if (out.trim().equals("failtoenter")) {
                            //TODO enter fail
                        } else {
                            //json解析
                            JSONArray jsonArray = new JSONArray(out);

                            JSONArray jsonusers = jsonArray.getJSONArray(0);
                            JSONArray jsonmesses = jsonArray.getJSONArray(1);

                            //all groups
                            allusers = new ArrayList<User>();
                            for (int i = 0; i < jsonusers.length(); i++) {
                                User u = new User();
                                u.setUid(jsonusers.getJSONObject(i).getInt("uid"));
                                u.setUsername(jsonusers.getJSONObject(i).getString("username"));
                                allusers.add(u);
                            }

                            //all joined groups
                            allmesses = new ArrayList<Message>();
                            for (int i = 0; i < jsonmesses.length(); i++) {
                                Message m = new Message();
                                m.setMid(jsonmesses.getJSONObject(i).getInt("mid"));
                                m.setGid(jsonmesses.getJSONObject(i).getInt("gid"));
                                m.setUid(jsonmesses.getJSONObject(i).getInt("uid"));
                                m.setTime(jsonmesses.getJSONObject(i).getString("time"));
                                m.setContent(jsonmesses.getJSONObject(i).getString("content"));
                                allmesses.add(m);
                            }

                            //赋值(对象传递)
                            bundle = new Bundle();
                            intent_ivtime = new Intent();
                            intent_ivtime.putExtra("allusers", (Serializable) allusers);
                            intent_ivtime.putExtra("allmesses", (Serializable) allmesses);
                            intent_ivtime.putExtra("loginuser", loginuser);
                            bundle.putInt("groupid", uj.getGid());
                            intent_ivtime.putExtras(bundle);
                            //跳转，将数据发送到下一个页面
                            intent_ivtime.setClass(AllGroupsActivity.this, CertainGroupActivity.class);
                            AllGroupsActivity.this.startActivity(intent_ivtime);

                        }

                    } else if (entity == null) {
                        //未请求到返回数据
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("tag", e.toString());
            }
        }
    }
}
