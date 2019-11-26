package com.drms.drmakersystem.Lev2_Controller;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.Communication.BluetoothService;
import com.drms.drmakersystem.Communication.DRS_Constants;
import com.drms.drmakersystem.Communication.DRS_SerialProtocol;
import com.drms.drmakersystem.Drone_Controller.UploadActivity;
import com.drms.drmakersystem.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class Cont2_2_Activity extends AppCompatActivity {

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

    private static final int FILEDOWNLOAD_COMP = 10;

    private static final int CURRENT_ROTATE = 20;

    private static final int ROLL_ROTATE = 30;

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

    private ImageView[] rotate = new ImageView[6];
    private ImageView all;

    private TextView current_vbat;


    /////////////////////////////////////////////////////////////////////////////////////////////////

    private int[] rotate_state = new int[6];
    private int[] angle_TO_Degree = {0,0,0,0,0,0};

    private static final int CURRENT_VBAT = 100;

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
                            Thread mThread = new Send_thread();
                            running = true;
                            mThread.start();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while(running){
                                        if(power_state) {
                                            mHandler.obtainMessage(ROLL_ROTATE, -1, -1).sendToTarget();
                                            try {
                                                Thread.sleep(2);
                                            } catch (InterruptedException e) {
                                            };
                                        }
                                    }
                                }
                            }).start();
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

                case CURRENT_ROTATE :
                    int[] data = (int[])msg.obj;

                    break;

                case CURRENT_VBAT :


                    break;

                case DRS_PROTOCOL :
                    int level_round = msg.arg1;
                    int command = msg.arg2;
                    int[] payload =(int[])msg.obj;

                    if(level_round == DRS_Constants.LEV2_R2){
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

                case ROLL_ROTATE :
                    for(int i=0 ; i<6; i++){
                        if(rotate_state[i] == 200){
                            angle_TO_Degree[i] += 3;

                            if(angle_TO_Degree[i] > 360)
                                angle_TO_Degree[i] = 3;
                        }

                        rotate[i].setRotation(angle_TO_Degree[i]);
                    }


                    break;



            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont2_2);

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

        rotate[0] = (ImageView)findViewById(R.id.one);
        rotate[1] = (ImageView)findViewById(R.id.two);
        rotate[2] = (ImageView)findViewById(R.id.three);
        rotate[3] = (ImageView)findViewById(R.id.four);
        rotate[4] = (ImageView)findViewById(R.id.five);
        rotate[5] = (ImageView)findViewById(R.id.six);

        all = (ImageView)findViewById(R.id.all);
        all.setOnTouchListener(rotateTouchListener);

        for(int i=0 ; i<6; i++)
            rotate[i].setOnTouchListener(rotateTouchListener);

        for(int i=0 ; i<6 ; i++)
            rotate_state[i] = 100;

        current_vbat = (TextView)findViewById(R.id.current_vbat);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorEventListener = new SensorListener();


        if (bluetoothService_obj == null) {
            bluetoothService_obj = new BluetoothService(this, mHandler, "L2R2","DRS");

            mOutStringBuffer = new StringBuffer("");
        }

        p = new DRS_SerialProtocol(p_c.LEV2_R2, mHandler, bluetoothService_obj);


    }



    private class Send_thread extends Thread{
        public Send_thread() {
            super();
        }

        @Override
        public void run() {
            while(running){
                if(power_state == true) {
                    p.make_send_DRS(p_c.LOTTO_STATE, rotate_state);

                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "InterruptedException for sending data");
                    }

                }
                else{
                    Log.d(TAG,"Power off");
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "InterruptedException for sending data");
                    }
                }

                p.make_send_DRS(p_c.CURRENT_VBAT);

                try{
                    Thread.sleep(30);
                }catch (InterruptedException e){

                }
            }
        }
    }

    public View.OnTouchListener rotateTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int selected = -1;


            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                switch(view.getId()){
                    case R.id.one :
                        rotate_state[0] = 200;

                        break;

                    case R.id.two :
                        rotate_state[1] = 200;

                        break;

                    case R.id.three :
                        rotate_state[2] = 200;

                        break;

                    case R.id.four :
                        rotate_state[3] = 200;

                        break;

                    case R.id.five :
                        rotate_state[4] = 200;

                        break;

                    case R.id.six :
                        rotate_state[5] = 200;

                        break;

                    case R.id.all :
                        for(int i=0 ; i<6; i++)
                            rotate_state[i] = 200;

                        break;
                }
                return true;
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                switch(view.getId()) {
                    case R.id.one:
                        rotate_state[0] = 100;

                        break;

                    case R.id.two:
                        rotate_state[1] = 100;

                        break;

                    case R.id.three:
                        rotate_state[2] = 100;

                        break;

                    case R.id.four:
                        rotate_state[3] = 100;

                        break;

                    case R.id.five:
                        rotate_state[4] = 100;

                        break;

                    case R.id.six:
                        rotate_state[5] = 100;

                        break;

                    case R.id.all:
                        for (int i = 0; i < 6; i++)
                            rotate_state[i] = 100;

                        break;
                }
            }
            return true;
        }
    };



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
                        p.make_send_DRS(p_c.POWER_ON);
                        try{
                            Thread.sleep(35);
                        }catch (InterruptedException e){};

                    }
                    else if(power_state == true && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && event.getAction() == MotionEvent.ACTION_DOWN){
                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
                        power_state = false;
                        p.make_send_DRS(p_c.POWER_OFF);
                        try{
                            Thread.sleep(35);
                        }catch (InterruptedException e){};

                    }
                    break;

                case R.id.motor_init:
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && event.getAction() == MotionEvent.ACTION_DOWN)
                        p.make_send_DRS(p_c.MOTOR_INIT);
                    try{
                        Thread.sleep(35);
                    }catch (InterruptedException e){};
                        break;

                case R.id.upload:

                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(getApplicationContext(),UploadActivity.class);
                        intent.putExtra("Firmware","L2R2");
                        startActivity(intent);
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
            if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED){
                float[] v = event.values;
                switch(event.sensor.getType()) {
                    case Sensor.TYPE_LIGHT:

                        break;

                    case Sensor.TYPE_GYROSCOPE:

                        break;
                }
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

        if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
            p.make_send_DRS(p_c.POWER_OFF);
            p.make_send_DRS(p_c.NOTHING);
            power_state = false;
            try{
                Thread.sleep(35);
            }catch (InterruptedException e){};
        }
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

}
