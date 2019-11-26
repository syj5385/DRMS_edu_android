package com.drms.drmakersystem.FileManagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.drms.drmakersystem.Activity.NoticeActivity;
import com.drms.drmakersystem.R;
import com.drms.drmakersystem.Application.DRMS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by comm on 2018-02-22.
 */

public class NoticeManager {
    private static final int FINISHED_READ_NOTICE = 1;
    private static final int FAILED_TO_READ_NOTICE = 2 ;
    private static final int SERVER_TIMEOUT = 4;
    private static final int NO_NOTICE = 3;

    private static final String TAG = "NoticeManager";

    public static final String APP_ACTIVATION = "app_activation";
    public static final String CONTENT_TOPIC = "content_topic";
    public static final String CONTENT = "content";

    public static final int REQUEST_NOTICE = 1;

    private Context context;
    private Activity activiy;

    private Notice mNotice;

    private File notice;

    private DRMS drms;

    public NoticeManager(Activity activity, Context context) {
        this.activiy = activity;
        this.context = context;

        drms = (DRMS)activity.getApplication();

        isUpdatedNotice();
    }

    private void isUpdatedNotice(){
        boolean isExist = false;
        DownLoadThread thread = new DownLoadThread();
        thread.start();
        try {
            thread.join();
        }catch (InterruptedException e){};

    }

    private class DownLoadThread extends Thread {
        String filename;
        String filePath ;

        String webServer = "http://183.103.12.82/notice/notice";

