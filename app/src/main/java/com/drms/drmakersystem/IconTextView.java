package com.drms.drmakersystem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by jjunj on 2016-12-20.
 */
public class IconTextView extends LinearLayout {

    ImageView mIcon;
    TextView mtext01;
    TextView mtext02;

    public IconTextView(Context context, IconTextItem aItem) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem, this, true);

        mIcon = (ImageView) findViewById(R.id.iconitem);
        mIcon.setImageDrawable(aItem.getIcon());

        mtext01 = (TextView) findViewById(R.id.dataitem01);
        mtext01.setText(aItem.getData(0));

        mtext02 = (TextView) findViewById(R.id.dataitem02);
        mtext02.setText(aItem.getData(1));
    }

    public void setText(int index, String data){
        if(index == 0 ){
            mtext01.setText(data);
        }
        else if(index == 1){
            mtext02.setText(data);
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    public void setIcon(Drawable icon){
        mIcon.setImageDrawable(icon);
    }
}