package com.drms.drmakersystem.Activity.Level3Controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.drms.drmakersystem.R;

/**
 * Created by jjun on 2018. 8. 1..
 */

public class Cont3_1Activity extends Level3_multi_Controller {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topic.setText("3단계 1주차 소금쟁이");
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cont3_1_image0),(int)(35*x), (int)(35*x),false));
        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));
    }

    private ConstraintLayout cont_sub_layout;
    private ImageView[] leg = new ImageView[4];
    private static final int LEFT_TOP = 0;
    private static final int RIGHT_TOP = 1;
    private static final int LEFT_BOTTOM = 2;
    private static final int RIGHT_BOTTOM=3;

    private SendThread mThread;
    private float rotation_left = 0;
    private float rotation_right=0;

    @Override
    protected void implementationControlView() {
        super.implementationControlView();

        cont_sub_layout= (ConstraintLayout) View.inflate(Cont3_1Activity.this, R.layout.activity_cont_3_1,null);
        cont_sub_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(cont_sub_layout);
        leg[LEFT_TOP] = cont_sub_layout.findViewById(R.id.left_top);
        leg[RIGHT_TOP] = cont_sub_layout.findViewById(R.id.right_top);
        leg[LEFT_BOTTOM] = cont_sub_layout.findViewById(R.id.left_Bottom);
        leg[RIGHT_BOTTOM] = cont_sub_layout.findViewById(R.id.right_Bottom);
        Log.d("leg","value : " + leg[LEFT_TOP]);

        mThread = new SendThread();
        mThread.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(running){
                    if(drms.getRcdata()[3] >= 1100 && isArmed){
                        rotation_left += (float)(drms.getRcdata()[3]-1000)*5/1000;
                        rotation_right -= (float)(drms.getRcdata()[3]-1000)*5/1000;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                leg[LEFT_TOP].setRotation(rotation_left);
                                leg[LEFT_BOTTOM].setRotation(rotation_left);
                                leg[RIGHT_TOP].setRotation(rotation_right);
                                leg[RIGHT_BOTTOM].setRotation(rotation_right);

                            }
                        });
                        try{
                            Thread.sleep(20);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
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
