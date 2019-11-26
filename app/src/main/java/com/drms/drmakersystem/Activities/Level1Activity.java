package com.drms.drmakersystem.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.Drone_Controller.ACCJoystick;
import com.drms.drmakersystem.Drone_Controller.DualJoystick;
import com.drms.drmakersystem.Drone_Controller.Setting_Activity;
import com.drms.drmakersystem.Drone_Controller.SingleJoystick;
import com.drms.drmakersystem.Drone_Controller.UploadActivity;
import com.drms.drmakersystem.Lev1_1_Controller.Cont1_1_1_Activity;
import com.drms.drmakersystem.Lev1_1_Controller.Cont1_1_2_Activity;
import com.drms.drmakersystem.Lev1_1_Controller.Cont1_1_3_Activity;
import com.drms.drmakersystem.Lev1_Controller.Cont1_2_Activity;
import com.drms.drmakersystem.Lev1_Controller.Cont1_3_Activity;
import com.drms.drmakersystem.Lev1_Controller.Cont1_4_Activity;
import com.drms.drmakersystem.Lev1_Controller.Cont1_5_Activity;
import com.drms.drmakersystem.Lev1_Controller.Cont1_6_Activity;
import com.drms.drmakersystem.Lev1_Controller.Cont1_7_Activity;
import com.drms.drmakersystem.Lev1_Controller.Cont1_8_Activity;
import com.drms.drmakersystem.Lev1_Controller.Cont1_9_Activity;
import com.drms.drmakersystem.R;

import java.util.List;

public class Level1Activity extends AppCompatActivity {

    private String information;

    private Button Tap_btn;
    private ImageView photo;
    private ImageView controller, way;
    private TextView topic;

