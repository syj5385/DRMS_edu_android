package com.drms.drmakersystem.Lev2_Controller;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.Communication.BluetoothService;
import com.drms.drmakersystem.Communication.DRS_Constants;
import com.drms.drmakersystem.Communication.DRS_SerialProtocol;
import com.drms.drmakersystem.Communication.ProcessSerialData;
import com.drms.drmakersystem.Drone_Controller.UploadActivity;
import com.drms.drmakersystem.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;

public class Cont2_11_Activity extends AppCompatActivity {

    public static final int MODE_REQUEST = 1;
    private int mSelectedBtn;
    private int mSendingState;

    private static final int STATE_SENDING = 1;
    private static final int STATE_NO_SENDING = 2;

    private static final String TAG = "MAIN";
    public static final boolean D = true;

    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CONNECT_DEVICE = 1;

    private static final int MESSAGE_STATE_CHANGE = 1;
    private static final int MESSAGE_WRITE = 2;
    private static final int MESSAGE_READ = 3;

    private static final int FILEDOWNLOAD_COMP = 10;
    private static final int LEFT_LAYOUT_SIZE = 11;
    private static final int RIGHT_LAYOUT_SIZE =12;
    private static final int LEFT_THRO_VELOCITY = 13;
    private static final int RIGHT_THRO_VELOCITY = 14;
    private static final int LEFT_RIGHT_RATE_TO_RADIAN = 15;

    private static final int DRS_PROTOCOL = 101;


    private BluetoothService bluetoothService_obj = null;
    public StringBuffer mOutStringBuffer;

    private long lastTimeBackPressed;

    private boolean power_state = false;

    private BluetoothDevice tmpdevice;

    private boolean file_state = false;

    private boolean running = false;

    private ProcessSerialData mProcessSerialData = null;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String[] parameter = new String[4];

    private int left_layout_width, left_layout_height;

    private int right_layout_width, right_layout_height;

    private int left_velocity=0;
    private int right_velocity=0;
    private int base_velocity = 0;

    private Thread mthread;

    int[] BUMPER_RC_DATA = new int[2];

    DRS_SerialProtocol p;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ImageView bluetooth, power, motor_init, upload ;

    private ImageView left_throttle, right_throttle,flight;

    private TextView current_vbat;


