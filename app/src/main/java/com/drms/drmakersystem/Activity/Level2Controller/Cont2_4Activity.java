package com.drms.drmakersystem.Activity.Level2Controller;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.drms.drmakersystem.Activity.ControllerActivity;
import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.Communication.Protocol.DRS_Constants;
import com.drms.drmakersystem.Communication.Protocol.DRS_SerialProtocol;
import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.Joystick.Joystick;
import com.drms.drmakersystem.R;
import com.drms.drmakersystem.Communication.Protocol.STK500v1.UploadManager;

/**
 * Created by jjun on 2018. 2. 23..
 */

public class Cont2_4Activity extends ControllerActivity {

    private Bitmap top, medium;
    private LinearLayout top_layout, medium_layout;
    private LinearLayout left_layout, right_layout;

    private LinearLayout left, right;

    private Joystick LeftJoystick, RightJoystick;


    private SendThread thread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topic.setText("2단계 4주차 기차");
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_4_image4),(int)(35*x), (int)(35*x),false));
        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));

    }

    @Override
    protected void loadBitmap(float x, float y) {
        super.loadBitmap(x, y);

    }

    @Override
    protected void implementationControlView() {
        super.implementationControlView();

        controller_layout = (LinearLayout) View.inflate(Cont2_4Activity.this, R.layout.activity_cont_2_4,null);
        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(controller_layout);

        top_layout = (LinearLayout)controller_layout.findViewById(R.id.top);
//        top_layout.setBackground(new BitmapDrawable(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_4_image0),(int)(60*x), (int)(20*y),false)));

        medium_layout = (LinearLayout)controller_layout.findViewById(R.id.bottom);
//        medium_layout.setBackground(new BitmapDrawable(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_4_image1),(int)(60*x), (int)(20*y),false)));

        left = (LinearLayout)controller_layout.findViewById(R.id.left);
        LeftJoystick = new Joystick(Cont2_4Activity.this,Cont2_4Activity.this,1000,2000, BitmapFactory.decodeResource(this.getResources(),R.drawable.cont2_4_image7)){
            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                if ((width == 0 || height == 0) && (w>0 && h>0)){
                    Log.d(TAG,"Size changed - width : " + w + ", height = " + h);
                    width = w;
                    height =h ;
                    float rate = (float)(width/3)/(float)joystickbmp.getWidth();
                    joystickbmp = Bitmap.createScaledBitmap(joystickbmp,width/5,width/5,false);
                    offsetX = joystickbmp.getWidth()/2;
                    offsetY = joystickbmp.getHeight()/2;
                    x = width/2 - offsetX * 4/ 5;
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
        };
        LeftJoystick.setJoysticMode(Joystick.MODE3);
//        LeftJoystick.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        left.addView(LeftJoystick);
        right = (LinearLayout)controller_layout.findViewById(R.id.right);
        RightJoystick = new Joystick(Cont2_4Activity.this,Cont2_4Activity.this,1000,2000, BitmapFactory.decodeResource(this.getResources(),R.drawable.cont2_4_image8));
        RightJoystick.setJoysticMode(Joystick.MODE5);
//        RightJoystick.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        right.addView(RightJoystick);

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
                        new UploadManager(Cont2_4Activity.this, mBluetoothService, new FileManagement(Cont2_4Activity.this).readBTAddress()[1], DRMS.LEV2_4){

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

    private DRS_SerialProtocol drs;

    private int[] payload = {100, 100,100,100,100}; // total 5 byte
    public class SendThread extends Thread {



        public SendThread() {
            super();
            drs = new DRS_SerialProtocol(DRS_Constants.LEV2_R4, btHandler,mBluetoothService);
        }

        @Override
        public void run() {
            super.run();

            while(running) {
                if (!isArmed) {
                    payload[0] = 100;
                    payload[1] = 1000 & 0xff;
                    payload[2] = (1000 >> 8) & 0xff;
                    payload[3] = 1500 & 0xff;
                    payload[4] = (1500 >> 8) & 0xff;
                } else {
                    payload[0] = 200;
                    payload[1] = LeftJoystick.getVertical() & 0xff;
                    payload[2] = (LeftJoystick.getVertical() >> 8) & 0xff;
                    payload[3] = RightJoystick.getHorizontal() & 0xff;
                    payload[4] = (RightJoystick.getHorizontal() >> 8) & 0xff;
                }

                drs.make_send_DRS(DRS_Constants.LEV2_R4_STATE1, payload);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }

                drs.make_send_DRS(DRS_Constants.VBAT);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                ;
            }
        }
    }

    @Override
    protected void endOfController() {
        super.endOfController();
        payload[0] = 100;
        drs.make_send_DRS(DRS_Constants.LEV2_R4_STATE1,payload);

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        thread = null;
    }



}
