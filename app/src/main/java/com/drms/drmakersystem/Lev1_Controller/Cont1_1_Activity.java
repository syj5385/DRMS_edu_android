package com.drms.drmakersystem.Lev1_Controller;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.R;

import java.util.List;

public class Cont1_1_Activity extends AppCompatActivity {

    private ImageView tower_btn, bridge_btn;

    private ImageView tower, bridge;

    private LinearLayout content_layout;

    TextView model_tower, model_bridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont1_1);

        getSupportActionBar().hide();
        Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display display = getWindowManager().getDefaultDisplay();


        content_layout = (LinearLayout)findViewById(R.id.content_layout);

        tower_btn = (ImageView)findViewById(R.id.tower);
        bridge_btn = (ImageView)findViewById(R.id.bridge);

        tower_btn.setOnClickListener(mClickListener);
        bridge_btn.setOnClickListener(mClickListener);

        bridge = new ImageView(this);
        tower = new ImageView(this);

        bridge.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bridge.setImageDrawable(getResources().getDrawable(R.drawable.bridge));

        tower.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tower.setImageDrawable(getResources().getDrawable(R.drawable.tower));

        model_bridge = new TextView(this);
        model_bridge.setText("다리 모형");
        model_bridge.setTextColor(getResources().getColor(R.color.textColor));
        model_bridge.setTextSize(18);
        model_bridge.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        model_bridge.setGravity(View.TEXT_ALIGNMENT_CENTER);


        model_tower = new TextView(this);
        model_tower.setText("탑 모형");
        model_tower.setTextColor(getResources().getColor(R.color.textColor));
        model_tower.setTextSize(18);
        model_tower.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        model_tower.setGravity(View.TEXT_ALIGNMENT_CENTER);

    }

    public View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.tower :
                    try{
                        content_layout.removeAllViews();
                        bridge_btn.setImageDrawable(getResources().getDrawable(R.drawable.model1));
                        tower_btn.setImageDrawable(getResources().getDrawable(R.drawable.model_on));
                    }catch(Exception e){
                        Log.e("Romove bridge","Exception",e);
                    }

                    content_layout.addView(tower);
                    content_layout.addView(model_tower);


                    break;

                case R.id.bridge :
                    try{
                        content_layout.removeAllViews();
                        tower_btn.setImageDrawable(getResources().getDrawable(R.drawable.model2));
                        bridge_btn.setImageDrawable(getResources().getDrawable(R.drawable.model_on));
                    }catch(Exception e){
                        Log.e("Romove tower","Exception",e);
                    }

                    content_layout.addView(bridge);
                    content_layout.addView(model_bridge);

                    break;
            }
        }
    };
}
