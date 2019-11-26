package com.drms.drmakersystem.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.drms.drmakersystem.Activity.ControllerProto;
import com.drms.drmakersystem.Activity.EducationAcitivty;
import com.drms.drmakersystem.Activity.ExecuteActivity;
import com.drms.drmakersystem.Activity.Level2Controller.Cont2_10Activity;
import com.drms.drmakersystem.Activity.Level2Controller.Cont2_11Activity;
import com.drms.drmakersystem.Activity.Level2Controller.Cont2_12Activity;
import com.drms.drmakersystem.Activity.Level2Controller.Cont2_1Activity;
import com.drms.drmakersystem.Activity.Level2Controller.Cont2_2Activity;
import com.drms.drmakersystem.Activity.Level2Controller.Cont2_3Activity;
import com.drms.drmakersystem.Activity.Level2Controller.Cont2_4Activity;
import com.drms.drmakersystem.Activity.Level2Controller.Cont2_5Activity;
import com.drms.drmakersystem.Activity.Level2Controller.Cont2_7Activity;
import com.drms.drmakersystem.Activity.Level2Controller.Cont2_8Activity;
import com.drms.drmakersystem.Activity.Level2Controller.Cont2_9Activity;
import com.drms.drmakersystem.Communication.Bluetooth.BluetoothService;
import com.drms.drmakersystem.Dialog.MainSettingDialog;
import com.drms.drmakersystem.FileManagement.DownloadManual;
import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.FileManagement.OpenPdfManager;
import com.drms.drmakersystem.R;
import java.util.List;

/**
 * Created by comm on 2018-02-20.
 */

public class ExecuteView extends rulerView {

    public static final int REQUEST_CLOSE = 0;
    public static final int REQUEST_CONTROLLER = 100;


    private Paint[] execPaint = new Paint[2];
    private int level;
    private int week;

    private String[] content ;

    private Icon controller, information;

    private MainSettingDialog ab;
    private AlertDialog settingDialog;


    public ExecuteView(Context context, Activity activity, int level, int week) {
        super(context, activity);
        this.setBackground(context.getResources().getDrawable(R.drawable.exec_image0));
        this.level = level;
        this.week = week;

        initializeContent();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // logo
        Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.exec_image1);
        Bitmap logo = Bitmap.createScaledBitmap(temp, (int) (30 * x), (int) (5 * y), true);
        canvas.drawBitmap(logo, (float) (22.5 * x) - logo.getWidth() / 2, 73 * y, null);


