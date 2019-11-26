package com.drms.drmakersystem.FileManagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.drms.drmakersystem.Activity.EducationAcitivty;
import com.drms.drmakersystem.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by comm on 2018-02-22.
 */

public class DownloadManual {

    private static final String TAG = "DownloadManual";

    private Activity activity;
    private Context context;
    private Handler mHandler;
    private int level;
    private int week;

    private AlertDialog.Builder ab ;
    private AlertDialog dialog;

    private LinearLayout layout_view;
    private LinearLayout progress_view;
    private ProgressBar progressView;

    private TextView question;
    private TextView[] answer = new TextView[2];
    private TextView information;

    private int progress;

    public DownloadManual(Activity activity, Context context, Handler mHandler, int level, int week) {
        this.activity = activity;
        this.context = context;
        this.mHandler = mHandler;
        this.level = level;
        this.week = week;

        ab = new AlertDialog.Builder(context);
        requestDialog();

    }

    private void requestDialog(){
        layout_view = (LinearLayout) View.inflate(context, R.layout.filedownload,null);
        ab.setView(layout_view);
        question = (TextView)layout_view.findViewById(R.id.question);
        answer[0] = (TextView)layout_view.findViewById(R.id.no);
        answer[1] = (TextView)layout_view.findViewById(R.id.yes);
        information = (TextView)layout_view.findViewById(R.id.question);
        progress_view = (LinearLayout)layout_view.findViewById(R.id.progress_layout);


        TextView filename = (TextView)layout_view.findViewById(R.id.filename);
        filename.setText(new FileManagement(context,null).getManualName(level,week));
        TextView filesize = (TextView)layout_view.findViewById(R.id.filesize);
        filesize.setText(String.valueOf(new FileManagement(context).getManualSize(level,week)) + " MB");
        progressView = (ProgressBar)layout_view.findViewById(R.id.progressBar);
//        progressView = new CustomProgress(context,activity);
////        progressView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        progress_view.addView(progressView);
        Log.d(TAG,"progreeView : " + progressView);


        for(int i=0; i<answer.length ; i++){
            answer[i].setOnClickListener(answerListener);
        }

        dialog = ab.create();

        dialog.show();

        progressView.invalidate();
    }

    private View.OnClickListener answerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.yes){
                for(int i=0; i<answer.length ;i++){
                    answer[i].setVisibility(View.GONE);
                }
                information.setVisibility(View.GONE);
                progress_view.setVisibility(View.VISIBLE);

                new DownLoadThread().start();
            }
            else if(view.getId() == R.id.no){
                dialog.dismiss();
            }
        }
    };


    private class DownLoadThread extends Thread {
        String filename;
        String filePath ;

        String webServer = "http://183.103.12.82/manual";

        public DownLoadThread() {
            super();
            switch(level){
                case EducationAcitivty.LEVEL1 :
                    webServer += "/level1/";
                    break;

                case EducationAcitivty.LEVEL2 :
                    webServer += "/level2/";
                    break;

                case EducationAcitivty.LEVEL3 :
                    webServer += "/level3/";
                    break;

                case EducationAcitivty.LEVEL4 :
                    webServer += "/level4/";
                    break;

                case EducationAcitivty.CONTROLLER :
                    webServer += "/controller/";
                    break;

            }
            filename = new FileManagement(context,null).getManualName(level, week);

            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DRMS(edu)/manual/" + filename;
            webServer += filename;

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
                    for (; ; ) {
                        read = is.read(tmpByte);
                        finishread += read;
                        progress = finishread * 100 / len;
                        Log.d(TAG, "Progress : " + progress);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressView.setProgress(progress);
                            }
                        });
                        if (read < 0)
                            break;
                        fos.write(tmpByte, 0, read);
                    }
                    is.close();
                    fos.close();



                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            information.setVisibility(View.VISIBLE);
                            information.setText("다운로드가 완료되었습니다.\n 잠시만 기다려 주세요.");
                        }
                    });

                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){};

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            new OpenPdfManager(activity,context,level,week);
                        }
                    });

                }
                else{
                    Log.d(TAG,"Connection Error");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress_view.setVisibility(View.GONE);
                            information.setVisibility(View.VISIBLE);
                            information.setText("네트워크 연결을 확인해주세요.");
                        }
                    });

                    try{
                        Thread.sleep(1000);
                    }catch (InterruptedException e){}

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });


                }
                conn.disconnect();


            }catch (IOException e1) {
                Log.d(TAG,"IOException");

            }

        }
    }
}
