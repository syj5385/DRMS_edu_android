package com.drms.drmakersystem.Lev1_Controller;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.Communication.BluetoothService;
import com.drms.drmakersystem.Drone_Controller.UploadActivity;
import com.drms.drmakersystem.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class Cont1_9_Activity extends AppCompatActivity {

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

    private static final int FILEDOWNLOAD_COMP = 10;

    private BluetoothService bluetoothService_obj = null;
    public StringBuffer mOutStringBuffer;

    private long lastTimeBackPressed;

    private boolean power_state = false;

    private BluetoothDevice tmpdevice;

    private boolean file_state = false;

    private boolean running = false;

    private float accx;
    private SensorManager mSensorManager;
    private Sensor accSensor;
    SensorEventListener mSensorEventListner;

    private float throttle_left = 0;
    private float throttle_right = 0;
    private float current_velocity_left = 0;
    private float current_velocity_right = 0;

    private float w_height = 0;
    private float w_width = 0;

    private static final int ACC_VALUE = 3;


    ////////////////////////controll button & touch image///////////////////////
    private ImageView bluetooth, power, motor_init, upload ;

    private ImageView throttle;

    private LinearLayout throttle_layout;

    private TextView current_speed;
    private TextView current_speed2;
    ////////////////////////////////////////////////////////////////////////////

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

                case FILEDOWNLOAD_COMP :
                    Toast.makeText(getApplicationContext(), "hex 파일을 다운로드 하였습니다 -> Download 폴더", Toast.LENGTH_SHORT).show();
                    file_state = true;
                    break;

                case ACC_VALUE :
                    int acc_value = msg.arg1;
                    float rate =(float) acc_value / 255;

                    if(rate > 0){
                        current_velocity_left = (float)(throttle_left + rate*throttle_left*0.5);
                        current_velocity_right = throttle_right - rate * throttle_right;

                        if(current_velocity_left > 255)
                            current_velocity_left = 255;
                    }
                    else{
                        current_velocity_left = throttle_left + rate*throttle_left;
                       current_velocity_right = (float)(throttle_right - rate * throttle_right*0.5);

                        if(current_velocity_right > 255)
                            current_velocity_right = 255;
                    }
                    current_speed.setText("좌 : "+String.valueOf(current_velocity_left));
                    current_speed2.setText("우 : "+String.valueOf(current_velocity_right));
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont1_9);

        getSupportActionBar().hide();
        Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display display = getWindowManager().getDefaultDisplay();
        w_height=display.getHeight();
        w_width = display.getWidth();

        bluetooth = (ImageView)findViewById(R.id.bluetooth);
        bluetooth.setOnTouchListener(mTouchListener);

        power = (ImageView)findViewById(R.id.onoff);
        power.setOnTouchListener(mTouchListener);

        motor_init = (ImageView)findViewById(R.id.motor_init);
        motor_init.setOnTouchListener(mTouchListener);

        upload = (ImageView)findViewById(R.id.upload);
        upload.setOnTouchListener(mTouchListener);

        current_speed = (TextView)findViewById(R.id.current_speed);

        current_speed2= (TextView)findViewById(R.id.current_speed2);

        throttle_layout = (LinearLayout)findViewById(R.id.throttle_layout);

        if(bluetoothService_obj == null){
            bluetoothService_obj = new BluetoothService(this, mHandler,"L0R9","DRS");

            mOutStringBuffer = new StringBuffer("");
        }

        throttle = new ImageView(this);
        throttle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        throttle.setImageDrawable(getResources().getDrawable(R.drawable.icon));
        throttle_layout.addView(throttle);
        throttle_layout.setY(0);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorEventListner = new accListener();

        throttle_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(power_state == true && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED){
                    if(event.getAction() == MotionEvent.ACTION_MOVE){
                        throttle.setY(event.getY()-throttle.getHeight()/2);
                        if(throttle.getY() >= w_height-throttle.getHeight())
                            throttle.setY(w_height- throttle.getHeight());
                        else if(throttle.getY() <= 0)
                            throttle.setY(0);
                    }
                throttle_left = (int)((w_height-throttle.getHeight())-throttle.getY());
                throttle_left =(int)( throttle_left* (255 /( w_height - throttle.getHeight())));
                throttle_right = throttle_left;
                }
                return true;
            }
        });
    }

    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(v.getId()){
                case R.id.onoff:
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED){
                            if(power_state == false) {
                                power_state = !power_state;
                                sendMessage("a"+'\n',MODE_REQUEST);
                                power.setImageDrawable(getResources().getDrawable(R.drawable.power_on));
                                Toast.makeText(getApplicationContext(), "시동이 걸렸습니다.", Toast.LENGTH_SHORT).show();
                                throttle.setY(w_height - throttle.getHeight());
                            }

                            else{
                                current_velocity_left=0;
                                current_velocity_right=0;
                                power_state = !power_state;
                                sendMessage("b"+'\n',MODE_REQUEST);
                                power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
                                Toast.makeText(getApplicationContext(),"사동이 꺼졌습니다.", Toast.LENGTH_SHORT).show();
                                throttle.setY(w_height - throttle.getHeight());
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"블루투스를 연결해주세요!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;

                case R.id.motor_init:
                   if(event.getAction() == MotionEvent.ACTION_DOWN){
                       if (bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
                           sendMessage("c"+'\n',MODE_REQUEST);
                           break;
                       }
                   }

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
                        running = false;


                        bluetoothService_obj.stop();
                    }

                    break;

                case R.id.upload :
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(getApplicationContext(),UploadActivity.class);
                        intent.putExtra("Firmware","L1R9");
                        startActivity(intent);
                    }
                    break;

            }
            return true;
        }
    };


    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed <1500) {
            if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
                sendMessage("z\n", MODE_REQUEST); // If the back_button is clicked, all PWM is stopped
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
        mSensorManager.unregisterListener(mSensorEventListner);
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
        Thread throttle = new thro_thread();
        running = true;
        throttle.start();
        mSensorManager.registerListener(mSensorEventListner,accSensor,SensorManager.SENSOR_DELAY_GAME);
    }

    public class accListener implements SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state == true) {
                int temp_accX = 0;
                for (int i = 0; i < 70; i++) {
                    temp_accX += event.values[1] * 100;
                }

                if (temp_accX >= 25500)
                    temp_accX = 25500;

                else if (temp_accX < -25500)
                    temp_accX = -25500;

                Message msg = Message.obtain();
                msg.what = ACC_VALUE;
                msg.arg1 = temp_accX / 100;
                mHandler.sendMessage(msg);

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
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

    private class thro_thread extends Thread{
        @Override
        public void run() {
            while(running){
                if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state == true){
                    sendMessage("left" +String.valueOf((int)current_velocity_left)+'\n',MODE_REQUEST);
                    sendMessage("right"+String.valueOf((int)current_velocity_right)+'\n',MODE_REQUEST);
                    try{
                        Thread.sleep(10);
                    }catch(InterruptedException e){
                        Log.e("Thread","Interrupted Exception",e);
                    }
                }
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
            byte[] send = message.getBytes();
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
