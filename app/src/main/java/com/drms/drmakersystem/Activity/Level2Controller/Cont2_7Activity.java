package com.drms.drmakersystem.Activity.Level2Controller;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import com.drms.drmakersystem.Communication.BluetoothService;
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

public class Cont2_7Activity extends ControllerActivity{

    private ImageView[] roll = new ImageView[7];
    private SendThread thread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topic.setText("2단계 7주차 복권 당첨");
//        thread = new SendThread();
//        thread.start();
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_7_image1),(int)(35*x), (int)(35*x),false));
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
        controller_layout = (LinearLayout) View.inflate(Cont2_7Activity.this, R.layout.activity_cont_2_7,null);
        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(controller_layout);
        roll[0] = (ImageView)controller_layout.findViewById(R.id.roll);
        roll[1] = (ImageView)controller_layout.findViewById(R.id.roll1);
        roll[2] = (ImageView)controller_layout.findViewById(R.id.roll2);
        roll[3] = (ImageView)controller_layout.findViewById(R.id.roll3);
        roll[4] = (ImageView)controller_layout.findViewById(R.id.roll4);
        roll[5] = (ImageView)controller_layout.findViewById(R.id.roll5);
        roll[6] = (ImageView)controller_layout.findViewById(R.id.roll6);

        for(int i=0; i<roll.length ;i++){
            roll[i].setOnTouchListener(rollTouchListenr);
        }

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
                        new UploadManager(Cont2_7Activity.this, mBluetoothService, new FileManagement(Cont2_7Activity.this).readBTAddress()[1],DRMS.LEV2_7){

                            @Override
                            public void setDismissListener() {
                                Log.d(TAG,"Hi");
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

    private boolean rolling[] = {false, false, false, false, false, false};
    private int degree[] = new int[6];

    private View.OnTouchListener rollTouchListenr = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                switch(view.getId()){
                    case R.id.roll :
                        for(int i=0; i<rolling.length; i++){
                            rolling[i] = true ;
                        }
                        break;

                    case R.id.roll1:
                        rolling[0] = true;
                        break;

                    case R.id.roll2:
                        rolling[1] = true;
                        break;

                    case R.id.roll3:
                        rolling[2] = true;
                        break;

                    case R.id.roll4:
                        rolling[3] = true;
                        break;

                    case R.id.roll5:
                        rolling[4] = true;
                        break;

                    case R.id.roll6:
                        rolling[5] = true;
                        break;
                }
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                switch(view.getId()) {
                    case R.id.roll:
                        for(int i=0; i<rolling.length; i++){
                            rolling[i] = false ;
                        }
                        break;

                    case R.id.roll1:
                        rolling[0] = false ;

                        break;

                    case R.id.roll2:
                        rolling[1] = false ;

                        break;

                    case R.id.roll3:
                        rolling[2] = false ;

                        break;

                    case R.id.roll4:
                        rolling[3] = false ;

                        break;

                    case R.id.roll5:
                        rolling[4] = false ;

                        break;

                    case R.id.roll6:
                        rolling[5] = false ;
                        break;
                }
            }
            return true;

        }
    };

    public class SendThread extends Thread {

        private int payload[] = {100,100,100,100,100,100,100};
        private DRS_SerialProtocol drs;

        public SendThread() {
            super();
            drs = new DRS_SerialProtocol(DRS_Constants.LEV2_R7, btHandler,mBluetoothService);
        }

        @Override
        public void run() {
            super.run();
            if(mBluetoothService != null) {
                while (mBluetoothService == null || mBluetoothService.getState() != BluetoothService.STATE_CONNECTED && running) {
                    try {
                        Thread.sleep(35);
                    } catch (InterruptedException e) {
                    }
                }
                while (running) {
                    if (isArmed)
                        payload[0] = 200;
                    else
                        payload[0] = 100;

                    for (int i = 0; i < rolling.length; i++) {
                        if (rolling[i]) {
                            payload[i + 1] = 200;
                            degree[i] += 3;
                            if(degree[i] == 360){
                                degree[i] = 0;
                            }
                        }
                        else {
                            payload[i + 1] = 100;
                        }
                    }

                    drs.make_send_DRS(DRS_Constants.LEV2_R7_STATE1, payload);

                    try {
                        Thread.sleep(35);
                    } catch (InterruptedException e) {
                    }
                    ;

                    for(int i=0; i<rolling.length; i++){
                        if(payload[i+1] == 200)
                            degree[i] += 3;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0; i<roll.length -1; i++){
                                roll[i+1].setRotation(degree[i]);
                            }
                        }
                    });

                    drs.make_send_DRS(DRS_Constants.VBAT);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                    ;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=0; i<roll.length -1; i++){
                                roll[i+1].setRotation(degree[i]);
                            }
                        }
                    });
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
    }



    @Override
    protected void onPause() {
        super.onPause();
        thread = null;
    }



}