    private LinearLayout left_layout, right_layout;


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE:" + msg.arg1);

                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(getApplicationContext(), "블루투스 연결에 성공하였습니다.\n"+"address" + bluetoothService_obj.address1, Toast.LENGTH_LONG).show();
                            p.make_send_DRS(DRS_Constants.NOTHING);
                            bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth_cnt));

                            mthread = new Send_thread();
                            running = true;
                            mthread.start();

                            break;

                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(getApplicationContext(), "연결중....", Toast.LENGTH_LONG).show();
                            break;

                        case BluetoothService.STATE_FAIL:
                            Toast.makeText(getApplicationContext(), "블루투스 연결에 실패하였습니다.", Toast.LENGTH_LONG).show();
                            bluetoothService_obj.stop();
                            break;
                    }
                    break;

                case DRS_PROTOCOL :
                    int level_round = msg.arg1;
                    int command = msg.arg2;
                    int[] payload =(int[])msg.obj;

                    if(level_round == DRS_Constants.LEV2_R11){
                        switch(command){
                            case DRS_Constants.CURRENT_VBAT :
                                float vbat_temp = ((float)payload[0] / 30);

                                DecimalFormat form = new DecimalFormat("#.##");

                                float vbat = vbat_temp;
                                current_vbat.setText(String.valueOf(form.format(vbat)) +" [V]");
                                break;
                        }
                    }
                    break;

                case LEFT_LAYOUT_SIZE :
                    left_layout_width = msg.arg1;
                    left_layout_height = msg.arg2;

                    break;

                case RIGHT_LAYOUT_SIZE :
                    right_layout_width = msg.arg1;
                    right_layout_height = msg.arg2;

                    break;

                case LEFT_THRO_VELOCITY :
                    if(left_velocity != msg.arg1)
                        left_velocity = msg.arg1;


                    break;


                case RIGHT_THRO_VELOCITY :
                    if(right_velocity != msg.arg1)
                        right_velocity = msg.arg1;

                    break;


                case FILEDOWNLOAD_COMP :
                    Toast.makeText(getApplicationContext(), "hex 파일을 다운로드 하였습니다 -> Download 폴더", Toast.LENGTH_SHORT).show();
                    file_state = true;
                    break;


            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont2_11);

        getSupportActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        bluetooth = (ImageView)findViewById(R.id.bluetooth);
        bluetooth.setOnTouchListener(mTouchListener);

        power = (ImageView)findViewById(R.id.onoff);
        power.setOnTouchListener(mTouchListener);

        motor_init = (ImageView)findViewById(R.id.motor_init);
        motor_init.setOnTouchListener(mTouchListener);

        upload = (ImageView)findViewById(R.id.upload);
        upload.setOnTouchListener(mTouchListener);

        current_vbat = (TextView)findViewById(R.id.current_vbat);

        flight = (ImageView)findViewById(R.id.flight);

        left_layout = (LinearLayout)findViewById(R.id.left_throttle_layout);

        right_layout = (LinearLayout)findViewById(R.id.right_throttle_layout);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        left_layout.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener(){
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                left_layout.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
            }
        });
        left_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float left_layout_width = left_layout.getWidth();
                float left_layout_height = left_layout.getHeight();

                mHandler.obtainMessage(LEFT_LAYOUT_SIZE,(int)left_layout_width,(int)left_layout_height).sendToTarget();
            }
        });

        right_layout.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener(){
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                right_layout.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
            }
        });
        right_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float right_layout_width = right_layout.getWidth();
                float right_layout_height = right_layout.getHeight();

                mHandler.obtainMessage(RIGHT_LAYOUT_SIZE,(int)right_layout_width,(int)right_layout_height).sendToTarget();
            }
        });

        left_layout.setOnTouchListener(throTouchListener);
        right_layout.setOnTouchListener(throTouchListener);

        left_throttle = new ImageView(this);
        right_throttle = new ImageView(this);

        ViewGroup.LayoutParams mParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        left_throttle.setLayoutParams(mParams);
        right_throttle.setLayoutParams(mParams);

        left_throttle.setImageDrawable(getResources().getDrawable(R.drawable.throttle));
        right_throttle.setImageDrawable(getResources().getDrawable(R.drawable.throttle));

        left_layout.addView(left_throttle);
        right_layout.addView(right_throttle);


        if(bluetoothService_obj == null){
            bluetoothService_obj = new BluetoothService(this, mHandler, "LEV2_R11","DRS");
            mOutStringBuffer = new StringBuffer("");
        }

        p = new DRS_SerialProtocol(DRS_Constants.LEV2_R11, mHandler, bluetoothService_obj);

        if(mProcessSerialData == null){
            mProcessSerialData = new ProcessSerialData();
        }


    }

    private class Send_thread extends Thread{
        public Send_thread() {
            super();
        }

        @Override
        public void run() {
            while(running){
                if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED){
                    if(!power_state){
                        for(int i=0 ; i<2; i++)
                            BUMPER_RC_DATA[i] = 0;
                    }

                    p.make_send_DRS(DRS_Constants.BUMPER_RC_DATA, BUMPER_RC_DATA);

                    try{
                        Thread.sleep(40);
                    }catch (InterruptedException e){};

                    p.make_send_DRS(DRS_Constants.CURRENT_VBAT);
                    try{
                        Thread.sleep(40);
                    }catch (InterruptedException e){};


                }
            }
        }
    }

    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.bluetooth:
                    if (event.getAction() == MotionEvent.ACTION_UP
                            &&( (bluetoothService_obj.getState() == BluetoothService.STATE_LISTEN)
                            || (bluetoothService_obj.getState() == BluetoothService.STATE_NONE))) {
                        if (bluetoothService_obj.getDeviceState()) {
                            bluetoothService_obj.setReadRunning(true);
                            bluetoothService_obj.enableBluetooth();
                        } else {
                            finish();
                        }
                        break;
                    }

                    else if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTING
                            && event.getAction() == MotionEvent.ACTION_UP){
                        Toast.makeText(getApplicationContext(),"블루투스를 연결중 입니다.",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    else if(bluetoothService_obj.getState() == BluetoothService.STATE_FAIL
                            && event.getAction() == MotionEvent.ACTION_UP){
                        return false;
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED){

                        bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth));


                        running = false;
                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
                        power_state = false;

                        p.make_send_DRS(DRS_Constants.POWER_OFF);
                        try{
                            Thread.sleep(15);
                        }catch (InterruptedException e){};

                        p.make_send_DRS(DRS_Constants.NOTHING);
                        try{
                            Thread.sleep(15);
                        }catch (InterruptedException e){};
                        bluetoothService_obj.stop();
                    }


                    break;


                case R.id.onoff:
                    if (power_state == false && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && event.getAction() == MotionEvent.ACTION_DOWN) {

                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_on));
                        left_throttle.setY(left_layout_height - left_throttle.getHeight());
                        right_throttle.setY(right_layout_height - right_throttle.getHeight());

                        long[] pattern = {200, 500, 200, 500, 200, 500, 200, 500, 200, 500, 200};

                        power_state = true;
                        p.make_send_DRS(DRS_Constants.POWER_ON);

                    } else if (power_state == true && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && event.getAction() == MotionEvent.ACTION_DOWN) {
                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
                        power_state = false;

                        p.make_send_DRS(DRS_Constants.POWER_OFF);
                    }
                    break;

                case R.id.motor_init:
                    if (bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && event.getAction() == MotionEvent.ACTION_DOWN)
                        p.make_send_DRS(DRS_Constants.MOTOR_INIT);

                    break;

                case R.id.upload:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(getApplicationContext(),UploadActivity.class);
                        intent.putExtra("Firmware","L2R11");
                        startActivity(intent);
                    }
                    break;
            }

            return true;
        }
    };

    private View.OnTouchListener throTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state == true){
                switch(v.getId()){
                    case R.id.left_throttle_layout :
                        if(event.getAction() == MotionEvent.ACTION_MOVE){
                            if ((event.getX() > left_layout_width / 3 - left_throttle.getWidth() / 2) && (event.getX() < left_layout_width * 2 / 3 + left_throttle.getWidth() / 2)) {
                                left_throttle.setY(event.getY() - left_throttle.getHeight()/2);
                                if(left_throttle.getY() < 0) left_throttle.setY(0);
                                if (left_throttle.getY() > left_layout_height - left_throttle.getHeight()) left_throttle.setY(left_layout_height - left_throttle.getHeight());
                            }
                        }

                        else if(event.getAction() == MotionEvent.ACTION_UP){
                            left_throttle.setY(left_layout_height - left_throttle.getHeight());
                        }

                        BUMPER_RC_DATA[0] = 255 - (int)( left_throttle.getY() * 255 / (left_layout_height - left_throttle.getHeight()));


                        break;

                    case R.id.right_throttle_layout :
                        if(event.getAction() == MotionEvent.ACTION_MOVE){
                            if ((event.getX() > right_layout_width / 3 - right_throttle.getWidth() / 2) && (event.getX() < right_layout_width * 2 / 3 + right_throttle.getWidth() / 2)) {
                                right_throttle.setY(event.getY() - right_throttle.getHeight()/2);
                                if(right_throttle.getY() < 0) right_throttle.setY(0);
                                if (right_throttle.getY() > right_layout_height - right_throttle.getHeight()) right_throttle.setY(right_layout_height - right_throttle.getHeight());
                            }
                        }

                        else if(event.getAction() == MotionEvent.ACTION_UP){
                            right_throttle.setY(right_layout_height - right_throttle.getHeight());
                        }

                        BUMPER_RC_DATA[1] = 255 - (int)(right_throttle.getY() * 255 / (right_layout_height - right_throttle.getHeight()));



                        break;

                }
            }

            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed <1500) {
            if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
                p.make_send_DRS(DRS_Constants.NOTHING);
            }
            finish();
            return;
        }
        if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED)
            Toast.makeText(getApplicationContext(),"'뒤로' 버튼을 한번 더 누르면 블루투스 연결 해제 후 화면이 종료됩니다.  " , Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(),"'뒤로' 버튼을 한번 더 누르면 화면이 종료됩니다.  " , Toast.LENGTH_SHORT).show();

        lastTimeBackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bluetoothService_obj.device != null) {
            tmpdevice = bluetoothService_obj.device;
            bluetoothService_obj.stop();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(tmpdevice != null){
            bluetoothService_obj.connect(tmpdevice);
            tmpdevice = null;
            bluetoothService_obj.setReadRunning(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void writeFile(InputStream is , OutputStream os) throws IOException {
        int c = 0 ;
        while ((c = is.read()) != -1){
            os.write(c);
            os.flush();
        }
    }

    public class FiledownThread extends Thread{
        @Override
        public void run() {
            try {
                InputStream is = getResources().openRawResource(R.raw.r1_9);

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/drs1_9_firmware.hex");
                OutputStream out = new FileOutputStream(file);
                writeFile(is, out);
                out.close();
                Message msg = Message.obtain();
                msg.what = FILEDOWNLOAD_COMP;
                mHandler.sendMessage(msg);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult" + resultCode);

        switch (requestCode) {
            case REQUEST_ENABLE_BT :
                if (resultCode != Activity.RESULT_OK) {
                    bluetoothService_obj.scanDevice();
                } else {//cancel button
                    Log.d(TAG, "Bluetooth is not enable");
                }
                break;

            case REQUEST_CONNECT_DEVICE :
                if (resultCode == Activity.RESULT_OK) {
                    bluetoothService_obj.getDeviceInfo(data);
                }
                break;
        }
    }

    private synchronized void sendMessage(String message, int mode){
        if (mSendingState == STATE_SENDING){
            try{
                wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        mSendingState = STATE_SENDING;

        if(bluetoothService_obj.getState() != BluetoothService.STATE_CONNECTED){
            mSendingState = STATE_NO_SENDING;
            return;
        }

        if(message.length()>0){
            byte[] send = (message + "\n").getBytes();
            bluetoothService_obj.write(send,mode);
            mOutStringBuffer.setLength(0);
        }

        mSendingState = STATE_NO_SENDING;
        notify();
    }

    public boolean getPackageList(){
        boolean isExist = false;

        PackageManager pm = getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN,null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pm.queryIntentActivities(mainIntent,0);

        try{
            for(int i=0 ; i <mApps.size() ; i++){
                if(mApps.get(i).activityInfo.packageName.startsWith("io.iotool.iotoolarduinohex")){
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
