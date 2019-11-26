package com.drms.drmakersystem.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.drms.drmakersystem.R;
import com.drms.drmakersystem.View.SplashView;

/**
 * Created by comm on 2018-02-20.
 */

public class SplashActivity extends AppCompatActivity {

    private static final String TAG ="SplashActivity";

    private static final int REQUEST_PERMISSION = 0;
    private static final int PERMISSION_RESULT_OK = 1;

    private SplashView view;
    private boolean running = false;
    private long count = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new SplashView(getApplicationContext(),SplashActivity.this);
        setContentView(view);

        float[] scale = {
                getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight()
        };
        view.getViewHandler().obtainMessage(SplashView.SET_SCREEN_SIZE,scale).sendToTarget();

        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(running){
                    if(count++ == 50){
                        startActivityRunnable.run();
                        running = false;
                    }
                    try{
                        Thread.sleep(20);
                    }catch (InterruptedException e){
                        Log.e(TAG, "InterruptedException during Thread");
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        finish();
    }

    private Runnable startActivityRunnable = new Runnable() {
        @Override
        public void run() {
            Intent splash_intent;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                int permissionResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                int permissionResult2 = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                int permissionresult4 = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int permissionresult5 = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

                if(permissionResult == PackageManager.PERMISSION_DENIED
                        || permissionResult2 == PackageManager.PERMISSION_DENIED
                        || permissionresult4 == PackageManager.PERMISSION_DENIED
                        || permissionresult5 == PackageManager.PERMISSION_DENIED){
                    splash_intent = new Intent(SplashActivity.this,CheckPermissionActivity.class);
                    startActivityForResult(splash_intent,REQUEST_PERMISSION);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                    finish();
                }
                else{
                    Log.d("HANDLER","OK");
                    splash_intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(splash_intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                    finish();
                }

            }
            else{
                splash_intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(splash_intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                finish();
            }
        }
    };
}
