package com.drms.drmakersystem.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.FileManagement.NoticeManager;
import com.drms.drmakersystem.R;

import java.util.ArrayList;

/**
 * Created by comm on 2018-02-25.
 */

public class NoticeActivity extends AppCompatActivity {
    private ArrayList<String> topicList = new ArrayList<>();
    private ArrayList<String> contentList = new ArrayList<>();
    private LinearLayout notice_layout;

    private TextView cancel ;
    private boolean activation;
    private DRMS drms ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        drms = (DRMS)this.getApplication();
        topicList = getIntent().getStringArrayListExtra(NoticeManager.CONTENT_TOPIC);
        contentList = getIntent().getStringArrayListExtra(NoticeManager.CONTENT);
        activation = getIntent().getBooleanExtra(NoticeManager.APP_ACTIVATION,false);

        notice_layout = (LinearLayout)findViewById(R.id.notice_content);
        Log.d("Notice", " size : " + topicList.size());

        ArrayList<View> notice_view = new ArrayList<View>();
        for(int i=0; i<topicList.size(); i++){
            View temp = View.inflate(NoticeActivity.this,R.layout.notice_item, null);
            TextView topic_text = (TextView)temp.findViewById(R.id.topic);
            final TextView content_text = (TextView)temp.findViewById(R.id.content);
            topic_text.setText(topicList.get(i));
            content_text.setText(contentList.get( i));

            notice_view.add(temp);
            if(i == topicList.size()-1){
                content_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cafe.naver.com/drmakersystem/42"));
//                        activity.startActivityForResult(webIntent, REQUEST_CAFE);
                        startActivity(webIntent);
                        overridePendingTransition(R.anim.fade, R.anim.hold);
                    }
                });
            }
        }

        Log.d("Notice","size notice " + notice_view.size());

        for(int i=0;i<notice_view.size(); i++){
            notice_layout.addView(notice_view.get(i),new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }





    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold,R.anim.appear);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            setResult(MainActivity.RESULT_EXIT_NOTIFICATION);
            finish();
//            if(activation == false) {
//                setResult(MainActivity.RESULT_ACTIVATION_FALSE);
//            }
//            else{
//                setResult(MainActivity.RESULT_EXIT_NOTIFICATION);
//            }
//            finish();
        }
        else{

        }
        return true;
    }
}
