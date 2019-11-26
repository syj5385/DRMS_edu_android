package com.drms.drmakersystem.Activity;

import android.bluetooth.BluetoothAdapter;
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
import android.widget.Toast;

import com.drms.drmakersystem.Communication.UsbSerial.UsbService;
import com.drms.drmakersystem.View.EducationVIew;
import com.drms.drmakersystem.View.SplashView;
import com.drms.drmakersystem.R;

import java.util.Set;


/**
 * Created by comm on 2018-02-20.
 */

public class EducationAcitivty extends AppCompatActivity {

    public static final String LEVEL = "level";
    public static final String WEEK = "week";


    public static final int LEVEL1 = 10;
    public static final int LEVEL2 = 11;
    public static final int LEVEL3 = 12;
    public static final int LEVEL4 = 13;
    public static final int CONTROLLER = 14;

    private EducationVIew view;

    public static final int REQUEST_ENABLE_BT = 10;
    public static final int MESSAGE_FROM_USBSERVERICE = 11;
    private UsbService usbService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new EducationVIew(getApplicationContext(),EducationAcitivty.this);
        setContentView(view);
        float[] scale = {
                getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight()
        };

        view.getViewHandler().obtainMessage(SplashView.SET_SCREEN_SIZE,scale).sendToTarget();

        BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
        if(!btadapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,REQUEST_ENABLE_BT);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT){
            Log.d("BTenable","result : " + resultCode);
            if(resultCode == RESULT_CANCELED){
                Toast.makeText(this,"블루투스가 실행되지 않아 더 이상 진행할 수 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }


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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
