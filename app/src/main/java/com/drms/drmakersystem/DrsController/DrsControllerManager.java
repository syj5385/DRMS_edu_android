package com.drms.drmakersystem.DrsController;

import android.app.Service;
import android.os.Handler;
import android.util.Log;

import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.Communication.UsbSerial.UsbService;
import com.drms.drmakersystem.DRS_data;


/**
 * Created by jjun on 2018. 5. 19..
 */

public class DrsControllerManager {

    private int roll, pitch, yaw, throttle, left_resistor, right_resistor;
    private int power, d1, d2, d3, d4, d5, d6;
    private int roll_tream, pitch_tream, yaw_tream;

    private static final String TAG = "DrsControllerManager";

    private UsbService service;

    private Handler mHandler;

    public static final int CONT_DIGIAL = 500;
    public static final int POWER = 0;
    public static final int D1 = 1;
    public static final int D2 = 2;
    public static final int D3 = 3;
    public static final int D4 = 4;
    public static final int D5 = 5;
    public static final int D6 = 6;

    public static final int R_T = 7;
    public static final int P_T = 8;
    public static final int Y_T = 9;

    public static final int ON   = 1;
    public static final int OFF = 0;

    private DRMS drms;
    public DrsControllerManager(UsbService service, Handler mHandler) {
        this.service  = service;
        drms = (DRMS)service.getApplication();
        this.mHandler = mHandler ;
    }

    public boolean processControllerData(byte[] data){
        if(data.length > 0) {

//            Log.d(TAG,"size : " + data.length);
            if ((char) data[0] == '#' && data.length == 28) {
                int[] recvdata = new int[14];
                String header = "";
                for (int i = 0; i < 5; i++) {
                    header += (char) data[i];
                }
//                Log.d(TAG, "header : " + header);
                int index = 5;
                byte checksum = 0;
                if(!header.equals("#cont")){
                    service.setReceived(true);
                    return false;
                }
//                Log.d(TAG,"Controller data");
                for(int i=0; i<22; i++){
                    checksum ^= data[index++];
                }

                if(checksum == data[data.length-1]) {
                    index = 5;

                    roll = read16(data[index++], data[index++]);
                    pitch = read16(data[index++], data[index++]);
                    yaw = read16(data[index++], data[index++]);
                    throttle = read16(data[index++], data[index++]);

                    power = read8(data[index++]);
                    d1 = read8(data[index++]);
                    d2 = read8(data[index++]);
                    d3 = read8(data[index++]);
                    d4 = read8(data[index++]);
                    d5 = read8(data[index++]);
                    d6 = read8(data[index++]);

                    drms.setDigitalData(new int[]{d1, d2, d3, d4, d5, d6, power});

                    left_resistor = read16(data[index++], data[index++]);
                    right_resistor = read16(data[index++], data[index++]);

                    roll_tream = read8(data[index++]);
                    pitch_tream = read8(data[index++]);
                    yaw_tream = read8(data[index++]);

                //Log.d(TAG,"Roll : " + roll + "\tpitch : " + pitch + "\tyaw : " + yaw + "\tthrottle : " + throttle);
                //Log.d(TAG,"power : " + power + "   D1 : " + d1 + "   D2 : " + d2 + "   D3 : " + d3 + "   D4 : " + d4 + "  D5 : " + d5 + "   D6 : " + d6);
                //Log.d(TAG,"D1 : " + d1);
                //Log.d(TAG,"D2 : " + d2);
                //Log.d(TAG,"D3 : " + d3);
                //Log.d(TAG,"D4 : " + d4);
                //Log.d(TAG,"D5 : " + d5);
                //Log.d(TAG,"D6 : " + d6);
                //Log.d(TAG,"left : " + left_resistor + "\tright : " + right_resistor);
                //Log.d(TAG,"roll_t : " + roll_tream + "\tpitch_t : " + pitch_tream + "\tYaw_t : " + yaw_tream);

                    setRPYT(roll,pitch,yaw,throttle);
                    setTream(roll_tream,pitch_tream,yaw_tream);

                    if(power == 200)
                        mHandler.obtainMessage(CONT_DIGIAL,POWER,0).sendToTarget();

                    if(d1 == 200)
                        mHandler.obtainMessage(CONT_DIGIAL,D1,0).sendToTarget();

                    if(d2 == 200)
                        mHandler.obtainMessage(CONT_DIGIAL,D2,0).sendToTarget();

                    if(d3 == 200)
                        mHandler.obtainMessage(CONT_DIGIAL,D3,0).sendToTarget();

                    if(d4 == 200)
                        mHandler.obtainMessage(CONT_DIGIAL, D4,0).sendToTarget();

                    if(d5 == 200)
                        mHandler.obtainMessage(CONT_DIGIAL,D5,0).sendToTarget();

                    if(d6 == 200)
                        mHandler.obtainMessage(CONT_DIGIAL,D6,0).sendToTarget();

                    if(roll_tream > 150)
                        mHandler.obtainMessage(CONT_DIGIAL,R_T, 1).sendToTarget();
                    else if(roll_tream < 150)
                        mHandler.obtainMessage(CONT_DIGIAL,R_T,-1).sendToTarget();

                    if(pitch_tream > 150)
                        mHandler.obtainMessage(CONT_DIGIAL,P_T, 1).sendToTarget();
                    else if(pitch_tream < 150)
                        mHandler.obtainMessage(CONT_DIGIAL,P_T,-1).sendToTarget();

                    if(yaw_tream > 150)
                        mHandler.obtainMessage(CONT_DIGIAL,Y_T, 1).sendToTarget();
                    else if(yaw_tream < 150)
                        mHandler.obtainMessage(CONT_DIGIAL,Y_T,-1).sendToTarget();


                }
                else{
                    service.setReceived(true);
                    return false;
                }
                service.setReceived(true);
                return true;
            }
        }
        else{
            service.setReceived(true);
            return false;
        }
        service.setReceived(true);
        return true;
    }

