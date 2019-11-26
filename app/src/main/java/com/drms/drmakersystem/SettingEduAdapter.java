package com.drms.drmakersystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.drms.drmakersystem.Activity.EducationAcitivty;
import com.drms.drmakersystem.Activity.ExecuteActivity;
import com.drms.drmakersystem.Activity.Level2Controller.Cont2_2Activity;
import com.drms.drmakersystem.Adapter.CustomAdapter1.Custom1_Item;
import com.drms.drmakersystem.Adapter.CustomAdapter1.CustomAdapter1;
import com.drms.drmakersystem.Application.DRMS;
import com.drms.drmakersystem.Communication.Protocol.STK500v1.UploadManager;
import com.drms.drmakersystem.Communication.UsbSerial.UsbService;
import com.drms.drmakersystem.FileManagement.FileManagement;
import com.drms.drmakersystem.R;
/**
 * Created by comm on 2018-02-20.
 */

public class SettingEduAdapter {

    private Activity activity;
    private Context context;
    private ListView edulist;
    private ImageView topic;
    private LinearLayout back,topic_layout;
    private int level;
    private UsbService service;

    private String[] content;

    private CustomAdapter1 adatpter;

    public SettingEduAdapter(Activity activity, Context context, LinearLayout back, LinearLayout topic_layout, ImageView topic, ListView edulist, int level, UsbService service) {
        this.activity = activity;
        this.context = context;
        this.edulist = edulist;
        this.topic_layout = topic_layout;
        this.back = back;
        this.topic = topic;
        this.level = level;
        this.service = service;


        implementaionAdpater();
    }

    public void implementaionAdpater(){
        if(level == EducationAcitivty.LEVEL1){
            topic.setImageDrawable(context.getResources().getDrawable(R.drawable.select_image0));
            back.setBackground(context.getResources().getDrawable(R.drawable.select_image1));
            content = new String[12];
            content[0] = context.getResources().getString(R.string.level1_1);
            content[1] = context.getResources().getString(R.string.level1_2);
            content[2] = context.getResources().getString(R.string.level1_3);
            content[3] = context.getResources().getString(R.string.level1_4);
            content[4] = context.getResources().getString(R.string.level1_5);
            content[5] = context.getResources().getString(R.string.level1_6);
            content[6] = context.getResources().getString(R.string.level1_7);
            content[7] = context.getResources().getString(R.string.level1_8);
            content[8] = context.getResources().getString(R.string.level1_9);
            content[9] = context.getResources().getString(R.string.level1_10);
            content[10] = context.getResources().getString(R.string.level1_11);
            content[11] = context.getResources().getString(R.string.level1_12);
        }

        if(level == EducationAcitivty.LEVEL2){
            topic.setImageDrawable(context.getResources().getDrawable(R.drawable.select_image3));
            back.setBackground(context.getResources().getDrawable(R.drawable.select_image4));
            topic_layout.setBackgroundColor(context.getResources().getColor(R.color.select_color2));
            content = new String[12];
            content[0] = context.getResources().getString(R.string.level2_1);
            content[1] = context.getResources().getString(R.string.level2_2);
            content[2] = context.getResources().getString(R.string.level2_3);
            content[3] = context.getResources().getString(R.string.level2_4);
            content[4] = context.getResources().getString(R.string.level2_5);
            content[5] = context.getResources().getString(R.string.level2_6);
            content[6] = context.getResources().getString(R.string.level2_7);
            content[7] = context.getResources().getString(R.string.level2_8);
            content[8] = context.getResources().getString(R.string.level2_9);
            content[9] = context.getResources().getString(R.string.level2_10);
            content[10] = context.getResources().getString(R.string.level2_11);
            content[11] = context.getResources().getString(R.string.level2_12);
        }

        if(level == EducationAcitivty.LEVEL3){
            topic.setImageDrawable(context.getResources().getDrawable(R.drawable.select_image6));
            back.setBackground(context.getResources().getDrawable(R.drawable.select_image5));
            topic_layout.setBackgroundColor(context.getResources().getColor(R.color.select_color3));
            content = new String[10];
            content[0] = context.getResources().getString(R.string.level3_1);
            content[1] = context.getResources().getString(R.string.level3_2);
            content[2] = context.getResources().getString(R.string.level3_3);
            content[3] = context.getResources().getString(R.string.level3_4);
            content[4] = context.getResources().getString(R.string.level3_5);
            content[5] = context.getResources().getString(R.string.level3_6);
            content[6] = context.getResources().getString(R.string.level3_7);
            content[7] = context.getResources().getString(R.string.level3_8);
            content[8] = context.getResources().getString(R.string.level3_9);
            content[9] = context.getResources().getString(R.string.level3_10);
        }

        implementation();

    }

   private void implementation(){
       adatpter = new CustomAdapter1(context);
       for(int i=0; i<content.length; i++)
           adatpter.addItem(new Custom1_Item(content[i]));

       edulist.setAdapter(adatpter);
       edulist.setOnItemClickListener(selectionClickListener);
   }

   private AdapterView.OnItemClickListener selectionClickListener = new AdapterView.OnItemClickListener() {
       @Override
       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
           Intent intent = new Intent(context,ExecuteActivity.class);
           intent.putExtra(EducationAcitivty.LEVEL,level);
           intent.putExtra(EducationAcitivty.WEEK,i+1);
           Log.d("LEVEL" , "level : " + level + "\tweek : " + i+1);
           if(level < EducationAcitivty.LEVEL3) {
               activity.startActivity(intent);
               activity.overridePendingTransition(R.anim.fade, R.anim.hold);
           }
           else{

                   if (!service.isAttached()) {
                       Toast.makeText(activity, "컨트롤러가 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                       return;
                   }
                   if (service.isAttached() && !service.isConnected()) {
                       Toast.makeText(activity, "컨트롤러 펌웨어를 업데이트 합니다.", Toast.LENGTH_SHORT).show();
                       service.writeControllerFirmware(activity);
                   }
                   if (service.isAttached() && service.isConnected()) {
                       activity.startActivity(intent);
                       activity.overridePendingTransition(R.anim.fade, R.anim.hold);
                   }


           }
       }
   };
}
