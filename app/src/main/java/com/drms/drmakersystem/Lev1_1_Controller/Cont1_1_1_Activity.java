package com.drms.drmakersystem.Lev1_1_Controller;

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
import android.view.WindowManager;
import android.widget.ImageView;
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

public class Cont1_1_1_Activity extends AppCompatActivity {

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
    private static final int MOLE = 11;

    private BluetoothService bluetoothService_obj = null;
    public StringBuffer mOutStringBuffer;

    private long lastTimeBackPressed;

    private boolean power_state = false;

    private BluetoothDevice tmpdevice;

    private boolean file_state = false;

    private SensorManager mSensorManager;
    private SensorEventListener mSensorEventListener;

    private ProcessSerialData mProcessSerialData;

    private boolean running;
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private Thread mthread;

    private int delay_time = 2000;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ImageView bluetooth, power,upload ;

    private ImageView start, end;

    private ImageView[] mole = new ImageView[3];

    private ImageView[] speed = new ImageView[3];

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
                            String temp = "#DRS^4^LEV1_1_R1^NULL$\n";
                            byte[] temp_byte = temp.getBytes();
                            bluetoothService_obj.write(temp_byte,MODE_REQUEST);
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

                case MOLE :


                    boolean[] mole_state = new boolean[3];
                    mole_state= (boolean[])msg.obj;
                    for(int index = 0 ; index < 3; index++){
                        if(mole_state[index] == true){
                            mole[index].setImageDrawable(getResources().getDrawable(R.drawable.mole_on));
                        }
                        else{
                            mole[index].setImageDrawable(getResources().getDrawable(R.drawable.mole_off));
                        }
                    }

