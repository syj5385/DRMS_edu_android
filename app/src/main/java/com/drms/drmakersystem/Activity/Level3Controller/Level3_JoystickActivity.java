package com.drms.drmakersystem.Activity.Level3Controller;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.LinkAddress;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.Activity.Level2Controller.Cont2_6Activity;
import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.Communication.Bluetooth.BluetoothHandler;
import com.drms.drmakersystem.Communication.Bluetooth.BluetoothService;
import com.drms.drmakersystem.Communication.Protocol.MSP;
import com.drms.drmakersystem.Communication.Protocol.STK500v1.UploadManager;
import com.drms.drmakersystem.Communication.UsbSerial.UsbService;
import com.drms.drmakersystem.DrsController.DrsControllerManager;
import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.Joystick.Joystick;
import com.drms.drmakersystem.R;
import com.drms.drmakersystem.etc.Icon;

import java.util.logging.Level;

public class Level3_JoystickActivity extends AppCompatActivity {

    private static final String TAG = "Level3_Activity";

    protected DRMS drms;
    private boolean running = false;
    protected BluetoothService mBluetoothService;
    private ImageView bluetooth_img,battery_img, power_img, d2_img,d3_img,d4_img,d5_img,d6_img;
    protected ImageView upload_img;
    private LinearLayout left_t, right_t;
    private Joystick left_throttle_content, right_throttle_content;
    private TextView r_tream, p_tream, y_tream, left_r, right_r,thro_speed;
    protected Icon character, power, upload, bluetooth,battery, btn;
    private DrsControllerManager controllerManager;

    private Bitmap throttle_bitmap;
    protected float x,y;

    protected boolean[] btn_status = {false, false, false, false, false, false, false};
    protected int[] tream = {0, 0, 0};
    protected int[] analog = {1500, 1500, 1500, 1500, 1000, 1000};

    protected UsbService usbService;
    public static final int MESSAGE_FROM_USBSERVERICE = 11;
    private InputThread thread;

    private TextToSpeech tts;

    private Handler btHandler;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int index = 0;
            int angle = 0;
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
                                switch(drms.getDRONE_SPEED()){
                                    case DRMS.veryslow:
                                        thro_speed.setText("1");
                                        break;
                                    case DRMS.slow:
                                        thro_speed.setText("2");
                                        break;
                                    case DRMS.middle:
                                        thro_speed.setText("3");
                                        break;
                                    case DRMS.fast:
                                        thro_speed.setText("4");
                                        break;
                                    case DRMS.veryfast:
                                        thro_speed.setText("5");
                                        break;
                                }

                                int thro_max = (500 * drms.getDRONE_SPEED() / 500) + 1500;
                                int thro_min = ((-500) * drms.getDRONE_SPEED() / 500) + 1500;

                                if(left_throttle_content != null)
                                    left_throttle_content.setMinMax(thro_min, thro_max);
                                if(right_throttle_content != null)
                                    right_throttle_content.setMinMax(thro_min, thro_max);
                                break;

                            case DrsControllerManager.D2:
                                if (btn_status[msg.arg1] == false)
                                    d2_img.setImageDrawable(new BitmapDrawable(btn.getImg2()));
                                else if (btn_status[msg.arg1] == true)
                                    d2_img.setImageDrawable(new BitmapDrawable(btn.getImg1()));
                                break;

                            case DrsControllerManager.D3:
                                if (btn_status[msg.arg1] == false)
                                    d3_img.setImageDrawable(new BitmapDrawable(btn.getImg2()));
                                else if (btn_status[msg.arg1] == true)
                                    d3_img.setImageDrawable(new BitmapDrawable(btn.getImg1()));
                                break;

                            case DrsControllerManager.D4:
                                if (btn_status[msg.arg1] == false)
                                    d4_img.setImageDrawable(new BitmapDrawable(btn.getImg2()));
                                else if (btn_status[msg.arg1] == true)
                                    d4_img.setImageDrawable(new BitmapDrawable(btn.getImg1()));
                                break;

