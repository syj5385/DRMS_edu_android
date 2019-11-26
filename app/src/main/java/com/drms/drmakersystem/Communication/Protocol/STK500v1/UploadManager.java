package com.drms.drmakersystem.Communication.Protocol.STK500v1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.Communication.Bluetooth.BluetoothService;
import com.drms.drmakersystem.Communication.UsbSerial.UsbService;
import com.drms.drmakersystem.FileManagement.DownloadFirmware;
import com.drms.drmakersystem.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by jjunj on 2017-10-25.
 */

public class UploadManager {

    // Macro
    private static final String TAG = "UplaodManager";
    public static final boolean D = true;

    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CONNECT_DEVICE = 1;

    private static final int MESSAGE_STATE_CHANGE = 7;
    private static final int MESSAGE_WRITE = 2;
    private static final int MESSAGE_READ = 3;
    private static final int UPDATE_STATE = 4;

    private static final int ORIENT_COMMAND = 20;
    private static final int RECEIVED_MESSAGE = 21;
    private static final int UPLOAD_PROGRESS = 22;
    private static final int UPLOAD_STATE = 23;
    public static final int DOWNLOAD_FIRM = 24;

    public static final int DOWNLOAD_SUCCSS = 0;
    public static final int DOWNLOAD_FAILD = 1;
    public static final int NETWORK_DISCONNECTED = 2;

    public static final int UPLOAD_FAILED = -1;
    public static final int UPLOAD_SUCCESS = 1;
    public static final int UPLOAD_START = 3;
    public static final int UPLOAD_END =4;

    public static final String USB_UPLOAD_RECEIVE = "com.drms.drmakersystem.USB>UPLOAD_RECEIVE";

    private byte orient_command;
    private boolean isUploading = false;
    private boolean uploadSuccess = false;
    private int stateOnIndex = 0;
    private boolean isInitializing = false;
    private boolean startUpload  = true;

    private Activity mActivity;
    private ArrayList<Integer> command_data;
    private String bt_address;
    private int request ;

    public AlertDialog.Builder upload_dialog;
    protected AlertDialog dialog;

    //Object
    private BluetoothService mBluetoothService;
    private UsbService service;
    private StringBuffer mOutStringBuffer;

    protected STK500v1 stk500;

    // View
    private ImageView[] state = new ImageView[3];
    private ImageView start, end;
    private TextView information;
    private LinearLayout progresslayout ;
    private ProgressBar progressBar;

    // upload data
    private String hexData = null;

    // Bitmap(arrow)
    private Bitmap arrowOn;
    private Bitmap arrowOff;
    private Bitmap notConnected;
    private Bitmap start_img,end_img;
//    private BitmapDrawable arrowOnDrawable;
//    private BitmapDrawable arrowOffDrawable;
//    private BitmapDrawable notConnectedDrawable;

    private Bitmap uploading1, uploading2, uploading3;

    protected Handler BackUpHandller;


    public UploadManager(Activity mActivity, BluetoothService mBluetoothService, String bt_address, int request) {

        this.mActivity = mActivity;
        this.bt_address = bt_address;
        this.mBluetoothService = mBluetoothService;
//        mBluetoothService = new BluetoothService(mActivity,uploadManagerHandler,"STK");
        BackUpHandller = mBluetoothService.getmHandler();
        this.mBluetoothService.setProtocol("STK");
        this.mBluetoothService.setmHandler(uploadManagerHandler);
        this.request = request;


        initializeUploadView();
    }

    public UploadManager(Activity mActivity , UsbService service, int request){
        this.mActivity = mActivity;
        this.service = service;
//        mBluetoothService = new BluetoothService(mActivity,uploadManagerHandler,"STK");
//        BackUpHandller = mBluetoothService.getmHandler();
//        this.mBluetoothService.setProtocol("STK");
//        this.mBluetoothService.setmHandler(uploadManagerHandler);
        mActivity.registerReceiver(UploadReceiver, new IntentFilter(USB_UPLOAD_RECEIVE));
        this.request = request;

        initializeUploadView();
    }

