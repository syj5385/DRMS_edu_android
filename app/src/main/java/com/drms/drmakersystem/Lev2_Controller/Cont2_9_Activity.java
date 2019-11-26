package com.drms.drmakersystem.Lev2_Controller;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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

public class Cont2_9_Activity extends AppCompatActivity {

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
    private static final int THROTTLE_LAYOUT_SIZE = 11;
    private static final int LEFT_RIGHT_LAYOUT_SIZE = 12;
    private static final int THROTTLE_VALUE = 13;
    private static final int LEFT_RIGHT_VALUE = 14;

    private BluetoothService bluetoothService_obj = null;
    public StringBuffer mOutStringBuffer;

    private long lastTimeBackPressed;

    private boolean power_state = false;

    private BluetoothDevice tmpdevice;

    private boolean file_state = false;

    private boolean running = false;

    private SensorManager mSensorManager;
    private SensorEventListener mSensorEventListener;

    private Thread mthread;

    private ProcessSerialData mProcessSerialData;

    private MediaPlayer mMediaPlayer;

    private Vibrator mVibe;

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private float throttle_layout_width, throttle_layout_height;

    private float left_right_layout_width, left_right_layout_height;

    private int left_velocity = 0;
    private int right_velocity = 0;

    private float throttle_value, left_right_range, left_right_range_TO_DEGREE;

    private boolean hand_onoff_state = false;

    private int hand_speed = 0;

    private String[] parameter = new String[4];

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ImageView bluetooth, power, motor_init, upload ;

    private LinearLayout throttle_layout, left_right_layout, state_layout;

    private ImageView throttle, left_right, orientation, hand_onoff;

    private TextView battery, hand_vel, equipment;

    private SeekBar hand_velocity;

    private int[] TR_RC_data; // { _________ , ________ , ________ , ________ }
//                                 throttle     forward  left_right  equipment

    private static final int DRS_PROTOCOL = 101;

    private DRS_SerialProtocol p;
    ///////////////////////////////////////////////////////////////////////////////////////////////
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
//                            String bluetooth_OK = "#DRS^4^LEV2_R8^NULL$\n";
//                            byte[] temp_bluetooth = bluetooth_OK.getBytes();
//                            bluetoothService_obj.write(temp_bluetooth,MODE_REQUEST);

                            p.make_send_DRS(DRS_Constants.NOTHING);
                            bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth_cnt));
                            mthread = new Send_Thread();
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

                case MESSAGE_READ :

                     break;

                case FILEDOWNLOAD_COMP :
                    Toast.makeText(getApplicationContext(), "hex 파일을 다운로드 하였습니다 -> Download 폴더", Toast.LENGTH_SHORT).show();
                    file_state = true;
                    break;

                case THROTTLE_LAYOUT_SIZE :
                    throttle_layout_width = msg.arg1;
                    throttle_layout_height = msg.arg2;

                    break;

                case LEFT_RIGHT_LAYOUT_SIZE :
                    left_right_layout_width = msg.arg1;
                    left_right_layout_height = msg.arg2;

                    break;

                case 100 :
                    hand_vel.setText(String.valueOf(TR_RC_data[2]) + " %");
                    break;

                case DRS_PROTOCOL :
                    int level_round = msg.arg1;
                    int command = msg.arg2;
                    int[] payload =(int[])msg.obj;

                    if(level_round == DRS_Constants.LEV2_R9){
                        switch(command){
                            case DRS_Constants.CURRENT_VBAT :
                                float vbat_temp = ((float)payload[0] / 30);

                                DecimalFormat form = new DecimalFormat("#.##");
                                float vbat = vbat_temp;
                                battery.setText(String.valueOf(form.format(vbat)) +" [V]");
                                break;
                        }
                    }
                    break;

            }
        }
    };

    public class Send_Thread extends Thread{

        public Send_Thread() {
            super();
        }

        @Override
        public void run() {
            while(running){
                if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED ){
                    if(power_state) {

                        if (!hand_onoff_state) {
                            TR_RC_data[2] = 0;

                        } else {
                            TR_RC_data[2] = hand_velocity.getProgress();
                        }

                        p.make_send_DRS(DRS_Constants.TRACTOR_RC_DATA, TR_RC_data);

                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                        }
                        ;
                    }

                    p.make_send_DRS(DRS_Constants.CURRENT_VBAT);

                    try{
                        Thread.sleep(40);
                    }catch (InterruptedException e){};

                    mHandler.obtainMessage(100,-1,-1).sendToTarget();

                }
            }
        }
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont2_9);

        getSupportActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TR_RC_data = new int[3];


        bluetooth = (ImageView)findViewById(R.id.bluetooth);
        bluetooth.setOnTouchListener(mTouchListener);

        power = (ImageView)findViewById(R.id.onoff);
        power.setOnTouchListener(mTouchListener);

        motor_init = (ImageView)findViewById(R.id.motor_init);
        motor_init.setOnTouchListener(mTouchListener);

        upload = (ImageView)findViewById(R.id.upload);
        upload.setOnTouchListener(mTouchListener);

        hand_onoff = (ImageView)findViewById(R.id.hand_onoff);
        hand_onoff.setOnTouchListener(mTouchListener);


        hand_vel = (TextView)findViewById(R.id.hand_speed);

        hand_velocity = (SeekBar)findViewById(R.id.hand_velocity);
        hand_velocity.setMax(100);
