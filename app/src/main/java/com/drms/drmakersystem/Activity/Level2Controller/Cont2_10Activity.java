package com.drms.drmakersystem.Activity.Level2Controller;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

public class Cont2_10Activity extends ControllerActivity {

    private SendThread thread;
    private LinearLayout left,center,right;

    private Joystick mLeftJoystick,mRightJoystick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topic.setText("2단계 10주차 비행기");
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_10_image6),(int)(35*x), (int)(35*x),false));
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
        controller_layout = (LinearLayout) View.inflate(Cont2_10Activity.this, R.layout.activity_cont_2_10,null);
        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(controller_layout);

        left = controller_layout.findViewById(R.id.left);
        right = controller_layout.findViewById(R.id.right);
        Bitmap characterTemp = BitmapFactory.decodeResource(this.getResources(),R.drawable.cont2_10_image6);
//        characterTemp = Bitmap.createScaledBitmap(characterTemp,characterTemp.getWidth()/2,characterTemp.getHeight()/2,false);
//        character_img.setImageDrawable(new BitmapDrawable(characterTemp));

        mLeftJoystick = new Joystick(this,this,1000,2000, BitmapFactory.decodeResource(getResources(),R.drawable.cont2_10_image3)){
            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                Log.d(TAG,"Size changed - width : " + w + ", height = " + h);
//                super.onSizeChanged(w, h, oldw, oldh);
                setBackgroundColor(Color.argb(00,00,00,00));
                if ((width == 0 || height == 0) && (w>0 && h>0)){
                    width = w;
                    height =h ;
                    float rate = (float)(width/2)/(float)joystickbmp.getWidth();
                    joystickbmp = Bitmap.createScaledBitmap(joystickbmp,(int)(joystickbmp.getWidth()*rate),(int)(joystickbmp.getHeight()*rate),false);
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
        };
        mRightJoystick = new Joystick(this,this,1000,2000, BitmapFactory.decodeResource(getResources(),R.drawable.cont2_10_image5)){
            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                Log.d(TAG,"Size changed - width : " + w + ", height = " + h);
                setBackgroundColor(Color.argb(00,00,00,00));
                if ((width == 0 || height == 0) && (w>0 && h>0)){
                    width = w;
                    height =h ;
                    float rate = (float)(width/2)/(float)joystickbmp.getWidth();
                    joystickbmp = Bitmap.createScaledBitmap(joystickbmp,(int)(joystickbmp.getWidth()*rate),(int)(joystickbmp.getHeight()*rate),false);
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
        };

        center = controller_layout.findViewById(R.id.center);
        LinearLayout centerDraw = new LinearLayout(this){
            private int width=0, height =0;
            private Bitmap cloud1, cloud2, plane;
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if(width >0 && height > 0){
                    canvas.drawBitmap(plane,width/2 - plane.getWidth()/2 , height/2 - plane.getHeight()/2,null);
                    canvas.drawBitmap(cloud1,0,0,null);
                    canvas.drawBitmap(cloud2,width-cloud2.getWidth(),height-cloud2.getHeight(),null);

                }
            }

            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                setBackgroundColor(Color.argb(00,00,00,00));
                if((width == 0 || height ==0) && w>0 && h>0){
                    width = w;
                    height = h;
                    plane = BitmapFactory.decodeResource(Cont2_10Activity.this.getResources(),R.drawable.cont2_10_image2);
                    plane = Bitmap.createScaledBitmap(plane,(int)((float)width* 0.8),(int)((float)height* 0.8),false);

                    cloud1 = BitmapFactory.decodeResource(Cont2_10Activity.this.getResources(),R.drawable.cont2_10_image0);
                    cloud2 = BitmapFactory.decodeResource(Cont2_10Activity.this.getResources(),R.drawable.cont2_10_image4);
                    cloud1 = Bitmap.createScaledBitmap(cloud1,(int)((float)width* 0.3),(int)((float)height* 0.3),false);
                    cloud2 = Bitmap.createScaledBitmap(cloud2,(int)((float)width* 0.3),(int)((float)height* 0.3),false);
                    invalidate();
                }
            }

        };
        centerDraw.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        center.addView(centerDraw);
        mLeftJoystick.setJoysticMode(Joystick.MODE3);
        mRightJoystick.setJoysticMode(Joystick.MODE3);

        left.addView(mLeftJoystick);
        right.addView(mRightJoystick);

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
                        new UploadManager(Cont2_10Activity.this, mBluetoothService, new FileManagement(Cont2_10Activity.this).readBTAddress()[1],DRMS.LEV2_10){

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

    private int payload[] = {100,100,100,100,100};

    private DRS_SerialProtocol drs;
    public class SendThread extends Thread {

        public SendThread() {
            super();
            drs = new DRS_SerialProtocol(DRS_Constants.LEV2_R10, btHandler,mBluetoothService);
            payload[1] = 1000 % 255;
            payload[2] = 1000 / 255;
            payload[3] = 1000 % 255;
            payload[4] = 1000 / 255;
        }

        @Override
        public void run() {
            super.run();
            while(running){
                while(running){
                    if (!isArmed) {
                        payload[0] = 100;
                        payload[1] = 1000 & 0xff;
                        payload[2] = (1000 >> 8) & 0xff;
                        payload[3] = 1000 & 0xff;
                        payload[4] = (1000 >> 8) & 0xff;
                    } else {
                        payload[0] = 200;
                        payload[1] = mLeftJoystick.getVertical() % 255;
                        payload[2] = mLeftJoystick.getVertical() / 255;
                        payload[3] = mRightJoystick.getVertical() % 255;
                        payload[4] = mRightJoystick.getVertical() / 255;
                    }
                    drs.make_send_DRS(DRS_Constants.LEV2_R10_STATE1, payload);

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
    }

    @Override
    protected void endOfController() {
        super.endOfController();
        payload[0] = 100;
        drs.make_send_DRS(DRS_Constants.LEV2_R10_STATE1,payload);

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
