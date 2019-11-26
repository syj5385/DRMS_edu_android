package com.drms.drmakersystem.Activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.FileManagement.NoticeManager;
import com.drms.drmakersystem.View.MainView;
import com.drms.drmakersystem.View.SplashView;
import com.drms.drmakersystem.R;


public class MainActivity extends AppCompatActivity {

    private FileManagement mFileManagement;
    private MainView view;

    private String bt_name;
    private String bt_address;

    public static final int RESULT_ACTIVATION_FALSE = 100;
    public static final int RESULT_EXIT_NOTIFICATION = 101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MainView(MainActivity.this,MainActivity.this);
        setContentView(view);
        float[] scale = {
                getWindowManager().getDefaultDisplay().getWidth(),
                getWindowManager().getDefaultDisplay().getHeight()
        };

        view.getViewHandler().obtainMessage(SplashView.SET_SCREEN_SIZE,scale).sendToTarget();

        mFileManagement = new FileManagement(MainActivity.this,mHandler);
        if (mFileManagement.readBTAddress() != null) {
            if(mFileManagement.readBTAddress()[1] != null) {
                if (mFileManagement.readBTAddress()[1] != "") {
                    bt_name = mFileManagement.readBTAddress()[0];
                    bt_address = mFileManagement.readBTAddress()[1];
                } else {
//                    bt_name = "";
//                    bt_address = "";
                    mFileManagement.writeBtAddressOnFile("","");
                }
            }

        }

        BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(event.getAction() == KeyEvent.ACTION_UP) {

                finish();
            }
        }
        return true;
    }

    private Handler mHandler = new Handler();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NoticeManager.REQUEST_NOTICE){
            if(resultCode == RESULT_ACTIVATION_FALSE){
                Toast.makeText(getApplicationContext(),"어플리케이션을 종료합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
            if(resultCode == RESULT_EXIT_NOTIFICATION){

            }

        }

        if(requestCode == EducationAcitivty.REQUEST_ENABLE_BT){
            Log.d("BTenable","result : " + resultCode);
            if(resultCode == RESULT_CANCELED){
                Toast.makeText(this,"블루투스가 실행되지 않아 어플리케이션을 종료합니다.", Toast.LENGTH_SHORT).show();

                finish();
            }
        }
    }
}