    private void initializeUploadView(){
        Paint arrowOnPaint = new Paint();
        arrowOnPaint.setColor(mActivity.getResources().getColor(R.color.upload_color1));
        arrowOnPaint.setStrokeWidth(20);

        Paint arrowOffPaint = new Paint();
        arrowOffPaint.setColor(Color.GRAY);
        arrowOffPaint.setStrokeWidth(20);

        Paint notConnectedPaint = new Paint();
        notConnectedPaint.setColor(Color.BLACK);
        notConnectedPaint.setStrokeWidth(20);

        Bitmap temp = BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.upload_image1);
//        arrowOn = Bitmap.createBitmap(temp.getWidth()/2,temp.getHeight()/2, Bitmap.Config.ARGB_8888);
//        Canvas arrowOnCanvas = new Canvas(arrowOn);
//        arrowOnCanvas.drawLine(0,arrowOnCanvas.getHeight()/2, arrowOnCanvas.getWidth()-arrowOnPaint.getStrokeWidth()/5,arrowOnCanvas.getHeight()/2,arrowOnPaint);
//        arrowOnCanvas.drawLine(arrowOnCanvas.getWidth()/2,arrowOnPaint.getStrokeWidth()/2,arrowOnCanvas.getWidth()-arrowOnPaint.getStrokeWidth()/2,arrowOnCanvas.getHeight()/2,arrowOnPaint);
//        arrowOnCanvas.drawLine(arrowOnCanvas.getWidth()/2,arrowOnCanvas.getHeight()-arrowOnPaint.getStrokeWidth()/2,arrowOnCanvas.getWidth()-arrowOnPaint.getStrokeWidth()/2,arrowOnCanvas.getHeight()/2,arrowOnPaint);
//        arrowOnDrawable = new BitmapDrawable(mActivity.getResources(),arrowOn);

//        arrowOff = Bitmap.createBitmap(temp.getWidth()/2,temp.getHeight()/2, Bitmap.Config.ARGB_8888);
//        Canvas arrowOffCanvas = new Canvas(arrowOff);
//        arrowOffCanvas.drawLine(0,arrowOffCanvas.getHeight()/2, arrowOffCanvas.getWidth()-arrowOffPaint.getStrokeWidth()/5,arrowOffCanvas.getHeight()/2,arrowOffPaint);
//        arrowOffCanvas.drawLine(arrowOffCanvas.getWidth()/2,arrowOffPaint.getStrokeWidth()/2,arrowOffCanvas.getWidth()-arrowOffPaint.getStrokeWidth()/2,arrowOffCanvas.getHeight()/2,arrowOffPaint);
//        arrowOffCanvas.drawLine(arrowOffCanvas.getWidth()/2,arrowOffCanvas.getHeight()-arrowOffPaint.getStrokeWidth()/2,arrowOffCanvas.getWidth()-arrowOffPaint.getStrokeWidth()/2,arrowOffCanvas.getHeight()/2,arrowOffPaint);
//        arrowOffDrawable = new BitmapDrawable(mActivity.getResources(),arrowOff);
//
//        notConnected = Bitmap.createBitmap(temp.getWidth()/2,temp.getHeight()/2, Bitmap.Config.ARGB_8888);
//        Canvas notConnectedCanvas = new Canvas(notConnected);
//        notConnectedCanvas.drawLine(0,0,notConnectedCanvas.getWidth(),notConnectedCanvas.getHeight(),notConnectedPaint);
//        notConnectedCanvas.drawLine(notConnectedCanvas.getWidth(),0,0,notConnectedCanvas.getHeight(),notConnectedPaint);
        Bitmap cancel = BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.upload_image0);
        Bitmap can = Bitmap.createScaledBitmap(cancel,temp.getWidth(),temp.getHeight(),true);
