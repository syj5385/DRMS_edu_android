package com.drms.drmakersystem.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.drms.drmakersystem.R;
import com.drms.drmakersystem.View.ExecuteView;
import com.drms.drmakersystem.View.SplashView;


/**
 * Created by comm on 2018-02-20.
 */

public class ExecuteActivity extends AppCompatActivity {

    public static final int REQUEST_CONTROLLER = 10;

    private ExecuteView view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent recvIntent = getIntent();
        int level = recvIntent.getIntExtra(EducationAcitivty.LEVEL,-1);
        int week = recvIntent.getIntExtra(EducationAcitivty.WEEK,-1);

        view = new ExecuteView(ExecuteActivity.this,ExecuteActivity.this,level,week);
        setContentView(view);
        float[] scale = {
                getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight()
        };

        view.getViewHandler().obtainMessage(SplashView.SET_SCREEN_SIZE,scale).sendToTarget();
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
}
