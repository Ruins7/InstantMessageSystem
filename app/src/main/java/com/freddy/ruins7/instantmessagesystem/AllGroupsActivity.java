package com.freddy.ruins7.instantmessagesystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.freddy.ruins7.instantmessagesystem.entity.Group;
import com.freddy.ruins7.instantmessagesystem.entity.User;

import java.util.ArrayList;

/**
 * Created by ruins7 on 2016-10-10.
 */

public class AllGroupsActivity extends AppCompatActivity{

    private static ArrayList<Group> allgroups;
    private static ArrayList<Group> alljoingroups;
    private static User loginuser;

    private static Button logoff;
    private static TextView status;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allgroups_activity);

        //TODO 连接server，并显示连接状态(需要计时器)

        //接收登录界面传来的参数
        loginuser = (User)getIntent().getSerializableExtra("loginuser");
        allgroups = (ArrayList<Group>) getIntent().getSerializableExtra("alljoingroups");
        alljoingroups = (ArrayList<Group>) getIntent().getSerializableExtra("alljoingroups");

        Log.i("log",loginuser.getUid()+"----"+loginuser.getUsername());
        Log.i("log", String.valueOf(allgroups.size()));
        Log.i("log", String.valueOf(alljoingroups.size()));

        //页面的控件
        status = (TextView) findViewById(R.id.status);
        logoff = (Button) findViewById(R.id.logff);

        Logoff logofffunction = new Logoff();

        //能登录成功肯定是连接成功，所以是up
        status.setText("Up");
        logoff.setOnClickListener(logofffunction);



    }

    //logoff
    class Logoff implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent_ivtime = new Intent();
            intent_ivtime.setClass(AllGroupsActivity.this, MainActivity.class);
            AllGroupsActivity.this.startActivity(intent_ivtime);
        }
    }

    //join group
    class JoinGroup implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //TODO 获得group的id,userID，写入server db,然后在该group的status上显示已加入,然后跳转
        }
    }
}