//        hand_velocity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state == true) {
//                    hand_speed = progress;
//                    TR_RC_data[2] = hand_speed;
//                    hand_vel.setText(String.valueOf(TR_RC_data[2] * 100 / 255) + " %");
//
//
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

        state_layout = (LinearLayout)findViewById(R.id.state_layout);

        throttle_layout = (LinearLayout)findViewById(R.id.throttle_layout);
        throttle_layout.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener(){
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                throttle_layout.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
            }
        });
        throttle_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float layout_width = throttle_layout.getWidth();
                float layout_height = throttle_layout.getHeight();

                mHandler.obtainMessage(THROTTLE_LAYOUT_SIZE,(int)layout_width,(int)layout_height).sendToTarget();
            }
        });

        left_right_layout = (LinearLayout)findViewById(R.id.left_right_layout);
        left_right_layout.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener(){
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                left_right_layout.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
            }
        });
        left_right_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float layout_width = left_right_layout.getWidth();
                float layout_height = left_right_layout.getHeight();

                mHandler.obtainMessage(LEFT_RIGHT_LAYOUT_SIZE,(int)layout_width,(int)layout_height).sendToTarget();
            }
        });

        throttle = new ImageView(this);
        left_right = new ImageView(this);

        throttle.setImageDrawable(getResources().getDrawable(R.drawable.throttle));
        left_right.setImageDrawable(getResources().getDrawable(R.drawable.throttle));

        ViewGroup.LayoutParams mLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        throttle.setLayoutParams(mLayoutParams);
        left_right.setLayoutParams(mLayoutParams);

        throttle_layout.addView(throttle);
        left_right_layout.addView(left_right);

        mMediaPlayer = MediaPlayer.create(this,R.raw.tractor_start);

        mVibe = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensorEventListener = new SensorListener();

        if(bluetoothService_obj == null) {
            bluetoothService_obj = new BluetoothService(this, mHandler, "L2R9","DRS");

            mOutStringBuffer = new StringBuffer("");
        }

        if(mProcessSerialData == null){
            mProcessSerialData = new ProcessSerialData();
        }

        p = new DRS_SerialProtocol(DRS_Constants.LEV2_R9,mHandler,bluetoothService_obj);

        throttle_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getX() > throttle_layout_width/3 - throttle.getWidth()/2 && event.getX() < throttle_layout_width*2/3 + throttle.getWidth()/2  && event.getAction() == MotionEvent.ACTION_MOVE && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state == true){
                    throttle.setY(event.getY() - throttle.getHeight()/2);
                    if(throttle.getY() < 0) throttle.setY(0);
                    if(throttle.getY() > throttle_layout_height - throttle.getHeight()) throttle.setY(throttle_layout_height - throttle.getHeight());

                    float throttle_temp = 255 -( throttle.getY() * 255 / (throttle_layout_height - throttle.getHeight()));

                    TR_RC_data[0] = (int)throttle_temp;

                    mHandler.obtainMessage(THROTTLE_VALUE, (int)throttle_temp,-1).sendToTarget();
                }
                return true;
            }
        });

        left_right_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getY() > left_right_layout_height / 3 - left_right.getHeight() / 2 && event.getY() < left_right_layout_height * 2 / 3 + left_right.getHeight() / 2 && event.getAction() == MotionEvent.ACTION_MOVE && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state == true) {
                    left_right.setX(event.getX() - left_right.getWidth() / 2);
                    if (left_right.getX() < 0) left_right.setX(0);
                    if (left_right.getX() > left_right_layout_width - left_right.getWidth() )
                        left_right.setX(left_right_layout_width - left_right.getWidth());
                }

                else if(event.getAction() == MotionEvent.ACTION_UP && power_state == true){
                    left_right.setX(left_right_layout_width/2 - left_right.getWidth()/2);
                }

                Message msg = Message.obtain();
                int left_right_speed = (int)((left_right.getX()) * 255 / (left_right_layout_width - left_right.getWidth()));
                TR_RC_data[1] = left_right_speed;

                int rotation = (left_right_speed - 127) * 45 / 127;

                orientation.setRotation(rotation);




                mHandler.sendMessage(msg);
                return true;
            }
        });

        orientation = new ImageView(this);
        orientation.setLayoutParams(mLayoutParams);
        orientation.setImageDrawable(getResources().getDrawable(R.drawable.tractor));
        orientation.setRotation(0);

        state_layout.addView(orientation);

        battery = (TextView)findViewById(R.id.battery);

    }

    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(v.getId()){
                case R.id.bluetooth :
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
                        battery.setText("0.0 [V]");

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
                    if(power_state == false && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && event.getAction() == MotionEvent.ACTION_DOWN){
                        throttle.setY(throttle_layout_height - throttle.getHeight());
                        throttle_value = 0;
                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_on));
                        mMediaPlayer.seekTo(0);
                        mMediaPlayer.start();

                        long[] pattern = {200, 500, 200, 500, 200, 500, 200, 500, 200, 500, 200};
                        mVibe.vibrate(pattern,-1);  // - : once , 3 : infinite repeat,

                        power_state = true;
                        p.make_send_DRS(DRS_Constants.POWER_ON);

                    }
                    else if(power_state == true && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && event.getAction() == MotionEvent.ACTION_DOWN){
                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
                        power_state = false;
                        p.make_send_DRS(DRS_Constants.POWER_OFF);
                    }
                    break;

                case R.id.motor_init:
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && event.getAction() == MotionEvent.ACTION_DOWN)
                        p.make_send_DRS(DRS_Constants.MOTOR_INIT);
                    break;

                case R.id.upload:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(getApplicationContext(),UploadActivity.class);
                        intent.putExtra("Firmware","L2R9");
                        startActivity(intent);
                    }
                    break;

                case R.id.hand_onoff :
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state == true && event.getAction() == MotionEvent.ACTION_DOWN){
                        if(hand_onoff_state){
                            hand_velocity.setVisibility(View.INVISIBLE);
                            hand_onoff_state = false;

                        }
                        else{
                            hand_velocity.setVisibility(View.VISIBLE);
                            hand_onoff_state = true;
                        }
                    }
                    break;


            }
            return true;
        }
    };

    public class SensorListener implements SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event) {

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed <1500) {
            if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
                p.make_send_DRS(DRS_Constants.NOTHING);
                bluetoothService_obj.stop();
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
        mSensorManager.unregisterListener(mSensorEventListener);
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
        mSensorManager.registerListener(mSensorEventListener,mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mSensorEventListener,mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_UI);

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
