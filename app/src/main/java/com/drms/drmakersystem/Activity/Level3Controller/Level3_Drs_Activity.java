package com.drms.drmakersystem.Activity.Level3Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.drms.drmakersystem.Activity.EducationAcitivty;
import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.Communication.Protocol.DRS_Constants;
import com.drms.drmakersystem.Communication.Protocol.DRS_SerialProtocol;
import com.drms.drmakersystem.Communication.Protocol.MSP;
import com.drms.drmakersystem.Communication.Protocol.STK500v1.UploadManager;
import com.drms.drmakersystem.FileManagement.FileManagement;

public class Level3_Drs_Activity extends Level3_JoystickActivity {

    private DRS_SerialProtocol drs;
    private DRMS drms;
    private MSP msp;
    private SendThread mthread;
    private MSPThread mspThread;
    private Intent intent;
    private int firmware = -1;
    private boolean running = false;


    private Handler drsHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private int week;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drms = (DRMS)this.getApplication();
        drs = new DRS_SerialProtocol(DRS_Constants.LEV3_R1,drsHandler, mBluetoothService);
        if(week == -1){
            Toast.makeText(this, "실행 중 에러가 발생하였습니다.",Toast.LENGTH_SHORT).show();
        }

        upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(Level3_Drs_Activity.this);

