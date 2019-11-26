package com.drms.drmakersystem.Lev1_1_Controller;

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
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.Communication.BluetoothService;
import com.drms.drmakersystem.Drone_Controller.UploadActivity;
import com.drms.drmakersystem.Communication.ProcessSerialData;
import com.drms.drmakersystem.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class Cont1_1_3_Activity extends AppCompatActivity {

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
    private static final int SENSOR_VALUE  = 11;
    private static final int GEAR_LAYOUT_SIZE = 12;
    private static final int VEL_METER_LAYOUT_SIZE = 13;

    private static final int FORWARD_DATA = 20;
    private static final int BACKWARD_DATA = 21;
    private static final int STOP_DATA = 22;

    private BluetoothService bluetoothService_obj = null;
    public StringBuffer mOutStringBuffer;

    private long lastTimeBackPressed;

    private boolean power_state = false;

    private BluetoothDevice tmpdevice;

    private boolean file_state = false;

    private boolean running = false;

    private Thread mthread;

    private int[] velocity = {STOP_DATA, 0, 0, 0};

    private String[] parameter = new String[4];

    private MediaPlayer mMedaiPlayer;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    private float gear_layout_width, gear_layout_height;
    private float vel_meter_layout_height;

    private int inv_rev_state = 0 ; // 0 : stop / 1 : inverse / 2 : reverse

    private SensorManager mSensorManager;
    private SensorEventListener mSensorEventListener;

    private int send_roll_value, send_pitch_value;

    private ProcessSerialData mProcessSerialData;

    private Vibrator mVibe;


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ImageView bluetooth, power, motor_init, upload ;

    private ImageView gear;

    private ImageView car, forward;

//    private TextView value1, value2, value3, value4;

    private TextView battery;

    private LinearLayout gear_layout;

    /////////////////////////////////////////////////////////////////////////////////////////////////

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
                            String bluetooth_OK = "#DRS^4^LEV1_1_R3^NULL$\n";
                            byte[] temp_bluetooth = bluetooth_OK.getBytes();
                            bluetoothService_obj.write(temp_bluetooth,MODE_REQUEST);
                            bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth_cnt));
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
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
                        byte[] readBuf = (byte[]) msg.obj;
                        int check = 0;
                        String readMessage = new String(readBuf, 0, msg.arg1);

                        for(int j=0 ; j<readMessage.length()-1; j++){
                            if(readMessage.charAt(j) == '^'){
                                check++;
                            }
                        }

                        if(readMessage.charAt(0) == '#' && check == 3 && readMessage.charAt(readMessage.length()-1) == '$') {
                            parameter = mProcessSerialData.process_data(readMessage,readMessage.length());
                            if(parameter[0].equals("DRS") && parameter[2].equals("LEV1_1_R3")){
                                String vbat = null;
                                for(int i=7 ; i<parameter[3].length() ; i++){
                                    if(vbat == null){
                                        vbat = String.valueOf(parameter[3].charAt(i));
                                    }
                                    else{
                                        vbat += parameter[3].charAt(i);
                                    }
                                }

                                battery.setText(vbat + " V");
                            }
                        }

                    }
                    break;

                case FILEDOWNLOAD_COMP :
                    Toast.makeText(getApplicationContext(), "hex 파일을 다운로드 하였습니다 -> Download 폴더", Toast.LENGTH_SHORT).show();
                    file_state = true;
                    break;

                case SENSOR_VALUE :
                    send_pitch_value = msg.arg1;
                    send_roll_value = msg.arg2 - 15;

                    float left_velocity = 0;
                    float right_velocity = 0;
                    float throttle = 0;

                    switch(inv_rev_state){
                        case 0 : // stop
                            car.setRotation(0);
                            left_velocity = 0;
                            right_velocity = 0;

                            velocity[0] = STOP_DATA;
                            velocity[1] = (int)left_velocity;
                            velocity[2] = (int)right_velocity;
                            velocity[3] = (int)throttle;
                            break;

                        case 1 : // inverse

                            if(send_roll_value <= 0) { // inverse mode check
                                float throttle_value = -send_roll_value * 255 / 45;
                                float pitch_rate = send_pitch_value * 255 / 45;

                                left_velocity = throttle_value - pitch_rate;
                                right_velocity = throttle_value + pitch_rate;
                                throttle = throttle_value;

                                if (throttle_value < 255) {
                                    if (left_velocity < 0) left_velocity = 0;
                                    if (left_velocity > throttle_value)
                                        left_velocity = throttle_value;
                                    if (right_velocity < 0) right_velocity = 0;
                                    if (right_velocity > throttle_value)
                                        right_velocity = throttle_value;
                                } else {
                                    if (left_velocity < 0) left_velocity = 0;
                                    if (left_velocity > 255) left_velocity = 255;
                                    if (right_velocity < 0) right_velocity = 0;
                                    if (right_velocity > 255) right_velocity = 255;
                                    throttle = 255;
                                }

                                float pitch_rate_to_DEGREE = pitch_rate * 45 / 255 * (-1);

                                if(pitch_rate_to_DEGREE > 45) pitch_rate_to_DEGREE = 45;
                                if(pitch_rate_to_DEGREE < -45) pitch_rate_to_DEGREE = -45;

                                car.setRotation(pitch_rate_to_DEGREE);

                            }
                            else if(send_roll_value > 0){
                                left_velocity = 0;
                                right_velocity = 0;
                                car.setRotation(0);
                            }

                            velocity[0] = FORWARD_DATA;
                            velocity[1] = (int)left_velocity;
                            velocity[2] = (int)right_velocity;
                            velocity[3] = (int)throttle;

                            break;


                    }
                    break;

                case GEAR_LAYOUT_SIZE :
                    gear_layout_width = msg.arg1;
                    gear_layout_height = msg.arg2;

                    break;

                case VEL_METER_LAYOUT_SIZE :
                    vel_meter_layout_height = msg.arg2;

                    break;

            }
        }
    };

    public class SendThread extends Thread{

        private boolean forward_data = false;
        private boolean  backward_data = false;
        private boolean stop_data = false;

        public SendThread() {
            super();
        }

        @Override
        public void run() {
            while(running) {
                if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state == true) {
                    int sizeofLeft = 0;
                    int sizeofRight = 0;
                    int sizeofThrottle = 0;
                    switch (velocity[0]) {
                        case STOP_DATA:
                            sendMessage("#DRS^4^LEV1_1_R3^STOP$", MODE_REQUEST);
                            try {
                                mthread.sleep(100);
                            }catch (InterruptedException e){};
                            break;

                        case FORWARD_DATA:
                            sizeofLeft = String.valueOf(velocity[1]).length();
                            sizeofRight = String.valueOf(velocity[2]).length();
                            sizeofThrottle = String.valueOf(velocity[3]).length();

                            sendMessage("#DRS^" + String.valueOf(sizeofThrottle+sizeofLeft + sizeofRight + 6) + "^LEV1_1_R3^INV_L"
                                    + String.valueOf(velocity[1])
                                    + "R" + String.valueOf(velocity[2])
                                    + "T" + String.valueOf(velocity[3])
                                    + "$", MODE_REQUEST);
                            try {
                                mthread.sleep(60);
                            }catch (InterruptedException e){};
                            break;

                    }
                }
            }
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont1_1_3);

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

        battery = (TextView)findViewById(R.id.battery);

        car = (ImageView)findViewById(R.id.car);
        forward = (ImageView)findViewById(R.id.forward);

        gear_layout = (LinearLayout)findViewById(R.id.gear_layout);

        gear_layout.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener(){
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                gear_layout.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
            }
        });
        gear_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float layout_width = gear_layout.getWidth();
                float layout_height = gear_layout.getHeight();

                mHandler.obtainMessage(GEAR_LAYOUT_SIZE,(int)layout_width,(int)layout_height).sendToTarget();
            }
        });


        gear = new ImageView(this);
        gear.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        gear.setImageDrawable(getResources().getDrawable(R.drawable.throttle));


        gear_layout.addView(gear);
        gear_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state == true){
                    gear.setY(event.getY() - gear.getHeight()/2);
                    if(gear.getY() < 0) gear.setY(0);
                    else if(gear.getY() > gear_layout_height - gear.getHeight()) gear.setY(gear_layout_height - gear.getHeight());
                }
                else if(event.getAction() == MotionEvent.ACTION_UP && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state == true) {

                    if(event.getY() < gear_layout_height/2 - gear.getHeight()/2) {
                        gear.setY(0);
                        sendMessage("#DRS^4^LEV1_1_R3^STOP$",MODE_REQUEST);
                        inv_rev_state = 1;
                        forward.setImageDrawable(getResources().getDrawable(R.drawable.forward_off));

                        forward.setImageDrawable(getResources().getDrawable(R.drawable.forward));

                    }
                    else if((event.getY() >= gear_layout_height/2- gear.getHeight()/2) ) {
                        gear.setY(gear_layout_height - gear.getHeight());
                        sendMessage("#DRS^4^LEV1_1_R3^STOP$",MODE_REQUEST);
                        inv_rev_state = 0;

                        forward.setImageDrawable(getResources().getDrawable(R.drawable.forward_off));


                    }

                }

                return true;
            }
        });

        mMedaiPlayer = MediaPlayer.create(this, R.raw.start);
        mVibe = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensorEventListener = new SensorListener();

        if(bluetoothService_obj == null) {
            bluetoothService_obj = new BluetoothService(this, mHandler, "L0R9","DRS");

            mOutStringBuffer = new StringBuffer("");
        }

        mProcessSerialData = new ProcessSerialData();
    }





    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(v.getId()){
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
                    else if(event.getAction() == MotionEvent.ACTION_UP
                            && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED){

                        bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth));

                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
                        power_state = false;


                        bluetoothService_obj.stop();
                    }

                    break;

                case R.id.onoff:
                    if(power_state == false && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && event.getAction() == MotionEvent.ACTION_DOWN){
                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_on));
                        power_state = true;
                        mMedaiPlayer.seekTo(0);
                        mMedaiPlayer.start();
                        long[] pattern = {200, 500, 200, 500, 200, 500, 200, 500, 200, 500, 200};
                        gear.setY(gear_layout_height - gear.getHeight());
                        mVibe.vibrate(pattern,-1);  // - : once , 3 : infinite repeat,

                        sendMessage("#DRS^8^LEV1_1_R3^POWER_ON$",MODE_REQUEST);
                    }
                    else if(power_state == true && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && event.getAction() == MotionEvent.ACTION_DOWN){

                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
                        gear.setY(gear_layout_height/2- gear.getHeight()/2);
                        sendMessage("#DRS^4^LEV1_1_R3^STOP$",MODE_REQUEST);
                        inv_rev_state = 0;

                        forward.setImageDrawable(getResources().getDrawable(R.drawable.forward_off));

                        power_state = false;
                        sendMessage("#DRS^9^LEV1_1_R3^POWER_OFF$",MODE_REQUEST);

                    }
                    break;

                case R.id.motor_init:
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && event.getAction() == MotionEvent.ACTION_DOWN)
                        sendMessage("#DRS^10^LEV2_R9^MOTOR_INIT$",MODE_REQUEST);
                    break;

                case R.id.upload:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                        intent.putExtra("Firmware", "L1_1_R3");
                        startActivity(intent);
                    }
                    break;


            }
            return true;
        }
    };

    public class SensorListener implements SensorEventListener{
        float roll;
        float pitch;
        float yaw;

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] v = event.values;
            switch(event.sensor.getType()){
                case Sensor.TYPE_ORIENTATION :
                    if(yaw != v[0] || pitch != v[1] || roll != v[2]){
                        yaw = v[0];

                        pitch = v[1];

                        roll = v[2];

                        if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state == true) {
                            Message sensor_msg = Message.obtain();
                            sensor_msg.what = SENSOR_VALUE;
                            sensor_msg.arg1 = (int) pitch;
                            sensor_msg.arg2 = (int) roll;
                            mHandler.sendMessage(sensor_msg);
                        }
                    }
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Nothiing
        }
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed <1500) {
            if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
                sendMessage("#DRS^3^LEV1_1_R3^OUT$", MODE_REQUEST); // If the back_button is clicked, all PWM is stopped
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

        gear.setY(gear_layout_height/2- gear.getHeight()/2);
        bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth));
        power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
        sendMessage("#DRS^4^LEV1_1_R3^STOP$",MODE_REQUEST);
        inv_rev_state = 0;

        forward.setImageDrawable(getResources().getDrawable(R.drawable.forward_off));

        power_state = false;
        sendMessage("#DRS^9^LEV1_1_R3^POWER_OFF$",MODE_REQUEST);
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
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSensorManager.registerListener(mSensorEventListener,mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_UI);
        mthread = new SendThread();
        running = true;
        mthread.start();

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

    private void execute_received_data(String[] parameter){

    }


}