//        notConnectedDrawable = new BitmapDrawable(can);

        Bitmap arrowRight = BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.upload_image1);
        Bitmap arrowRIght_on = BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.upload_image2);

        Bitmap off = Bitmap.createScaledBitmap(arrowRight,temp.getWidth(),temp.getHeight(),true);
        Bitmap on = Bitmap.createScaledBitmap(arrowRIght_on, temp.getWidth(),temp.getHeight(),true);

        uploading1 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.upload_image7),50, 50,true);
        uploading2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.upload_image8),100, 100,true);
        uploading3 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.upload_image9),50, 50,true);

        start_img = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.upload_image3),100, 100,true);
        end_img = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.upload_image4),100, 100,true);


//        arrowOnDrawable = new BitmapDrawable(on);
//        arrowOffDrawable = new BitmapDrawable(off);


//        hexData = makeHexStringFromHexFile();

        requestUploadDialog();

        if(mBluetoothService == null && service != null) {
            stk500 = new STK500v1(mActivity, service, uploadManagerHandler, request);
        }
        else if(mBluetoothService != null && service == null){
            stk500 = new STK500v1(mActivity, this.mBluetoothService, uploadManagerHandler, request);
        }


//        Log.d(TAG, String.valueOf(mBluetoothService.getState()));
        Log.d(TAG,"Firm ware \n " + hexData);
    }

    public void requestUploadDialog(){
        upload_dialog = new AlertDialog.Builder(mActivity);
        mActivity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        );

        ConstraintLayout upload_view = (ConstraintLayout) View.inflate(mActivity, R.layout.activity_firmware,null);
        upload_dialog.setCancelable(true);
//        upload_dialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if(!isUploading) {
//                    dialogInterface.dismiss();
//                }
//                else{
//                    Toast.makeText(mActivity,"업로드 중에는 종료할 수 없습니다.", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });

        state[0] = (ImageView)upload_view.findViewById(R.id.arrow0);
        state[1] = (ImageView)upload_view.findViewById(R.id.arrow1);
        state[2] = (ImageView)upload_view.findViewById(R.id.arrow2);

        state[0].setImageDrawable(null);
        state[1].setImageDrawable(new BitmapDrawable(uploading2));
        state[2].setImageDrawable(null);
        start = (ImageView)upload_view.findViewById(R.id.start);
        end = (ImageView)upload_view.findViewById(R.id.end);
        start.setImageDrawable(new BitmapDrawable(start_img));
        end.setImageDrawable(new BitmapDrawable(end_img));

        information = (TextView)upload_view.findViewById(R.id.information);
        information.setText("펌웨어를 다운로드 중입니다.");

        progresslayout = (LinearLayout)upload_view.findViewById(R.id.progress_layout);
        progressBar = (ProgressBar)upload_view.findViewById(R.id.progressBar);
//        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        progresslayout.addView(progressBar);
        progressBar.invalidate();

//        upload_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                if(stk500 != null){
//                    stk500.running = false;
//                    stk500 = null;
//                }
////                mBluetoothService.stop();
//                mBluetoothService.setmHandler(BackUpHandller);
//                mBluetoothService.setProtocol("DRS");
////                Log.d(TAG,"set Protocol as DRS");
//
//
//
////                mActivity.setResult(1);
////                mActivity.finish();
//
//            }
//        });
        upload_dialog.setView(upload_view);
        dialog = upload_dialog.create();