                if(week == 11) {

                    builder.setTitle("드론 선택").setPositiveButton("QUAD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (mthread != null) {
                                mthread.setRunning(false);
                            }
                            if (mspThread != null)
                                mspThread.setRunning(false);
                            if (usbService != null) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                }
                                ;
                                UploadManager manager =
                                        new UploadManager(Level3_Drs_Activity.this, mBluetoothService, new FileManagement(Level3_Drs_Activity.this).readBTAddress()[1], firmware) {

                                            @Override
                                            public void setDismissListener() {
                                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialogInterface) {
                                                        if (stk500 != null) {
                                                            stk500.running = false;
                                                            stk500 = null;
                                                        }
//                mBluetoothService.stop();
                                                        mBluetoothService.setmHandler(BackUpHandller);
                                                        mBluetoothService.setProtocol("DRS");
                                                        Log.d("Finished Upload", "set Protocol as DRS");

                                                        setProtocol(week);
                                                    }
                                                });
                                            }
                                        };
                                manager.setDismissListener();
                            }
                        }
                    });
                    builder.setNegativeButton("HEX", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (mthread != null) {
                                mthread.setRunning(false);
                            }
                            if (mspThread != null)
                                mspThread.setRunning(false);
                            if (usbService != null) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                }
                                ;
                                UploadManager manager =
                                        new UploadManager(Level3_Drs_Activity.this, mBluetoothService, new FileManagement(Level3_Drs_Activity.this).readBTAddress()[1], firmware + 1) {

                                            @Override
                                            public void setDismissListener() {
                                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialogInterface) {
                                                        if (stk500 != null) {
                                                            stk500.running = false;
                                                            stk500 = null;
                                                        }
//                mBluetoothService.stop();
                                                        mBluetoothService.setmHandler(BackUpHandller);
                                                        mBluetoothService.setProtocol("DRS");
                                                        Log.d("Finished Upload", "set Protocol as DRS");

                                                        setProtocol(week);
                                                    }
                                                });
                                            }
                                        };
                                manager.setDismissListener();
                            }
                        }

                    });
                    dialog = builder.create();
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            Toast.makeText(Level3_Drs_Activity.this,"업로드 취소", Toast.LENGTH_SHORT).show();

                        }
                    });
                    dialog.show();
                }
                else{

                    if (mthread != null) {
                        mthread.setRunning(false);
                    }
                    if (mspThread != null)
                        mspThread.setRunning(false);
                    if (usbService != null) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        ;
                        UploadManager manager =
                                new UploadManager(Level3_Drs_Activity.this, mBluetoothService, new FileManagement(Level3_Drs_Activity.this).readBTAddress()[1], firmware) {

                                    @Override
                                    public void setDismissListener() {
                                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialogInterface) {
                                                if (stk500 != null) {
                                                    stk500.running = false;
                                                    stk500 = null;
                                                }
//                mBluetoothService.stop();
                                                mBluetoothService.setmHandler(BackUpHandller);
                                                mBluetoothService.setProtocol("DRS");
                                                Log.d("Finished Upload", "set Protocol as DRS");

                                                setProtocol(week);
                                            }
                                        });
                                    }
                                };
                        manager.setDismissListener();
                    }
                }

            }
        });




    }

    private class SendThread extends Thread{

        private int[] rc;

        public SendThread() {
            super();
            mBluetoothService.setProtocol("DRS");
            setRunning(true);
            start();
        }

        @Override
        public void run() {
            super.run();
            while(running){
                int[] input = makeData();
                drs.make_send_DRS(DRS_Constants.LEV3_R1_STATE1,input);

                try{
                    Thread.sleep(   50);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                drs.make_send_DRS(DRS_Constants.VBAT);
                try{
                    Thread.sleep(50);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }


        void setRunning(boolean power){
             running = power;
        }

        boolean isRunning(){
            return running;
        }

        public int[] makeData(){
            int[] dst = new int[22];
            int[] rc = drms.getRcdata();
            int index = 0;

            for(int i=0 ; i<4; i++){
                dst[index++] = (byte)((rc[i] / 256) & 0xff);
                dst[index++] = (byte)((rc[i] % 256) & 0xff);
            }

            for(int i=0 ; i<7; i++){
                if(i==1) {
                    dst[index++] = drms.getDRONE_SPEED()/10;
                    continue;
                }
                if(btn_status[i])
                    dst[index++] = (byte)200;
                else
                    dst[index++] = (byte)100;
            }

            dst[index++] = drms.getTream()[0] + 127;
            dst[index++] = drms.getTream()[1] + 127;
            dst[index++] = drms.getTream()[2] + 127;

            dst[index++] = (byte)drms.getResistor()[0];
            dst[index++] =  (byte)drms.getResistor()[1];


            return dst;
        }
    }

    private class MSPThread extends Thread{
        private boolean running = false;
        private int[] rc;

        public MSPThread() {
            super();
            setRunning(true);
            start();
        }

        @Override
        public void run() {
            super.run();
            while(running){
                mBluetoothService.setProtocol("MSP");
                msp.sendRequestMSP_SET_RAW_RC(drms.getRcdata());
                int [] rc = drms.getRcdata();


                try{
                    Thread.sleep(20);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }


        void setRunning(boolean power){
            running = power;
        }

        boolean isRunning(){
            return running;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        week = getIntent().getIntExtra(EducationAcitivty.WEEK,-1);
        setProtocol(week);

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mthread != null){
            mthread.setRunning(false);
        }
        if(mspThread != null)
            mspThread.setRunning(false);


    }

    private void setProtocol(int round){
        switch(round){
            case 1 : case 3 : case 5 : case 7 : case 9 : case 11 :
                Toast.makeText(this,"MSP", Toast.LENGTH_SHORT).show();
                msp = new MSP(mBluetoothService,drsHandler);
                mspThread = new MSPThread();
                firmware = DRMS.LEV3_DRONE;
                //mspThread.start();
                break;

            case 2 : case 4: case 6: case 8: case 10:
                Toast.makeText(this,"DRS",Toast.LENGTH_SHORT).show();
                drs = new DRS_SerialProtocol(DRS_Constants.LEV3_R1,drsHandler, mBluetoothService);
                mthread = new SendThread();
                //mthread.start();
                if(round == 2)
                    firmware = DRMS.LEV3_2;
                else if(round == 4)
                    firmware = DRMS.LEV3_4;
                else if(round == 6)
                    firmware = DRMS.LEV3_6;
                else if(round == 8)
                    firmware = DRMS.LEV3_8;
                else if(round == 10)
                    firmware = DRMS.LEV3_10;
                else if(round == 12)
                    firmware = DRMS.LEV3_12;
                break;

            default :
                finish();

        }
    }
}
