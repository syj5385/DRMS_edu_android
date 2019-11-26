package com.drms.drmakersystem.Activity.Level3Controller;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.Communication.Bluetooth.BluetoothHandler;
import com.drms.drmakersystem.Communication.Bluetooth.BluetoothService;
import com.drms.drmakersystem.Communication.UsbSerial.UsbService;
import com.drms.drmakersystem.DrsController.DrsControllerManager;
import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.R;
import com.drms.drmakersystem.etc.Icon;

public class Level3_Activity extends AppCompatActivity {

    private static final String TAG = "Level3_Activity";

    private DRMS drms;
    private UsbService service;
    private boolean running = false;
    private BluetoothService mBluetoothService;
    private ImageView bluetooth_img,battery_img,upload_img, power_img, d1_img,d2_img,d3_img,d4_img,d5_img,d6_img;
    private TextView r_tream, p_tream, y_tream;
    protected Icon character, power, upload, bluetooth,battery;
    private DrsControllerManager controllerManager;
    protected float x,y;

    private boolean[] btn_status = {false, false, false, false, false, false, false};
    private int[] tream = {0, 0, 0};

    private UsbService usbService;
    public static final int MESSAGE_FROM_USBSERVERICE = 11;

    private TextToSpeech tts;

    private Handler btHandler;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int index = 0;
            switch(msg.what){
                case DrsControllerManager.CONT_DIGIAL:
                    if(msg.arg1 < DrsControllerManager.R_T) {
                        switch (msg.arg1) {
                            case DrsControllerManager.POWER:
                                if (btn_status[msg.arg1] == false)
                                    power_img.setImageDrawable(new BitmapDrawable(power.getImg2()));
                                else if (btn_status[msg.arg1] == true)
                                    power_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
                                break;

                            case DrsControllerManager.D1:
                                if (btn_status[msg.arg1] == false)
                                    d1_img.setImageDrawable(new BitmapDrawable(power.getImg2()));
                                else if (btn_status[msg.arg1] == true)
                                    d1_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
                                break;

                            case DrsControllerManager.D2:
                                if (btn_status[msg.arg1] == false)
                                    d2_img.setImageDrawable(new BitmapDrawable(power.getImg2()));
                                else if (btn_status[msg.arg1] == true)
                                    d2_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
                                break;

                            case DrsControllerManager.D3:
                                if (btn_status[msg.arg1] == false)
                                    d3_img.setImageDrawable(new BitmapDrawable(power.getImg2()));
                                else if (btn_status[msg.arg1] == true)
                                    d3_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
                                break;

                            case DrsControllerManager.D4:
                                if (btn_status[msg.arg1] == false)
                                    d4_img.setImageDrawable(new BitmapDrawable(power.getImg2()));
                                else if (btn_status[msg.arg1] == true)
                                    d4_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
                                break;

                            case DrsControllerManager.D5:
                                if (btn_status[msg.arg1] == false)
                                    d5_img.setImageDrawable(new BitmapDrawable(power.getImg2()));
                                else if (btn_status[msg.arg1] == true)
                                    d5_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
                                break;

                            case DrsControllerManager.D6:
                                if (btn_status[msg.arg1] == false)
                                    d6_img.setImageDrawable(new BitmapDrawable(power.getImg2()));
                                else if (btn_status[msg.arg1] == true)
                                    d6_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
                                break;
                        }

                        btn_status[msg.arg1] = !btn_status[msg.arg1];
                    }
                    else{
                        /* Tream set */
                        index = msg.arg1 - 7;
                        tream[index] += msg.arg2;
                        switch(msg.arg1){
                            case DrsControllerManager.R_T :
                                y_tream.setText(String.valueOf(tream[0]));
                                break;

                            case DrsControllerManager.P_T :
                                p_tream.setText(String.valueOf(tream[index]));
                                break;

                            case DrsControllerManager.Y_T :
                                r_tream.setText(String.valueOf(tream[2]));
                                break;
                        }
                    }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level3);
        x = getWindowManager().getDefaultDisplay().getWidth() / 80;
        y = getWindowManager().getDefaultDisplay().getHeight() / 45;



        drms = (DRMS)this.getApplication();


        initializeView();
        loadBitmap(x,y);


