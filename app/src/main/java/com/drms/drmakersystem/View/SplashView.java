package com.drms.drmakersystem.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import com.drms.drmakersystem.Activity.MainActivity;
import com.drms.drmakersystem.R;

/**
 * Created by comm on 2018-02-20.
 */

public class SplashView extends rulerView {

    private static final double PI = 3.141592;
    private Paint[] splashPaint = new Paint[2];
    private int alpha = 0;
    private double Theta = 1.5;
    private Path dronePath;
    private boolean running = true;


    public SplashView(Context context, Activity activity) {
        super(context,activity);

        // set Background Image
        this.setBackground(getResources().getDrawable(R.drawable.splash_image0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //step2
        //set Character
        Bitmap character = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.splash_image1),(int)(20*x), (int)(20*x),true);
        canvas.drawBitmap(character,(float)(22.5*x)-character.getWidth()/2, 58*y - character.getHeight(), null);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade, R.anim.hold);
            activity.finish();
        }
        return true;
    }

}