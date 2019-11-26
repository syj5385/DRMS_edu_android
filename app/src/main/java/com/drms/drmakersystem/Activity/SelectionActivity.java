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
        }
        if(currentLevel == EducationAcitivty.LEVEL4){

        }



    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(currentLevel >= EducationAcitivty.LEVEL3) {
//            unbindService(usbConnection);
//            usbService.onDestroy();
//            unregisterReceiver(usbReceiver);
                try {
                    usbService.setConnected(false);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
            finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold,R.anim.appear);
    }


    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {

        Intent startService = new Intent(this, service);
        if (extras != null && !extras.isEmpty()) {
            Set<String> keys = extras.keySet();
            for (String key : keys) {
                String extra = extras.getString(key);
                startService.putExtra(key, extra);
            }
        }

        Intent bindingIntent = new Intent(this,service);
        bindService(bindingIntent,serviceConnection, Context.BIND_AUTO_CREATE);
    }


    private UsbService usbService;
    public static final int MESSAGE_FROM_USBSERVERICE = 11;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
//               if(!usbService.isAttached()){
//                   Log.d(TAG,"USB Device is not connected");
//                   finish();
//                   Toast.makeText(getApplicationContext(),"컨트롤러가 연결되어 있지 않습니다",Toast.LENGTH_SHORT).show();
//               }
            Log.d(TAG,"Usb Service : " + usbService);
            new SettingEduAdapter(SelectionActivity.this, SelectionActivity.this,back,topic_layout, topic, edulist,  currentLevel,usbService);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
                usbService = null;
            }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case MESSAGE_FROM_USBSERVERICE :
                    switch (msg.arg1) {
                        case UsbService.MESSAGE_FROM_SERIAL_PORT:
                            String data = (String) msg.obj;
                            Log.d("USBActivity", data);
                            break;

                        case UsbService.CTS_CHANGE:
//                            Toast.makeText(DroneMainActivity.this, "CTS_CHANGE", Toast.LENGTH_LONG).show();
                            break;
                        case UsbService.DSR_CHANGE:
//                           Toast.makeText(DroneMainActivity.this, "DSR_CHANGE", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
            }

        }
    };

    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(UsbService.UNATTACHED_CONTROLLER)){
                Log.d(TAG,"컨트롤러가 연결되어 있지 않습니다.");
                Toast.makeText(getApplicationContext(),"컨트롤러가 연결되어 있지 않습니다.",Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    };

    private void setFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.UNATTACHED_CONTROLLER);

        registerReceiver(usbReceiver,filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentLevel >= EducationAcitivty.LEVEL3) {
            setFilter();
            startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currentLevel >= EducationAcitivty.LEVEL3) {
//            setFilter();
//            Intent intent = new Intent(this, UsbService.class);
//            bindService(intent, usbConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
//        Toast.makeText(this,"onStop",Toast.LENGTH_SHORT).show();
        if(currentLevel >= EducationAcitivty.LEVEL3) {
//            usbService.disconnect();
//            while(usbService.isConnected()){
//                try{
//                    Thread.sleep(1);
//                }catch (InterruptedException e){
//
//                }
//            }
            unregisterReceiver(usbReceiver);
//            unbindService(usbConnection);
        }
//        service
    }
}