    private void setRPYT(int roll, int pitch, int yaw, int throttle){
        this.roll = ((roll - 1500) * drms.getDRONE_SPEED() / 500) + 1500 + drms.getTream()[0];
        this.pitch = ((pitch - 1500) * drms.getDRONE_SPEED() / 500) + 1500 + drms.getTream()[1];
        this.yaw = ((yaw - 1500) * drms.getDRONE_SPEED() / 500) + 1500 + drms.getTream()[2];
        drms.setRawRCDataRollPitch(this.roll, this.pitch);
        drms.setRawRCDataYawThrottle(this.yaw, this.throttle);
    }

    private void setPower(int aux4){
        if (power == 200) {
            if (drms.getRcdata()[7] == 2000) {
                drms.setRawRCDataAux(4, 1000);
            } else if (drms.getRcdata()[7] == 1000) {
                drms.setRawRCDataAux(4, 2000);
            }
        }
    }

    private void setPreviousDisplay(int d4){
        if(d4 == 200){
//            service.sendBroadcast(new Intent(BTService.PREVIOUS_DISPLAY));
        }

    }

    private void setNextDisplay(int d5){
        if(d5 == 200){
//            service.sendBroadcast(new Intent(BTService.NEXT_DISPLAY));
        }

    }


    private void setTream(int roll, int pitch, int yaw){
        int[] tream = drms.getTream();
        int interval =  drms.getTreamInterval();

        if(roll == 200){
            tream[0] += interval;
        }
        else if(roll == 100){
            tream[0] -= interval;
        }
        else if(roll == 150){

        }

        if(pitch == 200){
            tream[1] += interval;
        }
        else if(pitch == 100){
            tream[1] -= interval;
        }
        else if(pitch == 150){

        }

        if(yaw == 200){
            tream[2] += interval;
        }
        else if(yaw == 100){
            tream[2] -= interval;
        }
        else if(yaw == 150){

        }

        drms.setRollTream(tream[0]);
        drms.setPitchTream(tream[1]);
        drms.setYawTream(tream[2]);


    }

    private void setDroneSpeed(int d1){
        if(d1 == 200){
            if(drms.getDRONE_SPEED() == DRMS.veryslow){
                drms.setDRONE_SPEED(DRMS.slow);
                return;
            }
            if(drms.getDRONE_SPEED() == DRMS.slow){
                drms.setDRONE_SPEED(DRMS.middle);
                return;
            }
            if(drms.getDRONE_SPEED() == DRMS.middle){
                drms.setDRONE_SPEED(DRMS.fast);
                return;
            }
            if(drms.getDRONE_SPEED() == DRMS.fast){
                drms.setDRONE_SPEED(DRMS.veryfast);
                return;
            }
            if(drms.getDRONE_SPEED() == DRMS.veryfast){
                drms.setDRONE_SPEED(DRMS.veryslow);
                return;
            }
        }
    }

    private void setAccCalibration(int d6){
        if(d6 == 200){
//            service.sendBroadcast(new Intent(BTService.REQUEST_ACC_CALIBRATION));
        }
    }

    public int read8(byte int_8_1){
        return int_8_1 & 0xff;
    }

    public int read16(byte int_16_1, byte int_16_2){
        return ((int_16_1 & 0xff) + (int_16_2 << 8));
    }

}
