package com.drms.drmakersystem.Activities;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.drms.drmakersystem.R;

public class EducationActivity extends AppCompatActivity {

    private ImageView topic, level1, level2, level3, level4;
    private LinearLayout arrow_layout, level_layout;

    private LinearLayout go1, go2, go3, go4;

    private ImageView arrow;

    private Intent edu_intent;
    private boolean running = false;

    private LinearLayout title_layout;
    private ImageView title;

    private AlertDialog.Builder dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_education);

        topic = (ImageView)findViewById(R.id.topic);
        level1 = (ImageView)findViewById(R.id.level1);
        level2 = (ImageView)findViewById(R.id.level2);
        level3 = (ImageView)findViewById(R.id.level3);

        topic.setOnTouchListener(mTouchListener);
        level1.setOnTouchListener(mTouchListener);
        level2.setOnTouchListener(mTouchListener);
        level3.setOnTouchListener(mTouchListener);



    }

    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Intent intent = new Intent(EducationActivity.this, EduSelectActivity.class);
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                arrow = new ImageView(EducationActivity.this);
                arrow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                arrow.setImageDrawable(getResources().getDrawable(R.drawable.go));

                switch(v.getId()){
                    case R.id.level1 :
                        level1.setImageDrawable(getResources().getDrawable(R.drawable.level1_on));
                        break;

                    case R.id.level2 :
                        level2.setImageDrawable(getResources().getDrawable(R.drawable.level2_on));
                        break;

                    case R.id.level3 :
                        level3.setImageDrawable(getResources().getDrawable(R.drawable.level3_on));
                        break;


                }
            }
            switch(v.getId()){
                case R.id.level1 :
                    if(event.getAction() == MotionEvent.ACTION_UP) {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(EducationActivity.this);
                        title_layout = new LinearLayout(EducationActivity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        title_layout.setLayoutParams(params);
                        title_layout.setPadding(100,20,100,20);
                        title_layout.setBackgroundColor(getResources().getColor(R.color.dialogColor));

                        title = new ImageView(EducationActivity.this);
                        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        level1.setImageDrawable(getResources().getDrawable(R.drawable.level1));
                        title.setImageDrawable(getResources().getDrawable(R.drawable.level1));

                        title_layout.addView(title);


                        dialog.setCustomTitle(title_layout);
                        dialog.setView(R.layout.dialog1);

                        dialog.show();

                    }
                    break;

                case R.id.level2 :
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(EducationActivity.this);
                        title_layout = new LinearLayout(EducationActivity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        title_layout.setLayoutParams(params);
                        title_layout.setPadding(100,20,100,20);
                        title_layout.setBackgroundColor(getResources().getColor(R.color.dialogColor));

                        title = new ImageView(EducationActivity.this);
                        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        level2.setImageDrawable(getResources().getDrawable(R.drawable.level2));
                        title.setImageDrawable(getResources().getDrawable(R.drawable.level2));

                        title_layout.addView(title);


                        dialog.setCustomTitle(title_layout);
                        dialog.setView(R.layout.dialog2);

                        dialog.show();


                    }
                    break;

                case R.id.level3 :
                    if(event.getAction() == MotionEvent.ACTION_UP) {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(EducationActivity.this);
                        title_layout = new LinearLayout(EducationActivity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        title_layout.setLayoutParams(params);
                        title_layout.setPadding(100,20,100,20);
                        title_layout.setBackgroundColor(getResources().getColor(R.color.dialogColor));

                        title = new ImageView(EducationActivity.this);
                        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        level3.setImageDrawable(getResources().getDrawable(R.drawable.level3));
                        title.setImageDrawable(getResources().getDrawable(R.drawable.level3));

                        title_layout.addView(title);


                        dialog.setCustomTitle(title_layout);
                        dialog.setView(R.layout.dialog3);

                        dialog.show();

                    }
                    break;


            }
            return true;
        }
    };

    public void level1_basic_onClick(View v){
        edu_intent = new Intent(getApplicationContext(),EduSelectActivity.class);
        edu_intent.putExtra("LEVEL","1_1");
        startActivity(edu_intent);

    }

    public void level1_expansion_onClick(View v){
        edu_intent = new Intent(getApplicationContext(),EduSelectActivity.class);
        edu_intent.putExtra("LEVEL","1_2");
        startActivity(edu_intent);
    }

    public void level2_basic_onClick(View v){
        edu_intent = new Intent(getApplicationContext(),EduSelectActivity.class);
        edu_intent.putExtra("LEVEL","2_1");
        startActivity(edu_intent);
    }

    public void level2_expansion_onClick(View v){
        edu_intent = new Intent(getApplicationContext(),EduSelectActivity.class);
        edu_intent.putExtra("LEVEL","2_2");
        startActivity(edu_intent);
    }

    public void level3_basic_onClick(View v){
        edu_intent = new Intent(getApplicationContext(),EduSelectActivity.class);
        edu_intent.putExtra("LEVEL","3_1");
//        startActivity(edu_intent);
        Toast.makeText(getApplicationContext(),"준비 중 입니다 ^^",Toast.LENGTH_SHORT).show();

    }

    public void level3_expansion_onClick(View v){
        edu_intent = new Intent(getApplicationContext(),EduSelectActivity.class);
        edu_intent.putExtra("LEVEL","3_2");
        Toast.makeText(getApplicationContext(),"준비 중 입니다 ^^",Toast.LENGTH_SHORT).show();
    }





}
