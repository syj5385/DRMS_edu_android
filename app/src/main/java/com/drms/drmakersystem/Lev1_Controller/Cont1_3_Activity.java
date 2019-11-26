package com.drms.drmakersystem.Lev1_Controller;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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

public class Cont1_3_Activity extends AppCompatActivity {
    // 모빌
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

    ////////////////////////controll button & touch image///////////////////////

    private ImageView motor_init, power, blue_btn, upload;
    private ImageView clock_rotate, counterclock_rotate, clock_vel_up, clock_vel_down, counter_vel_up, counter_vel_down;
    private ImageView character;
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
                            Toast.makeText(getApplicationContext(), "블루투스 연결에 성공하였습니다.\nDevice Address : "+bluetoothService_obj.address1, Toast.LENGTH_SHORT).show();
                            blue_btn.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth_cnt));
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
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont1_3);

        getSupportActionBar().hide();
        Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        motor_init = (ImageView)findViewById(R.id.motor_init);
        motor_init.setOnTouchListener(mTouchListener);

        power = (ImageView)findViewById(R.id.onoff);
        power.setOnTouchListener(mTouchListener);

        blue_btn = (ImageView)findViewById(R.id.bluetooth);
        blue_btn.setOnTouchListener(mTouchListener);

        upload = (ImageView)findViewById(R.id.upload);
        upload.setOnTouchListener(mTouchListener);

        clock_rotate = (ImageView)findViewById(R.id.clock_rotate);
        clock_rotate.setOnTouchListener(mTouchListener);

        counterclock_rotate = (ImageView)findViewById(R.id.counterclock_rotate);
        counterclock_rotate.setOnTouchListener(mTouchListener);

        clock_vel_up = (ImageView)findViewById(R.id.clock_up);
        clock_vel_up.setOnTouchListener(mTouchListener);

        clock_vel_down = (ImageView)findViewById(R.id.clock_down);
        clock_vel_down.setOnTouchListener(mTouchListener);

        counter_vel_up = (ImageView)findViewById(R.id.counterclock_up);
        counter_vel_up.setOnTouchListener(mTouchListener);

        counter_vel_down = (ImageView)findViewById(R.id.counterclock_down);
        counter_vel_down.setOnTouchListener(mTouchListener);

        character = (ImageView)findViewById(R.id.character);

        if(bluetoothService_obj == null){
            bluetoothService_obj = new BluetoothService(this, mHandler,"L0R2","DRS");
            mOutStringBuffer = new StringBuffer("");
        }

    }

    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(v.getId()){
                case R.id.motor_init :
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED){
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            sendMessage("a\n",MODE_REQUEST);
                        }
                    }
                    break;

                case R.id.onoff :
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED){
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            if(power_state == false) {
                                power_state = true;
                                sendMessage("b\n", MODE_REQUEST);
                                power.setImageDrawable(getResources().getDrawable(R.drawable.power_on));
                                Toast.makeText(getApplicationContext(),"시동이 걸렸습니다.",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                sendMessage("c\n",MODE_REQUEST);
                                power_state= false;
                                power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
                                Toast.makeText(getApplicationContext(),"시동이 꺼졌습니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"블루투스를 먼저 연결해 주세요",Toast.LENGTH_SHORT).show();
                    }
                    break;

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

                        blue_btn.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth));

                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
                        power_state = false;


                        bluetoothService_obj.stop();
                    }

                    break;

                case R.id.upload :
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {

                        Intent intent = new Intent(getApplicationContext(),UploadActivity.class);
                        intent.putExtra("Firmware","L1R3");
                        startActivity(intent);

                    }
                    break;

                case R.id.clock_rotate :
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state){
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            character.setImageDrawable(getResources().getDrawable(R.drawable.roll_left));
                            sendMessage("d\n",MODE_REQUEST);
                        }
                        else if(event.getAction() == MotionEvent.ACTION_UP){
                            character.setImageDrawable(getResources().getDrawable(R.drawable.roll));
                            sendMessage("e\n",MODE_REQUEST);
                            break;
                        }
                    }
                    break;

                case R.id.counterclock_rotate :
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state){
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            character.setImageDrawable(getResources().getDrawable(R.drawable.roll_right));
                            sendMessage("f\n",MODE_REQUEST);
                        }
                        else if(event.getAction() == MotionEvent.ACTION_UP){
                            character.setImageDrawable(getResources().getDrawable(R.drawable.roll));
                            sendMessage("g\n",MODE_REQUEST);
                        }
                    }
                    break;

                case R.id.clock_up :
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state ){
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            sendMessage("h\n",MODE_REQUEST);
                        }
                    }
                    break;

                case R.id.clock_down :
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state){
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            sendMessage("i\n",MODE_REQUEST);
                        }
                    }
                    break;

                case R.id.counterclock_up :
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state){
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            sendMessage("j\n",MODE_REQUEST);
                        }
                    }
                    break;

                case R.id.counterclock_down :
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED && power_state){
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            sendMessage("k\n",MODE_REQUEST);
                        }
                    }
                    break;
            }
            return true;
        }
    };

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
                InputStream is = getResources().openRawResource(R.raw.r1_3);

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/drs1_3_firmware.hex");
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
