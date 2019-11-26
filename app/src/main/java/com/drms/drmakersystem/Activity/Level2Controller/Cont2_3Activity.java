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
import com.drms.drmakersystem.Communication.Protocol.DRS_Constants;
import com.drms.drmakersystem.Communication.Protocol.DRS_SerialProtocol;
import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.R;
import com.drms.drmakersystem.Communication.Protocol.STK500v1.UploadManager;
import com.drms.drmakersystem.etc.Icon;

/**
 * Created by jjun on 2018. 2. 23..
 */

public class Cont2_3Activity extends ControllerActivity {

    private Bitmap[] eyes = new Bitmap[4];
    private Bitmap[] mouths = new Bitmap[4];
    private Bitmap face;
    private Icon[] controller = new Icon[2];


    private LinearLayout face_img;
    private ImageView[] cont_img = new ImageView[2];
    private ImageView eye, mouth ;

    private boolean eye_running = false, mouth_running = false;

    private SendThread thread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topic.setText("2단계 3주차 표정 만들기");
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image3),(int)(35*x), (int)(35*x),false));
        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));
    }

    @Override
    protected void loadBitmap(float x, float y) {
        super.loadBitmap(x, y);

    }

    @Override
    protected void implementationControlView() {
        super.implementationControlView();

        controller_layout = (LinearLayout) View.inflate(Cont2_3Activity.this, R.layout.activity_cont_2_3_2,null);
        controller_layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(controller_layout);

        face_img = (LinearLayout)controller_layout.findViewById(R.id.face);
        cont_img[0] = (ImageView)controller_layout.findViewById(R.id.control_eye);
        cont_img[1] = (ImageView)controller_layout.findViewById(R.id.controller_mouth);
        Log.d(TAG,"face : " + face_img.getWidth() + "\theight : " + face_img.getHeight());


        face = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image0),(int)(80*x), (int)(80*x),false);
        face_img.setBackground(new BitmapDrawable(face));

        controller[0] = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image1),(int)(35*x), (int)(35*x),false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image2),(int)(35*x), (int)(35*x),false));

        controller[1] = new Icon(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image1),(int)(35*x), (int)(35*x),false),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image2),(int)(35*x), (int)(35*x),false));

        eye = (ImageView)controller_layout.findViewById(R.id.eye);
        mouth = (ImageView)controller_layout.findViewById(R.id.mouth);

        eyes[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image4),(int)(80*x), (int)(25*x),false);
        eyes[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image5),(int)(80*x), (int)(25*x),false);
        eyes[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image6),(int)(80*x), (int)(25*x),false);
        eyes[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image7),(int)(80*x), (int)(25*x),false);

        mouths[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image8),(int)(80*x), (int)(25*x),false);
        mouths[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image9),(int)(80*x), (int)(25*x),false);
        mouths[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image10),(int)(80*x), (int)(25*x),false);
        mouths[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image11),(int)(80*x), (int)(25*x),false);

//        eyes[0] = BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image4);
//        eyes[1] = BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image5);
//        eyes[2] = BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image6);
//        eyes[3] = BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image7);
//
//        mouths[0] = BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image8);
//        mouths[1] = BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image9);
//        mouths[2] = BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image10);
//        mouths[3] = BitmapFactory.decodeResource(getResources(),R.drawable.cont2_3_image11);


        eye.setImageDrawable(new BitmapDrawable(eyes[1]));
        mouth.setImageDrawable(new BitmapDrawable(mouths[1]));
        for(int i=0; i<2; i++){
            cont_img[i].setImageDrawable(new BitmapDrawable(controller[i].getImg1()));
        }

        running = true;
        thread = new SendThread();
        thread.start();

        cont_img[0].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    cont_img[0].setImageDrawable(new BitmapDrawable(controller[0].getImg2()));
                    payload[1] = 200;
                    eye_running = true;

                    new Thread(new Runnable() {
                        int index= 0;
                        @Override
                        public void run() {
                            while(eye_running){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        eye.setImageDrawable(new BitmapDrawable(eyes[index]));
                                    }
                                });

                                try{
                                    Thread.sleep(300);
                                }catch (InterruptedException e){}
                                if(++index == 4){
                                    index = 0;
                                }
                            }
                        }
                    }).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    eye_running = false;
                    cont_img[0].setImageDrawable(new BitmapDrawable(controller[0].getImg1()));
                    payload[1] = 100;

                }
                return true;
            }
        });

        cont_img[1].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    cont_img[1].setImageDrawable(new BitmapDrawable(controller[1].getImg2()));
                    mouth_running = true;
                    payload[2] = 200;
                    new Thread(new Runnable() {
                        int index= 0;
                        @Override
                        public void run() {
                            while(mouth_running){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mouth.setImageDrawable(new BitmapDrawable(mouths[index]));
                                    }
                                });

                                try{
                                    Thread.sleep(300);
                                }catch (InterruptedException e){}
                                if(++index == 4){
                                    index = 0;
                                }
                            }
                        }
                    }).start();
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    cont_img[1].setImageDrawable(new BitmapDrawable(controller[1].getImg1()));
                    mouth_running = false;
                    payload[2] = 100;
                }
                return true;
            }
        });

        upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                running = false;
                // wait for stop Thread
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){};
                UploadManager manager =
                        new UploadManager(Cont2_3Activity.this, mBluetoothService, new FileManagement(Cont2_3Activity.this).readBTAddress()[1], DRMS.LEV2_3){

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

    private int payload[] = {100,100,100};
    private DRS_SerialProtocol drs;

    public class SendThread extends Thread {

        public SendThread() {
            super();
            drs = new DRS_SerialProtocol(DRS_Constants.LEV2_R3, btHandler,mBluetoothService);
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


                drs.make_send_DRS(DRS_Constants.LEV2_R3_STATE1,payload);

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
        drs.make_send_DRS(DRS_Constants.LEV2_R3_STATE1,payload);

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
