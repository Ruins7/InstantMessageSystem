package com.freddy.ruins7.instantmessagesystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.certaingroup_activity);

        groupid = (TextView) findViewById(R.id.groupid);
        status = (TextView) findViewById(R.id.onegroupstatus);
        messContent = (TextView) findViewById(R.id.messagecontent);
        sendMess = (Button) findViewById(R.id.sendmessage);
        allMemberse = (TextView) findViewById(R.id.allmembers);
        quit = (Button) findViewById(R.id.quitgroup);
        backToAllGroups = (Button) findViewById(R.id.backtoallgroups);

        Send sendmess = new Send();
        QuitGroup quitGroup = new QuitGroup();

        sendMess.setOnClickListener(sendmess);
        quit.setOnClickListener(quitGroup);


        //TODO 连接server，并显示连接状态(需要计时器)

        //TODO 接收上一个页面传过来的参数，group的id,userID

        //TODO 查询该group的消息记录，按时间s排列，并且显示在页面上(需要计时器)

        //TODO 查询该group内的所有user，并显示在页面上


    }

    //send message
    class Send implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            messContentStr = messContent.getText().toString();
            if (!messContentStr.trim().contentEquals("")) {
                //TODO send, write DB, and show on activity
            } else {
                Toast.makeText(CertainGroupActivity.this, "Message can not be empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //quit group
    class QuitGroup implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //TODO delete 该用户的该group记录, 跳转
            Toast.makeText(CertainGroupActivity.this, "quit group successfully", Toast.LENGTH_SHORT).show();
            Intent intent_ivtime = new Intent();
            intent_ivtime.setClass(CertainGroupActivity.this, AllGroupsActivity.class);
            CertainGroupActivity.this.startActivity(intent_ivtime);

        }
    }

    //回到所有message groups页面
    class BackToAllGroups implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CertainGroupActivity.this);
            builder.setTitle("");
            builder.setMessage("Are you sure you want to leave ？");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //go back to all groups
                    Intent intent_ivtime = new Intent();
                    intent_ivtime.setClass(CertainGroupActivity.this, AllGroupsActivity.class);
                    CertainGroupActivity.this.startActivity(intent_ivtime);
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //do nothing
                }
            });

        }
    }
}
