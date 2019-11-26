package com.drms.drmakersystem.Communication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.etc.Icon;

import java.text.DecimalFormat;

import static android.content.ContentValues.TAG;

/**
 * Created by jjun on 2018. 2. 23..
 */

public class BluetoothHandler extends Handler {

    protected BluetoothService mBluetoothService;
    protected Context context;
    protected Activity activity;
    protected ImageView image;
    protected Icon bticon;
    protected ImageView vbat;
    private int Connectioncount = 0 ;

    public boolean isContinuetryConnect = true;


    public BluetoothHandler(Context context, Activity activity, BluetoothService mBluetoothService, ImageView image, Icon bticon, ImageView vbat) {
        this.mBluetoothService = mBluetoothService;
        this.context = context;
        this.activity = activity;
        this.image = image;
        this.bticon = bticon;
        this.vbat = vbat;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case BluetoothService.MESSAGE_STATE_CHANGE :
                switch(msg.arg1){
                    case BluetoothService.STATE_CONNECTED :
                        Log.d(TAG,"Bluetooth : " + mBluetoothService);
                        image.setImageDrawable(new BitmapDrawable(bticon.getImg2()));

                        break;

                    case BluetoothService.STATE_FAIL :
                        Log.d(TAG,"Bluetooth : " + mBluetoothService);
                        mBluetoothService.stop();
                        if(isContinuetryConnect) {
                            if (++Connectioncount < 3) {
                                Intent intent = new Intent();
                                intent.putExtra(BluetoothService.EXTRA_DEVICE_ADDRESS, new FileManagement(context).readBTAddress()[1]);
                                mBluetoothService.getDeviceInfo(intent);
                                Toast.makeText(context, "connection (" + (Connectioncount + 1) + " / 3)", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "연결 실패", Toast.LENGTH_SHORT).show();
                                Connectioncount = 0;
                                activity.finish();
                            }
                        }
                        break;

                    case BluetoothService.STATE_DISCONNECTED :
                        image.setImageDrawable(new BitmapDrawable(bticon.getImg1()));
                        Toast.makeText(context,"블루투스 연결이 끊어졌습니다.", Toast.LENGTH_SHORT).show();
                        activity.finish();
                        break ;

                    case BluetoothService.STATE_LISTEN :
                        image.setImageDrawable(new BitmapDrawable(bticon.getImg1()));
                        Toast.makeText(context,"블루투스 연결이 끊어졌습니다.", Toast.LENGTH_SHORT).show();
                        activity.finish();
                        break;

                }
                break;

            case BluetoothService.DRS_PROTOCOL :
                int level_round = msg.arg1;
                int command = msg.arg2;
                int[] payload =(int[])msg.obj;


                switch(command){
                    case DRS_Constants.VBAT :
                        float vbat_temp = ((float)payload[0] / 30);
                        DecimalFormat form = new DecimalFormat("#.##");
                        float vbat = vbat_temp;
                        Log.d(TAG, "Current vbat : " + form.format(vbat) );
                        break;
                    }

                break;
        }
    }

    public void setContinuetryConnect(boolean trying){
        isContinuetryConnect = false;
    }
}
