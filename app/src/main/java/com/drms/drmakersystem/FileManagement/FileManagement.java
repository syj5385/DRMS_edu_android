package com.drms.drmakersystem.FileManagement;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import com.drms.drmakersystem.Activity.EducationAcitivty;
import com.drms.drmakersystem.R;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by jjunj on 2017-09-15.
 */

public class FileManagement {

    private static final String TAG = "FileManagement.class";
    private static final int FILEMANAGEMENT = 4;
    public static final int FINISHED_WRITE_DATA = 41;
    public static final int FINISHED_READ_DATA = 42;
    public static final int FAILED_READ_DATA = 43;
    public static final int REQUEST_SAVE_TEMP = 44;
    public static final int REQUEST_OPEN_TEMP = 45;

    private Context context;
    private ArrayList<File> myFile_list = new ArrayList<File>();

    private String BTdirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private File myFile;
    private File[] filelist ;
    private FileOutputStream outputStream;
    private FileInputStream inputStream;
    private Handler mHandler ;


    public FileManagement(Context context, Handler mHandler ){
        this.context = context;
        this.mHandler = mHandler;

        BTdirPath += "/DRMS(edu)";

        myFile = new File(BTdirPath);
        myFile.mkdirs();

        String ManualFilePath = BTdirPath + "/" + "manual";
        myFile = new File(ManualFilePath);
        myFile.mkdir();
        createBTFile();
    }

    public FileManagement(Context context) {
        this.context = context;
        this.mHandler = mHandler;

        BTdirPath += "/DRMS(edu)";

        myFile = new File(BTdirPath);
        myFile.mkdirs();

        String ManualFilePath = BTdirPath + "/" + "manual";
    }

    private boolean createBTFile(){
        boolean success = false;
        String Btfile_Path = BTdirPath+"/"+"bluetoothAddr.txt";
        myFile = new File(Btfile_Path);
        if(!myFile.exists()){
            try{
                myFile.createNewFile();
                success= true;
            }catch (IOException e){
                Log.e(getClass().getSimpleName(), "IOException");
                success= false;
            }

        }
        return success;
    }

    public boolean writeBtAddressOnFile(String name, String address){
        boolean success = false;

        myFile = new File(BTdirPath + "/bluetoothAddr.txt");
        if(!myFile.exists()) {
            createBTFile();
        }

        try {
            outputStream = new FileOutputStream(myFile);
            String Bt = name + "\n" + address;

            byte[] address_temp = Bt.getBytes();

            outputStream.write(address_temp);

            success = true;
        } catch (IOException e) {
            success = false;
        }
        ;

        return success;
    }

    public String[] readBTAddress(){
        myFile = new File(BTdirPath + "/bluetoothAddr.txt");
        char[] addr = new char[(int)myFile.length()];
        if(myFile.exists()){
            if(myFile.length() > 0) {
                Log.d("FILE", BTdirPath + "bluetooth.txt exist");
                try {
                    inputStream = new FileInputStream(myFile);
                    BufferedInputStream bufferReader = new BufferedInputStream(inputStream);

                    for (int i = 0; i < myFile.length(); i++) {
                        addr[i] = (char) bufferReader.read();
                        Log.w("READ DATA", String.valueOf(addr[i]));
                    }
                    inputStream.close();
                    bufferReader.close();
                } catch (IOException e) {
                }

            }
            else{
                writeBtAddressOnFile("","");
                return readBTAddress();
            }
        }
        else{
            Log.d("FILE",BTdirPath + "bluetooth.txt not exist");
        }

        String[] BT = new String[2];
        BT[0] = "";
        BT[1] = "";

        if(addr.length > 0) {
            int index = 0;
            while (addr[index] != '\n') {
                BT[0] += addr[index];
                index++;
            }

            index++;
            for (; index < addr.length; index++) {
                BT[1] += addr[index];

            }

            Log.d(TAG, "name : " + BT[0] + "\naddress : " + BT[1]);
        }
        return BT;
    }

    ////////////////////////////Manual

    public boolean isManualExist(int level, int week){
        boolean isExist = false;
            String ManualName = getManualName(level, week);
            String checkFile = BTdirPath + "/" + "manual" + "/" + ManualName;
            myFile = new File(checkFile);


        return myFile.exists();
    }

    public String getManualName(int level, int week){
        String manual = "";

        switch(level){
            case EducationAcitivty.LEVEL1 :
                manual += "manual1_" + String.valueOf(week);
                break;

            case EducationAcitivty.LEVEL2 :
                manual += "manual2_" + String.valueOf(week);
                break;

            case EducationAcitivty.LEVEL3 :
                manual += "manual2_" + String.valueOf(week);
                break;

            case EducationAcitivty.LEVEL4 :
                manual += "manual2_" + String.valueOf(week);
                break;

            case EducationAcitivty.CONTROLLER :
                manual += "manual_controller";
                break;
        }

        manual += ".pdf";
        Log.d(TAG, "manual Name : " + manual);
        return manual;
    }

    public float getManualSize(int level, int week){
        float manual = 0;

        switch(level){
            case EducationAcitivty.LEVEL1 :
                if(week == 1)
                    manual = (float)1.23;
                if(week == 2)
                    manual = (float)1.78;
                if(week == 3)
                    manual = (float)2.22;
                if(week == 4)
                    manual = (float)2.28;
                if(week == 5)
                    manual = (float)1.83;
                if(week == 6)
                    manual = (float)1.03;
                if(week == 7)
                    manual = (float)1.95;
                if(week == 8)
                    manual = (float)2.21;
                if(week == 9)
                    manual = (float)1.22;
                if(week == 10)
                    manual = (float)1.32;
                if(week == 11)
                    manual = (float)1.17;
                if(week == 12)
                    manual = (float)1.34;
                break;

            case EducationAcitivty.LEVEL2 :
                if(week == 1)
                    manual = (float)1.46;
                if(week == 2)
                    manual = (float)1.82;
                if(week == 3)
                    manual = (float)1.26;
                if(week == 4)
                    manual = (float)1.45;
                if(week == 5)
                    manual = (float)1.77;
                if(week == 6)
                    manual = (float)5.58;
                if(week == 7)
                    manual = (float)1.39;
                if(week == 8)
                    manual = (float)1.39;
                if(week == 9)
                    manual = (float)2.17;
                if(week == 10)
                    manual = (float)1.35;
                if(week == 11)
                    manual = (float)2.05;
                if(week == 12)
                    manual = (float)1.51;
                break;

            case EducationAcitivty.LEVEL3 :

                break;

            case EducationAcitivty.LEVEL4 :

                break;

            case EducationAcitivty.CONTROLLER :
                manual = (float)1.928;
                break;
        }

    ;
        Log.d(TAG, "manual Size : " + manual);
        return manual;
    }



}
