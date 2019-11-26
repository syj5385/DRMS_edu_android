package com.drms.drmakersystem.Activity.Level2Controller;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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

public class Cont2_5Activity extends ControllerActivity {

    private Icon on, off;
    private Bitmap[] back_img = new Bitmap[2];

    private ImageView start, end;
    private LinearLayout back;

    private SendThread thread;

    private MediaPlayer mMediaPlayer;
    private SensorManager mSensorManager;
    private SensorEventListener mSensorEventListener;

    private boolean isStart = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorEventListener = new SensorListener();
        topic.setText("2단계 5주차 오르골");
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_5_image7),(int)(35*x), (int)(35*x),false));
        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));
    }

    @Override
    protected void loadBitmap(float x, float y) {
        super.loadBitmap(x, y);

    }

    @Override
    protected void implementationControlView() {
        super.implementationControlView();

        controller_layout = (LinearLayout) View.inflate(Cont2_5Activity.this, R.layout.activity_cont_2_5,null);
        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(controller_layout);

        back_img[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_5_image5),(int)(60*x), (int)(45*x),false);
        back_img[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_5_image6),(int)(60*x), (int)(45*x),false);
        back = (LinearLayout)controller_layout.findViewById(R.id.back);
        back.setBackground(new BitmapDrawable(back_img[0]));

        on = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_5_image1),(int)(35*x), (int)(17*x),false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_5_image2),(int)(35*x), (int)(17*x),false)
        );

        off = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_5_image3),(int)(35*x), (int)(17*x),false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_5_image4),(int)(35*x), (int)(17*x),false)
        );

        start = (ImageView)controller_layout.findViewById(R.id.start);
        end = (ImageView)controller_layout.findViewById(R.id.end);

        start.setImageDrawable(new BitmapDrawable(on.getImg1()));
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isStart){
                    isStart = true;
                    start.setImageDrawable(new BitmapDrawable(on.getImg2()));
                    end.setImageDrawable(new BitmapDrawable(off.getImg1()));
                    if(light_value < 50){
                        mMediaPlayer = MediaPlayer.create(Cont2_5Activity.this,R.raw.dark);

                    }
                    else if(light_value >= 50){
                        mMediaPlayer = MediaPlayer.create(Cont2_5Activity.this,R.raw.bright);
                    }
                    mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                        @Override
                        public void onSeekComplete(MediaPlayer mediaPlayer) {
                            mMediaPlayer.start();
                        }
                    });
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mMediaPlayer.release();
                        }
                    });
                    mMediaPlayer.seekTo(0);
                }
            }
        });
        end.setImageDrawable(new BitmapDrawable(off.getImg2()));
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStart){
                    isStart = false;
                    start.setImageDrawable(new BitmapDrawable(on.getImg1()));
                    end.setImageDrawable(new BitmapDrawable(off.getImg2()));
                    if(mMediaPlayer.isPlaying()){
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                    }
                }
            }
        });


        running = true;
        thread = new SendThread();
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
                        new UploadManager(Cont2_5Activity.this, mBluetoothService, new FileManagement(Cont2_5Activity.this).readBTAddress()[1],DRMS.LEV2_5){

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

    @Override
    protected void initializeView() {
        super.initializeView();

    }
    private int payload[] = {100,100,100,100}; // armed device_on bright/dark sensorvalueOflight(0~255)
    private DRS_SerialProtocol drs;
    public class SendThread extends Thread {



        public SendThread() {
            super();

        }

        @Override
        public void run() {
            super.run();
            while(running) {
                drs = new DRS_SerialProtocol(DRS_Constants.LEV2_R5, btHandler, mBluetoothService);
                if (!isArmed) {
                    payload[0] = 100;
                } else {
                    payload[0] = 200;
                    if (isStart)
                        payload[1] = 200;
                    else
                        payload[1] = 100;

                }

                try {
                    drs.make_send_DRS(DRS_Constants.LEV2_R5_STATE1, payload);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }

                    drs.make_send_DRS(DRS_Constants.VBAT);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }catch (NullPointerException e1){
                    Log.e(TAG,"NullPointException");
                }

            }
        }
    }

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
        mSensorManager.registerListener(mSensorEventListener,mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void endOfController() {
        super.endOfController();
        payload[0] = 100;
        drs.make_send_DRS(DRS_Constants.LEV2_R5_STATE1,payload);

    }

    @Override
    protected void onPause() {
        super.onPause();
        thread = null;

        try {
            if(mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
            }
            mSensorManager.unregisterListener(mSensorEventListener);

            start.setImageDrawable(new BitmapDrawable(on.getImg1()));
            end.setImageDrawable(new BitmapDrawable(on.getImg2()));
        }catch (NullPointerException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    private float light_value;

    public class SensorListener implements SensorEventListener {
        float x,y,z;
        @Override
        public void onSensorChanged(SensorEvent event) {
//            if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED){
            float[] v = event.values;
            switch(event.sensor.getType()) {
                case Sensor.TYPE_LIGHT:
                    light_value = v[0];
//                    Log.d(TAG,"light value : " + light_value);
                    if(back != null) {
                        if (light_value >= 50) {
                            back.setBackground(new BitmapDrawable(back_img[0]));
                            payload[2] = 200;
                            payload[3] = (int)light_value;
                            if(payload[3] > 255)
                                payload[3] = 255;

                        } else if (light_value < 50) {
                            payload[2] = 100;
                            payload[3] = (int)light_value;
                            back.setBackground(new BitmapDrawable(back_img[1]));
                        }
                    }
                    break;
            }
//            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Nothiing
        }
    }



}
