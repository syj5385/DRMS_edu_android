package com.drms.drmakersystem.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.drms.drmakersystem.R;
/**
 * Created by comm on 2018-02-21.
 */

public class PairedDeviceDialog extends AlertDialog.Builder{

    private Context context;
    private Activity activity;

    private LinearLayout dialogView;
    private LinearLayout pairedDeviceView;
    private Handler mHandler;

    // in the dialogView
    private ListView set_content;


    public PairedDeviceDialog(Context context, Activity activity, Handler mHandler) {
        super(context);

        this.context = context;
        this.activity = activity;
        this.mHandler = mHandler;

        dialogView = (LinearLayout) View.inflate(context, R.layout.paired_device,null);
        setView(dialogView);

    }


}
