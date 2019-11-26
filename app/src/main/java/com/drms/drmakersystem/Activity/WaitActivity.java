package com.drms.drmakersystem.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.Communication.UsbSerial.UsbService;
import com.drms.drmakersystem.R;

public class WaitActivity extends AppCompatActivity {

    public static final String MESSAGE = "message";

    private TextView message_t;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MESSAGE);

        message_t = findViewById(R.id.message);
        message_t.setText(message);

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();
            if (action.equals(UsbService.CONNECTED_CONTROLLER)){
                Toast.makeText(getApplicationContext(),"연결 되었습니다.",Toast.LENGTH_SHORT).show();

            }
            if (action.equals(UsbService.UNATTACHED_CONTROLLER)){
                Toast.makeText(getApplicationContext(),"조종기가 연결되지 않았습니다.",Toast.LENGTH_SHORT).show();

            }
            if(action.equals(UsbService.REQUEST_UPDATE_FIRMWARE)){
                Toast.makeText(getApplicationContext(),"조종기에 펌웨어를 업로드 해주세요.",Toast.LENGTH_SHORT).show();
            }

            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            finish();
        }
    };

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.CONNECTED_CONTROLLER);
        filter.addAction(UsbService.UNATTACHED_CONTROLLER);
        filter.addAction(UsbService.REQUEST_UPDATE_FIRMWARE);

        registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setFilter();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold,R.anim.appear);
    }
}