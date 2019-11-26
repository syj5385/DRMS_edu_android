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

import com.drms.drmakersystem.Activity.ControllerActivity;
import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.Communication.DRS_Constants;
import com.drms.drmakersystem.Communication.DRS_SerialProtocol;
import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.R;
import com.drms.drmakersystem.STK500v1.UploadManager;


/**
 * Created by jjun on 2018. 2. 23..
 */

public class Cont2_2Activity extends ControllerActivity {

    private Bitmap[] roll = new Bitmap[3];

    private ImageView[] shot = new ImageView[3];
    private ImageView start,end;
    private boolean isStart = false;
    private int count = 0;

    private SendThread thread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_2_image8),(int)(35*x), (int)(35*x),false));
        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));
        topic.setText("2단계 2주차 나는 사격왕");
    }

    @Override
    protected void loadBitmap(float x, float y) {
        super.loadBitmap(x, y);

    }
    private int payload[] = {100,100,100,100};  // [power] [animal speed] -> 100 : stop / 200 : move

    @Override
    protected void implementationControlView() {
        super.implementationControlView();
        controller_layout = (LinearLayout) View.inflate(Cont2_2Activity.this, R.layout.activity_cont_2_2,null);
        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(controller_layout);

        LinearLayout temp = (LinearLayout)controller_layout.findViewById(R.id.game);
        shot[0] = (ImageView)controller_layout.findViewById(R.id.shot0);
        shot[1] = (ImageView)controller_layout.findViewById(R.id.shot1);
        shot[2] = (ImageView)controller_layout.findViewById(R.id.shot2);

        roll[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_2_image5),(int)(30*x),(int)(30*x),false);
        roll[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_2_image6),(int)(30*x),(int)(30*x),false);
        roll[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.cont2_2_image7),(int)(30*x),(int)(30*x),false);

        for(int i=0; i<shot.length ; i++){
            shot[i].setImageDrawable(new BitmapDrawable(roll[i]));
        }

        start = findViewById(R.id.start);
        end = findViewById(R.id.end);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isArmed) {
                    if(!isStart){
                        tts.speak("동작을 시작합니다.", TextToSpeech.QUEUE_FLUSH,null);
                        isStart = true;
                        try{
                            Thread.sleep(1000);
                        }catch (InterruptedException e){

                        }

                        new Thread(new Runnable() {
                            int rotation = 0;
                            int order = 0;
                            @Override
                            public void run() {
                                count = 0;
                                while (isStart && count++ < 10) {
                                    order = (int) (Math.random() * 3);
                                    Log.d(TAG, "this order : " + order + "\ncount : " + count);
                                    payload[order + 1] = 200;
                                    while(rotation != 360){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                shot[order].setRotation(rotation);
                                            }
                                        });
                                        try{
                                            Thread.sleep(30);
                                        }catch (InterruptedException e){};
                                        rotation +=3;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            shot[order].setRotation(0);}
                                    });
                                    payload[order+1] = 100;
                                    rotation = 0;
                                    try{
                                        Thread.sleep(700);
                                    }catch (InterruptedException e){}
                                }

                                    isStart = false;
                                    count = 0;
                                if(count == 11) {
                                    tts.speak("동작이 완료되었습니다.", TextToSpeech.QUEUE_FLUSH, null);
                                }

                            }
                        }).start();
                    }
                }
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i<shot.length ; i++){
                    shot[i].setRotation(0);
                    payload[i+1] = 100;
                }
                isStart = false;
                tts.speak("동작을 중지합니다.", TextToSpeech.QUEUE_FLUSH,null);
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
                        new UploadManager(Cont2_2Activity.this, mBluetoothService, new FileManagement(Cont2_2Activity.this).readBTAddress()[1], DRMS.LEV2_2){

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


    @Override
    protected void endOfController() {
        super.endOfController();
        payload[0] = 100;
        drs.make_send_DRS(DRS_Constants.LEV2_R2_STATE1,payload);

    }

    public class SendThread extends Thread {


        public SendThread() {
            super();
            drs = new DRS_SerialProtocol(DRS_Constants.LEV2_R2, btHandler,mBluetoothService);
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


                drs.make_send_DRS(DRS_Constants.LEV2_R2_STATE1,payload);

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
