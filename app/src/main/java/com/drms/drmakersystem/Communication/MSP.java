package com.drms.drmakersystem.Communication;

/**
 * Created by jjunj on 2017-05-31.
 */

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MSP {

    public static final int MODE_REQUEST = 1;

    /////////////////// MSP DATA/////////////////////////////////////////////////////////////////

    private static final String MSP_HEADER = "$M<";

    public static final int MSP_IDENT = 100;
    public static final int MSP_STATUS = 101;
    public static final int MSP_RAW_IMU = 102;
    public static final int MSP_SERVO = 103;
    public static final int MSP_MOTOR = 104;
    public static final int MSP_RC = 105;
    public static final int MSP_RAW_GPS = 106;
    public static final int MSP_COMP_GPS = 107;
    public static final int MSP_ATTITUDE = 108;
    public static final int MSP_ALTITUDE = 109;
    public static final int MSP_ANALOG = 110;
    public static final int MSP_RC_TUNING = 111;
    public static final int MSP_PID = 112;
    public static final int MSP_BOX = 113;
    public static final int MSP_MISC = 114;
    public static final int MSP_MOTOR_PINS = 115;
    public static final int MSP_BOXNAMES = 116;
    public static final int MSP_PIDNAMES = 117;
    public static final int MSP_WP = 118;
    public static final int MSP_BOXIDS = 119;
    public static final int MSP_SERVO_CONF = 120; // out message Servo settings

    public static final int MSP_NAV_STATUS = 121; // out message Returns
    // navigation status
    public static final int MSP_NAV_CONFIG = 122; // out message Returns
    // navigation parameters

    public static final int MSP_SET_RAW_RC = 200;
    public static final int MSP_SET_RAW_GPS = 201;
    public static final int MSP_SET_PID = 202;
    public static final int MSP_SET_BOX = 203;
    public static final int MSP_SET_RC_TUNING = 204;
    public static final int MSP_ACC_CALIBRATION = 205;
    public static final int MSP_MAG_CALIBRATION = 206;
    public static final int MSP_SET_MISC = 207;
    public static final int MSP_RESET_CONF = 208;
    public static final int MSP_SET_WP = 209;
    public static final int MSP_SELECT_SETTING = 210;
    public static final int MSP_SET_HEAD = 211;
    public static final int MSP_SET_SERVO_CONF = 212;
    public static final int MSP_SET_MOTOR = 214;

    public static final int MSP_BIND = 240;

    public static final int MSP_EEPROM_WRITE = 250;

    public static final int MSP_DEBUGMSG = 253;
    public static final int MSP_DEBUG = 254;

    public static final int MSP_SET_SERIAL_BAUDRATE = 199;
    public static final int MSP_ENABLE_FRSKY = 198;
    private BluetoothService mbluetoothservice;
    private Handler mHandler;


    private ArrayList<Character> payload;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    public MSP(BluetoothService bluetoothservice, Handler mHandler) {
        mbluetoothservice = bluetoothservice;
        this.mHandler = mHandler;
    }

    private static final int CHECKBOXITEM = 8;

    public void sendRequestMSP_SET_BOX(boolean[][] boxdata){
        boolean check_temp[][] = boxdata;
        payload = new ArrayList<Character>();
        int[] activation = new int[CHECKBOXITEM];

        for (int i = 0; i < CHECKBOXITEM; i++) {
            activation[i] = 0;
            for (int aa = 0; aa < 12; aa++) {
                activation[i] += (int) (((int) (check_temp[i][aa] ? 1 : 0)) * (1 << aa));
                // activation[i] += (int) (checkbox[i].arrayValue()[aa] * (1 <<
                // aa));

            }
            payload.add((char) (activation[i] % 256));
            payload.add((char) (activation[i] / 256));
        }

        sendRequestMSP(requestMSP(MSP_SET_BOX, payload.toArray(new Character[payload.size()])));

        sendRequestMSP(requestMSP(MSP_EEPROM_WRITE));
    }

    public void SendRequestMSP_SET_MISC(int PowerTrigger, int minthrottle,
                                        int maxthrottle, int mincommand, int failsafe_throttle,
                                        float mag_decliniation, int vbatscale, float vbatlevel_warn1,
                                        float vbatlevel_warn2, float vbatlevel_crit) {



        payload = new ArrayList<Character>();
        int intPowerTrigger = 0 ;

        intPowerTrigger = (Math.round(PowerTrigger));
        payload.add((char) (intPowerTrigger % 256));
        payload.add((char) (intPowerTrigger / 256));

        payload.add((char) (minthrottle % 256));
        payload.add((char) (minthrottle / 256));

        payload.add((char) (maxthrottle % 256));
        payload.add((char) (maxthrottle / 256));

        payload.add((char) (mincommand % 256));
        payload.add((char) (mincommand / 256));

        payload.add((char) (failsafe_throttle % 256));
        payload.add((char) (failsafe_throttle / 256));

        payload.add((char) (0));// plog.arm (16bit) not used
        payload.add((char) (0));// plog.arm (16bit) not used

        payload.add((char) (0)); // plog.lifetime + (plog.armed_time / 1000000)
//         (32bit) not used
        payload.add((char) (0)); // plog.lifetime + (plog.armed_time / 1000000)
        // (32bit) not used
        payload.add((char) (0)); // plog.lifetime + (plog.armed_time / 1000000)
        // (32bit) not used
        payload.add((char) (0)); // plog.lifetime + (plog.armed_time / 1000000)
        // (32bit) not used

//        int nn = Math.round(mag_decliniation * 10); // mag_decliniation
//        payload.add((char) (nn - ((nn >> 8) << 8))); // mag_decliniation
//        payload.add((char) (nn >> 8)); // mag_decliniation

        payload.add((char)(mag_decliniation % 256));
        payload.add((char)(mag_decliniation / 256));

//        int nn = Math.round(vbatscale);
//        payload.add((char) (nn)); // VBatscale

        payload.add((char)(vbatscale & 0xFF));

        int q = (int) (vbatlevel_warn1 * 30);
        payload.add((char) (q));

        q = (int) (vbatlevel_warn2 * 30);
        payload.add((char) (q));

        q = (int) (vbatlevel_crit * 30);
        payload.add((char) (q));

        sendRequestMSP(requestMSP(MSP_SET_MISC, payload.toArray(new Character[payload.size()])));


        sendRequestMSP(requestMSP(MSP_EEPROM_WRITE));

        // ///////////////////////////////////////////////////////////

    }

    public void SendRequestMSP_SET_RC_TUNING(float[] rc_tuning_data){
        float[] rc_tune = rc_tuning_data;

        payload = new ArrayList<Character>();

        for(int i=0 ; i<rc_tune.length ; i++){
            payload.add((char)((int)rc_tune[i] & 0xFF));
        }

        sendRequestMSP(requestMSP(MSP_SET_RC_TUNING,payload.toArray(new Character[payload.size()])));

        sendRequestMSP(requestMSP(MSP_EEPROM_WRITE));
    }

    public void sendRequestMSP_SET_RAW_RC(int[] rcdata) {
        int index = 0;

        Character[] rc_signal_array = new Character[16];

        for (int i = 0; i < 8; i++) {
            rc_signal_array[index++] = (char) (rcdata[i] & 0xFF);
            rc_signal_array[index++] = (char) ((rcdata[i] >> 8) & 0xFF);
        }

        String rcData = "";
        for (int i : rcdata) {
            rcData += String.valueOf(i) + " ";
        }

        sendRequestMSP(requestMSP(MSP_SET_RAW_RC, rc_signal_array));
    }

    public List<Byte> requestMSP(int msp) {
        return requestMSP(msp, null);
    }

    public List<Byte> requestMSP(int msp, Character[] payload) {
        byte checksum = 0;

        List<Byte> msp_data = new LinkedList<Byte>();

        for (byte c : MSP_HEADER.getBytes()) {
            msp_data.add(c);
        }

        byte sizeOfpayload = (byte) ((payload != null ? (int) (payload.length) : 0) & 0xFF);
        msp_data.add(sizeOfpayload);
        checksum ^= (sizeOfpayload & 0xFF);

        msp_data.add((byte) (msp & 0xFF));
        checksum ^= (msp & 0xFF);

        if (payload != null) {
            for (char c : payload) {
                msp_data.add((byte) (c & 0xFF));
                checksum ^= (c & 0xFF);
            }
        }

        msp_data.add(checksum);

        return msp_data;

    }

    public void sendRequestMSP(List<Byte> msp) {
        byte[] arr = new byte[msp.size()];
        int i = 0;
        for (byte b : msp) {
            arr[i++] = b;
        }
        mbluetoothservice.write(arr, MODE_REQUEST);

    }

    public void SendRequestMSP_ACC_CALIBRATION() {
        sendRequestMSP(requestMSP(MSP_ACC_CALIBRATION));
    }

    public void SendRequestMSP_MAG_CALIBRATION() {
        sendRequestMSP(requestMSP(MSP_MAG_CALIBRATION));
    }


    public void SendRequestMSP_ENABLE_FRSKY() {
        sendRequestMSP(requestMSP(MSP_ENABLE_FRSKY));
        Log.d("aaa", "MSP_ENABLE_FRSKY");

    }

    public void readMSP(byte[] read_data) {
        byte[] data = read_data;

        String header = null;
        int msp = read8(data[4]) & 0xFF;
        byte checksum = 0;

        switch (msp) {
            case MSP_MISC:

                for (int i = 0; i < 3; i++) {
                    if (header == null)
                        header = String.valueOf((char) data[i]);

                    else
                        header += (char) data[i];
                }

                for (int i = 0; i < 24; i++) {
                    checksum ^= (read8(data[i + 3]) & 0xFF);
                }

                if (header.equals("$M>") && checksum == data[27] ){

                    float[] payload = new float[12];
                    payload[0] = read16(data[5],data[6]); //inPowerTrigger
                    payload[1] = read16(data[7],data[8]); // conf.minthrottle
                    payload[2] = read16(data[9],data[10]); // MAXTHROTTLE
                    payload[3] = read16(data[11],data[12]); // MINCOMMAND
                    payload[4] = read16(data[13],data[14]); // conf.failsafe throttle
                    payload[5] = read16(data[15],data[16]); // plog.arm
                    payload[6] = read32(data[17],data[18],data[19],data[20]); // plog.lifetime
                    payload[7] = read16(data[21],data[22])/10f; // conf.mag_deelination
                    payload[8] = read8(data[23]); // conf.vbatscale
                    payload[9] = (float)(read8(data[24])/30.0f); // conf.vbatwarn1
                    payload[10] =(float)(read8(data[25])/30.0f);// conf.vbatwarn2
                    payload[11] =(float)(read8(data[26])/30.0f);// conf.vbatwarn3


                    mHandler.obtainMessage(MSP_MISC, payload.length,-1, payload).sendToTarget();
                }

                break;

            case MSP_RC_TUNING :
                for (int i = 0; i < 3; i++) {
                    if (header == null)
                        header = String.valueOf((char) data[i]);

                    else
                        header += (char) data[i];
                }

                for (int i = 0; i < 9; i++) {
                    checksum ^= (read8(data[i + 3]) & 0xFF);
                }

                if (header.equals("$M>") && checksum == data[12] ){
                    float payload[] = new float[7];

                    for(int i=0; i<7 ; i++){
                        payload[i] = read8(data[i+5]);
                    }

                    mHandler.obtainMessage(MSP_RC_TUNING,payload.length,-1,payload).sendToTarget();
                }

                break;

            case MSP_BOX :
                for (int i = 0; i < 3; i++) {
                    if (header == null)
                        header = String.valueOf((char) data[i]);

                    else
                        header += (char) data[i];
                }

                for (int i = 0; i < 18; i++) {
                    checksum ^= (read8(data[i + 3]) & 0xFF);
                }

                if (header.equals("$M>") && checksum == data[21]) {
                    int payload[] = new int[8];
                    int index = 5;
                    boolean[][] Checkbox = new boolean[8][12];

                    for (int i = 0; i < 8; i++) {
                        payload[i] = read16(data[index++] , data[index++]);

                        for (int aa = 0; aa < 12; aa++) {
                            if ((payload[i] & (1 << aa)) > 0)
                                Checkbox[i][aa] = true;
                            else
                                Checkbox[i][aa] = false;
                        }
                    }

                    mHandler.obtainMessage(MSP_BOX,payload.length,-1,Checkbox).sendToTarget();
                }



                break;

            case MSP_ANALOG :
                for (int i = 0; i < 3; i++) {
                    if (header == null)
                        header = String.valueOf((char) data[i]);

                    else
                        header += (char) data[i];
                }

                for (int i = 0; i < 9; i++) {
                    checksum ^= (read8(data[i + 3]) & 0xFF);
                }

                if (header.equals("$M>") && checksum == data[12]) {
                    float[] payload = new float[4];

                    payload[0] = read8(data[5])/30.0f;
                    payload[1] = read16(data[6],data[7]);
                    payload[2] = read16(data[8],data[9]);
                    payload[3] = read16(data[10],data[11]);

                    mHandler.obtainMessage(MSP_ANALOG, payload.length,-1,payload).sendToTarget();
                }

                break;

            case MSP_RC :

                for (int i = 0; i < 3; i++) {
                    if (header == null)
                        header = String.valueOf((char) data[i]);

                    else
                        header += (char) data[i];
                }

                for (int i = 0; i < 18; i++) {
                    checksum ^= (read8(data[i + 3]) & 0xFF);
                }

                if (header.equals("$M>") && checksum == data[21]) {
                    int[] payload = new int[8];
                    int index = 5;
                    for(int i=0; i<8 ; i++){
                        payload[i] = read16(data[index++],data[index++]);
                    }

                    mHandler.obtainMessage(MSP_RC,payload.length, -1,payload).sendToTarget();

                }
                break;

            case MSP_ATTITUDE :

                for (int i = 0; i < 3; i++) {
                    if (header == null)
                        header = String.valueOf((char) data[i]);

                    else
                        header += (char) data[i];
                }

                for (int i = 0; i < 8; i++) {
                    checksum ^= (read8(data[i + 3]) & 0xFF);
                }

                if (header.equals("$M>") && checksum == data[11]) {
                    float[] payload = new float[3];
                    int index = 5;

                    for(int i=0; i<3 ; i++){
                        payload[i] = read16(data[index++],data[index++]) / 10;

                    }
                    mHandler.obtainMessage(MSP_ATTITUDE,payload.length,-1,payload).sendToTarget();
                }
                break;

            case MSP_ACC_CALIBRATION :

                mHandler.obtainMessage(MSP_ACC_CALIBRATION,0,-1).sendToTarget();
                break;

        }


    }

    public int read8(byte int_8_1){
        return int_8_1 & 0xff;
    }

    public int read16(byte int_16_1, byte int_16_2){
        return ((int_16_1 & 0xff) + (int_16_2 << 8));
    }

    public int read32(byte int_32_1, byte int_32_2, byte int_32_3, byte int_32_4) {
        return (int_32_1 & 0xff) + ((int_32_2 & 0xff) << 8)
                + ((int_32_3 & 0xff) << 16) + ((int_32_4 & 0xff) << 24);
    }
}