//        requestConnectClassicBT();
//        Log.d(TAG,"Bluetooth connection : " + mBluetoothService.getState());
        dialog.show();

        DownloadFirmware firm = new DownloadFirmware(request,uploadManagerHandler);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(startUpload){
                    try {
                        Thread.sleep(100);
                    }catch (InterruptedException e){}
                }
                uploadManagerHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });
                    }
                },2000);
            }
        }).start();


    }


    private boolean requestUpload(){
        Log.d(TAG,"Start Upload");
        boolean success = true;
        if(mBluetoothService != null) {
            if (mBluetoothService.getState() == BluetoothService.STATE_CONNECTED) {
//            displayStateOfUpload();
                information.setText("리셋 버튼을 눌러주세요.");


                // make binaryFile
//                if (stk500 == null) {
                    Log.d(TAG,"make stk500 instance");

//                }

                final byte[] binaryFile = new byte[hexData.length() / 2];
                for (int i = 0; i < hexData.length(); i += 2) {
//                    Log.d(TAG,"i : " + i);
                        binaryFile[i / 2] = Integer.decode("0x" + hexData.substring(i, i + 2)).byteValue();
                }
                int UnitOfByte = 256;

                // execute Uploading
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(stk500 != null) {
                            uploadSuccess = stk500.programHexFile(binaryFile, 256, true);
                            if (uploadSuccess) {
                                Log.d(TAG, "Uploading Successful");
                                isUploading = false;
                                uploadManagerHandler.obtainMessage(UPLOAD_STATE, 1, -1).sendToTarget();
                            } else {
                                Log.e(TAG, "Failed to Uploading");
                            }
                        }
                        else{
                            Log.e(TAG,"STK500 instance is null");
                        }
                    }
                }).start();

            } else {
                success = false;
                Log.e(TAG, "bluetooth communication is not connected");
                isUploading = false;
//                for (int i = 0; i < state.length; i++)
//                    state[i].setImageDrawable(notConnectedDrawable);
                state[0].setImageDrawable(null);
                state[1].setImageDrawable(new BitmapDrawable(uploading2));
                state[2].setImageDrawable(null);

                information.setTextColor(Color.argb(255, 255, 61, 95));
                information.setText("블루투스 연결이 끊어졌습니다.");
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){}
                dialog.dismiss();
                return success;
            }
        }
        if(service != null) {
            if (service.isAttached()) {
//            displayStateOfUpload();
                information.setText("리셋 버튼을 눌러주세요.");


                // make binaryFile
//                if (stk500 == null) {
                Log.d(TAG,"make stk500 instance");

//                }

                final byte[] binaryFile = new byte[hexData.length() / 2];
                for (int i = 0; i < hexData.length(); i += 2) {
//                    Log.d(TAG,"i : " + i);
                    binaryFile[i / 2] = Integer.decode("0x" + hexData.substring(i, i + 2)).byteValue();
                }
                int UnitOfByte = 256;

                // execute Uploading
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(stk500 != null) {
                            uploadSuccess = stk500.programHexFile(binaryFile, 256, true);
                            if (uploadSuccess) {
                                Log.d(TAG, "Uploading Successful");
                                isUploading = false;
                                uploadManagerHandler.obtainMessage(UPLOAD_STATE, 1, -1).sendToTarget();
                            } else {
                                Log.e(TAG, "Failed to Uploading");
                            }
                        }
                        else{
                            Log.e(TAG,"STK500 instance is null");
                        }
                    }
                }).start();

            } else {
                success = false;
                Log.e(TAG, "bluetooth communication is not connected");
                isUploading = false;
//                for (int i = 0; i < state.length; i++)
//                    state[i].setImageDrawable(notConnectedDrawable);
                state[0].setImageDrawable(null);
                state[1].setImageDrawable(new BitmapDrawable(uploading2));
                state[2].setImageDrawable(null);

                information.setTextColor(Color.argb(255, 255, 61, 95));
                information.setText("USB 연결이 끊어졌습니다.");
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){}
                dialog.dismiss();
                return success;
            }
        }
        else {
            success =false;
        }
        return success;
    }

    private void displayStateOfUpload(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isUploading){
                    uploadManagerHandler.obtainMessage(UPDATE_STATE,stateOnIndex++,-1).sendToTarget();
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e){};
                    if(stateOnIndex > 2)
                        stateOnIndex = 0;

                }
            }
        }).start();
    }

    private boolean init_state = false;
    private void display_initializing(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isInitializing){
                    try{
                        Thread.sleep(200);
                    }catch (InterruptedException e){};
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(init_state){
                                init_state = false;
                                information.setVisibility(View.VISIBLE);
//                                state[0].setImageDrawable(new BitmapDrawable(uploading1));
//                                state[1].setImageDrawable(new BitmapDrawable(uploading2));
//                                state[2].setImageDrawable(null);
                            }
                            else{
                                init_state = true;
                                information.setVisibility(View.INVISIBLE);
//                                state[0].setImageDrawable(new BitmapDrawable(uploading1));
//                                state[1].setImageDrawable(new BitmapDrawable(uploading2));
//                                state[2].setImageDrawable(null);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private int BT_Connecting_tries = 0;
    public Handler getUploadManagerHandler(){
        return uploadManagerHandler;
    }

    private Handler uploadManagerHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE:" + msg.arg1);
                    switch (msg.arg1) {

                        case BluetoothService.STATE_CONNECTED:
                            Log.d(TAG,"Bluetooth Connection OK");
                            uploadManagerHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    requestUpload();
                                }
                            },500);
                            break;

                        case BluetoothService.STATE_CONNECTING:
