package com.drms.drmakersystem.Adapter.CustomAdapter2;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drms.drmakersystem.R;


/**
 * Created by jjunj on 2017-09-13.
 */

public class Custom2_View extends LinearLayout {
    ImageView icon;
    TextView name;

    public Custom2_View(Context context, Custom2_Item iconbox) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom2_item,this,true);

        name = (TextView)findViewById(R.id.name);
        name.setText(iconbox.getName());

    }

    public void setName( String name){
        this.name.setText(name);
    }


}
