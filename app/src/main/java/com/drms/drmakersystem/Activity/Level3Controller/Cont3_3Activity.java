package com.drms.drmakersystem.Activity.Level3Controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.drms.drmakersystem.R;

/**
 * Created by jjun on 2018. 8. 1..
 */

public class Cont3_3Activity extends Level3_multi_Controller {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topic.setText("3단계 3주차 응급 드론");
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cont3_3_image3),(int)(35*x), (int)(35*x),false));
        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));
    }

    private SendThread mThread;

    private ConstraintLayout cont_sub_layout;
    private ImageView car;
    private boolean onoff = true;

    @Override
    protected void implementationControlView() {
        super.implementationControlView();

        cont_sub_layout= (ConstraintLayout) View.inflate(Cont3_3Activity.this, R.layout.activity_cont_3_3,null);
        cont_sub_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(cont_sub_layout);
        car = cont_sub_layout.findViewById(R.id.car);

        mThread = new SendThread();
        mThread.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(running){
                    final int[] rc = drms.getRcdata();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(rc[7] == 1000){
                                car.setImageDrawable(getResources().getDrawable(R.drawable.cont3_3_image0));
                            }
                            else if(rc[7] == 2000) {
                                if(onoff)
                                    car.setImageDrawable(getResources().getDrawable(R.drawable.cont3_3_image1));
                                else
                                    car.setImageDrawable(getResources().getDrawable(R.drawable.cont3_3_image2));
                                onoff = !onoff;
                            }
                        }
                    });

                    try{
                        Thread.sleep(250);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }

                }
            }
        }).start();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }
}
