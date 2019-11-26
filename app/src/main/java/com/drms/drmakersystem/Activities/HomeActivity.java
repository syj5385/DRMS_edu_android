package com.drms.drmakersystem.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.R;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    /////////////////////////////////////////////////////

    private LinearLayout frame_layout;
    private ImageView frame;
    private ImageView drone;
    private ImageView menu1, menu2;

    private Handler mHandler;

    private Thread frame_thread;

    private boolean running = false;

    private long lastTimeBackPressed;

    private static final int MOVE_DRONE = 0;

    private float frame_x, frame_y;

    private AlertDialog.Builder mA_B;

    private Intent home_intent;

    private LinearLayout gotoFly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home);

        menu1 = (ImageView)findViewById(R.id.menu1);
        menu2 = (ImageView)findViewById(R.id.menu2);

        menu1.setOnTouchListener(mTouchListener);
        menu2.setOnTouchListener(mTouchListener);

        gotoFly = (LinearLayout)findViewById(R.id.gotoFly);
        gotoFly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getPackageList()){
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.drms.drms_drone");
                    startActivity(intent);

                }
                else{
                    String url = "market://details?id=" + "com.drms.drms_drone";
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }
            }
        });

        mA_B = new AlertDialog.Builder(this);

        LinearLayout information_title = new LinearLayout(this);
        ImageView image1 = new ImageView(this);
        TextView text1 = new TextView(this);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        information_title.setLayoutParams(params);
        image1.setLayoutParams(params);
        text1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        information_title.setGravity(View.TEXT_ALIGNMENT_CENTER);

        image1.setImageDrawable(getResources().getDrawable(R.drawable.letter));
        text1.setText("공 지 사 항");
        text1.setTextSize(30);
        text1.setPadding(20,0,0,0);

        information_title.setBackgroundColor(getResources().getColor(R.color.colorinformation));
        information_title.setPadding(50,30,50,30);


        information_title.addView(image1);
        information_title.addView(text1);
        ;
        mA_B.setCustomTitle(information_title);


        mA_B.setIcon(getResources().getDrawable(R.drawable.character1_7));
        mA_B.setView(R.layout.dialoglayout);
        mA_B.setNegativeButton("닫기",null);


//

       mA_B.show();



    }





    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch(v.getId()){
                case R.id.menu1 :
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        menu1.setImageDrawable(getResources().getDrawable(R.drawable.goeducation1));
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP) {
                        home_intent = new Intent(HomeActivity.this, EducationActivity.class);
                        startActivity(home_intent);
                        try{
                            Thread.sleep(300);
                        }catch (InterruptedException e){};
                        menu1.setImageDrawable(getResources().getDrawable(R.drawable.goeducation));
                    }
                    break;

                case R.id.menu2 :
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        menu2.setImageDrawable(getResources().getDrawable(R.drawable.gocafe));
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP){
                        home_intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.drmakersystem.com"));
                        startActivity(home_intent);
                        try{
                            Thread.sleep(300);
                        }catch (InterruptedException e){};
                        menu2.setImageDrawable(getResources().getDrawable(R.drawable.cafe));
                    }
                    break;

            }
            return true;
        }
    };



    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            finish();
            return;
        }
        Toast.makeText(getApplicationContext(), "'뒤로' 버튼을 한번 더 누르면 어플리케이션이 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

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
                if(mApps.get(i).activityInfo.packageName.startsWith("com.drms.drms_drone")){
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