                    break;

            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont1_1_1);

        getSupportActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        bluetooth = (ImageView) findViewById(R.id.bluetooth);
        bluetooth.setOnTouchListener(mTouchListener);

        power = (ImageView) findViewById(R.id.onoff);
        power.setOnTouchListener(mTouchListener);


        upload = (ImageView) findViewById(R.id.upload);
        upload.setOnTouchListener(mTouchListener);

        start = (ImageView)findViewById(R.id.start);
        start.setOnTouchListener(mTouchListener);

        end = (ImageView)findViewById(R.id.end);
        end.setOnTouchListener(mTouchListener);

        mole[0] = (ImageView)findViewById(R.id.mole1);
        mole[1] = (ImageView)findViewById(R.id.mole2);
        mole[2] = (ImageView)findViewById(R.id.mole3);

        speed[0] = (ImageView)findViewById(R.id.speed1);
        speed[0].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    delay_time = 5000;

                }
                return true;
            }
        });

        speed[1] = (ImageView)findViewById(R.id.speed2);
        speed[1].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    delay_time = 3000;
                }
                return true;
            }
        });

        speed[2] = (ImageView)findViewById(R.id.speed3);
        speed[2].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    delay_time = 1000;
                }
                return true;
            }
        });



        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorEventListener = new SensorListener();

        if (bluetoothService_obj == null) {
            bluetoothService_obj = new BluetoothService(this, mHandler, "L0R9","DRS");

            mOutStringBuffer = new StringBuffer("");
        }

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
                        sendMessage("#DRS^8^LEV1_1_R1^POWER_ON$",MODE_REQUEST);

                    }
                    else if(power_state == true && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && event.getAction() == MotionEvent.ACTION_DOWN){
                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
                        power_state = false;
                        sendMessage("#DRS^9^LEV1_1_R1^POWER_OFF$",MODE_REQUEST);

                    }

                    else if(event.getAction() == MotionEvent.ACTION_DOWN && bluetoothService_obj.getState() != BluetoothService.STATE_CONNECTED){
                        Toast.makeText(getApplicationContext(),"블루투스를 연결해주세요",Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.upload:
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                        intent.putExtra("Firmware", "L1_1_R1");
                        startActivity(intent);
                    }
                    break;

                case R.id.start :
                    if(event.getAction() == MotionEvent.ACTION_DOWN
                            && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED
                            && power_state == true) {
                        Toast.makeText(Cont1_1_1_Activity.this, "OK", Toast.LENGTH_SHORT).show();
                        mthread = new Mole_thread();
                        running = true;
                        mthread.start();
                    }
                    else if(event.getAction() == MotionEvent.ACTION_DOWN && bluetoothService_obj.getState() != BluetoothService.STATE_CONNECTED){
                        Toast.makeText(getApplicationContext(),"블루투스를 연결해주세요",Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.end :
                    if(event.getAction() == MotionEvent.ACTION_DOWN
                            && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
                        running = false;
                        for (int i = 0; i < 3; i++) {
                            mole[i].setImageDrawable(getResources().getDrawable(R.drawable.mole_off));
                        }
                        sendMessage("#DRS^9^LEV1_1_R1^MOLE_STOP$", MODE_REQUEST);
                    }else if(event.getAction() == MotionEvent.ACTION_DOWN && bluetoothService_obj.getState() != BluetoothService.STATE_CONNECTED){
                        Toast.makeText(getApplicationContext(),"블루투스를 연결해주세요",Toast.LENGTH_SHORT).show();
                    }
                    break;


            }
            return true;
        }
    };

    public class Mole_thread extends Thread{

        private boolean mole_state[] = new boolean[3];

        private int step = 1;

        private int step_count = 0;


        private int random_number_for_1step;
        private int prev_random_number_for_1step;

        private int random_number_for_2step[]= new int[2];
        private int prev_random_number_for_2step[]= new int[2];

        private String[] mole_data = new String[3];



        public Mole_thread() {
            super();

            for(int index=0 ; index<3; index++)
                mole_state[index] = false;

            for(int index=0 ; index<2 ; index++){
                random_number_for_2step[index] = 0;
            }
        }

        @Override
        public void run() {
            while(running && step < 3 && bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state == true){

                    switch (step) {
                        case 1:
                            for (int index = 0; index < 3; index++)
                                mole_state[index] = false;

                            do {
                                random_number_for_1step = (int) (Math.random() * 3);
                            } while (prev_random_number_for_1step == random_number_for_1step);

                            mole_state[random_number_for_1step] = true;

                            for (int index = 0; index < 3; index++) {
                                if (mole_state[index] == true) {
                                    mole_data[index] = "ON";
                                } else {
                                    mole_data[index] = "OFF";
                                }
                            }

                            sendMessage("#DRS^14^LEV1_1_R1^M1" + mole_data[0] + "M2" + mole_data[1] + "M3" + mole_data[2] + "$", MODE_REQUEST);
                            mHandler.obtainMessage(MOLE,random_number_for_1step, -1, mole_state).sendToTarget();
                            try {
                                mthread.sleep(delay_time);
                            } catch (InterruptedException e) {}




                            prev_random_number_for_1step = random_number_for_1step;

                            step_count++;

                            if (step_count == 7) {
                                step++;
                                step_count = 0;
                            }

                            break;

                        case 2:
                            for (int index = 0; index < 3; index++)
                                mole_state[index] = false;

                            do {
                                random_number_for_2step[0] = (int) (Math.random() * 3);
                                random_number_for_2step[1] = (int) (Math.random() * 3);
                            } while ((prev_random_number_for_2step[0] == random_number_for_2step[0]
                                    && prev_random_number_for_2step[1] == random_number_for_2step[1])
                                    || (prev_random_number_for_2step[0] == random_number_for_2step[1]
                                    && prev_random_number_for_2step[1] == random_number_for_2step[0])
                                    || (random_number_for_2step[0] == random_number_for_2step[1]));


                            mole_state[random_number_for_2step[0]] = true;
                            mole_state[random_number_for_2step[1]] = true;


                            for (int index = 0; index < 3; index++) {
                                if (mole_state[index] == true) {
                                    mole_data[index] = "ON";
                                } else {
                                    mole_data[index] = "OFF";
                                }
                            }

                            sendMessage("#DRS^13^LEV1_1_R1^M1" + mole_data[0] + "M2" + mole_data[1] + "M3" + mole_data[2] + "$", MODE_REQUEST);
                            mHandler.obtainMessage(MOLE, random_number_for_2step[0], random_number_for_2step[1], mole_state).sendToTarget();
                            try {
                                mthread.sleep(delay_time);
                            } catch (InterruptedException e) {
                            }

                            for (int i = 0; i < 2; i++)
                                prev_random_number_for_2step[i] = random_number_for_2step[i];
                            step_count++;

                            if (step_count == 7) {
                                step++;
                                step_count = 0;
                                running = false;

                            }

                            break;
                    }


            }
            sendMessage("#DRS^9^LEV1_1_R1^MOLE_STOP$", MODE_REQUEST);
            for(int i=0; i<3; i++)
                mole_state[i] = false;

            mHandler.obtainMessage(MOLE,-1,-1,mole_state).sendToTarget();


        }
    }

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
        mSensorManager.unregisterListener(mSensorEventListener);
        running = false;
        sendMessage("#DRS^9^LEV1_1_R1^MOLE_STOP$", MODE_REQUEST);

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
