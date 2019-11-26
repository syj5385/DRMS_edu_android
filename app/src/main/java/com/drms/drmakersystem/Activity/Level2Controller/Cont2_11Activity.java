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

public class Cont2_11Activity extends ControllerActivity {

    private SendThread thread;
    private LinearLayout left, right;
    private Joystick mLeftJoystick, mRightJoystick;
    private ImageView equip;
    private Bitmap equip_off,equip_on;

    private boolean isEquip = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topic.setText("2단계 11주차 트랙터");
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_11_image6),(int)(35*x), (int)(35*x),false));
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
        controller_layout = (LinearLayout) View.inflate(Cont2_11Activity.this, R.layout.activity_cont_2_11,null);
        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(controller_layout);
        left = controller_layout.findViewById(R.id.left);
        right  = controller_layout.findViewById(R.id.right);

        mLeftJoystick = new Joystick(this,this,1000,2000, BitmapFactory.decodeResource(getResources(),R.drawable.cont2_11_image3)){
            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w,h,oldw,oldh);
//                setBackgroundColor(Color.GRAY);
            }
        };
        mLeftJoystick.setJoysticMode(Joystick.MODE3);
        mRightJoystick = new Joystick(this,this,1000,2000, BitmapFactory.decodeResource(getResources(),R.drawable.cont2_11_image3)){
            @Override
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
//                setBackgroundColor(Color.GRAY);
            }
        };
        mRightJoystick.setJoysticMode(Joystick.MODE5);
        left.addView(mLeftJoystick);
        right.addView(mRightJoystick);

        equip = controller_layout.findViewById(R.id.equip);
        equip_off = BitmapFactory.decodeResource(getResources(),R.drawable.cont2_11_image4);
        equip_on = BitmapFactory.decodeResource(getResources(),R.drawable.cont2_11_image5);
        equip_off = Bitmap.createScaledBitmap(equip_off,(int)(equip_off.getWidth()*0.5),(int)(equip_off.getHeight()*0.5),false);
        equip_on = Bitmap.createScaledBitmap(equip_on,(int)(equip_on.getWidth()*0.5),(int)(equip_on.getHeight()*0.5),false);
        equip.setImageDrawable(new BitmapDrawable(equip_off));
        equip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEquip){
                    isEquip = false;
                    equip.setImageDrawable(new BitmapDrawable(equip_off));
                }
                else{
                    isEquip = true;
                    equip.setImageDrawable(new BitmapDrawable(equip_on));
                }
            }
        });

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
                        new UploadManager(Cont2_11Activity.this, mBluetoothService, new FileManagement(Cont2_11Activity.this).readBTAddress()[1],DRMS.LEV2_11){

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

    private int payload[] = {100,100,100,100,100,100};
    private DRS_SerialProtocol drs;

    public class SendThread extends Thread {


        public SendThread() {
            super();
            drs = new DRS_SerialProtocol(DRS_Constants.LEV2_R11, btHandler,mBluetoothService);
            payload[0] = 100;
            payload[1] = 1000 % 255;
            payload[2] = 1000 / 255;
            payload[3] = 1500 % 255;
            payload[4] = 1500 / 255;
            payload[5] = 100;
        }

        @Override
        public void run() {
            super.run();
            while(running){
                if (!isArmed) {
                    payload[0] = 100;
                    payload[1] = 1000 & 0xff;
                    payload[2] = (1000 >> 8) & 0xff;
                    payload[3] = 1500 & 0xff;
                    payload[4] = (1500 >> 8) & 0xff;
                    payload[5] = 100;
                } else {
                    payload[0] = 200;
                    payload[1] = mLeftJoystick.getVertical() % 255;
                    payload[2] = mLeftJoystick.getVertical() / 255;
                    payload[3] = mRightJoystick.getHorizontal() % 255;
                    payload[4] = mRightJoystick.getHorizontal() / 255;
                    if(isEquip)
                        payload[5] = 200;
                    else
                        payload[5] = 100;
                }
                drs.make_send_DRS(DRS_Constants.LEV2_R11_STATE1, payload);

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

    @Override
    protected void endOfController() {
        super.endOfController();
        payload[0] = 100;
        drs.make_send_DRS(DRS_Constants.LEV2_R11_STATE1,payload);

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
