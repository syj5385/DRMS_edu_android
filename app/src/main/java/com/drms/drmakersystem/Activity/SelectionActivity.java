package com.drms.drmakersystem.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.drms.drmakersystem.Communication.UsbSerial.UsbService;
import com.drms.drmakersystem.R;
import com.drms.drmakersystem.SettingEduAdapter;

import java.util.Set;

/**
 * Created by comm on 2018-02-20.
 */

public class SelectionActivity extends AppCompatActivity {

    private static final String TAG = "SelectionActivity";

    private int currentLevel = -1;

    //View
    private ListView edulist;
    private ImageView topic;
    private LinearLayout back, topic_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Intent recvIntent = getIntent();
        currentLevel = recvIntent.getIntExtra(EducationAcitivty.LEVEL,-1);
        edulist = (ListView)findViewById(R.id.content);
        topic = (ImageView)findViewById(R.id.topic);
        back = (LinearLayout)findViewById(R.id.back);
        topic_layout = findViewById(R.id.topic_layout);
        if(currentLevel == EducationAcitivty.LEVEL1){
            Log.d(TAG, "Selected Level : " + currentLevel);
            topic.setImageDrawable(getResources().getDrawable(R.drawable.select_image0));
            new SettingEduAdapter(SelectionActivity.this, SelectionActivity.this,back,topic_layout, topic, edulist,  currentLevel,null);
        }
        if(currentLevel == EducationAcitivty.LEVEL2){
            Log.d(TAG, "Selected Level : " + currentLevel);
            topic.setImageDrawable(getResources().getDrawable(R.drawable.select_image3));
            new SettingEduAdapter(SelectionActivity.this, SelectionActivity.this,back,topic_layout, topic, edulist,  currentLevel,null);
        }
        if(currentLevel == EducationAcitivty.LEVEL3){
            Log.d(TAG, "Selected Level : " + currentLevel);
            topic.setImageDrawable(getResources().getDrawable(R.drawable.select_image6));
            new SettingEduAdapter(SelectionActivity.this, SelectionActivity.this,back,topic_layout, topic, edulist,  currentLevel,null);
        }
        if(currentLevel == EducationAcitivty.LEVEL4){

        }



    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){

            finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold,R.anim.appear);
    }




    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
