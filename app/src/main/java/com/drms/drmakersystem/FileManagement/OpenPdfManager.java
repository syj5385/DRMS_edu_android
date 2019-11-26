package com.drms.drmakersystem.FileManagement;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.BuildConfig;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;



import java.io.File;

/**
 * Created by jjunj on 2017-12-07.
 */

public class OpenPdfManager {

    private static final String TAG = "OpenFileManager";
    private Context mContext;
    private Activity mActivity;
    private int level;
    private int week;

    public static final int REQUEST_HOWTO_USE = 100;

    public OpenPdfManager(Activity mActivity, Context mContext, int level, int week) {
        this.mActivity = mActivity;
        this.mContext = mContext;
        this.level = level;
        this.week = week;
        open();
    }
    private void open(){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DRMS(edu)/manual/";
        String filename = "";
        filename = new FileManagement(mContext).getManualName(level, week);


        filePath += filename;
        Log.d(TAG, "open : " + filePath);


        File file = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        String extension = MimeTypeMap.getFileExtensionFromUrl(filename);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        Log.d("down", extension);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.setDataAndType(Uri.fromFile(file), mimeType);
            try {
                mActivity.startActivityForResult(intent, REQUEST_HOWTO_USE);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mActivity, "pdf파일을 열수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
//            Uri pdfuri = FileProvider.getUriForFile(mActivity.getApplicationContext(), BuildConfig.APPLICATION_ID+".provider",new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/DRMS(fly)"+"/howto.pdf"));
            Uri uri = FileProvider.getUriForFile(mContext, "com.drms.drmakersystem.provider", file);
            intent.setDataAndType(uri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                mActivity.startActivityForResult(intent, REQUEST_HOWTO_USE);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mActivity, "pdf파일을 열수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
