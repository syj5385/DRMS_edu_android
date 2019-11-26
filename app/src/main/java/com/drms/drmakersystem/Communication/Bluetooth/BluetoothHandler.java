package com.drms.drmakersystem.Communication.Bluetooth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import com.drms.drmakersystem.Communication.Protocol.DRS_Constants;
import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.R;
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

    private Bitmap[] vbat_img = new Bitmap[3];

    public BluetoothHandler(Context context, Activity activity, BluetoothService mBluetoothService, ImageView image, Icon bticon, ImageView vbat) {
        this.mBluetoothService = mBluetoothService;
        this.context = context;
        this.activity = activity;
        this.image = image;
        this.bticon = bticon;
        this.vbat = vbat;
        vbat_img[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.controller_image11), bticon.getImg1().getWidth(), bticon.getImg1().getHeight(), false);
        vbat_img[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.controller_image10), bticon.getImg1().getWidth(), bticon.getImg1().getHeight(), false);
        vbat_img[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.controller_image9), bticon.getImg1().getWidth(), bticon.getImg1().getHeight(), false);
}

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case BluetoothService.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                    case BluetoothService.STATE_CONNECTED:
                        Log.d(TAG, "Bluetooth : " + mBluetoothService);
                        image.setImageDrawable(new BitmapDrawable(bticon.getImg2()));

                        break;

                    case BluetoothService.STATE_FAIL:
                        Log.d(TAG, "Bluetooth : " + mBluetoothService);
                        mBluetoothService.stop();
                        if (isContinuetryConnect) {
                            Intent intent = new Intent();
                            intent.putExtra(BluetoothService.EXTRA_DEVICE_ADDRESS, new FileManagement(context).readBTAddress()[1]);
                            mBluetoothService.getDeviceInfo(intent);
                            //:Toast.makeText(context, "connection (" + (Connectioncount + 1) + " / 3)", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "연결 실패", Toast.LENGTH_SHORT).show();
                            Connectioncount = 0;
                            activity.finish();

                        }
                        break;

                    case BluetoothService.STATE_DISCONNECTED:
                        image.setImageDrawable(new BitmapDrawable(bticon.getImg1()));
                        Toast.makeText(context, "블루투스 연결이 끊어졌습니다.", Toast.LENGTH_SHORT).show();
                        activity.finish();
                        break;

                    case BluetoothService.STATE_LISTEN:
                        image.setImageDrawable(new BitmapDrawable(bticon.getImg1()));
                        Toast.makeText(context, "블루투스 연결이 끊어졌습니다.", Toast.LENGTH_SHORT).show();
                        activity.finish();
                        break;

                }
                break;

            case BluetoothService.DRS_PROTOCOL:
                int level_round = msg.arg1;
                int command = msg.arg2;
                int[] payload = (int[]) msg.obj;

                switch (command) {
                    case DRS_Constants.VBAT:
                        float vbat_temp = ((float) payload[0] / 30);
                        DecimalFormat form = new DecimalFormat("#.##");
                        float vbat_val = vbat_temp;
                        Log.d(TAG, "Current vbat : " + form.format(vbat_val));
                        if (vbat != null) {
                            if (vbat_val >= 4) {
                                vbat.setImageDrawable(new BitmapDrawable(vbat_img[0]));
                            } else if (vbat_val >= 3.6) {
                                vbat.setImageDrawable(new BitmapDrawable(vbat_img[1]));
                            } else
                                vbat.setImageDrawable(new BitmapDrawable(vbat_img[2]));
                        }

                        break;
                }

                break;
        }
    }

    public void setContinuetryConnect(boolean trying){
        isContinuetryConnect = false;
    }
}