        if(btHandler == null){
            mBluetoothService = new BluetoothService(this,btHandler,"DRS");
            btHandler = new BluetoothHandler(getApplicationContext(), this, mBluetoothService,bluetooth_img,bluetooth,battery_img);
            mBluetoothService.setmHandler(btHandler);

            Intent intent = new Intent();
            intent.putExtra(BluetoothService.EXTRA_DEVICE_ADDRESS,new FileManagement(Level3_Activity.this).readBTAddress()[1]);

            mBluetoothService.getDeviceInfo(intent);
        }


    }

    protected void loadBitmap(float x, float y) {

        power = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image3), (int) (15 * x), (int) (15 * x), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image4), (int) (15 * x), (int) (15 * x), false));

        upload = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image7), (int) (15 * x), (int) (15 * x), false));

        bluetooth = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image5), (int) (10 * x), (int) (10 * x), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image6), (int) (10 * x), (int) (10 * x), false));

        character = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image8), (int) (35 * x), (int) (35 * x), false));

        battery = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image11), (int) (6 * x), (int) (10 * y), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image10), (int) (6 * x), (int) (10 * y), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image9), (int) (6 * x), (int) (10 * y), false));



       // power_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
//        power_img.setOnClickListener(new View.OnClickListener() {
  //          @Override
    //        public void onClick(View view) {
      //          if(!isArmed){
        //            setArmed(true);
          //      }else{
            //        setArmed(false);
              //  }
           // }
        //});
//        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));
        bluetooth_img.setImageDrawable(new BitmapDrawable(bluetooth.getImg1()));
        upload_img.setImageDrawable(new BitmapDrawable(upload.getImg1()));
        battery_img.setImageDrawable(new BitmapDrawable(battery.getImg1()));
        power_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
        d1_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
        d2_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
        d3_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
        d4_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
        d5_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
        d6_img.setImageDrawable(new BitmapDrawable(power.getImg1()));

    }

    protected void initializeView(){
        bluetooth_img = (ImageView) findViewById(R.id.bluetooth);
        battery_img = (ImageView) findViewById(R.id.battery);
        upload_img= findViewById(R.id.upload);
        power_img = findViewById(R.id.power);
        d1_img = findViewById(R.id.d1);
        d2_img = findViewById(R.id.d2);
        d3_img = findViewById(R.id.d3);
        d4_img = findViewById(R.id.d4);
        d5_img = findViewById(R.id.d5);
        d6_img = findViewById(R.id.d6);
        r_tream = findViewById(R.id.roll_tream);
      //  r_tream.setText(tream[0]);
        p_tream = findViewById(R.id.pitch_tream);
       // p_tream.setText(tream[1]);
        y_tream = findViewById(R.id.yaw_tream);
       //' y_tream.setText(tream[2]);
        upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usbService != null){
                    usbService.writeControllerFirmware(Level3_Activity.this);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(btHandler != null){
            if(mBluetoothService == null) {
//                controller_layout.addView(View.inflate(ControllerActivity.this,R.layout.connectionlayout,null));
                Log.d(TAG,"onResume");
                mBluetoothService = new BluetoothService(this, btHandler, "DRS");
                String address = new FileManagement(Level3_Activity.this).readBTAddress()[1];
                Intent intent = new Intent();
                intent.putExtra(BluetoothService.EXTRA_DEVICE_ADDRESS,address);
                mBluetoothService.getDeviceInfo(intent);
            }


        }
        else{
            mBluetoothService = new BluetoothService(this, null, "DRS");

        }
        setFilter();

        Intent bindingIntent = new Intent(this,UsbService.class);
        bindService(bindingIntent,usbConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mBluetoothService.setProtocol("DRS");
        try{
            Thread.sleep(300);
        }catch (InterruptedException e){

        }
//        controller_layout.removeAllViews();
       // requestonPause = true;
        running = false;
        try{
            Thread.sleep(500);
        }catch (InterruptedException e){

        }

//        finish();
        if(!mBluetoothService.getProtocol().equals("MSP")) {
            mBluetoothService.stop();
            mBluetoothService = null;
        }

        if(usbService != null) {
            usbService.setConnected(false);
            usbService.disconnect();
            unbindService(usbConnection);
        }
        unregisterReceiver(usbReceiver);
    }



    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
            //controllerManager = new DrsControllerManager(usbService, mHandler);
            Toast.makeText(Level3_Activity.this,"Controller Service Connected",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
            Log.d(TAG,"STop Service");
        }
    };

    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(UsbService.ACTION_USB_DETACHED)){
                Toast.makeText(Level3_Activity.this,"조종기가 분리되어 종료합니다.",Toast.LENGTH_SHORT).show();
                finish();
                drms.initializeMultiData();
            }
        }
    };

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_DETACHED);
        registerReceiver(usbReceiver, filter);
    }



}
