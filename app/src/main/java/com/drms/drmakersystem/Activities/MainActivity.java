package com.drms.drmakersystem.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drms.drmakersystem.R;

public class MainActivity extends AppCompatActivity {

    private static final int DRONE_COUNT = 0;
    private static final int MOVE_DRONE = 1;
    private static final int MOVE_DRONE2 = 2;
    private static final int MOVE_DRONE3 = 3;
    private static final int MOVE_DRONE4 = 4;
    ///////////////Display informayion/////////////
    private float width, height;

    ///////////////View//////////////////////
    private LinearLayout main_layout;
    private LinearLayout drone_layout;
    private ImageView drone, drone2;
    private ImageView logo;
    private TextView text;

    ///////////////thread state/////////////
    private Thread mthread;
    private boolean running;
    Handler mainHandler;
    private int count = 0;
    private boolean text_state = false;

    @Override
    protected void onStart() {
        super.onStart();
        if(mthread == null) {
            mthread = new drone_thread();
            running = true;
            mthread.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mthread = new drone_thread();
        running = true;
        mthread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionResult2 = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionResult3 = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionResult == PackageManager.PERMISSION_DENIED || permissionResult2 == PackageManager.PERMISSION_DENIED
                    || permissionResult3 == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                        || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("권한이 필요합니다.").setMessage("이 기능을 사용하기 위해서는 단말기의 권한이 필요합니다. 계속 하시겠습니까")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                                Manifest.permission.ACCESS_FINE_LOCATION}, 1000);



                                    }
                                }
                            }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();

                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);


                }

            }
            else{
            }
        }
        else{

        }

        setContentView(R.layout.activity_main);

        text = (TextView)findViewById(R.id.text);


        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        width = display.getWidth();
        height = display.getHeight();

        logo = (ImageView)findViewById(R.id.logo);

        drone_layout = (LinearLayout) findViewById(R.id.logo_layout);
        drone = new ImageView(MainActivity.this);
        ViewGroup.LayoutParams drone_params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        drone.setLayoutParams(drone_params);
        drone.setImageDrawable(getResources().getDrawable(R.drawable.drone));
        drone_layout.addView(drone);

        drone2 = new ImageView(this);
        drone2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        drone2.setImageDrawable(getResources().getDrawable(R.drawable.drone));
        drone_layout.addView(drone2);

        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case MOVE_DRONE :
                        float drone_x = (float) msg.arg1;
                        float drone_y = (float) msg.arg2;
                        drone.setX(drone_x);
                        drone.setY(drone_y);
                        count++;
                        if(count == 100 && text_state == false){
                            text.setTextColor(getResources().getColor(R.color.textColor2));
                            text_state = true;
                            count = 0;
                        }
                        else if(count == 100 && text_state == true ){
                            text.setTextColor(getResources().getColor(R.color.textColor1));
                            text_state = false;
                            count = 0;
                        }
                        break;

                    case MOVE_DRONE2 :
                        float drone_x2 = (float)msg.arg1;
                        float drone_y2 = (float)msg.arg2;
                        drone2.setX(drone_x2);
                        drone2.setY(drone_y2);
                        break;


                }

            }
        };

        main_layout = (LinearLayout) findViewById(R.id.activity_main);
        main_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    //mthread.interrupt();
                    running = false;
                    Intent main_intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(main_intent);
                    finish();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    class drone_thread extends Thread {
        double center_x;
        double center_y;
        double current_x;
        double current_y;
        double current_x2;
        double current_y2;
        double current_x3;
        double current_y3;
        double current_x4;
        double current_y4;

        double interval_x = 1;
        double interval_y;
        double logo_width;
        double logo_height;
        double radius;

        double radian = 0;
        double radian2 = 3.14;

        @Override
        public void run() {

            drone_layout.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener(){
                @Override
                public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                    drone_layout.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
                }
            });

            drone_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                }
            });

            logo.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener(){
                @Override
                public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                    drone_layout.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
                }
            });

            logo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    center_x = (double)(logo.getX() +logo.getWidth()/2 - drone.getWidth()/2);
                    center_y = (double)(logo.getY() + logo.getHeight()*0.6 );
                    current_x = center_x + logo.getWidth()/2 + drone.getWidth();
                    current_y = center_y;
                    current_x2 = center_x -logo.getWidth()/2 - drone2.getWidth();
                    current_y2 = center_y;
                    radius = logo.getWidth()/2 + 2.5*drone.getWidth();
                    logo_width = (double)logo.getWidth();
                    logo_height = (double)logo.getHeight();
                }
            });

            while (running) {
                if (mthread.interrupted()) {
                    running = false;
                }

                current_x = center_x +  radius * Math.cos(radian);
                current_y = center_y + radius * Math.sin(radian);

                current_x2 = center_x +  radius * Math.cos(radian2);
                current_y2 = center_y + radius * Math.sin(radian2);


                radian += 0.007;
                if(radian > 6.28)
                    radian = 0;

                radian2 -= 0.007;
                if(radian2 < -6.28 )
                    radian2 = 0;

                Message move_msg = Message.obtain();
                move_msg.what = MOVE_DRONE;
                move_msg.arg1 = (int)current_x;
                move_msg.arg2 = (int)current_y;
                mainHandler.sendMessage(move_msg);

                Message move_msg2 = Message.obtain();
                move_msg2.what = MOVE_DRONE2;
                move_msg2.arg1 = (int)current_x2;
                move_msg2.arg2 = (int)current_y2;
                mainHandler.sendMessage(move_msg2);


                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    Log.e("Drone Thread", "Interrupted Exception", e);
                }

            }



            drone_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    drone_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            logo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    logo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }



        public void onBackPressed() {
            running = false;
            finish();
        }
    }
}