        // icon
        temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.exec_image2);
        controller = new Icon(Bitmap.createScaledBitmap(temp, (int)(10*x), (int)(10*y), true),(float)(6.5*x),(float)(50*y));
        canvas.drawBitmap(controller.getIcon(),controller.getX(),controller.getY(),null);

        temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.exec_image3);
        information = new Icon(Bitmap.createScaledBitmap(temp, (int)(10*x), (int)(10*y), true),(28*x),(float)(50*y));
        canvas.drawBitmap(information.getIcon(),information.getX(),information.getY(),null);

        execPaint[0] = new Paint();
        execPaint[0].setTextSize(3*y);
        execPaint[0].setStrokeWidth(y);
        execPaint[0].setColor(context.getResources().getColor(R.color.exec_color0));
        execPaint[0].setTextAlign(Paint.Align.CENTER);

        if(level == EducationAcitivty.LEVEL1){
            canvas.drawText("Controller", controller.getX() + controller.getIcon().getWidth() / 2, controller.getY() + controller.getIcon().getHeight() * 5 / 4 , execPaint[0]);
            canvas.drawText("사용 방법", controller.getX() + controller.getIcon().getWidth() / 2, controller.getY() + controller.getIcon().getHeight() * 5 / 4 + execPaint[0].getTextSize()*3/2, execPaint[0]);
        }
        else {
            canvas.drawText("Controller", controller.getX() + controller.getIcon().getWidth() / 2, controller.getY() + controller.getIcon().getHeight() * 5 / 4 + execPaint[0].getTextSize(), execPaint[0]);
        }
        canvas.drawText("사용 방법",information.getX()+information.getIcon().getWidth()/2, information.getY()+information.getIcon().getHeight()*5/4+execPaint[0].getTextSize(),execPaint[0]);

        // frame
        execPaint[0].setStrokeWidth(y/3);
        execPaint[0].setStyle(Paint.Style.STROKE);
        canvas.drawRect(5*x, 20*y,40*x, 45*y,execPaint[0]);
        execPaint[0].setColor(Color.WHITE);
        execPaint[0].setStyle(Paint.Style.FILL);
        canvas.drawRect(5*x + execPaint[0].getStrokeWidth()/2, 20*y + execPaint[0].getStrokeWidth()/2,
                40*x - execPaint[0].getStrokeWidth()/2, 45*y-execPaint[0].getStrokeWidth()/2,execPaint[0]);

        setPhoto(canvas);

        //character
        temp = BitmapFactory.decodeResource(context.getResources(),R.drawable.exec_image4);
        Icon character = new Icon(Bitmap.createScaledBitmap(temp,(int)(10*x), (int)(10*y), true),30*x, 20*y - 9*y);
        canvas.drawBitmap(character.getIcon(),character.getX(),character.getY(),null);

        //topic
        execPaint[1] = new Paint();
        execPaint[1].setColor(Color.WHITE);
        execPaint[1].setStrokeWidth(5*y);
        execPaint[1].setTextSize(3*y);

        execPaint[1].setTextAlign(Paint.Align.CENTER);
        if(content != null) {
            String topic = week + "주차 " + content[week - 1];
            canvas.drawText(topic, (5 * x + character.getX()) / 2, character.getY() + character.getIcon().getHeight() / 2, execPaint[1]);
        }
    }

    public void initializeContent(){
        if(level == EducationAcitivty.LEVEL1){
            content = new String[12];
            content[0] = context.getResources().getString(R.string.level1_1);
            content[1] = context.getResources().getString(R.string.level1_2);
            content[2] = context.getResources().getString(R.string.level1_3);
            content[3] = context.getResources().getString(R.string.level1_4);
            content[4] = context.getResources().getString(R.string.level1_5);
            content[5] = context.getResources().getString(R.string.level1_6);
            content[6] = context.getResources().getString(R.string.level1_7);
            content[7] = context.getResources().getString(R.string.level1_8);
            content[8] = context.getResources().getString(R.string.level1_9);
            content[9] = context.getResources().getString(R.string.level1_10);
            content[10] = context.getResources().getString(R.string.level1_11);
            content[11] = context.getResources().getString(R.string.level1_12);
        }

        if(level == EducationAcitivty.LEVEL2){
            content = new String[12];
            content[0] = context.getResources().getString(R.string.level2_1);
            content[1] = context.getResources().getString(R.string.level2_2);
            content[2] = context.getResources().getString(R.string.level2_3);
            content[3] = context.getResources().getString(R.string.level2_4);
            content[4] = context.getResources().getString(R.string.level2_5);
            content[5] = context.getResources().getString(R.string.level2_6);
            content[6] = context.getResources().getString(R.string.level2_7);
            content[7] = context.getResources().getString(R.string.level2_8);
            content[8] = context.getResources().getString(R.string.level2_9);
            content[9] = context.getResources().getString(R.string.level2_10);
            content[10] = context.getResources().getString(R.string.level2_11);
            content[11] = context.getResources().getString(R.string.level2_12);
        }

        if(level == EducationAcitivty.LEVEL3){
            content = new String[10];
            content[0] = context.getResources().getString(R.string.level3_1);
            content[1] = context.getResources().getString(R.string.level3_2);
            content[2] = context.getResources().getString(R.string.level3_3);
            content[3] = context.getResources().getString(R.string.level3_4);
            content[4] = context.getResources().getString(R.string.level3_5);
            content[5] = context.getResources().getString(R.string.level3_6);
            content[6] = context.getResources().getString(R.string.level3_7);
            content[7] = context.getResources().getString(R.string.level3_8);
            content[8] = context.getResources().getString(R.string.level3_9);
            content[9] = context.getResources().getString(R.string.level3_10);
        }
//        try{
//            Thread.sleep(100);
//        }catch (InterruptedException e){
//
//        }
        invalidate();
    }

    private int[] lev2_Photoid = {
            R.drawable.lev2_r1_photo,
            R.drawable.lev2_r2_photo,
            R.drawable.lev2_r3_photo,
            R.drawable.lev2_r4_photo,
            R.drawable.lev2_r5_photo,
            R.drawable.lev2_r6_photo,
            R.drawable.lev2_r7_photo,
            R.drawable.lev2_r8_photo,
            R.drawable.lev2_r9_photo,
            R.drawable.lev2_r10_photo,
            R.drawable.lev2_r11_photo,
            R.drawable.lev2_r12_photo
    };
    private void setPhoto(Canvas canvas){
        Bitmap temp;
        if(level == EducationAcitivty.LEVEL2){
            // photo
//            temp =
            Icon photo = new Icon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),lev2_Photoid[week-1]), (int)(35*x-execPaint[0].getStrokeWidth()),(int)( 25*y - execPaint[0].getStrokeWidth()),true),5*x +execPaint[0].getStrokeWidth()/2,
                    20*y +execPaint[0].getStrokeWidth()/2);
            canvas.drawBitmap(photo.getIcon(),photo.getX(),photo.getY(),null);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            if(event.getX() >= controller.getX() && event.getX() <= controller.getX()+controller.getIcon().getWidth()){
                if(event.getY() >= controller.getY() && event.getY() <= controller.getY()+controller.getIcon().getHeight()){
                    if(level == EducationAcitivty.LEVEL1){
                        FileManagement filemanager = new FileManagement(context,null);
                        if(filemanager.isManualExist(EducationAcitivty.CONTROLLER,0)){
                            Log.d("Execute","exist Manual");
                            new OpenPdfManager(activity,context,EducationAcitivty.CONTROLLER,0);
                        }else{
                            Log.d("Execute","not exist Manual");
                            new DownloadManual(activity,context,null,EducationAcitivty.CONTROLLER,0);
                        }
                    }
                    else {
                        ExecuteController();
                    }
                }
            }

            if(event.getX() >= information.getX() && event.getX() <= information.getX()+information.getIcon().getWidth()){
                if(event.getY() >= information.getY() && event.getY() <= information.getY()+information.getIcon().getHeight()) {
                    Log.d("Execute","open Manual");
                    FileManagement filemanager = new FileManagement(context,null);
                    if(filemanager.isManualExist(level,week)){
                        Log.d("Execute","exist Manual");
                        new OpenPdfManager(activity,context,level,week);
                    }else{
                        Log.d("Execute","not exist Manual");
                        new DownloadManual(activity,context,null,level,week);
                    }

                }
            }
        }
        return true;
    }

    private void ExecuteController(){
        String address = new FileManagement(context).readBTAddress()[1];
        if(address == "" || address == null){
            Toast.makeText(context,"블루투스 장치가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
            ab = new MainSettingDialog(context,activity, settingHandler){

                @Override
                public void setDismissListener() {
                    super.setDismissListener();
                    ab.setOnDismissListener(dismissListener);
                }

                private DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(isDialogOpen == FIND_DEVICE){
                            btAdapter.cancelDiscovery();
                        }
                        else if(isDialogOpen == MAIN_SETTING){
                            String address = new FileManagement(context).readBTAddress()[1];
                            if(address != ""){
                                checkLevelController();
                            }
                        }
                    }
                };

            };

            ab.setDismissListener();
            settingDialog = ab.create();
            settingDialog.show();
        }
        else{
            checkLevelController();
        }
    }

    private void checkLevelController(){
        Intent intent;
        Log.d(TAG,"level in Execute : " + level);
        if(level == EducationAcitivty.LEVEL2){
            switch(week){
                case 1 :
                    intent = new Intent(context, Cont2_1Activity.class);
                    break;

                case 2 :
                    intent = new Intent(context, Cont2_2Activity.class);
                    break;

                case 3 :
                    intent = new Intent(context, Cont2_3Activity.class);
                    break;

                 case 4 :
                     intent = new Intent(context, Cont2_4Activity.class);
                     break;

                 case 5 :
                     intent = new Intent(context, Cont2_5Activity.class);
                     break;

                case 6 :
                    if(getPackageList()){
                        intent = activity.getPackageManager().getLaunchIntentForPackage("com.drms.drms_drone");
                        activity.startActivityForResult(intent,MainView.REQUEST_FLY);

                    }
                    else{
                        String url = "market://details?id=" + "com.drms.drms_drone";
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        activity.startActivityForResult(intent,MainView.REQUEST_FLY_MARKET);
                    }
                    break;

                case 7 :
                    intent = new Intent(context, Cont2_7Activity.class);
                    break;

                case 8 :
                    intent = new Intent(context, Cont2_8Activity.class);
                    break;

                case 9 :
                    intent = new Intent(context, Cont2_9Activity.class);
                    break;

                case 10 :
                    intent = new Intent(context, Cont2_10Activity.class);
                    break;

                case 11 :
                    intent = new Intent(context, Cont2_11Activity.class);
                    break;

                case 12 :
                    intent = new Intent(context, Cont2_12Activity.class);
                    break;

                default :
                    intent = new Intent(context,ControllerProto.class);
            }
            activity.startActivityForResult(intent, ExecuteActivity.REQUEST_CONTROLLER);

        }
        if(level == EducationAcitivty.LEVEL3){
            switch(week){
                case 1 :
                    intent = new Intent(context, Cont3_1Activity.class);
                    break;

//                case 2 :
//                    intent = new Intent(context, Cont2_2Activity.class);
//                    break;
//
                case 3 :
                    intent = new Intent(context, Cont3_3Activity.class);
                    break;

                case 4 :
                    intent = new Intent(context, Cont3_4Activity.class);
                    break;

                case 5 :
                    intent = new Intent(context, Cont3_5Activity.class);
                    break;

//                case 6 :
//                    if(getPackageList()){
//                        intent = activity.getPackageManager().getLaunchIntentForPackage("com.drms.drms_drone");
//                        activity.startActivityForResult(intent,MainView.REQUEST_FLY);
//
//                    }
//                    else{
//                        String url = "market://details?id=" + "com.drms.drms_drone";
//                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        activity.startActivityForResult(intent,MainView.REQUEST_FLY_MARKET);
//                    }
//                    break;
//
//                case 7 :
//                    intent = new Intent(context, Cont2_7Activity.class);
//                    break;
//
                case 7 :
                    intent = new Intent(context, Cont3_8Activity.class);
                    break;

                case 9 :
                    intent = new Intent(context, Cont3_10Activity.class);
                    break;

//                case 10 :
//                    intent = new Intent(context, Cont2_10Activity.class);
//                    break;

                default :
                    intent = new Intent(context,ControllerProto.class);
            }
            activity.startActivityForResult(intent, ExecuteActivity.REQUEST_CONTROLLER);
        }
    }
    private Handler settingHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case REQUEST_CLOSE :
                    Log.d(TAG,"Request close Dialog");
                    settingDialog.dismiss();
                    break;

                case REQUEST_CONTROLLER :
                    settingDialog.dismiss();
                    break;

                case BluetoothService.MESSAGE_STATE_CHANGE :
                    ab.BtConnectionTest(msg.arg1);

                    break;
            }
        }
    };

    public boolean getPackageList(){
        boolean isExist = false;

        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN,null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pm.queryIntentActivities(mainIntent,0);

        try{
            for(int i=0 ; i <mApps.size() ; i++){
                if(mApps.get(i).activityInfo.packageName.startsWith("com.drms.drms_drone")){
                    isExist = true;
                    break;
                }
            }
        }catch(Exception e){
            isExist = false;
        }
        return isExist;
    }

}
