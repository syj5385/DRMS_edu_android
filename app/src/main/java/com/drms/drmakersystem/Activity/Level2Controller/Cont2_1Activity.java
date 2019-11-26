package com.drms.drmakersystem.Activity.Level2Controller;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.Communication.DRS_Constants;
import com.drms.drmakersystem.Communication.DRS_SerialProtocol;
import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.R;
import com.drms.drmakersystem.Activity.ControllerActivity;
import com.drms.drmakersystem.STK500v1.UploadManager;


/**
 * Created by jjun on 2018. 2. 23..
 */

public class Cont2_1Activity extends ControllerActivity {

    private int speedOfAnimal = 2000;
    private int count = 0;
    private Bitmap velocity[] = new Bitmap[3];
    private BitmapDrawable onDrawable, offDrawable;
    private ImageView animal[] = new ImageView[3];
    private TextView speedText;
    private Bitmap on, off;

    private ImageView start, end;
    private boolean isStart = false;

    private ImageView speed[] = new ImageView[3];
    private boolean speedClick[] = {true, false, false}; // default : slow

    private SendThread thread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topic.setText("2단계 1주차 두더지를 잡아라");
//        thread = new SendThread();
//        thread.start();
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_1_image6),(int)(35*x),(int)(35*x),false));
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

        controller_layout = (LinearLayout) View.inflate(Cont2_1Activity.this, R.layout.activity_cont_2_1,null);
        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(controller_layout);

        velocity[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_1_image2),(int)(20*x),(int)(20*x),false);
        velocity[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_1_image3),(int)(20*x),(int)(20*x),false);
        velocity[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_1_image4),(int)(20*x),(int)(20*x),false);

        on = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_1_image7),(int)(50*x),(int)(50*x),false);
        off = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_1_image5),(int)(50*x),(int)(50*x),false);
        onDrawable = new BitmapDrawable(on);
        offDrawable = new BitmapDrawable(off);



        start = (ImageView)controller_layout.findViewById(R.id.start);
        end = (ImageView)controller_layout.findViewById(R.id.end);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start
                if(isArmed){
                    //start
                    if(!isStart) {
                        tts.speak("동작을 시작합니다.", TextToSpeech.QUEUE_FLUSH, null);
                        isStart = true;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        new Thread(new Runnable() {

                            int order = 0;
                            int temp = 0;

                            @Override
                            public void run() {
                                count = 0;
                                while (isStart && count++ < 10) {
                                    order = (int) (Math.random() * 3);
//                                while(order == temp){
//                                    temp = (int)(Math.random()*3);
//                                }
                                    Log.d(TAG, "this order : " + order + "\ncount : " + count);
                                    payload[order + 1] = 200;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            animal[order].setImageDrawable(onDrawable);
                                        }
                                    });
                                    try {
                                        Thread.sleep(speedOfAnimal);
                                    } catch (InterruptedException e) {
                                    }
                                    ;
                                    payload[order + 1] = 100;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            animal[order].setImageDrawable(offDrawable);
                                        }
                                    });
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                    }
                                    ;
                                }
                                if (count == 11) {
                                    tts.speak("동작이 완료되었습니다.", TextToSpeech.QUEUE_FLUSH, null);
                                    isStart = false;
                                }
                            }
                        }).start();
                    }
                    else{
                        Toast.makeText(Cont2_1Activity.this,"동작 중..", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    tts.speak("시동이 꺼져있습니다.", TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=1; i<payload.length-1; i++){
                    isStart = false;
                    payload[i] = 100;

                }
                tts.speak("동작을 중지합니다.", TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        speedText= (TextView)controller_layout.findViewById(R.id.speed_text);
        speed[0] = (ImageView)controller_layout.findViewById(R.id.slow);
        speed[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isStart) {
                    speedOfAnimal = 2000;
                    payload[4] = 100;
                    speedText.setText("속도 : 느림");
                }else{
                    Toast.makeText(Cont2_1Activity.this,"동작 중...", Toast.LENGTH_SHORT).show();
                }

            }
        });
        speed[1] = (ImageView)controller_layout.findViewById(R.id.medium);
        speed[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isStart) {
                    speedOfAnimal = 1500;
                    payload[4] = 150;
                    speedText.setText("속도 : 중간");
                }else{
                    Toast.makeText(Cont2_1Activity.this,"동작 중...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        speed[2] = (ImageView)controller_layout.findViewById(R.id.fast);
        speed[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isStart) {
                    speedOfAnimal = 1000;
                    payload[4] = 200;
                    speedText.setText("속도 : 빠름");
                }else{
                    Toast.makeText(Cont2_1Activity.this,"동작 중...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        animal[0] = (ImageView)controller_layout.findViewById(R.id.dudu1);
        animal[1] = (ImageView)controller_layout.findViewById(R.id.dudu2);
        animal[2] = (ImageView)controller_layout.findViewById(R.id.dudu3);

        for(int i=0; i<animal.length;i++){
            animal[i].setImageDrawable(new BitmapDrawable(off));
        }


        for(int i=0; i<speed.length; i++){
            speed[i].setImageDrawable(new BitmapDrawable(velocity[i]));
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
                        new UploadManager(Cont2_1Activity.this, mBluetoothService, new FileManagement(Cont2_1Activity.this).readBTAddress()[1], DRMS.LEV2_1){

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

    private int payload[] = {100,100,100,100,100};  // [power] [animal speed] -> 100 : stop / 200 : move

    private DRS_SerialProtocol drs;

    public class SendThread extends Thread {


        public SendThread() {
            super();
            drs = new DRS_SerialProtocol(DRS_Constants.LEV2_R1, btHandler,mBluetoothService);
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


                drs.make_send_DRS(DRS_Constants.LEV2_R1_STATE1,payload);

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
        drs.make_send_DRS(DRS_Constants.LEV2_R1_STATE1,payload);

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