    private Intent Upload_Intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_level1);

        ImageView photo = (ImageView)findViewById(R.id.photo);
        controller = (ImageView)findViewById(R.id.controller);
        controller.setOnTouchListener(mTouchListener);

        way = (ImageView)findViewById(R.id.way);
        way.setOnTouchListener(mTouchListener);

        topic = (TextView)findViewById(R.id.round_topic);

        Upload_Intent = new Intent(getApplicationContext(),UploadActivity.class);

        Intent lev_round_intent = getIntent();
        information = lev_round_intent.getStringExtra("level1");

        switch(information){
            case "R1" :
                topic.setText("1주차 여러가지 구조");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.r1));
                break;

            case "R2" :
                topic.setText("2주차 선풍기");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.r2));
                break;

            case "R3" :
                topic.setText("3주차 빙글빙글 모빌");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.r3));
                break;

            case "R4" :
                topic.setText("4주차 돌림판");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.r4));
                break;

            case "R5" :
                topic.setText("5주차 핸드 드라이기");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.r5));
                break;

            case "R6" :
                topic.setText("6주차 핸드 믹서기");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.r6));
                break;

            case "R7" :
                topic.setText("7주차 컬링 게임");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.r7));
                break;

            case "R8" :
                topic.setText("8주차 잔디 깎이");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.r8));
                break;

            case "R9" :
                topic.setText("9주차 스키 모형");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.drs1_9));
                break;

            case "R10" :
                topic.setText("10주차 QUAD 드론");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r7));
                break;

            case "R11" :
                topic.setText("11주차 열기구");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.r11));
                break;

            case "R12" :
                topic.setText("12주차 Y축 드론");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.r12));
                break;

            case "R1_1" :
                topic.setText("1주차 두더지를 잡아라!");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev1_1_r_1));

                break;

            case "R1_2" :
                topic.setText("2주차 나는 사격왕");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev1_1_r_2));

                break;

            case "R1_3" :
                topic.setText("3주차 바나나 보트");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev1_1_r_3));

                break;

            case "R1_4" :
                topic.setText("4주차 QUAD 드론 (조종기)");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r7));
                break;

            default :
                break;
        }

    }

    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                switch(v.getId()){
                    case R.id.controller :

                        controller.setImageDrawable(getResources().getDrawable(R.drawable.controller_on));
                        break;

                    case R.id.way :
                        way.setImageDrawable(getResources().getDrawable(R.drawable.way_on));
                        break;
                }

            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                Intent level0_intent;
                switch(v.getId()){
                    case R.id.controller :
                        try{
                            Thread.sleep(300);
                        }catch (InterruptedException e){};

                        controller.setImageDrawable(getResources().getDrawable(R.drawable.controller));
                        switch (information) {
//                            case "R1": // 여러가지 구조
//                                level0_intent = new Intent(getApplicationContext(), Cont1_1_Activity.class);
//                                startActivity(level0_intent);
//                                break;
//
                            case "R2": // 선풍기
                                level0_intent = new Intent(getApplicationContext(), Cont1_2_Activity.class);
                                startActivity(level0_intent);
                                break;

                            case "R3":  //모빌
                                level0_intent = new Intent(getApplicationContext(), Cont1_3_Activity.class);
                                startActivity(level0_intent);
                                break;

                            case "R4": // 돌림판
                                level0_intent = new Intent(getApplicationContext(), Cont1_4_Activity.class);
                                startActivity(level0_intent);
                                break;

                            case "R5":  // 핸드드라이기
                                level0_intent = new Intent(getApplicationContext(), Cont1_5_Activity.class);
                                startActivity(level0_intent);
                                break;

                            case "R6":  // 핸드  믹서기
                                level0_intent = new Intent(getApplicationContext(), Cont1_6_Activity.class);
                                startActivity(level0_intent);
                                break;

                            case "R7":  // 컬링게임
                                level0_intent = new Intent(getApplicationContext(), Cont1_7_Activity.class);
                                startActivity(level0_intent);
                                break;

                            case "R8":   // 잔디깎이
                                level0_intent = new Intent(getApplicationContext(), Cont1_8_Activity.class);
                                startActivity(level0_intent);
                                break;

                            case "R9":  // 스키모형
                                level0_intent = new Intent(getApplicationContext(), Cont1_9_Activity.class);
                                startActivity(level0_intent);
                                break;

                            case "R10":  // QUAD 드론
                                Upload_Intent.putExtra("Firmware","QUAD");
                                AlertDialog.Builder dialog = new AlertDialog.Builder(Level1Activity.this);
                                LinearLayout title_layout = new LinearLayout(Level1Activity.this);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                title_layout.setLayoutParams(params);
                                title_layout.setPadding(100,20,100,20);
                                title_layout.setBackgroundColor(getResources().getColor(R.color.dialogColor));

                                ImageView title = new ImageView(Level1Activity.this);
                                title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                title.setImageDrawable(getResources().getDrawable(R.drawable.quad_info));

                                title_layout.addView(title);


                                dialog.setCustomTitle(title_layout);
                                dialog.setView(R.layout.drone_dialog);

                                dialog.show();

                                break;

                            case "R11":  // 열기구
                                Upload_Intent.putExtra("Firmware","QUAD");
                                AlertDialog.Builder dialog2 = new AlertDialog.Builder(Level1Activity.this);
                                LinearLayout title_layout2 = new LinearLayout(Level1Activity.this);
                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                title_layout2.setLayoutParams(params2);
                                title_layout2.setPadding(100,20,100,20);
                                title_layout2.setBackgroundColor(getResources().getColor(R.color.dialogColor));

                                ImageView title2 = new ImageView(Level1Activity.this);
                                title2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                title2.setImageDrawable(getResources().getDrawable(R.drawable.quad_info));

                                title_layout2.addView(title2);


                                dialog2.setCustomTitle(title_layout2);
                                dialog2.setView(R.layout.drone_dialog);

                                dialog2.show();
                                break;

                            case "R12":  // Y축 드론
//                                if(getPackageList()){
//                                    level0_intent = getPackageManager().getLaunchIntentForPackage("com.pinggusoft.edrone");
//                                    startActivity(level0_intent);
//                                    break;
//                                }
//                                else{
//                                    String url = "market://details?id=" + "com.pinggusoft.edrone";
//                                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                                    startActivity(i);
//                                }
                                break;

                            case "R1_1" :
                                level0_intent = new Intent(getApplicationContext(), Cont1_1_1_Activity.class);
                                startActivity(level0_intent);
                                break;

                            case "R1_2" :
                                level0_intent = new Intent(getApplicationContext(), Cont1_1_2_Activity.class);
                                startActivity(level0_intent);

                                break;

                            case "R1_3" :
                                level0_intent = new Intent(getApplicationContext(), Cont1_1_3_Activity.class);
                                startActivity(level0_intent);

                                break;

                            case "R1_4" : // QUAD
                                Upload_Intent.putExtra("Firmware","QUAD");
                                AlertDialog.Builder dialog3 = new AlertDialog.Builder(Level1Activity.this);
                                LinearLayout title_layout3 = new LinearLayout(Level1Activity.this);
                                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                title_layout3.setLayoutParams(params3);
                                title_layout3.setPadding(100,20,100,20);
                                title_layout3.setBackgroundColor(getResources().getColor(R.color.dialogColor));

                                ImageView title3 = new ImageView(Level1Activity.this);
                                title3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                title3.setImageDrawable(getResources().getDrawable(R.drawable.quad_info));

                                title_layout3.addView(title3);


                                dialog3.setCustomTitle(title_layout3);
                                dialog3.setView(R.layout.drone_dialog);

                                dialog3.show();
                                break;

                            default :
                                break;
                        }

                        break;

                    case R.id.way :
                        try{
                            Thread.sleep(300);
                        }catch (InterruptedException e){}

                        way.setImageDrawable(getResources().getDrawable(R.drawable.way));
                        Intent way_intent;
                        switch(information){

                            case "R2" :
                                way_intent = new Intent(getApplicationContext(),DRS_uploadExplainActivity.class);
                                startActivity(way_intent);

                                break;

                            case "R3" :
                                way_intent = new Intent(getApplicationContext(),DRS_uploadExplainActivity.class);
                                startActivity(way_intent);

                                break;

                            case "R4" :
                                way_intent = new Intent(getApplicationContext(),DRS_uploadExplainActivity.class);
                                startActivity(way_intent);

                                break;

                            case "R5" :
                                way_intent = new Intent(getApplicationContext(),DRS_uploadExplainActivity.class);
                                startActivity(way_intent);

                                break;

                            case "R6" :
                                way_intent = new Intent(getApplicationContext(),DRS_uploadExplainActivity.class);
                                startActivity(way_intent);

                                break;

                            case "R7" :
                                way_intent = new Intent(getApplicationContext(),DRS_uploadExplainActivity.class);
                                startActivity(way_intent);

                                break;

                            case "R8" :
                                way_intent = new Intent(getApplicationContext(),DRS_uploadExplainActivity.class);
                                startActivity(way_intent);

                                break;

                            case "R9" :
                                way_intent = new Intent(getApplicationContext(),DRS_uploadExplainActivity.class);
                                startActivity(way_intent);

                                break;

                            case "R10" :
                                way_intent = new Intent(getApplicationContext(),DroneExplainActivity.class);
                                startActivity(way_intent);

                                break;

                            case "R11" :
                                way_intent = new Intent(getApplicationContext(), DroneExplainActivity.class);
                                startActivity(way_intent);
                                break;


                            case "R12" :
                                way_intent = new Intent(getApplicationContext(), DroneExplainActivity.class);
                                startActivity(way_intent);
                                break;

                        }
                        break;

                    }

                }



            return true;
        }
    };

    public void onClick1(View v){
        Intent intent = new Intent(getApplicationContext(),DualJoystick.class);
        startActivity(intent);

    }

    public void onClick2(View v){
        Intent intent = new Intent(getApplicationContext(),SingleJoystick.class);
        startActivity(intent);

    }

    public void onClick3(View v){
        Intent intent = new Intent(getApplicationContext(),ACCJoystick.class);
        startActivity(intent);

    }

    public void onClick4(View v){
        Intent intent = new Intent(getApplicationContext(),Setting_Activity.class);
        startActivity(intent);

    }

    public void onClick5(View v){


        startActivity(Upload_Intent);
    }

    public boolean getPackageList(){
        boolean isExist = false;

        PackageManager pm = getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN,null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pm.queryIntentActivities(mainIntent,0);

        try{
            for(int i=0 ; i <mApps.size() ; i++){
                if(mApps.get(i).activityInfo.packageName.startsWith("com.pinggusoft.edrone")){
                    isExist = true;
                    break;
                }
            }
        }catch(Exception e){
            isExist = false;
        }
        return isExist;
    }
}
