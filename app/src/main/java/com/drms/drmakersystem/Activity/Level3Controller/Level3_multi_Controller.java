package com.drms.drmakersystem.Activity.Level3Controller;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.drms.drmakersystem.Activity.ControllerActivity;
import com.drms.drmakersystem.Activity.EducationAcitivty;
import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.Communication.Bluetooth.BluetoothService;
import com.drms.drmakersystem.Communication.Protocol.DRS_Constants;
import com.drms.drmakersystem.Communication.Protocol.DRS_SerialProtocol;
import com.drms.drmakersystem.Communication.Protocol.MSP;
import com.drms.drmakersystem.Communication.UsbSerial.UsbService;

/**
 * Created by jjun on 2018. 8. 1..
 */

public class Level3_multi_Controller extends ControllerActivity {

    protected MSP msp;
    protected DRMS drms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();
        drms =(DRMS)this.getApplication();

    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case BluetoothService.Multiwii_PROTOCOL:
                    byte[] data = (byte[]) msg.obj;
                    msp.readMSP(data);
                    break;

                case MESSAGE_FROM_USBSERVERICE :
                    switch (msg.arg1) {
                        case UsbService.MESSAGE_FROM_SERIAL_PORT:
                            String data_r = (String) msg.obj;
                            Log.d("USBActivity",data_r);
                            break;

                        case UsbService.CTS_CHANGE:
//                            Toast.makeText(DroneMainActivity.this, "CTS_CHANGE", Toast.LENGTH_LONG).show();
                            break;
                        case UsbService.DSR_CHANGE:
//                            Toast.makeText(DroneMainActivity.this, "DSR_CHANGE", Toast.LENGTH_LONG).show();
                            break;

                    }

                    break;
            }
        }
    };

    public class SendThread extends Thread {
        public SendThread() {
            super();
            running = true;
            msp = new MSP(mBluetoothService,mHandler);
            mBluetoothService.setProtocol("MSP");
        }

        @Override
        public void run() {
            super.run();
            while(running) {

                final int[] rcData = drms.getRcdata();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(rcData[7] == 2000){
                            isArmed = true;
                            power_img.setImageDrawable(new BitmapDrawable(power.getImg2()));
                        }
                        else if(rcData[7] == 1000){
                            isArmed = false;
                            power_img.setImageDrawable(new BitmapDrawable(power.getImg1()));
                        }
                    }
                });
                if(!isArmed) {
                    int[] rc_temp = {1500, 1500, 1500, 1000, 1000, 1000, 1000, drms.getRcdata()[7]};
                    drms.setRcdata(rc_temp);
                }


                msp.sendRequestMSP_SET_RAW_RC(drms.getRcdata());
                try{
                    sleep(50);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
//                Log.d(TAG,"RC data : " + drms.getRcdata()[0] + ","+ drms.getRcdata()[1] +","+drms.getRcdata()[2] + "," + drms.getRcdata()[3]);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFilter();
        Intent bindingIntent = new Intent(this,UsbService.class);
        bindService(bindingIntent,usbConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mBluetoothService.setProtocol("DRS");
        running = false;

        try {
            unbindService(usbConnection);
            unregisterReceiver(usbReceiver);
        }catch (Exception e1){
            e1.printStackTrace();
        }
        int[] rc_temp = {1500, 1500, 1500, 1000, 1000, 1000, 1000, drms.getRcdata()[7]};
        drms.setRcdata(rc_temp);
        for(int i=0 ;i<5; i++) {
            msp.sendRequestMSP_SET_RAW_RC(drms.getRcdata());
            try{
                Thread.sleep(20);
            }catch (InterruptedException e){

            }
        }

        mBluetoothService.stop();
        mBluetoothService = null;
        finish();
    }

    private UsbService usbService;
    public static final int MESSAGE_FROM_USBSERVERICE = 11;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
            Toast.makeText(Level3_multi_Controller.this,"Controller Service Connected",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(UsbService.ACTION_USB_DETACHED)){
                Toast.makeText(Level3_multi_Controller.this,"조종기가 분리되어 종료합니다.",Toast.LENGTH_SHORT).show();
                finish();
                drms.initializeMultiData();
            }
        }
    };

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_DETACHED);
        registerReceiver(usbReceiver, filter);
    }


    @Override
    protected void onStop() {
        super.onStop();

    }
}
