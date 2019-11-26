package com.drms.drmakersystem.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import com.drms.drmakersystem.Lev2_Controller.Cont2_2_Activity;
import com.drms.drmakersystem.Lev2_Controller.Cont2_3_Activity;
import com.drms.drmakersystem.Lev2_Controller.Cont2_4_Activity;
import com.drms.drmakersystem.Lev2_Controller.Cont2_5_Activity;
import com.drms.drmakersystem.Lev2_Controller.Cont2_6_Activity;
import com.drms.drmakersystem.Lev2_Controller.Cont2_8_Activity;
import com.drms.drmakersystem.Lev2_Controller.Cont2_9_Activity;
import com.drms.drmakersystem.R;

import java.util.List;

public class Level2Activity extends AppCompatActivity {

    private String information;

    private Button Tap_btn;
    private ImageView photo;
    private ImageView controller, way, Cont_firmware;
    private TextView topic;

    private Intent UploadIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_level2);

        ImageView photo = (ImageView)findViewById(R.id.photo);
        controller = (ImageView)findViewById(R.id.controller);
        controller.setOnTouchListener(mTouchListener);

        Cont_firmware = (ImageView)findViewById(R.id.Cont_Firmware);
        Cont_firmware.setOnTouchListener(mTouchListener);

        way = (ImageView)findViewById(R.id.way);
        way.setOnTouchListener(mTouchListener);

        topic = (TextView)findViewById(R.id.round_topic);

        Intent lev_round_intent = getIntent();
        information = lev_round_intent.getStringExtra("level2");

        UploadIntent = new Intent(getApplicationContext(), UploadActivity.class);

        switch(information){
            case "R1" :
                topic.setText("1주차 조종기 사용방법");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.r1));
                break;

            case "R2" :
                topic.setText("2주차 복권 당첨");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r2));
                break;

            case "R3" :
                topic.setText("3주차 얼굴 만들기");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r3));
                break;

            case "R4" :
                topic.setText("4주차 기차");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r4));
                break;

            case "R5" :
                topic.setText("5주차 오르골");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r5));
                break;

            case "R6" :
                topic.setText("6주차 착시 현상");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r6));
                break;

            case "R7" :
                topic.setText("7주차 HEX 드론");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r12));
                break;

            case "R8" :
                topic.setText("8주차 비행기");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r8));
                break;

            case "R9" :
                topic.setText("9주차 트랙터");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r9));
                break;

            case "R10" :
                topic.setText("10주차 스피드 자동차");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r10));
                break;

            case "R11" :
                topic.setText("11주차 밀어내기");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r11));
                break;

            case "R12" :
                topic.setText("12주차 HEX 드론(조종기)");
                photo.setImageDrawable(getResources().getDrawable(R.drawable.lev2_r12));
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

                    case R.id.Cont_Firmware :
                        Cont_firmware.setImageDrawable(getResources().getDrawable(R.drawable.cont_upload_on));

                        break;

                    case R.id.way :
                        way.setImageDrawable(getResources().getDrawable(R.drawable.way_on));

                        break;
                }

            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {


                Intent level2_intent;
                switch(v.getId()){
                    case R.id.controller :

                        try{
                            Thread.sleep(300);
                        }catch (InterruptedException e){};

                        controller.setImageDrawable(getResources().getDrawable(R.drawable.controller));

                        switch (information) {
                            case "R1": //조종기 사용방법
//                                level0_intent = new Intent(getApplicationContext(), Cont1_1_Activity.class);
//                                startActivity(level0_intent);
                                break;

                            case "R2": // 복권 당첨
                                level2_intent = new Intent(getApplicationContext(), Cont2_2_Activity.class);
                                startActivity(level2_intent);
                                break;

                            case "R3":  //얼굴 만들기
                                level2_intent = new Intent(getApplicationContext(), Cont2_3_Activity.class);
                                startActivity(level2_intent);
                                break;

                            case "R4": // 기차
                                level2_intent = new Intent(getApplicationContext(), Cont2_4_Activity.class);
                                startActivity(level2_intent);
                                break;

                            case "R5":  // 오르골
                                level2_intent = new Intent(getApplicationContext(), Cont2_5_Activity.class);
                                startActivity(level2_intent);
                                break;

                            case "R6":  // 착시 현상
                                level2_intent = new Intent(getApplicationContext(), Cont2_6_Activity.class);
                                startActivity(level2_intent);
                                break;

                            case "R7":  // Hex 드론(1)
                                UploadIntent.putExtra("Firmware","HEX");
                                AlertDialog.Builder dialog1 = new AlertDialog.Builder(Level2Activity.this);
                                LinearLayout title_layout1 = new LinearLayout(Level2Activity.this);
                                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                title_layout1.setLayoutParams(params1);
                                title_layout1.setPadding(100,20,100,20);
                                title_layout1.setBackgroundColor(getResources().getColor(R.color.dialogColor));

                                ImageView title1 = new ImageView(Level2Activity.this);
                                title1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                title1.setImageDrawable(getResources().getDrawable(R.drawable.hex_info));

                                title_layout1.addView(title1);


                                dialog1.setCustomTitle(title_layout1);
                                dialog1.setView(R.layout.drone_dialog);

                                dialog1.show();
                                break;

                            case "R8":   // 비행기
                                level2_intent = new Intent(getApplicationContext(), Cont2_8_Activity.class);
                                startActivity(level2_intent);
                                break;

                            case "R9":  // 트랙터
                                level2_intent = new Intent(getApplicationContext(), Cont2_9_Activity.class);
                                startActivity(level2_intent);
                                break;

                            case "R10":  // 스피드 자동차
                                level2_intent = new Intent(getApplicationContext(),com.drms.drmakersystem.Lev2_Controller.Cont2_10_Activity.class);
                                startActivity(level2_intent);
                                break;

                            case "R11":  // HEX 드론(Controller)
                                level2_intent = new Intent(getApplicationContext(),com.drms.drmakersystem.Lev2_Controller.Cont2_11_Activity.class);
                                startActivity(level2_intent);
                                break;

                            case "R12":  // HEX 드론(2)
                                UploadIntent.putExtra("Firmware","HEX");
                                AlertDialog.Builder dialog2 = new AlertDialog.Builder(Level2Activity.this);
                                LinearLayout title_layout2 = new LinearLayout(Level2Activity.this);
                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                title_layout2.setLayoutParams(params2);
                                title_layout2.setPadding(100,20,100,20);
                                title_layout2.setBackgroundColor(getResources().getColor(R.color.dialogColor));

                                ImageView title2 = new ImageView(Level2Activity.this);
                                title2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                title2.setImageDrawable(getResources().getDrawable(R.drawable.hex_info));

                                title_layout2.addView(title2);


                                dialog2.setCustomTitle(title_layout2);
                                dialog2.setView(R.layout.drone_dialog);
                                dialog2.show();
                                break;
                        }



                        break;

                    case R.id.Cont_Firmware :

                        try{
                            Thread.sleep(300);
                        }catch (InterruptedException e){};

                        Cont_firmware.setImageDrawable(getResources().getDrawable(R.drawable.cont_upload));

                        Intent Cont_firmware_intent = new Intent(getApplicationContext(),UploadActivity.class);
                        switch(information){
                            case "R1" :

                                break;

                            case "R2" :

                                break;

                            case "R3" :

                                break;

                            case "R4" :

                                break;

                            case "R5" :

                                break;

                            case "R6" :

                                break;

                            case "R7" :

                                break;

                            case "R8" :

                                break;

                            case "R9" :

                                break;

                            case "R10" :

                                break;

                            case "R11" :

                                break;

                            case "R12" :
                                Cont_firmware_intent.putExtra("Firmware","DRONE_CONTROLLER");

                                break;

                        }

//                        startActivity(Cont_firmware_intent);
                        Toast.makeText(getApplicationContext(),"DRS 조종기 펌웨어를 준비중입니다.",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.way :
                        try{
                            Thread.sleep(300);
                        }catch (InterruptedException e){};

                        way.setImageDrawable(getResources().getDrawable(R.drawable.way));


                        Intent way_intent;
                        switch(information){
                            case "R1" :
                                way_intent = new Intent(getApplicationContext(),DRS_uploadExplainActivity.class);
                                startActivity(way_intent);

                                break;

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
                                way_intent = new Intent(getApplicationContext(),DroneExplainActivity.class);
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
                                way_intent = new Intent(getApplicationContext(),DRS_uploadExplainActivity.class);
                                startActivity(way_intent);

                                break;

                            case "R11" :
                                way_intent = new Intent(getApplicationContext(), DRS_uploadExplainActivity.class);
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


        startActivity(UploadIntent);
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