                            case DrsControllerManager.D5:
                                if (btn_status[msg.arg1] == false)
                                    d5_img.setImageDrawable(new BitmapDrawable(btn.getImg2()));
                                else if (btn_status[msg.arg1] == true)
                                    d5_img.setImageDrawable(new BitmapDrawable(btn.getImg1()));
                                break;

                            case DrsControllerManager.D6:
                                if (btn_status[msg.arg1] == false)
                                    d6_img.setImageDrawable(new BitmapDrawable(btn.getImg2()));
                                else if (btn_status[msg.arg1] == true)
                                    d6_img.setImageDrawable(new BitmapDrawable(btn.getImg1()));
                                break;

                            case DrsControllerManager.ANALOG:
                                /*angle = (analog[0] - 1500) * 60 / 500;
                                right_t.setRotationY((float)angle);
                                angle = (analog[1] - 1500) * 60 / 500;
                                right_t.setRotationX((float)angle);
                                angle = (analog[2] - 1500) * 60 / 500;
                                left_t.setRotationY((float)angle);
                                angle = (analog[3] - 1500) * 60 / 500;
                                left_t.setRotationX((float)angle);

                                */
                                left_throttle_content.setX(analog[2]);
                                left_throttle_content.setY_MaxThrottle(analog[3]);
                                right_throttle_content.setX(analog[0]);
                                right_throttle_content.setY(analog[1]);
                                left_r.setText(String.valueOf(analog[4]));
                                right_r.setText(String.valueOf(analog[5]));

                                //Log.d(TAG, "value : " + analog[0] + "\t" + analog[1] + " \t" + analog[2] + "\t" + analog[3]);

