package com.drms.drmakersystem.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.drms.drmakersystem.R;

/**
 * Created by comm on 2018-02-27.
 */

public class CustomProgress extends View {

    private static final String TAG = "ProgresS";

    private Context context;
    private Activity activity;

    private Paint non_execuetPaint ;
    private Paint executedPaint;
    private Bitmap progressDrone;
    private int progress;
    private float droneLocation =0 ;

    public CustomProgress(Context context, Activity activity) {
        super(context);
        Log.d(TAG,"Progress Yes");
        this.context = context;
        this.activity = activity;
        non_execuetPaint = new Paint();
        non_execuetPaint.setStrokeWidth(30);
        non_execuetPaint.setColor(Color.GRAY);

        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        executedPaint = new Paint();
        executedPaint.setStrokeWidth(30);
        executedPaint.setColor(activity.getResources().getColor(R.color.upload_color0));

        progressDrone = BitmapFactory.decodeResource(activity.getResources(),R.drawable.upload_image5);
        droneLocation = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG,"progress : " + progress + "w : " + canvas.getWidth() + "@@@ H : "  + canvas.getHeight());

        float progressLength = canvas.getWidth()-(progressDrone.getWidth()*2);
        droneLocation = progressDrone.getWidth()/2 + (progressLength*progress)/100;

//            canvas.drawLine(progressDrone.getWidth()/2,canvas.getHeight()/2, canvas.getWidth()-progressDrone.getWidth()/2,canvas.getHeight()/2,non_execuetPaint);
        canvas.drawLine(progressDrone.getWidth(),canvas.getHeight()/2,droneLocation+progressDrone.getWidth()/2, canvas.getHeight()/2,executedPaint);
        canvas.drawLine(droneLocation+progressDrone.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()-progressDrone.getWidth(),canvas.getHeight()/2,non_execuetPaint);

        // progress -> location

        canvas.drawBitmap(progressDrone,droneLocation ,canvas.getHeight()/2-progressDrone.getHeight()/2,null);

    }

    public void setProgress(int progress){
        this.progress = progress;
        invalidate();
    }
}
