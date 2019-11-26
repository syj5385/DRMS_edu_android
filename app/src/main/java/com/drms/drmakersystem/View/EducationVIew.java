package com.drms.drmakersystem.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.widget.Toast;

import com.drms.drmakersystem.Activity.EducationAcitivty;
import com.drms.drmakersystem.Activity.Level3Controller.Level3_Activity;
import com.drms.drmakersystem.Activity.Level3Controller.Level3_Drs_Activity;
import com.drms.drmakersystem.Activity.SelectionActivity;
import com.drms.drmakersystem.R;
/**
 * Created by comm on 2018-02-20.
 */

public class EducationVIew extends rulerView {


    public EducationVIew(Context context, Activity activity) {
        super(context, activity);
        this.setBackgroundColor(context.getResources().getColor(R.color.main_color0));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Bitmap temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.edu_image0);
        Bitmap topic = Bitmap.createScaledBitmap(temp, (int)(30*x), (int)(6*y), true);
        canvas.drawBitmap(topic,(float)(22.5*x) - topic.getWidth()/2, 6*y, null);

        temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.edu_image1);
        Bitmap level1 = Bitmap.createScaledBitmap(temp, (int)(41*x), (int)(12*y), true);
        canvas.drawBitmap(level1,2*x, 17*y, null);

        temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.edu_image2);
        Bitmap level2 = Bitmap.createScaledBitmap(temp, (int)(41*x), (int)(12*y), true);
        canvas.drawBitmap(level2,2*x, 32*y, null);

        temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.edu_image3);
        Bitmap level3 = Bitmap.createScaledBitmap(temp, (int)(41*x), (int)(12*y), true);
        canvas.drawBitmap(level3,2*x, 47*y, null);

        temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.edu_image4);
        Bitmap level4 = Bitmap.createScaledBitmap(temp, (int)(41*x), (int)(12*y), true);
        canvas.drawBitmap(level4,2*x, 62*y, null);

    }

    private boolean[] selection = {false, false, false, false ,false};

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            Intent intent = new Intent(context, SelectionActivity.class);
            if(event.getX() >= 2*x && event.getX() <= 43*x){
                if(event.getY() >= 17*y && event.getY() <= 29*y)
                    selection[0] = true;

                else if(event.getY() >= 32*y && event.getY() <= 44*y)
                    selection[1] = true;

                else if(event.getY() >= 47*y && event.getY() <= 59*y)
                    selection[2] = true;

                else if(event.getY() >= 62*y && event.getY() <= 74*y)
                    selection[3] = true;

                else
                    return false;


            }
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            Intent intent = new Intent(context, SelectionActivity.class);
            if(event.getX() >= 2*x && event.getX() <= 43*x){
                if(event.getY() >= 17*y && event.getY() <= 29*y) {
                    if (selection[0]) {
                        intent = new Intent(context, SelectionActivity.class);
                        intent.putExtra(EducationAcitivty.LEVEL, EducationAcitivty.LEVEL1);
                    }
                }

                else if(event.getY() >= 32*y && event.getY() <= 44*y) {
                    if(selection[1]) {
                        intent = new Intent(context, SelectionActivity.class);
                        intent.putExtra(EducationAcitivty.LEVEL, EducationAcitivty.LEVEL2);
                    }
                }

                else if(event.getY() >= 47*y && event.getY() <= 59*y) {
                    if(selection[2]) {
                        intent = new Intent(context, Level3_Drs_Activity.class);
                        intent.putExtra(EducationAcitivty.LEVEL, EducationAcitivty.LEVEL3);
                    }
                }

                else if(event.getY() >= 62*y && event.getY() <= 74*y){
                    if(selection[3]) {
                        intent = new Intent(context, SelectionActivity.class);
                        Toast.makeText(context, "준비 중...", Toast.LENGTH_SHORT).show();
                        return true;
                    }
//                        intent.putExtra(EducationAcitivty.LEVEL,EducationAcitivty.LEVEL4);
                }
                else
                    return false;

                for(int i=0; i<selection.length; i++){
                    selection[i] = false;
                }
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade, R.anim.hold);
                activity.finish();
            }
        }
        return true;
    }
}