//                            Toast.makeText(getApplicationContext(), "연결중....", Toast.LENGTH_LONG).show();
                            break;


                        case BluetoothService.STATE_DISCONNECTED :
                            Log.e(TAG,"bluetooth disconnected");
                            if(!uploadSuccess) {
                                if(stk500 != null)
                                    stk500.running = false;
                                isUploading = false;
                                information.setTextColor(Color.argb(255, 255, 61, 95));
                                information.setText("블루투스 연결이 끊어져 종료합니다.");
//                                state[0].setImageDrawable(null);
//                                state[0].setImageDrawable(null);
                                state[1].setImageDrawable(new BitmapDrawable(uploading2));
                                uploadManagerHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                },2000);
                            }

                            break;
                    }
                    break;

                case BluetoothService.MESSAGE_READ :
                    byte[] data = (byte[])msg.obj;
                    Log.d(TAG,"stk500 : " + String.valueOf(stk500));
                    Log.d(TAG,"stkHandler : " + String.valueOf(stk500.getStk500_handler()));
                    stk500.getStk500_handler().obtainMessage(RECEIVED_MESSAGE,(int)orient_command,-1,data).sendToTarget();
                    int index = 0;

                    break;

                case ORIENT_COMMAND :
                    orient_command = (byte)msg.arg1;
                    break;


                case UPLOAD_STATE :
                    switch(msg.arg1){
                        case UPLOAD_SUCCESS:     // Finished Uploading Firmware
                            isUploading = false;
                            startUpload = false;
                            try {
                                Thread.sleep(500);
                            }catch (InterruptedException e){}
                            stk500.running = false;
                            information.setText("업로드가 완료 되었습니다.");
                            state[0].setImageDrawable(null);
                            state[1].setImageDrawable(new BitmapDrawable(uploading2));
                            state[2].setImageDrawable(null);
                            break;

                        case UPLOAD_FAILED:    // Failed to Upload Firmware

                            isUploading = false;

                            break;

                        case 2 :    // Timeout to press Arduino Reset btn
                            Log.d(TAG,"Try Again to Upload");
                            requestUpload();
                            break;

                        case UPLOAD_START :
                            dialog.setCancelable(false);
//                            information.setTextColor(mActivity.getResources().getColor(R.color.upload_color2));
                            information.setText("initializing...");
                            isInitializing = true;
                            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                    if(keyEvent.getAction() == MotionEvent.ACTION_UP){
                                        if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK){
                                            Toast.makeText(mActivity,"업로드 중에는 종료할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    return true;
                                }
                            });
                            display_initializing();
                            break;

                        case UPLOAD_END :
                            information.setText("업로드 종료 요청 중입니다.");
                            break;

                        default :
                            if(mBluetoothService != null) {
                                if (mBluetoothService.getState() == BluetoothService.STATE_CONNECTED) {
                                    if (information.getVisibility() == View.INVISIBLE) {
                                        information.setVisibility(View.VISIBLE);
                                    }
                                    if (!isUploading) {
                                        isInitializing = false;
                                        isUploading = true;
                                        displayStateOfUpload();
                                    }
                                    int progress = msg.arg1 * 100 / msg.arg2;
                                    float current_byte = (float) msg.arg1 / 1000;
                                    float finished_byte = (float) msg.arg2 / 1000;

                                    information.setText("업로드 중... ( " + progress + " % 완료 )");
                                    progressBar.setProgress(progress);
                                    progressBar.invalidate();
                                }
                            }
                            if(service != null){
                                if(service.isAttached()){
                                    if (information.getVisibility() == View.INVISIBLE) {
                                        information.setVisibility(View.VISIBLE);
                                    }
                                    if (!isUploading) {
                                        isInitializing = false;
                                        isUploading = true;
                                        displayStateOfUpload();
                                    }
                                    int progress = msg.arg1 * 100 / msg.arg2;
                                    float current_byte = (float) msg.arg1 / 1000;
                                    float finished_byte = (float) msg.arg2 / 1000;

                                    information.setText("업로드 중... ( " + progress + " % 완료 )");
                                    progressBar.setProgress(progress);
                                    progressBar.invalidate();
                                }
                            }


                            break;
                    }
                    break;

                case UPDATE_STATE :
