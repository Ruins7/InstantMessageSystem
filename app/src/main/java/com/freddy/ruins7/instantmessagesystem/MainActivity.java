package com.freddy.ruins7.instantmessagesystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.freddy.ruins7.instantmessagesystem.entity.Group;
import com.freddy.ruins7.instantmessagesystem.entity.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static EditText username;
    private static EditText password;
    private static Button login;
    private static Intent intent_ivtime;

    private static User loginuser;
    private List<Group> allgroups;
    private List<Group> joinedgroups;

    private static HttpClient client;
    private static HttpPost request;
    private static HttpEntity entity;
    private static HttpResponse response;

    private static String urlbase;
    private static String url;
    private static String connServerTime;

    private ConnTimer connTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlbase = this.getResources().getString(R.string.url);
        url = urlbase + "login.action";
        connServerTime = this.getResources().getString(R.string.connservertime);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);

        Login loginfunction = new Login();
        login.setOnClickListener(loginfunction);

        //server conn timer(singleton)
        connTimer = ConnTimer.getConnTimerInstance(this);
        if (ConnTimer.getFlag() == 0) {
            connTimer.timerForConnServer(Integer.valueOf(connServerTime), this);
        }
    }

    //login 监听事件
    class Login implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (username.getText().toString().trim().equals("") || password.getText().toString().trim().equals("")) {
                Toast.makeText(MainActivity.this, "username and password cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                loginuser = new User();
                loginuser.setUsername(username.getText().toString());
                loginuser.setPassword(password.getText().toString());

                //启动通信线程
                LoginConn loginConn = new LoginConn();
                loginConn.start();
            }
        }
    }

    //login 与 server 通信 继承Thread类
    class LoginConn extends Thread {

        private String statusMessage = "";

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
                json.put("password", loginuser.getPassword());

                //登录时不需要cookie信息
                StringEntity se = new StringEntity(json.toString(), "utf-8");
                request.setEntity(se);

                try {
                    response = client.execute(request);
                } catch (Exception e) {
                    statusMessage = "something wrong with network";
                }

                int statecode = response.getStatusLine().getStatusCode();
                //get cookie(session) for the first time
                List<Cookie> cookies = ((AbstractHttpClient) client).getCookieStore().getCookies();
                if (cookies.isEmpty()) {
                    statusMessage = "Server cannot validate your information";
                } else {
                    for (int i = 0; i < cookies.size(); i++) {
                        //保存cookie
                        Cookie cookie = cookies.get(i);
                        //全局变量存储cookie
                        CookieApplication appCookie = ((CookieApplication) getApplication());
                        appCookie.setCookie(cookies);
                    }
                    //Log.v("connect status", String.valueOf(statecode));
                    if (statecode == 200) { //请求成功
                        entity = response.getEntity();
                        if (entity != null) {
                            String out = EntityUtils.toString(entity, "UTF-8");
                            //Log.i("result from server", out);
                            if (out.trim().equals("empty")) {
                                statusMessage = "Fail to login";
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

                                //登录成功提示
                                statusMessage = "Login successfully";

                                //赋值(对象传递)
                                intent_ivtime = new Intent();
                                intent_ivtime.putExtra("allgroups", (Serializable) allgroups);
                                intent_ivtime.putExtra("alljoingroups", (Serializable) joinedgroups);
                                intent_ivtime.putExtra("loginuser", (Serializable) loginuser);
                                //跳转，将数据发送到下一个页面
                                intent_ivtime.setClass(MainActivity.this, AllGroupsActivity.class);
                                MainActivity.this.startActivity(intent_ivtime);
                            }
                        } else if (entity == null) {
                            //未请求到返回数据
                            statusMessage = "There is no return information from server";
                        }
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
            } catch (JSONException e) {
                //e.printStackTrace();
            } catch (URISyntaxException e) {
                //e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, statusMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
