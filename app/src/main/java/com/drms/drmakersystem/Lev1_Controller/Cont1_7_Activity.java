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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

public class Cont1_7_Activity extends AppCompatActivity {

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

    private static final int THROTTLE_SIZE = 3;
    private static final int THROTTLE_LOCATION =4;

    private int thro_width = 0;
    private int thro_height = 0;
    private int thro_X = 0;
    private int thro_Y = 0;
    private boolean msg_state = false;
    float touch_Y;
    private boolean power_state = false;
    private boolean send_running = false;

    private boolean file_state = false;

    private BluetoothDevice tmpdevice;
    ////////////////////////controll button & touch image///////////////////////

    private ImageView throttle;
    private LinearLayout layout_thro;

    private ImageView power, motor_init, bluetooth, upload;
    private TextView current;


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
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont1_7);

        getSupportActionBar().hide();
        Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        motor_init = (ImageView)findViewById(R.id.motor_init);
        motor_init.setOnTouchListener(mTouchListener);

        bluetooth = (ImageView)findViewById(R.id.bluetooth);
        bluetooth.setOnTouchListener(mTouchListener);

        upload = (ImageView)findViewById(R.id.upload);
        upload.setOnTouchListener(mTouchListener);

        power = (ImageView)findViewById(R.id.onoff);
        power.setOnTouchListener(mTouchListener);

        current = (TextView)findViewById(R.id.velocity);

        layout_thro = (LinearLayout)findViewById(R.id.layout_thro);

        if(bluetoothService_obj == null){
            bluetoothService_obj = new BluetoothService(this, mHandler,"L0R6","DRS");
            mOutStringBuffer = new StringBuffer("");
        }

        layout_thro.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener(){
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                layout_thro.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
            }
        });
        layout_thro.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                thro_height = layout_thro.getHeight();
                layout_thro.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        throttle = new ImageView(this);
        LinearLayout.LayoutParams throparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        throttle.setLayoutParams(throparams);
        throttle.setImageDrawable(getResources().getDrawable(R.drawable.throttle));
        layout_thro.addView(throttle);

        layout_thro.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
                    if (power_state == true && event.getAction() == MotionEvent.ACTION_MOVE) {
                        touch_Y = event.getY();
                        throttle.setY(touch_Y);
                        if (touch_Y < 0)
                            throttle.setY(0);
                        if (touch_Y > thro_height - throttle.getHeight()) {
                            throttle.setY(thro_height - throttle.getHeight());
                        }
                        current.setText(String.valueOf(((thro_height-throttle.getHeight())-(int)throttle.getY())*255/(thro_height-throttle.getHeight())));
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        throttle.setY(thro_height - throttle.getHeight());
                        current.setText(String.valueOf(((thro_height-throttle.getHeight())-(int)throttle.getY())*255/(thro_height-throttle.getHeight())));
                    }
                }
                return true;
            }
        });
    }

    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(v.getId()){
                case R.id.motor_init :
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED){
                        if(event.getAction() == MotionEvent.ACTION_DOWN){
                            sendMessage("a"+'\n',MODE_REQUEST);
                        }
                        break;
                    }

                case R.id.onoff :
                    if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            if(power_state == false) {
                                sendMessage("b"+'\n', MODE_REQUEST);
                                Toast.makeText(getApplicationContext(),"시동이 걸렸습니다.",Toast.LENGTH_SHORT).show();
                                power.setImageDrawable(getResources().getDrawable(R.drawable.power_on));
                                throttle.setY(thro_height - throttle.getHeight());
                                power_state = true;
                            }
                            else{
                                sendMessage("c"+'\n',MODE_REQUEST);
                                power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
                                Toast.makeText(getApplicationContext(),"시동이 꺼졌습니다.",Toast.LENGTH_SHORT).show();
                                power_state = false;
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

                        bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.bluetooth));

                        power.setImageDrawable(getResources().getDrawable(R.drawable.power_off));
                        power_state = false;
                        send_running = false;


                        bluetoothService_obj.stop();
                    }

                    break;

                case R.id.upload :
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(getApplicationContext(),UploadActivity.class);
                        intent.putExtra("Firmware","L1R7");
                        startActivity(intent);
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
                InputStream is = getResources().openRawResource(R.raw.r1_7);
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/drs1_7_firmware.hex");
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

    class Thro_thread extends Thread{
        float t_width;
        float t_height;
        float t_X;
        float t_Y;

        @Override
        public void run() {

            while(send_running){
                if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
                    if (power_state == true) {
                        int cal_data = ((thro_height-throttle.getHeight())-(int)throttle.getY())*255/(thro_height-throttle.getHeight());
                        String send_data = String.valueOf(cal_data);
                        sendMessage('d' + send_data + '\n', MODE_REQUEST);
                        try {
                            Thread.sleep(80);
                        } catch (InterruptedException e) {
                            Log.e("Thread", "InterruptedException", e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed <1500) {
            if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
                send_running = false;
                sendMessage("z", MODE_REQUEST); // If the back_button is clicked, all PWM is stopped
                bluetoothService_obj.stop();
            }
            finish();
            return;
        }
        if(bluetoothService_obj.getState() == BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), "'뒤로' 버튼을 한번 더 누르면 블루투스 연결 해제 후 화면이 종료됩니다.  ", Toast.LENGTH_SHORT).show();
        }
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
        send_running = false;
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
        Thread thro_thread = new Thro_thread();
        thro_thread.start();
        send_running = true;
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