                                return ;
                        }

                        btn_status[msg.arg1] = !btn_status[msg.arg1];
                    }
                    else{
                        /* Tream set */
                        index = msg.arg1 - 8;
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
        setContentView(R.layout.activity_joystick_level3);
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
            intent.putExtra(BluetoothService.EXTRA_DEVICE_ADDRESS,new FileManagement(Level3_JoystickActivity.this).readBTAddress()[1]);

            mBluetoothService.getDeviceInfo(intent);
        }


    }

    protected void loadBitmap(float x, float y) {

        power = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image3), (int) (15 * x), (int) (15 * x), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image4), (int) (15 * x), (int) (15 * x), false));

        btn = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image12), (int) (15 * x), (int) (15 * x), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image13), (int) (15 * x), (int) (15 * x), false));

        upload = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image7), (int) (15 * x), (int) (15 * x), false));

        bluetooth = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image5), (int) (12 * x), (int) (12 * x), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image6), (int) (12 * x), (int) (12 * x), false));

        character = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image8), (int) (35 * x), (int) (35 * x), false));

        battery = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image11), (int) (12 * x), (int) (12 * x), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image10), (int) (12 * x), (int) (12 * x), false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.controller_image9), (int) (12 * x), (int) (12 * x), false));



        left_t.setMinimumWidth((int)(20*x));
        left_t.setMinimumHeight((int)(15*y));

        right_t.setMinimumWidth((int)(20*x));
        right_t.setMinimumHeight((int)(15*y));

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
       // d1_img.setImageDrawable(new BitmapDrawable(btn.getImg1()));
        d2_img.setImageDrawable(new BitmapDrawable(btn.getImg1()));
        d3_img.setImageDrawable(new BitmapDrawable(btn.getImg1()));
        d4_img.setImageDrawable(new BitmapDrawable(btn.getImg1()));
        d5_img.setImageDrawable(new BitmapDrawable(btn.getImg1()));
        d6_img.setImageDrawable(new BitmapDrawable(btn.getImg1()));

    }

    protected void initializeView(){
        bluetooth_img = (ImageView) findViewById(R.id.bluetooth);
        battery_img = (ImageView) findViewById(R.id.battery);
        upload_img= findViewById(R.id.upload);
        power_img = findViewById(R.id.power);
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

        thro_speed = findViewById(R.id.thro_speed);
        switch(drms.getDRONE_SPEED()){
            case DRMS.veryslow:
                thro_speed.setText("1");
                break;
            case DRMS.slow:
                thro_speed.setText("2");
                break;
            case DRMS.middle:
                thro_speed.setText("3");
                break;
            case DRMS.fast:
                thro_speed.setText("4");
                break;
            case DRMS.veryfast:
                thro_speed.setText("5");
                break;
        }

        left_t = findViewById(R.id.left_throttle);
        right_t = findViewById(R.id.right_throttle);
        throttle_bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.cont2_2_image7);
        int thro_max = (500 * drms.getDRONE_SPEED() / 500) + 1500;
        int thro_min = ((-500) * drms.getDRONE_SPEED() / 500) + 1500;
        left_throttle_content = new Joystick(this, this, thro_min,thro_max, throttle_bitmap);
        ((Joystick) left_throttle_content).setJoysticMode(Joystick.MODE7);
        right_throttle_content = new Joystick(this, this, thro_min,thro_max, throttle_bitmap);
        ((Joystick) right_throttle_content).setJoysticMode(Joystick.MODE7);
        left_throttle_content.setLayoutParams(new LinearLayout.LayoutParams((int)(20*x), (int)(15*y)));
        right_throttle_content.setLayoutParams(new LinearLayout.LayoutParams((int)(20*x), (int)(15*y)));
        left_t.addView(left_throttle_content);
        right_t.addView(right_throttle_content);
        left_r = findViewById(R.id.left_resistor);
        right_r = findViewById(R.id.right_resistor);

    }

    private class InputThread extends Thread{
        private boolean running ;
        public InputThread() {
            super();
            running = true;
        }

        @Override
        public void run() {
            super.run();
            while(running){
                int[] rc = drms.getRcdata();
                analog[0] = rc[0];
                analog[1] = rc[1];
                analog[2] = rc[2];
                analog[3] = rc[3];
                int[] resistor = drms.getResistor();
                analog[4] = resistor[0];
                analog[5] = resistor[1];
                mHandler.obtainMessage(DrsControllerManager.CONT_DIGIAL, DrsControllerManager.ANALOG,-1).sendToTarget();

                try{
                    Thread.sleep(50);
                }catch (InterruptedException e){

                }

            }
        }

        public void setRunning(boolean running){
            this.running = running;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        thread.setRunning(false);
        try{
            Thread.sleep(50);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        thread = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(btHandler != null){
            if(mBluetoothService == null) {
//                controller_layout.addView(View.inflate(ControllerActivity.this,R.layout.connectionlayout,null));
                Log.d(TAG,"onResume");
                mBluetoothService = new BluetoothService(this, btHandler, "DRS");
                String address = new FileManagement(Level3_JoystickActivity.this).readBTAddress()[1];
                Intent intent = new Intent();
                intent.putExtra(BluetoothService.EXTRA_DEVICE_ADDRESS,address);
                mBluetoothService.getDeviceInfo(intent);
            }


        }
        else{
            mBluetoothService = new BluetoothService(this, null, "DRS");

        }
        setFilter();
        thread = new InputThread();
        thread.start();

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
        else{
            MSP msp = new MSP(mBluetoothService,new Handler());
            for(int i=0; i<5; i++){
                msp.sendRequestMSP_SET_RAW_RC(new int[]{1500,1500,1500,1000,1000,1000,1000,1000});
                try{
                    Thread.sleep(10);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

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
            Toast.makeText(Level3_JoystickActivity.this,"Controller Service Connected",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Level3_JoystickActivity.this,"조종기가 분리되어 종료합니다.",Toast.LENGTH_SHORT).show();
                finish();
                drms.initializeMultiData();
            }
            if(action.equals(UsbService.REQUEST_UPDATE_FIRMWARE)){
                openFirmwareRequestDialog();
            }
        }
    };

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_DETACHED);
        filter.addAction(UsbService.REQUEST_UPDATE_FIRMWARE);
        registerReceiver(usbReceiver, filter);
    }

    private void openFirmwareRequestDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog;

        builder.setTitle("Not Found firmware in Controller");
        builder.setMessage("조종기 펌웨어를 찾을 수 없습니다. 조종기 펌웨어를 수행하시겠습니까?");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(usbService != null){
                    usbService.writeControllerFirmware(Level3_JoystickActivity.this);
                }
            }
        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Level3_JoystickActivity.this,"조종기를 종료합니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        dialog =builder.create();

        dialog.show();

    }



}
