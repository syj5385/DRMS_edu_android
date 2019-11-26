package com.drms.drmakersystem.Activity.Level3Controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.drms.drmakersystem.R;

/**
 * Created by comm on 2018-08-13.
 */

public class Cont3_8Activity extends Level3_multi_Controller {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topic.setText("3단계 8주차 구기 드론");
        character.setImg(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.cont3_8_image9),(int)(35*x), (int)(35*x),false));
        character_img.setImageDrawable(new BitmapDrawable(character.getImg1()));
    }

    @Override
    protected void initializeView() {
        super.initializeView();
    }

    private ConstraintLayout cont_sub_layout;

    @Override
    protected void implementationControlView() {
        super.implementationControlView();
        cont_sub_layout= (ConstraintLayout) View.inflate(Cont3_8Activity.this, R.layout.activity_cont_3_8,null);
        cont_sub_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        controller_layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(cont_sub_layout);
    }
}
