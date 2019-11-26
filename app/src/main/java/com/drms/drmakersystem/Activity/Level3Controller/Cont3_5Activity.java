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

public class Cont3_5Activity extends Level3_multi_Controller {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topic.setText("3단계 5주차 양산");
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cont_3_5_image0),(int)(35*x), (int)(35*x),false));
        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));
    }

    private SendThread mThread;
    private ConstraintLayout cont_sub_layout;
    private ImageView equip;
    @Override
    protected void implementationControlView() {
        super.implementationControlView();

        cont_sub_layout= (ConstraintLayout) View.inflate(Cont3_5Activity.this, R.layout.activity_cont_3_5,null);
        cont_sub_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(cont_sub_layout);

        equip = cont_sub_layout.findViewById(R.id.equip);

        mThread = new SendThread();
        mThread.start();


    }
}