//                    Log.d(TAG,"state on : " + msg.arg1);
//                    for(int i=0; i<state.length ;i++)
//                        state[i].setImageDrawable(arrowOffDrawable);
//
//                    state[msg.arg1].setImageDrawable(arrowOnDrawable);
                    switch(msg.arg1){
                        case 0 :
                            state[0].setImageDrawable(new BitmapDrawable(uploading1));
                            state[1].setImageDrawable(new BitmapDrawable(uploading2));
                            state[2].setImageDrawable(null);
                            break;

                        case 1 :
                            state[0].setImageDrawable(null);
                            state[1].setImageDrawable(new BitmapDrawable(uploading3));
                            state[2].setImageDrawable(null);
                            break;

                        case 2 :
                            state[0].setImageDrawable(null);
                            state[1].setImageDrawable(new BitmapDrawable(uploading2));
                            state[2].setImageDrawable(new BitmapDrawable(uploading1));
                            break;
                    }
                    break;

                case DOWNLOAD_FIRM  :
                    switch(msg.arg1){
                        case DOWNLOAD_SUCCSS :
                            Log.d(TAG,"Success to download file");
                            information.setText("firmware 다운로드가 완료되었습니다.\n " + getLevelName(msg.arg2));
                            hexData = (String)msg.obj;
                            hexData = replaceHexData(hexData);
                            Log.d(TAG,"hex : \n" + hexData);
                            uploadManagerHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    information.setText("업로드를 준비 중입니다.");
                                    requestUpload();
                                }
                            },1000);
                            break;

                        case DOWNLOAD_FAILD :

                            information.setText("펌웨어 다운로드를 실패하였습니다.");
                            startUpload = false;
                            break;

                        case NETWORK_DISCONNECTED :
                            information.setText("펌웨어 다운로드를 실패하였습니다.\n네트워크 상태를 확인해주세요");
                            startUpload = false;
                            break;
                    }
            }
        }
    };




    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String getLevelName(int levelround){
        switch (request){
            case DRMS.LEV2_1 :
                return "Level2 1주차";

            case DRMS.LEV2_2 :
                return "Level2 2주차";

            case DRMS.LEV2_3 :
                return "Level2 3주차";

            case DRMS.LEV2_4 :
                return "Level2 4주차";

            case DRMS.LEV2_5 :
                return "Level2 5주차";

            case DRMS.LEV2_6 :
                return "Level2 6주차";

            case DRMS.LEV2_7 :
                return "Level2 7주차";

            case DRMS.LEV2_8 :
                return "Level2 8주차";

            case DRMS.LEV2_9 :
                return "Level2 9주차";

            case DRMS.LEV2_10 :
                return "Level2 10주차";

            case DRMS.LEV2_11 :
                return "Level2 11주차";

            case DRMS.LEV2_12 :
                return "Level2 12주차";


            case DRMS.LEV3_2 :
                return "Level3 2주차";

            case DRMS.LEV3_4 :
                return "Level3 4주차";


            case DRMS.LEV3_6 :
                return "Level3 6주차";


            case DRMS.LEV3_8 :
                return "Level3 8주차";


            case DRMS.LEV3_10 :
                return "Level3 10주차";

            case DRMS.LEV3_DRONE:
                return "Level3 QUAD DRONE";

            case DRMS.LEV3_HEX:
                return "Level3 HEX DRONE";

            case DRMS.CONTROLLER :
                return "DRMS 조종기";

            default :
                return null;
        }
    }

    // make upload File

    private String makeHexStringFromHexFile(){
        InputStream inputStream = null;
        switch (request){
            case DRMS.LEV2_1 :
                inputStream = mActivity.getResources().openRawResource(R.raw.lev2_r10);
                break;

            case DRMS.LEV2_2 :
                inputStream = mActivity.getResources().openRawResource(R.raw.lev2_r10);
                break;

            case DRMS.LEV2_3 :
                inputStream = mActivity.getResources().openRawResource(R.raw.lev2_r10);
                break;

            case DRMS.LEV2_4 :
                inputStream = mActivity.getResources().openRawResource(R.raw.lev2_r10);
                break;

            case DRMS.LEV2_5 :
                inputStream = mActivity.getResources().openRawResource(R.raw.lev2_r10);
                break;

            case DRMS.LEV2_6 :
                inputStream = mActivity.getResources().openRawResource(R.raw.lev2_r10);
                break;

            case DRMS.LEV2_7 :
                inputStream = mActivity.getResources().openRawResource(R.raw.lev2_r10);
                break;

            case DRMS.LEV2_8 :
                inputStream = mActivity.getResources().openRawResource(R.raw.lev2_r10);
                break;

            case DRMS.LEV2_9 :
                inputStream = mActivity.getResources().openRawResource(R.raw.lev2_r10);
                break;

            case DRMS.LEV2_10 :
                inputStream = mActivity.getResources().openRawResource(R.raw.lev2_r10);
                break;

            case DRMS.LEV2_11 :
                inputStream = mActivity.getResources().openRawResource(R.raw.lev2_r10);
                break;

            case DRMS.LEV2_12 :
                inputStream = mActivity.getResources().openRawResource(R.raw.lev2_r10);
                break;

        }


        StringBuffer buffer = new StringBuffer();
        byte[] b = new byte[1024];

        try {
            for (int n; (n =inputStream.read(b)) != -1; ) {
                buffer.append(new String(b, 0, n));
            }
        }catch (IOException e){}

        String str = buffer.toString();

        String hexData_temp = str.replaceAll(":","3A");
        hexData_temp = hexData_temp.replaceAll(System.getProperty("line.separator"),"");
        hexData_temp = hexData_temp.replaceAll("\\p{Space}","");

        return hexData_temp;
    }



    private byte[] requestCommand(Character[] command){
        byte[] data_set = new byte[command.length];

        for(int i=0; i<data_set.length; i++) {
            data_set[i] = (byte) (command[i] & 0xff);
        }

//        Log.d(TAG,"DataSize = " + String.valueOf(data_set.length));
//        Log.d(TAG,"data 0 : " + String.valueOf(data_set[0]));

        return data_set;

    }

    private void requestConnectClassicBT(){
        mBluetoothService = new BluetoothService(mActivity,uploadManagerHandler,"STK");

        Intent btintent = new Intent();
        btintent.putExtra(BluetoothService.EXTRA_DEVICE_ADDRESS,bt_address);
        mBluetoothService.getDeviceInfo(btintent);
    }

    public void setDismissListener(){

    }

    private String replaceHexData(String hex){
        String hexData_temp = hex.replaceAll(":","3A");
        hexData_temp = hexData_temp.replaceAll(System.getProperty("line.separator"),"");
        hexData_temp = hexData_temp.replaceAll("\\p{Space}","");

        return hexData_temp;
    }

    private BroadcastReceiver UploadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,"Received step 2");
            if (USB_UPLOAD_RECEIVE.equals(action)) {
                byte[] received = intent.getByteArrayExtra(USB_UPLOAD_RECEIVE);
                stk500.getStk500_handler().obtainMessage(RECEIVED_MESSAGE,(int)orient_command,-1,received).sendToTarget();

            }
        }
    };

}
