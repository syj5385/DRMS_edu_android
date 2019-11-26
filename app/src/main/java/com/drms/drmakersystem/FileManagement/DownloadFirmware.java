package com.drms.drmakersystem.FileManagement;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;


import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.STK500v1.UploadManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by comm on 2018-04-28.
 */

public class DownloadFirmware  {

    private static final String TAG = "DownloadFirmware";

    private int levelround;
    private Handler mHandler;

    private String hexData = "";

    public DownloadFirmware(int levelround, Handler mHandler) {
        super();
        this.levelround = levelround;
        this.mHandler = mHandler;

        DownLoadThread thread = new DownLoadThread();
        thread.start();

    }

    private class DownLoadThread extends Thread {
        String filename;
        String filePath = Environment.getExternalStorageDirectory() + "/DRMS(edu)/temp.tmp";

        String webServer = "http://183.103.12.82/firmware";

        public DownLoadThread() {
            super();
            switch(levelround){
                case DRMS.LEV2_1 :
                    webServer += "/level2/firm2_1.hex";
                    break;

                case DRMS.LEV2_2 :
                    webServer += "/level2/firm2_2.hex";
                    break;

                case DRMS.LEV2_3 :
                    webServer += "/level2/firm2_3.hex";
                    break;

                case DRMS.LEV2_4 :
                    webServer += "/level2/firm2_4.hex";
                    break;

                case DRMS.LEV2_5 :
                    webServer += "/level2/firm2_5.hex";
                    break;

                case DRMS.LEV2_6 :
                    webServer += "/level2/firm2_6.hex";
                    break;

                case DRMS.LEV2_7 :
                    webServer += "/level2/firm2_7.hex";
                    break;

                case DRMS.LEV2_8 :
                    webServer += "/level2/firm2_8.hex";
                    break;

                case DRMS.LEV2_9 :
                    webServer += "/level2/firm2_9.hex";
                    break;

                case DRMS.LEV2_10 :
                    webServer += "/level2/firm2_10.hex";
                    break;

                case DRMS.LEV2_11 :
                    webServer += "/level2/firm2_11.hex";
                    break;

                case DRMS.LEV2_12 :
                    webServer += "/level2/firm2_12.hex";
                    break;
            }
            Log.d(TAG,webServer);

        }

        int finishread = 0;

        @Override
        public void run() {
            URL serverUrl;
            int read;
            try{
                Log.d(TAG,"Download Thread");
                serverUrl = new URL(webServer);
                final HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
                Log.d(TAG,"Connect");

                int len = conn.getContentLength();
                Log.d(TAG,"len : " + len);
                if(len > 0) {
                    byte[] tmpByte = new byte[len];
                    InputStream is = conn.getInputStream();
                    File file = new File(filePath);
                    FileOutputStream fos = new FileOutputStream(file);
                    for (;;) {
                        read = is.read(tmpByte);
                        finishread += read;
                        if (read < 0)
                            break;
                        fos.write(tmpByte, 0, read);
//                        hexData += new String(tmpByte);
                    }
                    is.close();
                    fos.close();
                    InputStream inputStream = new FileInputStream(file);
                    StringBuffer buffer = new StringBuffer();

                    byte[] b = new byte[1024];

                    for (int n; (n =inputStream.read(b)) != -1; ) {
                        buffer.append(new String(b, 0, n));
                    }


                    hexData= buffer.toString();
                    file.delete();
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            information.setVisibility(View.VISIBLE);
//                            information.setText("다운로드가 완료되었습니다.\n 잠시만 기다려 주세요.");
//                        }
//                    });
                    inputStream.close();
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e){};
                    Log.d(TAG,hexData);
//                    mHandler.obtainMessage(UploadManager.DOWNLOAD_FIRM,UploadManager./*DOWNLOAD_SUCCSS*/,-1,hexData).sendToTarget();
                    mHandler.obtainMessage(UploadManager.DOWNLOAD_FIRM,UploadManager.DOWNLOAD_SUCCSS,-1,hexData).sendToTarget();
                }
                else{
                    Log.d(TAG,"Connection Error");
                    try{
                        Thread.sleep(300);
                    }catch (InterruptedException e){}

                    mHandler.obtainMessage(UploadManager.DOWNLOAD_FIRM, UploadManager.NETWORK_DISCONNECTED,levelround).sendToTarget();

//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            dialog.dismiss();
//                        }
//                    });


                }
                conn.disconnect();


            }catch (IOException e1) {
                Log.d(TAG,"IOException");
                e1.printStackTrace();
                mHandler.obtainMessage(UploadManager.DOWNLOAD_FIRM,UploadManager.DOWNLOAD_FAILD,-1).sendToTarget();
            }

        }
    }


}
