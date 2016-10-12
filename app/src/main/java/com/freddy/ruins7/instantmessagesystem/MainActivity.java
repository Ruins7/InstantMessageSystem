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

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private static EditText username;
    private static EditText password;
    private static Button login;
    private static ArrayList<Group> allgrouplist;
    private static ArrayList<UserJoin> userjoingrouplist;
    private static User loginuser;
    private static Intent intent_ivtime;

    private static HttpClient client；
    private static HttpPost request；
    private static HttpEntity entity；



    private static String url = "https://127.0.0.1:8080/InatantMessageServer/user/login.action";//ip address may change

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
            try(response.getStatusLine().getStatusCode() == 200){//请求成功
                entity = response.getEntity();
                if (entity != null) {
                    String out = EntityUtils.toString(entity, "UTF-8");
                    Log.i(TAG, out);

                    JSONArray jsonArray = new JSONArray(out);

                    videos = new ArrayList<HashMap<String, Object>>();
                    for(int i = 0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("title");
                        int timelength = jsonObject.getInt("timelength");

                        video = new HashMap<String, Object>();
                        video.put("id", id);
                        video.put("name", name);
                        video.put("timelength", "时长为:" + timelength);

                        videos.add(video);
                    }
                    SimpleAdapter adapter = new SimpleAdapter(this, videos, R.layout.item,
                            new String[]{"name", "timelength"},
                            new int[]{R.id.title, R.id.timelength}
                    );
                    listView.setAdapter(adapter);

                }
        } catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            Toast.makeText(MainActivity.this, "获取数据失败", Toast.LENGTH_LONG).show();
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