        public DownLoadThread() {
            super();

            Log.d(TAG,webServer);
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DRMS(edu)/manual/notice";
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
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                int requestCode = conn.getResponseCode();

                Log.d(TAG,"Connect");

                int len = conn.getContentLength();
                Log.d(TAG,"len : " + len);
                if(len > 0) {
                    byte[] tmpByte = new byte[len];
                    InputStream is = conn.getInputStream();
                    notice = new File(filePath);
                    FileOutputStream fos = new FileOutputStream(notice);
                    for (; ; ) {
                        read = is.read(tmpByte);
                        finishread += read;

                        if (read < 0)
                            break;
                        fos.write(tmpByte, 0, read);
                    }
                    is.close();
                    fos.close();

                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){};
                    mHandler.obtainMessage(FINISHED_READ_NOTICE).sendToTarget();

                } else if (len == 0) {
                    mHandler.obtainMessage(NO_NOTICE).sendToTarget();

                } else {
                    Log.d(TAG, "Connection Error");
                    mHandler.obtainMessage(FAILED_TO_READ_NOTICE).sendToTarget();
                }
                conn.disconnect();


            }
            catch (SocketTimeoutException e2){
                Log.e(TAG,"IOException");
                e2.printStackTrace();
                mHandler.obtainMessage(SERVER_TIMEOUT).sendToTarget();
            }
            catch (IOException e1) {
                Log.e(TAG,"IOException");
                e1.printStackTrace();
                mHandler.obtainMessage(FAILED_TO_READ_NOTICE).sendToTarget();
            }



        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            byte[] temp = new byte[1024];
            int read = 0;
            try{
                Thread.sleep(100);
            }catch (InterruptedException e){};
            switch(msg.what){
                case FINISHED_READ_NOTICE :

                    try {

                        FileInputStream fis = new FileInputStream(notice);

                        while((read = fis.read(temp)) != -1){}

                        fis.close();

                    }catch (IOException e){

                    }

                    String content = new String(temp);
                    mNotice = new Notice(content);
                    if(mNotice.getActivation().equals("false")){
                        Intent intent = new Intent(activiy,NoticeActivity.class);
                        ArrayList<String> topic = new ArrayList<>();
                        ArrayList<String> notice_content = new ArrayList<>();
                        topic.add("어플리케이션을 실행할 수 없습니다.");
                        notice_content.add("어플리케이션 관리자에 의해 잠시 어플리케이션 실행이 중지 되었습니다.\n 잠시 후 다시 실행해 주세요.");
                        intent.putExtra(APP_ACTIVATION,false);
                        intent.putStringArrayListExtra(CONTENT_TOPIC,topic);
                        intent.putStringArrayListExtra(CONTENT,notice_content);
                        activiy.startActivityForResult(intent, REQUEST_NOTICE);
                        activiy.overridePendingTransition(R.anim.fade, R.anim.hold);

                    }
                    else {
                        Log.d(TAG, "length : " + read);
                        Log.d(TAG, "current in drms :  " + drms.getCurrentNotice());
                        Log.d(TAG, "Notice in Serever :  " + mNotice.getDate());
                        Log.d(TAG, "isHide : " + drms.isHideNotice());
                        if (drms.getCurrentNotice().equals(mNotice.getDate())) {
                            Log.d(TAG, "equal Notice");
                            if (!drms.isHideNotice()) {
                                activiy.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(activiy, NoticeActivity.class);
                                        intent.putExtra(APP_ACTIVATION,true);
                                        intent.putStringArrayListExtra(CONTENT_TOPIC, mNotice.getContent_topic());
                                        intent.putStringArrayListExtra(CONTENT, mNotice.getContent());
                                        activiy.startActivityForResult(intent, REQUEST_NOTICE);
                                        activiy.overridePendingTransition(R.anim.fade, R.anim.hold);

                                    }
                                });
                            } else {
                                Log.d(TAG, "No show Notice");
                            }
                        } else { // updated Notice;
                            Log.d(TAG, "Update Notice");
                            drms.setHideNotice(false);
                            drms.setCurrentNotice(mNotice.getDate());
                            activiy.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(activiy, NoticeActivity.class);
                                    intent.putStringArrayListExtra(CONTENT_TOPIC, mNotice.getContent_topic());
                                    intent.putStringArrayListExtra(CONTENT, mNotice.getContent());
                                    intent.putExtra(APP_ACTIVATION,true);
                                    activiy.startActivityForResult(intent, REQUEST_NOTICE);
                                    activiy.overridePendingTransition(R.anim.fade, R.anim.hold);

                                }
                            });

                        }
                    }
                    break;

                case FAILED_TO_READ_NOTICE :
                    Intent intent = new Intent(activiy,NoticeActivity.class);
                    ArrayList<String> topic = new ArrayList<>();
                    ArrayList<String> notice_content = new ArrayList<>();
                    topic.add("어플리케이션을 실행할 수 없습니다.");
                    notice_content.add("네트워크 상태를 확인해주세요.");
                    intent.putExtra(APP_ACTIVATION,false);
                    intent.putStringArrayListExtra(CONTENT_TOPIC,topic);
                    intent.putStringArrayListExtra(CONTENT,notice_content);
                    activiy.startActivityForResult(intent, REQUEST_NOTICE);
                    activiy.overridePendingTransition(R.anim.fade, R.anim.hold);
                    break;

                case SERVER_TIMEOUT :
                    Intent intent2 = new Intent(activiy,NoticeActivity.class);
                    ArrayList<String> topic2 = new ArrayList<>();
                    ArrayList<String> notice_content2 = new ArrayList<>();
                    topic2.add("어플리케이션을 실행할 수 없습니다.");
                    notice_content2.add("서버와 연결할 수 없습니다.");
                    intent2.putExtra(APP_ACTIVATION,false);
                    intent2.putStringArrayListExtra(CONTENT_TOPIC,topic2);
                    intent2.putStringArrayListExtra(CONTENT,notice_content2);
                    activiy.startActivityForResult(intent2, REQUEST_NOTICE);
                    activiy.overridePendingTransition(R.anim.fade, R.anim.hold);
                    break;


                case NO_NOTICE :
                    Log.d(TAG,"No Notice");
                    break;
            }
        }
    };

    private class Notice{
        private String whole_string;

        private String date = "";
        private String topic ="";
        private String activation = "";
        private HashMap<Integer, String[]> notice_hash = new HashMap<Integer,String[]>();
        private ArrayList<String> content_topic  = new ArrayList<String>();
        private ArrayList<String> content  = new ArrayList<String>();

        int index =0 ;

        public Notice(String notice) {
            super();
            whole_string = notice;
            evaluateNoice();


        }

        public ArrayList<String> getContent_topic(){
            return content_topic;
        }

        public ArrayList<String> getContent(){
            return content;
        }

        public String getDate(){
            return date;
        }

        public String getActivation(){return activation;}

        private void evaluateNoice(){
            char temp;
            while((temp = whole_string.charAt(index++)) != '\n' ){
                date += temp;
            }
            index++;

            Log.d(TAG,"Date : "  + date);

            // Activation
            index = whole_string.indexOf("#Activation") + "#Activation".length() + 1;
            Log.d(TAG,"index : " + index);
            while((temp = whole_string.charAt(index++)) != '\n' ){
                activation += temp;
            }
            Log.d(TAG,"activation : "  + activation );

            // topic
            index = whole_string.indexOf("#topic") + "#topic".length() + 1;
            Log.d(TAG,"index : " + index);
            while((temp = whole_string.charAt(index++)) != '\n' ){
                topic += temp;
            }
            Log.d(TAG,"topic : "  + topic);

            //content
            String contentTemp1 = "";
            String contentTemp2 = "";
            int key = 0;
            index = whole_string.indexOf("#content") + "#content".length() + 1;
            while((temp = whole_string.charAt(index) )== '<' && index <= whole_string.length()-1){
                index++;
                while((temp = whole_string.charAt(index++)) != '>' ){
                    contentTemp1 += temp;
                }
                index++;
                while((temp = whole_string.charAt(index++)) != '\n' ){
                    contentTemp2 += temp;
                }
                index++;
                Log.d(TAG,contentTemp1 + "\t/\t"+ contentTemp2);

                content_topic.add(key, contentTemp1);
                content.add(key++, contentTemp2);
                contentTemp1 = "";
                contentTemp2 = "";

                Log.d(TAG,"Hash size : "  + notice_hash.size());
            }
        }
    }



}
