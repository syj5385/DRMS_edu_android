package com.drms.drmakersystem.Lev2_Controller;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.Communication.BluetoothService;
import com.drms.drmakersystem.Communication.DRS_Constants;
import com.drms.drmakersystem.Communication.DRS_SerialProtocol;
import com.drms.drmakersystem.Drone_Controller.UploadActivity;
import com.drms.drmakersystem.R;

import org.json.JSONStringer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class Cont2_5_Activity extends AppCompatActivity {

    public static final int MODE_REQUEST = 1;
    private int mSelectedBtn;
    private int mSendingState;

    private static final int STATE_SENDING = 1;
    private static final int STATE_NO_SENDING = 2;

    private static final String TAG = "Cont2_2";
    public static final boolean D = true;

    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_CONNECT_DEVICE = 1;

    private static final int MESSAGE_STATE_CHANGE = 1;
    private static final int MESSAGE_WRITE = 2;
    private static final int MESSAGE_READ = 3;
    private static final int SENSOR_VALUE = 4;

    private static final int FILEDOWNLOAD_COMP = 10;

    private static final int CURRENT_ROTATE = 20;

    private static final int DRS_PROTOCOL = 101;

    private BluetoothService bluetoothService_obj = null;
    public StringBuffer mOutStringBuffer;

    private long lastTimeBackPressed;

    private boolean power_state = false;

    private BluetoothDevice tmpdevice;

    private boolean file_state = false;

    private SensorManager mSensorManager;
    private SensorEventListener mSensorEventListener;

    private boolean running;

    private DRS_SerialProtocol p;

    private DRS_Constants p_c;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ImageView bluetooth, power, motor_init, upload ;


    private TextView current_vbat;

    private ImageView start, finish;

    private ImageView frame;


    /////////////////////////////////////////////////////////////////////////////////////////////////

    private int current_sensor_value;

    private int middle_light_value = 100;

    private int current_light_state; // 1 : bright / -1 : dark

    private Vibrator mVibe;

    private MediaPlayer bright_mMediaPlayer;
    private MediaPlayer dark_mMediaPlayer;

    private int[] MUSIC_RC_DATA = {0,0,0,0};


    /////////////////////////////////////////////////////////////////////////////////////////////////////


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
                            p.make_send_DRS(DRS_Constants.NOTHING);
                            bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth_cnt));
                            running =  true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while(running){
                                        if(!power_state){
                                            for(int i=0; i<4; i++){
                                                MUSIC_RC_DATA[i] = 0;
                                            }
                                        }
                                        p.make_send_DRS(DRS_Constants.MUSIC_RC_DATA,MUSIC_RC_DATA);

                                        try{
                                            Thread.sleep(40);
                                        }catch (InterruptedException e){}

                                        p.make_send_DRS(DRS_Constants.CURRENT_VBAT);
                                        try{
                                            Thread.sleep(20);
                                        }catch (InterruptedException e){};

                                    }
                                }
                            }).start();
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
                    byte[] temp = (byte[])msg.obj;
                    String data2 = new String(temp,-1,msg.arg1);

                    current_vbat.setText(data2);

                    break;

                case DRS_PROTOCOL :
                    int level_round = msg.arg1;
                    int command = msg.arg2;
                    int[] payload =(int[])msg.obj;

                    if(level_round == DRS_Constants.LEV2_R5){
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



                case FILEDOWNLOAD_COMP :
                    Toast.makeText(getApplicationContext(), "hex 파일을 다운로드 하였습니다 -> Download 폴더", Toast.LENGTH_SHORT).show();
                    file_state = true;
                    break;


                case SENSOR_VALUE :
                    current_sensor_value = msg.arg1;


                    if(current_sensor_value >= middle_light_value) {
                        current_light_state = 1;
                        frame.setImageDrawable(getResources().getDrawable(R.drawable.bright));
                    }

                    else if(current_sensor_value < middle_light_value) {
                        current_light_state = -1;
                        frame.setImageDrawable(getResources().getDrawable(R.drawable.dark));
                    }


                    break;


            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont2_5);

        getSupportActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        bluetooth = (ImageView) findViewById(R.id.bluetooth);
        bluetooth.setOnTouchListener(mTouchListener);

        power = (ImageView) findViewById(R.id.onoff);
        power.setOnTouchListener(mTouchListener);

        motor_init = (ImageView) findViewById(R.id.motor_init);
        motor_init.setOnTouchListener(mTouchListener);

        upload = (ImageView) findViewById(R.id.upload);
        upload.setOnTouchListener(mTouchListener);

        start = (ImageView)findViewById(R.id.start);
        start.setOnTouchListener(mTouchListener);

        finish = (ImageView)findViewById(R.id.finish);
        finish.setOnTouchListener(mTouchListener);

        frame = (ImageView)findViewById(R.id.frame);

        current_vbat = (TextView)findViewById(R.id.current_vbat);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorEventListener = new SensorListener();

        bright_mMediaPlayer = MediaPlayer.create(this, R.raw.bright);
        dark_mMediaPlayer = MediaPlayer.create(this, R.raw.dark);

        mVibe = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);


        if (bluetoothService_obj == null) {
            bluetoothService_obj = new BluetoothService(this, mHandler, "L2R5","DRS");

            mOutStringBuffer = new StringBuffer("");
        }

        p = new DRS_SerialProtocol(p_c.LEV2_R5, mHandler, bluetoothService_obj);

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
                        current_vbat.setText("0.0 [V]");

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
                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_on));
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
                        intent.putExtra("Firmware","L2R5");
                        startActivity(intent);
                    }
                    break;

                case R.id.start :
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        start.setImageDrawable(getResources().getDrawable(R.drawable.music_play));

                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP) {
                        try{
                            Thread.sleep(300);
                        }catch (InterruptedException e){}

                        switch (current_light_state) {
                            case 1:
                                bright_mMediaPlayer.stop();
                                dark_mMediaPlayer.stop();

                                try {
                                    bright_mMediaPlayer.prepare();
                                    dark_mMediaPlayer.prepare();
                                } catch (IOException e) {
                                }


                                bright_mMediaPlayer.seekTo(0);
                                bright_mMediaPlayer.start();

                                MUSIC_RC_DATA[0] = 100;
                                MUSIC_RC_DATA[1] = 60;
                                MUSIC_RC_DATA[2] = 100;
                                MUSIC_RC_DATA[3] = 60;

                                break;

                            case -1:
                                bright_mMediaPlayer.stop();
                                dark_mMediaPlayer.stop();

                                try {
                                    bright_mMediaPlayer.prepare();
                                    dark_mMediaPlayer.prepare();
                                } catch (IOException e) {
                                }

                                dark_mMediaPlayer.seekTo(0);
                                dark_mMediaPlayer.start();

                                MUSIC_RC_DATA[0] = 50;
                                MUSIC_RC_DATA[1] = 30;
                                MUSIC_RC_DATA[2] = 50;
                                MUSIC_RC_DATA[3] = 30;

                                break;

                            default:
                                break;

                        }

                        start.setImageDrawable(getResources().getDrawable(R.drawable.music_unplay));
                    }
                    break;

                case R.id.finish :
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        finish.setImageDrawable(getResources().getDrawable(R.drawable.music_stop));

                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP) {
                        try{
                            Thread.sleep(300);
                        }catch (InterruptedException e){}

                        bright_mMediaPlayer.stop();
                        dark_mMediaPlayer.stop();

                        try {
                            bright_mMediaPlayer.prepare();
                            dark_mMediaPlayer.prepare();
                        } catch (IOException e) {}

                        MUSIC_RC_DATA[0] = 0;
                        MUSIC_RC_DATA[1] = 0;
                        MUSIC_RC_DATA[2] = 0;
                        MUSIC_RC_DATA[3] = 0;

                        finish.setImageDrawable(getResources().getDrawable(R.drawable.music_upstop));
                    }
                    break;



            }
            return true;
        }
    };

    public class SensorListener implements SensorEventListener{
        float x,y,z;
        @Override
        public void onSensorChanged(SensorEvent event) {
//            if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED){
            float[] v = event.values;
            switch(event.sensor.getType()) {
                case Sensor.TYPE_LIGHT:
                    float current_light = v[0];
                    mHandler.obtainMessage(SENSOR_VALUE,(int)current_light,-1).sendToTarget();

                    break;
            }
//            }
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
                p.make_send_DRS(DRS_Constants.NOTHING);
                p.make_send_DRS(DRS_Constants.POWER_OFF);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bluetoothService_obj.device != null) {
            tmpdevice = bluetoothService_obj.device;
            bluetoothService_obj.stop();
        }

        bright_mMediaPlayer.stop();
        dark_mMediaPlayer.stop();
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
}
