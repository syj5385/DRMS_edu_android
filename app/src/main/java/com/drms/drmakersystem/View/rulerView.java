package com.drms.drmakersystem.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

/**
 * Created by comm on 2018-02-20.
 */

public class rulerView extends View {
    protected static final String TAG = "View";
    public static final int SET_SCREEN_SIZE = 0;
    public static final int UPDATE_UI = 1;

    protected float width, height;
    protected float x, y;

    protected Context context;
    protected Activity activity;

    public rulerView(Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    public class Icon{
        private float x,y;
        private Bitmap icon;

        public Icon(Bitmap icon, float x, float y) {
            this.icon = icon;
            this.x = x;
            this.y = y;
        }

        public Bitmap getIcon(){
            return icon;
        }

        public float getX(){
            return x;
        }

        public float getY(){
            return y;
        }

        public void setIcon(Bitmap icon){
            this.icon = icon;
        }


    }

    protected Handler viewHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case SET_SCREEN_SIZE :
                    float[] scale = (float[])msg.obj;
                    width = scale[0];
                    height = scale[1];
                    x = width/45;
                    y = height/80;
                    Log.d(TAG,"Width : " + scale[0] + " / " + "Height : " + scale[1]);
                    Log.d(TAG,"X : " + x + " / " + " Y: " + y);
                    invalidate();
                    break;

                case UPDATE_UI :
                    invalidate();
                    break;
            }
        }
    };


    public Handler getViewHandler(){
        return viewHandler;
    }



}
