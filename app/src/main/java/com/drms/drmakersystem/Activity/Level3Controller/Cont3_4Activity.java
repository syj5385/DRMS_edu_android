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

import com.drms.drmakersystem.Activity.ControllerActivity;
import com.drms.drmakersystem.R;

/**
 * Created by jjun on 2018. 8. 1..
 */

public class Cont3_4Activity extends ControllerActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topic.setText("3단계 4주차 청소기");
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cont_3_4_image3),(int)(35*x), (int)(35*x),false));
        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));
    }

    private ConstraintLayout cont_sub_layout;
    private ImageView robot, wifi, dust;
    private float rotation = 0;
    private float alpha = 1;
    private float interval = 1;


    @Override
    protected void implementationControlView() {
        super.implementationControlView();

        cont_sub_layout= (ConstraintLayout) View.inflate(Cont3_4Activity.this, R.layout.activity_cont_3_4,null);
        cont_sub_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(cont_sub_layout);
//
//        mThread = new SendThread();
//        mThread.start();

        robot = cont_sub_layout.findViewById(R.id.robot);
        wifi = cont_sub_layout.findViewById(R.id.wifi);
        dust = cont_sub_layout.findViewById(R.id.dust);
        wifi.setVisibility(View.INVISIBLE);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(running){
//                    final int[] rc = drms.getRcdata();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(rc[7] == 1000){
//                                wifi.setVisibility(View.INVISIBLE);
//                                alpha = 1;
//                                dust.setAlpha(alpha);
//                                robot.setRotation(rotation);
//                            }
//                            else{
//                                wifi.setVisibility(View.VISIBLE);
//                                alpha-=0.01;
//                                if(alpha < 0)
//                                    alpha = 1;
//                                dust.setAlpha(alpha);
//                                if(rotation >= 10 || rotation <= -10){
//                                    interval *= -1;
//                                }
//                                rotation += interval;
//                                robot.setRotation(rotation);
//
//                            }
//                        }
//                    });
//                    try{
//                        Thread.sleep(50);
//                    }
//                    catch (InterruptedException e){
//                        e.printStackTrace();
//                    }

                }
            }
        }).start();


    }
}
