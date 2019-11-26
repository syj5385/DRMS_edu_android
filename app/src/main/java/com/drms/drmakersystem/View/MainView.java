package com.drms.drmakersystem.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.drms.drmakersystem.Activity.EducationAcitivty;
import com.drms.drmakersystem.Communication.Bluetooth.BluetoothService;
import com.drms.drmakersystem.Dialog.MainSettingDialog;
import com.drms.drmakersystem.R;
import java.util.List;

/**
 * Created by comm on 2018-02-20.
 */

public class MainView extends rulerView {

    public static final int REQUEST_CLOSE = 0;


    public static final int REQUEST_CAFE = 0;
    public static final int REQUEST_FLY =1;
    public static final int REQUEST_BLOCK = 2;
    public static final int REQUEST_FLY_MARKET = 3;
    public static final int REQUEST_BLOCK_MARKET = 4;

    public static final int REQUEST_PAIRED_DEIVCE = 10;
    public static final int REQUEST_NEW_DEVICE = 11;
    public static final int TESTING_BT = 12;


    private AlertDialog settingDialog;
    private MainSettingDialog ab;

    private Icon education, web, fly, block, setting;

    public MainView(Context context, Activity activity) {
        super(context,activity);
        this.setBackgroundColor(context.getResources().getColor(R.color.main_color0));
        this.mHandler = mHandler;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap temp;
//        Bitmap temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.main_image0);
//        float scale = temp.getWidth()/(30*x);
//        Bitmap topic = Bitmap.createScaledBitmap(temp,(int)(30*x),(int)(temp.getHeight()/scale),true);
//        canvas.drawBitmap(topic, (float)(22.5*x) - topic.getWidth()/2, (float)(3*y), null);

        temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.main_image1);
        education = new Icon(Bitmap.createScaledBitmap(temp,(int)(41*x),(int)(25*y),true),(float)(2*x),(float)(2*y) );
        canvas.drawBitmap(education.getIcon(),education.getX(),education.getY(),null);

        temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.main_image5);
        setting = new Icon(Bitmap.createScaledBitmap(temp, (int)(41*x),  (int)(12*y),true),2*x, (float)28.5*y);
        canvas.drawBitmap(setting.getIcon(),setting.getX(),setting.getY(),null);

        temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.main_image2);
        web = new Icon(Bitmap.createScaledBitmap(temp, (int)(20*x), (int)(33*y), true),2*x,(float)(42*y) );
        canvas.drawBitmap(web.getIcon(),web.getX(),web.getY(),null);

        temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.main_image3);
        fly = new Icon(Bitmap.createScaledBitmap(temp, (int)(20*x), (int)(15.5*y), true),23*x,42*y );
        canvas.drawBitmap(fly.getIcon(),fly.getX(),fly.getY(),null);


        temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.main_image4);
        block = new Icon(Bitmap.createScaledBitmap(temp, (int)(20*x), (int)(15.5*y), true),(float)(23*x),(float)(59.5*y ));
        canvas.drawBitmap(block.getIcon(),block.getX(),block.getY(),null);

    }

    private boolean[] menuSelection = {false,false,false,false,false};
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "X : " + event.getX() + "\tY : " + event.getY());
            Log.d(TAG, "X : " + fly.getX() + "\tY : " + fly.getY());
            Intent intent;

            if (event.getX() >= education.getX() && event.getX() <= education.getX() + education.getIcon().getWidth()) {
                if (event.getY() >= education.getY() && event.getY() <= education.getY() + education.getIcon().getHeight()) {
                    menuSelection[0] = true;
                }
            }

            if (event.getX() >= setting.getX() && event.getX() <= setting.getX() + setting.getIcon().getWidth()) {
                if (event.getY() >= setting.getY() && event.getY() <= setting.getY() + setting.getIcon().getHeight()) {
                    menuSelection[1] = true;
                }
            }

            if (event.getX() >= web.getX() && event.getX() <= web.getX() + web.getIcon().getWidth()) {
                if (event.getY() >= web.getY() && event.getY() <= web.getY() + web.getIcon().getHeight()) {
                    menuSelection[2] = true;
                }
            } else if (event.getX() >= fly.getX() && event.getX() <= fly.getX() + fly.getIcon().getWidth()) {
                if (event.getY() >= fly.getY() && event.getY() <= fly.getY() + fly.getIcon().getHeight()) {
                    menuSelection[3] = true;
                }

                if (event.getY() >= block.getY() && event.getY() <= block.getY() + block.getIcon().getHeight()) {
                    menuSelection[4] = true;
                }
            }
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            Log.d(TAG,"X : " + event.getX() + "\tY : " + event.getY());
            Log.d(TAG,"X : " + fly.getX() + "\tY : " + fly.getY());
            Intent intent;

            if(event.getX() >= education.getX() && event.getX() <= education.getX() +  education.getIcon().getWidth()){
                if(event.getY() >= education.getY() && event.getY() <= education.getY()+education.getIcon().getHeight()){
                    if(menuSelection[0]) {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drmakersystem.com"));
                        intent = new Intent(context, EducationAcitivty.class);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.fade, R.anim.hold);
                    }
                }
            }

            if(event.getX() >= setting.getX() && event.getX() <= setting.getX() +  setting.getIcon().getWidth()){
                if(event.getY() >= setting.getY() && event.getY() <= setting.getY()+setting.getIcon().getHeight()){
                    if(menuSelection[1]) {
                        ab = new MainSettingDialog(context, activity, mHandler);
                        settingDialog = ab.create();
                        settingDialog.setOnKeyListener(ab.getKeyListener());
                        settingDialog.setOnDismissListener(ab.getDismissListener());
                        settingDialog.show();
                        BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
                        if(!btadapter.isEnabled()){
                            Intent bt_intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            activity.startActivityForResult(bt_intent, BluetoothService.REQUEST_ENABLE_BT);
                        }
                    }
                }
            }

            if(event.getX() >= web.getX() && event.getX() <= web.getX() +  web.getIcon().getWidth()){
                if(event.getY() >= web.getY() && event.getY() <= web.getY()+web.getIcon().getHeight()){
                    if(menuSelection[2]) {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cafe.naver.com/drmakersystem"));
                        activity.startActivityForResult(webIntent, REQUEST_CAFE);
                        activity.overridePendingTransition(R.anim.fade, R.anim.hold);
                    }
                }
            }

            else if(event.getX() >= fly.getX() && event.getX() <= fly.getX() + fly.getIcon().getWidth()){
                if(event.getY() >= fly.getY() && event.getY() <= fly.getY()+fly.getIcon().getHeight()){
                    if(menuSelection[3]) {
                        if (getPackageList(0)) {
                            intent = activity.getPackageManager().getLaunchIntentForPackage("com.drms.drms_drone");
                            activity.startActivityForResult(intent, REQUEST_FLY);

                        } else {
                            String url = "market://details?id=" + "com.drms.drms_drone";
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            activity.startActivityForResult(i, REQUEST_FLY_MARKET);
                        }
                    }
                }

                if(event.getY() >= block.getY() && event.getY() <= block.getY()+block.getIcon().getHeight()){
                    if(menuSelection[4]) {
                        if (getPackageList(1)) {
                            intent = activity.getPackageManager().getLaunchIntentForPackage("jjun.jjunapp.programdrs");
                            activity.startActivityForResult(intent, REQUEST_BLOCK);
//                            activity.startActivity(intent);

                        } else {
                            String url = "market://details?id=" + "jjun.jjunapp.programdrs";
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            activity.startActivityForResult(i, REQUEST_BLOCK_MARKET);
                        }
                    }
                }
            }
            for(int i=0; i<menuSelection.length ; i++)
                menuSelection[i] = false;
        }
        return true;
    }

    public boolean getPackageList(int what){
        boolean isExist = false;

        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN,null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pm.queryIntentActivities(mainIntent,0);

        try{
            for(int i=0 ; i <mApps.size() ; i++){
                if(what == 0) {
                    if (mApps.get(i).activityInfo.packageName.startsWith("jjun.jjunapp.programdrs")) {
                        isExist = true;
                        break;
                    }
                }
                else if (what == 1){
                    if (mApps.get(i).activityInfo.packageName.startsWith("com.drms.drms_drone")) {
                        isExist = true;
                        break;
                    }
                }
            }
        }catch(Exception e){
            isExist = false;
        }
        return isExist;
    }

    public AlertDialog getSettingDialog(){
        return settingDialog;
    }

    public void setSettingDialog(AlertDialog dialog){
        this.settingDialog = dialog;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case REQUEST_CLOSE :
                    settingDialog.dismiss();
                    break;

                case BluetoothService.MESSAGE_STATE_CHANGE :
                    settingDialog.setCancelable(true);
                    ab.BtConnectionTest(msg.arg1);

                    break;
                case TESTING_BT :
                    Toast.makeText(context,"블루투스 테스트 중...\n잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                    settingDialog.setCancelable(false);
                    break;
            }
        }
    };

    public void implementationMainSetting(){
        ab.implementationMainSetting();
    }




}
