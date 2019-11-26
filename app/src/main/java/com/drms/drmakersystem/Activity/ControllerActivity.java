package com.drms.drmakersystem.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drms.drmakersystem.R;

import com.drms.drmakersystem.Communication.BluetoothHandler;
import com.drms.drmakersystem.Communication.BluetoothService;
import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.etc.Icon;

import java.util.Locale;

/**
 * Created by jjun on 2018. 2. 23..
 */

public class ControllerActivity extends AppCompatActivity {

    protected static final String TAG = "ControllerActivity";

    protected static final int REQUEST_CONNECTION_ACTIVITY = 100;

    protected boolean isArmed = false;
    protected boolean running = true;
    protected boolean requestonPause = false;

    protected ImageView character_img, power_img, upload_img, bluetooth_img, battery_img;
    protected LinearLayout layout;
    protected ViewGroup controller_layout;
    protected TextView topic;

    protected Icon character, power, upload, bluetooth,battery;
    protected float x,y;

    protected BluetoothService mBluetoothService;
    protected BluetoothHandler btHandler;

    protected TextToSpeech tts;

    private View connection;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        x = getWindowManager().getDefaultDisplay().getWidth() / 80;
        y = getWindowManager().getDefaultDisplay().getHeight() / 45;
        Log.d(TAG,"x : " + x + "\ny : " + y);
        initializeView();
        loadBitmap(x,y);

        if(btHandler == null){
            mBluetoothService = new BluetoothService(this,btHandler,"DRS");
            btHandler = new BluetoothHandler(getApplicationContext(), this, mBluetoothService,bluetooth_img,bluetooth,battery_img);
            mBluetoothService.setmHandler(btHandler);

            Intent intent = new Intent();
            intent.putExtra(BluetoothService.EXTRA_DEVICE_ADDRESS,new FileManagement(ControllerActivity.this).readBTAddress()[1]);
            mBluetoothService.getDeviceInfo(intent);
        }

        connection = View.inflate(ControllerActivity.this,R.layout.connectionlayout,null);
        controller_layout = (LinearLayout)findViewById(R.id.controller_layout);
        controller_layout.addView(connection,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));



        new Thread(new Runnable() {
            @Override
            public void run() {
                while(running){
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e){

                    }
                    if(mBluetoothService != null) {
                        if (mBluetoothService.getState() == BluetoothService.STATE_CONNECTED) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    implementationControlView();
                                    tts.speak("블루투스가 연결되었습니다.", TextToSpeech.QUEUE_FLUSH,null);
                                }
                            });
                            break;
                        }
                    }
                }
            }
        }).start();

    }

    protected void initializeView(){
        layout = (LinearLayout)findViewById(R.id.controller_layout);
        character_img = (ImageView) findViewById(R.id.character);
        power_img = (ImageView) findViewById(R.id.power);
        upload_img = (ImageView) findViewById(R.id.upload);
        bluetooth_img = (ImageView) findViewById(R.id.bluetooth);
        battery_img = (ImageView) findViewById(R.id.battery);
        topic = (TextView)findViewById(R.id.topic);
    }

    protected void implementationControlView(){
        controller_layout.removeAllViews();
    }

    protected void loadBitmap(float x, float y) {

        power = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image3), (int) (25 * x), (int) (25 * x), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image4), (int) (25 * x), (int) (25 * x), false));

        upload = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image7), (int) (25 * x), (int) (25 * x), false));

        bluetooth = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image5), (int) (10 * x), (int) (10 * x), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image6), (int) (10 * x), (int) (10 * x), false));

        character = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image8), (int) (35 * x), (int) (35 * x), false));

        battery = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image11), (int) (6 * x), (int) (10 * y), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image10), (int) (6 * x), (int) (10 * y), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image9), (int) (6 * x), (int) (10 * y), false));



        power_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
        power_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isArmed){
                    setArmed(true);
                }else{
                    setArmed(false);
                }
            }
        });
//        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));
        bluetooth_img.setImageDrawable(new BitmapDrawable(bluetooth.getImg1()));
        upload_img.setImageDrawable(new BitmapDrawable(upload.getImg1()));
        battery_img.setImageDrawable(new BitmapDrawable(battery.getImg1()));

    }

    protected void endOfController(){

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(btHandler != null){
            if(mBluetoothService == null) {
//                controller_layout.addView(View.inflate(ControllerActivity.this,R.layout.connectionlayout,null));
                Log.d(TAG,"onResume");
                mBluetoothService = new BluetoothService(this, btHandler, "DRS");
                String address = new FileManagement(ControllerActivity.this).readBTAddress()[1];
                Intent intent = new Intent();
                intent.putExtra(BluetoothService.EXTRA_DEVICE_ADDRESS,address);
                mBluetoothService.getDeviceInfo(intent);
            }
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    tts.setLanguage(Locale.KOREA);
                }
            });

        }
        else{
            mBluetoothService = new BluetoothService(this, null, "DRS");
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    tts.setLanguage(Locale.KOREA);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


        try{
            Thread.sleep(300);
        }catch (InterruptedException e){

        }
//        controller_layout.removeAllViews();
        requestonPause = true;
        running = false;
        try{
            Thread.sleep(500);
        }catch (InterruptedException e){

        }

        try {
            endOfController();
        }catch (NullPointerException e){

        }
//        finish();

        mBluetoothService.stop();
        mBluetoothService = null;
        tts.stop();
        tts.shutdown();
    }

    protected void setArmed(boolean armed){
        isArmed = armed;
        if(armed){
            power_img.setImageDrawable(new BitmapDrawable(power.getImg2()));
        }
        if(!armed){
            power_img.setImageDrawable(new BitmapDrawable(power.getImg1()));

        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            btHandler.setContinuetryConnect(false);
            running = false;
            try{
                Thread.sleep(500);
            }catch (InterruptedException e){

            }

            try {
                endOfController();
            }catch (NullPointerException e){

            }
            finish();
        }
        return true;
    }
}
