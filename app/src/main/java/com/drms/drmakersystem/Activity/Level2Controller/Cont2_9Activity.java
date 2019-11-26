package com.drms.drmakersystem.Activity.Level2Controller;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.drms.drmakersystem.Activity.ControllerActivity;
import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.Communication.DRS_Constants;
import com.drms.drmakersystem.Communication.DRS_SerialProtocol;
import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.Joystick.Joystick;
import com.drms.drmakersystem.R;
import com.drms.drmakersystem.STK500v1.UploadManager;
import com.drms.drmakersystem.etc.Icon;

/**
 * Created by jjun on 2018. 2. 23..
 */

public class Cont2_9Activity extends ControllerActivity {

    private LinearLayout throttleLayout;
    private ImageView left, right;
    private ImageView drive, reverse;
    private SendThread thread;

    private Joystick mJoystick;

    private int car_state ;

    private static final int STOP_MODE = 0;
    private static final int DRIVE = 1;
    private static final int REVERSE = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topic.setText("2단계 9주차 스피드 자동차");
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_9_image9),(int)(35*x), (int)(35*x),false));
        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));

    }

    @Override
    protected void loadBitmap(float x, float y) {
        super.loadBitmap(x, y);

    }

    @Override
    protected void initializeView() {
        super.initializeView();

    }

    @Override
    protected void implementationControlView() {
        super.implementationControlView();

        controller_layout = (LinearLayout) View.inflate(Cont2_9Activity.this, R.layout.activity_cont_2_9,null);
        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(controller_layout);

        throttleLayout = (LinearLayout)controller_layout.findViewById(R.id.throttle);
        left = controller_layout.findViewById(R.id.left);
        right = controller_layout.findViewById(R.id.right);
        drive = controller_layout.findViewById(R.id.drive);
        reverse = controller_layout.findViewById(R.id.reverse);

        car_state = STOP_MODE;

        mJoystick = new Joystick(this,this,1000,2000, BitmapFactory.decodeResource(getResources(),R.drawable.cont2_4_image7)){

            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                Log.d(TAG,"Size changed - width : " + w + ", height = " + h);
                setBackgroundColor(Color.argb(00,00,00,00));
                if ((width == 0 || height == 0) && (w>0 && h>0)){
                    width = w;
                    height =h ;
                    float rate = (float)(width/2)/(float)joystickbmp.getWidth();
                    joystickbmp = Bitmap.createScaledBitmap(joystickbmp,(int)(joystickbmp.getWidth()*0.8),(int)(joystickbmp.getWidth()*0.8),false);
                    offsetX = joystickbmp.getWidth()/2;
                    offsetY = joystickbmp.getHeight()/2;
                    x = width/2 - offsetX;
                    y = height/2 - offsetY;
                    if(JoysticMode == MODE3 || JoysticMode== MODE4 || JoysticMode == MODE6){
                        y = height - offsetY*2;
                    }

                    double temp = (double)x / (double)(width - 2*offsetX);
                    horizon = (int)((temp * (MaxValue - MinValue) )+ MinValue);
                    temp = (double)y / (double)(height - 2*offsetY);
                    vertical = ((MaxValue - MinValue) - (int)(temp * (MaxValue - MinValue) ) ) + MinValue;
                    Log.d(TAG,"horizon : " + horizon + "\tvertical : " + vertical);
                    invalidate();
                }
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d(TAG,"Y : " + event.getY() + "\theight : " + height);
                    if(event.getY() <= height/3){
                        // Go
                        y = 0;
                        car_state = DRIVE;
                        drive.setImageDrawable(getResources().getDrawable(R.drawable.cont2_9_image3));
                        reverse.setImageDrawable(getResources().getDrawable(R.drawable.cont2_9_image4));
                    }
                    if(event.getY() >= height/3 && event.getY() <= height*2/3){
                        y = height/2 - offsetY;
                        car_state = STOP_MODE;
                        drive.setImageDrawable(getResources().getDrawable(R.drawable.cont2_9_image5));
                        reverse.setImageDrawable(getResources().getDrawable(R.drawable.cont2_9_image4));
                    }
                    if(event.getY() >= height*2/3 && event.getY() <= height){
                        // Go
                        y = height - offsetY*2;
                        car_state = REVERSE;
                        drive.setImageDrawable(getResources().getDrawable(R.drawable.cont2_9_image5));
                        reverse.setImageDrawable(getResources().getDrawable(R.drawable.cont2_9_image6));
                    }
                }
                else if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(event.getY() <= height/3){
                        // Go
                        y = 0;
                        car_state = DRIVE;
                        drive.setImageDrawable(getResources().getDrawable(R.drawable.cont2_9_image3));
                        reverse.setImageDrawable(getResources().getDrawable(R.drawable.cont2_9_image4));
                    }
                    if(event.getY() >= height/3 && event.getY() <= height*2/3){
                        y = height/2 - offsetY;
                        car_state = STOP_MODE;
                        drive.setImageDrawable(getResources().getDrawable(R.drawable.cont2_9_image5));
                        reverse.setImageDrawable(getResources().getDrawable(R.drawable.cont2_9_image4));
                    }
                    if(event.getY() >= height*2/3 && event.getY() <= height){
                        // Go
                        y = height - offsetY*2;
                        car_state = REVERSE;
                        drive.setImageDrawable(getResources().getDrawable(R.drawable.cont2_9_image5));
                        reverse.setImageDrawable(getResources().getDrawable(R.drawable.cont2_9_image6));
                    }
                }
                invalidate();
                return true;
            }
        };
        throttleLayout.addView(mJoystick);
        thread = new SendThread();
        running = true;
        thread.start();

        upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                running = false;
                // wait for stop Thread
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){};
                UploadManager manager =
                        new UploadManager(Cont2_9Activity.this, mBluetoothService, new FileManagement(Cont2_9Activity.this).readBTAddress()[1],DRMS.LEV2_9){

                            @Override
                            public void setDismissListener() {
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        if(stk500 != null){
                                            stk500.running = false;
                                            stk500 = null;
                                        }
//                mBluetoothService.stop();
                                        mBluetoothService.setmHandler(BackUpHandller);
                                        mBluetoothService.setProtocol("DRS");
                                        Log.d(TAG,"set Protocol as DRS");
                                        SendThread thread = new SendThread();
                                        running = true;
                                        thread.start();
                                    }
                                });
                            }
                        };
                manager.setDismissListener();
            }
        });
    }
    private int payload[] = {100,100,100,100,100,100}; // Arm D/R roll pitchr
    private DRS_SerialProtocol drs;
    public class SendThread extends Thread {




        public SendThread() {
            super();
            drs = new DRS_SerialProtocol(DRS_Constants.LEV2_R9, btHandler,mBluetoothService);
        }

        @Override
        public void run() {
            super.run();
            while(running){
                if (!isArmed) {
                    payload[0] = 100;
                    payload[1] = 150;
                    payload[2] = 1000 & 0xff;
                    payload[3] = (1000 >> 8) & 0xff;
                    payload[4] = 1500 & 0xff;
                    payload[5] = (1500 >> 8) & 0xff;
                } else {
                    payload[0] = 200;
                    if(car_state == DRIVE){
                        payload[1] = 200;
                    }
                    else if(car_state == STOP_MODE){
                        payload[1] = 150;
                    }
                    else if(car_state == REVERSE){
                        payload[1] = 100;
                    }
                }
                drs.make_send_DRS(DRS_Constants.LEV2_R9_STATE1, payload);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }

                drs.make_send_DRS(DRS_Constants.VBAT);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }

        }
    }

    private SensorManager mSensorManager;
    private SensorEventListener mSensorEventListener;

    @Override
    protected void onResume() {
        super.onResume();
        if(requestonPause){
            Log.d(TAG,"rstart");
            requestonPause = false;
            running = true;
            thread = new SendThread();
            thread.start();
        }

        mSensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        mSensorEventListener = new SensorListener();
        mSensorManager.registerListener(mSensorEventListener,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        super.onPause();
        thread = null;
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    @Override
    protected void endOfController() {
        super.endOfController();
        payload[0] = 100;
        drs.make_send_DRS(DRS_Constants.LEV2_R9_STATE1,payload);

    }

    public class SensorListener implements SensorEventListener {
        float roll;
        float pitch;
        float yaw;

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] v = event.values;
            switch(event.sensor.getType()){
                case Sensor.TYPE_ORIENTATION :
//                       yaw = v[0];

                    pitch = (int)((-v[1]) * 1000 / 60 + 1500);
                    roll = (int)((-v[2]-30) * 1000 / 60 + 2000);
                    if(pitch > 2000)
                        pitch = 2000;
                    if(pitch < 1000)
                        pitch = 1000;

                    if(roll > 2000)
                        roll= 2000;
                    if(roll < 1000)
                        roll = 1000;
//                    Log.d(TAG,"roll : " + roll+ "\tpitch : " + pitch);
//
                    try {
                        if (pitch > 1600 && pitch <= 2000) {
                            right.setVisibility(View.VISIBLE);
                            left.setVisibility(View.INVISIBLE);
                        }
                        if (pitch >= 1400 && pitch <= 1600) {
                            right.setVisibility(View.INVISIBLE);
                            left.setVisibility(View.INVISIBLE);
                        }
                        if (pitch >= 1000 && pitch < 1400) {
                            right.setVisibility(View.INVISIBLE);
                            left.setVisibility(View.VISIBLE);
                        }

                        payload[2] = (int)roll % 255;
                        payload[3] = (int)roll / 255;
                        payload[4] = (int)pitch % 255;
                        payload[5] = (int)pitch / 255;
                    }catch (NullPointerException e){
                    }
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Nothiing
        }
    }



}
