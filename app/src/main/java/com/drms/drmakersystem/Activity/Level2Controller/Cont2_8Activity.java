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

public class Cont2_8Activity extends ControllerActivity {

    private ImageView[] animal = new ImageView[3];
    private Icon[] animal_icon = new Icon[3];
    private SendThread thread;


    private boolean[] isRotate = {false,false, false};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topic.setText("2단계 8주차 착시현상");
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_8_image7),(int)(35*x), (int)(35*x),false));
        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));

    }

    @Override
    protected void loadBitmap(float x, float y) {
        super.loadBitmap(x, y);

    }

    @Override
    protected void implementationControlView() {
        super.implementationControlView();

        controller_layout = (LinearLayout) View.inflate(Cont2_8Activity.this, R.layout.activity_cont_2_8,null);
        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(controller_layout);

        ImageView top1 = (ImageView)controller_layout.findViewById(R.id.top1);
        top1.setImageDrawable(new BitmapDrawable(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_8_image0),(int)(40*x), (int)(40*y),false)));

        ImageView top2 = (ImageView)controller_layout.findViewById(R.id.top2);
        top2.setImageDrawable(new BitmapDrawable(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_8_image0),(int)(40*x), (int)(40*y),false)));

        animal[0] = (ImageView)controller_layout.findViewById(R.id.animal0);
        animal[1] = (ImageView)controller_layout.findViewById(R.id.animal1);
        animal[2] = (ImageView)controller_layout.findViewById(R.id.animal2);

        animal_icon[0] = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_8_image1),(int)(35*x), (int)(40*x),false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_8_image2),(int)(35*x), (int)(40*x),false)
        );

        animal_icon[1] = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_8_image3),(int)(35*x), (int)(40*x),false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_8_image4),(int)(35*x), (int)(40*x),false)
        );

        animal_icon[2] = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_8_image5),(int)(35*x), (int)(40*x),false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_8_image6),(int)(35*x), (int)(40*x),false)
        );

        for(int i=0; i<animal.length ; i++){
            animal[i].setImageDrawable(new BitmapDrawable(animal_icon[i].getImg1()));
        }

        for(int i=0; i<animal.length ; i++){
            animal[i].setOnTouchListener(new View.OnTouchListener() {
                int index = 0;
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        switch(view.getId()){
                            case R.id.animal0 :
                                payload[1] = 200;
                                isRotate[0] = true;
                                index = 0;
                                break;

                            case R.id.animal1 :
                                payload[2] = 200;
                                isRotate[1] = true;
                                index = 1;

                                break;

                            case R.id.animal2 :
                                payload[3] = 200;
                                isRotate[2] = true;
                                index = 2;
                                break;
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while(isRotate[index]){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            animal[index].setImageDrawable(new BitmapDrawable(animal_icon[index].getImg2()));
                                        }
                                    });
                                    try{
                                        Thread.sleep(50);
                                    }catch (InterruptedException e){}
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            animal[index].setImageDrawable(new BitmapDrawable(animal_icon[index].getImg1()));
                                        }
                                    });
                                    try{
                                        Thread.sleep(50);
                                    }catch (InterruptedException e){}
                                }
                            }
                        }).start();
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        switch(view.getId()){
                            case R.id.animal0 :
                                payload[1] = 100;
                                isRotate[0] = false;
                                break;

                            case R.id.animal1 :
                                payload[2] = 100;
                                isRotate[1] = false;
                                break;

                            case R.id.animal2 :
                                payload[3] = 100;
                                isRotate[2] = false;
                                break;
                        }
                    }
                    return true;
                }
            });
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
                        new UploadManager(Cont2_8Activity.this, mBluetoothService, new FileManagement(Cont2_8Activity.this).readBTAddress()[1],DRMS.LEV2_8){

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

    private int payload[] = {100,100,100,100};
    public class SendThread extends Thread {



        public SendThread() {
            super();
            drs = new DRS_SerialProtocol(DRS_Constants.LEV2_R8, btHandler,mBluetoothService);
        }

        @Override
        public void run() {
            super.run();
            while(running) {
                if (!isArmed) {
                    for (int i = 0; i < payload.length; i++) {
                        payload[i] = 100;
                    }
                } else {
                    payload[0] = 200;
                }


                drs.make_send_DRS(DRS_Constants.LEV2_R8_STATE1,payload);

                try{
                    Thread.sleep(50);
                }catch (InterruptedException e){}

                drs.make_send_DRS(DRS_Constants.VBAT);

                try{
                    Thread.sleep(50);
                }catch (InterruptedException e){}


            }
        }
    }

    @Override
    protected void endOfController() {
        super.endOfController();
        payload[0] = 100;
        drs.make_send_DRS(DRS_Constants.LEV2_R8_STATE1,payload);

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
