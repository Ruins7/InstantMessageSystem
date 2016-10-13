package com.freddy.ruins7.instantmessagesystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.freddy.ruins7.instantmessagesystem.entity.Group;
import com.freddy.ruins7.instantmessagesystem.entity.User;
import com.freddy.ruins7.instantmessagesystem.entity.UserJoin;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static EditText username;
    private static EditText password;
    private static Button login;
    private static ArrayList<Group> allgrouplist;
    private static ArrayList<UserJoin> userjoingrouplist;
    private static User loginuser;
    private static Intent intent_ivtime;

    private List<HashMap<String, Object>> users;
    private HashMap<String, Object> user;

    private static HttpClient client;
    private static HttpPost request;
    private static HttpEntity entity;
    private static HttpResponse response;

    private static String url = "http://192.168.0.29:8080/InstantMessageServer/jsonmess/getmess.action";//ip address may change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);

        Login loginfunction = new Login();

        login.setOnClickListener(loginfunction);

    }

    //与server 通信
    class Login implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            loginuser = new User();
            loginuser.setUsername(username.getText().toString());
            loginuser.setPassword(username.getText().toString());

            //server 通信
            Boolean loginresult = false;

            client = new DefaultHttpClient();
            HttpPost request;

            try {
                request = new HttpPost(new URI(url));
                response = client.execute(request);
                int statecode = response.getStatusLine().getStatusCode();
                if (statecode == 200) { //请求成功
                    entity = response.getEntity();
                    if (entity != null) {
                        String out = EntityUtils.toString(entity, "UTF-8");
                        Log.i("tag", out);

                        JSONArray jsonArray = new JSONArray(out);

                        users = new ArrayList<HashMap<String, Object>>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            int id = jsonObject.getInt("id");
                            String username = jsonObject.getString("username");
                            String password = jsonObject.getString("password");

                            Log.v("mess",id + " " + username  +  "   "  + password );
                            user = new HashMap<String, Object>();
                            user.put("id", id);
                            user.put("username", username);
                            user.put("password", password);

                            users.add(user);
                        }
                        //将数据返回到页面上
                      /*  SimpleAdapter adapter = new SimpleAdapter(this, users, R.layout.item, new String[]{"name", "timelength"},new int[]{R.id.title, R.id.timelength});
                             listView.setAdapter(adapter);*/
                        }else if(entity == null){
                        //TODO
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("tag", e.toString());
                Toast.makeText(MainActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }


            // login(loginuser);

            if (loginresult == true) {
                //查询所有group
                allgrouplist = findAllGroups();
                //查找该用户join的所有group
                userjoingrouplist = findAllUserJoinGroups(loginuser);
                //跳转到所有group页面
                //TODO 跳转,传递 allgrouplist, userjoingrouplist, loginuser 三个对象
                intent_ivtime = new Intent();
                intent_ivtime.setClass(MainActivity.this, AllGroupsActivity.class);
                //赋值(对象传递)
                intent_ivtime.putExtra("allgroups", allgrouplist);
                intent_ivtime.putExtra("alljoingroups", userjoingrouplist);
                intent_ivtime.putExtra("loginuser", loginuser);
                MainActivity.this.startActivity(intent_ivtime);
            } else {
                Toast.makeText(MainActivity.this, "Wrong username or password, plase try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //查询所有group，以及当前登录用户的join groups
    public ArrayList<Group> findAllGroups() {
        allgrouplist = new ArrayList<Group>();
        //TODO server通信，查询所有groups

        return allgrouplist;
    }

    public ArrayList<UserJoin> findAllUserJoinGroups(User user) {
        userjoingrouplist = new ArrayList<UserJoin>();
        //TODO server通信，查询所有当前登录用户join的groups

        return userjoingrouplist;
    }
}
