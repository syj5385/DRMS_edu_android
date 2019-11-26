package com.drms.drmakersystem.Joystick;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by comm on 2018-04-27.
 */

public class Joystick extends LinearLayout {
    private static final String TAG = "Joystick";
    protected Context context;
    protected Activity activity;
    protected Bitmap joystickbmp;
    protected int x, y;
    protected int offsetX, offsetY;
    protected int MinValue, MaxValue;

    protected int width=0, height=0;

    protected ImageView throttle;

    public static final int MODE1 = 10; // return center / only throttle
    public static final int MODE2 = 11; // return center / both throttle and side
    public static final int MODE3 = 12; // Non return 0 / only throttle
    public static final int MODE4 = 13; // Non return 0 / both throttle and side
    public static final int MODE5 = 14; // return center / only side
    public static final int MODE6 = 15; // Non return 0 / only side

    public static final int MODE7 = 16; // External control mode e.g. DRS Controller
    public int JoysticMode = -1;

    public int horizon;
    public int vertical;


    public Joystick(Context context, Activity activity, int MinValue, int MaxValue, Bitmap img) {
        super(context);

        this.context = context;
        this.activity = activity;
        this.joystickbmp = img;
        this.MinValue = MinValue;
        this.MaxValue = MaxValue;
        this.setBackgroundColor(Color.argb(0x2f,00,00,00));
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        setBackgroundColor(Color.YELLOW);
        setOrientation(VERTICAL);
        removeAllViews();


        throttle = new ImageView(context);
        throttle.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(joystickbmp,x,y,null);
        try{
            Thread.sleep(1);
        }catch (InterruptedException e){

        }

        double temp = (double)x / (double)(width - 2*offsetX);
        horizon = (int)((temp * (MaxValue - MinValue) )+ MinValue);
        temp = (double)y / (double)(height - 2*offsetY);
        vertical = ((MaxValue - MinValue) - (int)(temp * (MaxValue - MinValue) ) ) + MinValue;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(JoysticMode==MODE2 || JoysticMode == MODE4 || JoysticMode == MODE5 || JoysticMode == MODE6)
                x = (int)event.getX() - offsetX;

            if(JoysticMode==MODE1 || JoysticMode == MODE2 || JoysticMode == MODE3|| JoysticMode == MODE4)
                y = (int)event.getY() - offsetY;

            checkRangeOfThrottle();
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(JoysticMode==MODE2 || JoysticMode == MODE4 || JoysticMode == MODE5 || JoysticMode == MODE6)
                x = (int)event.getX() - offsetX;

            if(JoysticMode==MODE1 || JoysticMode == MODE2 || JoysticMode == MODE3|| JoysticMode == MODE4)
                y = (int)event.getY() - offsetY;
            checkRangeOfThrottle();
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            if(JoysticMode == MODE1 || JoysticMode == MODE2 || JoysticMode == MODE5) {
                x = width / 2 - offsetX;
                y = height / 2 - offsetY;
            }
        }
        invalidate();
        return true;
    }

    protected void checkRangeOfThrottle(){
        if(x <= 0){
            x = 0;
        }
        if(x >= width - offsetX*2){
            x = width - offsetX*2;
        }
        if(y <= 0){
            y = 0;
        }
        if(y >= height - offsetY*2){
            y =  height - offsetY*2;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //if ((width == 0 || height == 0) && (w>0 && h>0)){
            Log.d(TAG,"Size changed - width : " + w + ", height = " + h);
            width = w;
            height =h ;
            float rate = (float)(width/3)/(float)joystickbmp.getWidth();
            joystickbmp = Bitmap.createScaledBitmap(joystickbmp,width/5,width/5,false);
            offsetX = joystickbmp.getWidth()/2;
            offsetY = joystickbmp.getHeight()/2;
            x = width/2 - offsetX;
            y = height/2 - offsetY;
            if(JoysticMode == MODE3 || JoysticMode== MODE4 || JoysticMode == MODE6){
                y = height - offsetY*2;
            }

            double temp = (double)x / (double)(width - 2*offsetX);
            horizon = (int)((temp * (MaxValue - MinValue) )+ MinValue);
            temp = (double)y / (double)(height - 2*offsetY);
            vertical = ((MaxValue - MinValue) - (int)(temp * (MaxValue - MinValue) ) ) + MinValue;
            Log.d(TAG,"horizon : " + horizon + "\tvertical : " + vertical);
            invalidate();
        //}
    }

    public int getHorizontal(){
        return horizon;
    }

    public int getVertical(){
        return vertical;
    }

    public void setJoysticMode(int MODE){
        this.JoysticMode = MODE;
    }

    public void setX(int x){
        if(JoysticMode == MODE7){
            int inter = MaxValue - MinValue;

            this.x = width/2 - offsetX+ (x - 1500)*(width-offsetX*2)/inter;

            invalidate();
        }



    }

    public void setY_MaxThrottle(int y){
        if(JoysticMode == MODE7) {
            this.y = height/2 - offsetY - (y - 1500)*(height-offsetY*2)/1000;

            invalidate();
        }
    }

    public void setY(int y){
        int inter = MaxValue - MinValue;
        if(JoysticMode == MODE7) {
            this.y = height/2 - offsetY - (y - 1500)*(height-offsetY*2)/inter;

            invalidate();
        }
    }

    public void setMinMax(int minValue, int maxValue){
        this.MinValue = minValue;
        this.MaxValue = maxValue;
    }
}
