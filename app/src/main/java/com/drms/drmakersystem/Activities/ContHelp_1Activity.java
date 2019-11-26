package com.drms.drmakersystem.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drms.drmakersystem.R;

public class ContHelp_1Activity extends AppCompatActivity {

    private ImageView btn_speed, btn_rst, btn_bluetooth, btn_power;

    private ImageView btn_thro1, btn_thro2;

    private ImageView btn_tream1, btn_tream2, btn_tream3, btn_tream4, btn_tream5, btn_tream6;

    private ImageView btn_lcd;

    private ImageView btn_a, btn_b, btn_c;

    private ImageView btn_lcdchange, btn_btmode;

    private ImageView btn_powersw;

    private TextView btn_name, btn_contents;


    private LinearLayout monitor;


    private static final int THROTTLE = 10;
    private static final int SPEED  =11;
    private static final int RESET = 12;
    private static final int BLUETOOTH = 13;
    private static final int POWER = 14;
    private static final int TREAM = 15;
    private static final int LCD = 16;
    private static final int DIGIT_BTN = 17;
    private static final int LCD_CHANGE =18;
    private static final int BTMODE = 19;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case THROTTLE :
                    if(msg.arg1 == 1){
                        btn_thro1.setImageDrawable(getResources().getDrawable(R.drawable.btn_throttle_on));
                         btn_thro2.setImageDrawable(getResources().getDrawable(R.drawable.btn_throttle_on));
                    }
                    else if(msg.arg1 == -1){
                        btn_thro1.setImageDrawable(getResources().getDrawable(R.drawable.btn_throttle));
                        btn_thro2.setImageDrawable(getResources().getDrawable(R.drawable.btn_throttle));
                    }

                    break;

                case SPEED :
                    if(msg.arg1 == 1){
                        btn_speed.setImageDrawable(getResources().getDrawable(R.drawable.btn_rect_on));
                    }
                    else if(msg.arg1 == -1){
                        btn_speed.setImageDrawable(getResources().getDrawable(R.drawable.btn_rect));
                    }

                    break;

                case RESET :
                    if(msg.arg1 == 1){
                        btn_rst.setImageDrawable(getResources().getDrawable(R.drawable.btn_rect_on));
                    }
                    else if(msg.arg1 == -1){
                        btn_rst.setImageDrawable(getResources().getDrawable(R.drawable.btn_rect));
                    }

                    break;

                case BLUETOOTH :
                    if(msg.arg1 == 1){
                        btn_bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.btn_rect_on));
                    }
                    else if(msg.arg1 == -1){
                        btn_bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.btn_rect));
                    }

                    break;

                case POWER :
                    if(msg.arg1 == 1){
                        btn_power.setImageDrawable(getResources().getDrawable(R.drawable.btn_rect_on));
                    }
                    else if(msg.arg1 == -1){
                        btn_power.setImageDrawable(getResources().getDrawable(R.drawable.btn_rect));
                    }

                    break;


                case TREAM :
                    if(msg.arg1 == 1){
                        btn_tream1.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on));
                        btn_tream2.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on));
                        btn_tream3.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on));
                        btn_tream4.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on));
                        btn_tream5.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on));
                        btn_tream6.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on));

                    }
                    else if(msg.arg1 == -1){
                        btn_tream1.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
                        btn_tream2.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
                        btn_tream3.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
                        btn_tream4.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
                        btn_tream5.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
                        btn_tream6.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
                    }
                    break;

                case LCD :
                    if(msg.arg1 == 1){
                        btn_lcd.setImageDrawable(getResources().getDrawable(R.drawable.btn_lcd_on));
                    }
                    else if(msg.arg1 == -1){
                        btn_lcd.setImageDrawable(getResources().getDrawable(R.drawable.btn_lcd));
                    }
                    break;

                case DIGIT_BTN :

                    if(msg.arg1 == 1){
                        btn_a.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on));
                        btn_b.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on));
                        btn_c.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on));
                    }
                    else if(msg.arg1 == -1) {
                        btn_a.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
                        btn_b.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
                        btn_c.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
                    }

                    break;

                case LCD_CHANGE :
                    if(msg.arg1 == 1){
                        btn_lcdchange.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on));
                    }
                    else if(msg.arg1 == -1){
                        btn_lcdchange.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
                    }
                    break;


                case BTMODE :

                    if(msg.arg1 == 1){
                        btn_btmode.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on));
                    }
                    else if(msg.arg1 == -1){
                        btn_btmode.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
                    }
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cont_help1);

        getSupportActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

    }

    private void initView(){
        btn_speed = (ImageView)findViewById(R.id.btn_speed);
        btn_speed.setOnTouchListener(mTouchListener);

        btn_rst = (ImageView)findViewById(R.id.btn_rst);
        btn_rst.setOnTouchListener(mTouchListener);

        btn_bluetooth = (ImageView)findViewById(R.id.btn_bluetooth);
        btn_bluetooth.setOnTouchListener(mTouchListener);

        btn_power=  (ImageView)findViewById(R.id.btn_power);
        btn_power.setOnTouchListener(mTouchListener);


        btn_thro1=  (ImageView)findViewById(R.id.btn_thro1);
        btn_thro1.setOnTouchListener(mTouchListener);
        btn_thro2=  (ImageView)findViewById(R.id.btn_thro2);
        btn_thro2.setOnTouchListener(mTouchListener);

        btn_tream1=  (ImageView)findViewById(R.id.btn_tream1);
        btn_tream1.setOnTouchListener(mTouchListener);
        btn_tream2=  (ImageView)findViewById(R.id.btn_tream2);
        btn_tream2.setOnTouchListener(mTouchListener);
        btn_tream3=  (ImageView)findViewById(R.id.btn_tream3);
        btn_tream3.setOnTouchListener(mTouchListener);
        btn_tream4=  (ImageView)findViewById(R.id.btn_tream4);
        btn_tream4.setOnTouchListener(mTouchListener);
        btn_tream5=  (ImageView)findViewById(R.id.btn_tream5);
        btn_tream5.setOnTouchListener(mTouchListener);
        btn_tream6=  (ImageView)findViewById(R.id.btn_tream6);
        btn_tream6.setOnTouchListener(mTouchListener);

        btn_lcd = (ImageView)findViewById(R.id.btn_lcd);
        btn_lcd.setOnTouchListener(mTouchListener);

        btn_a = (ImageView)findViewById(R.id.btn_A);
        btn_a.setOnTouchListener(mTouchListener);
        btn_b = (ImageView)findViewById(R.id.btn_b);
        btn_b.setOnTouchListener(mTouchListener);
        btn_c = (ImageView)findViewById(R.id.btn_c);
        btn_c.setOnTouchListener(mTouchListener);

        btn_lcdchange = (ImageView)findViewById(R.id.btn_lcdchange);
        btn_lcdchange.setOnTouchListener(mTouchListener);
        btn_btmode = (ImageView)findViewById(R.id.btn_btmode);
        btn_btmode.setOnTouchListener(mTouchListener);
        btn_powersw = (ImageView)findViewById(R.id.btn_powersw);
        btn_powersw.setOnTouchListener(mTouchListener);

        monitor = (LinearLayout)findViewById(R.id.monitor);

        btn_name = (TextView)findViewById(R.id.btn_name);
        btn_contents = (TextView)findViewById(R.id.btn_contents);



    }

    private boolean[] running = new boolean[10];

    // [THROTTLE, SPEED, RESET, BLUETOOTH, POWER, TREAM, LCD, DIGIT_BTN, LCD_CHANGE, B TMODE]

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                initAllBtnDrawable();
                for(int i=0; i<10; i++){
                    running[i] = false;
                }
            }
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                btn_name.setText("쓰로틀(throttle)");
                btn_contents.setText(getResources().getString(R.string.throttle_contents));
                if(view.getId() == R.id.btn_thro1 || view.getId() == R.id.btn_thro2){

                    running[0] = true;

                    new Thread(new Runnable() {
                        boolean state = false;

                        @Override
                        public void run() {
                            while(running[0]){
                                if(state) {
                                    mHandler.obtainMessage(THROTTLE, 1, -1).sendToTarget();
                                    state = false;
                                }
                                else{
                                    mHandler.obtainMessage(THROTTLE, -1, -1).sendToTarget();
                                    state = true;
                                }
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){};


                            }

                        }
                    }).start();

                }

                else if(view.getId() == R.id.btn_speed){
                    btn_name.setText("speed 설정 버튼");
                    btn_contents.setText(getResources().getString(R.string.speed_contents));

                    running[1] = true;

                    new Thread(new Runnable() {
                        boolean state = false;

                        @Override
                        public void run() {
                            while(running[1]){
                                if(state) {
                                    mHandler.obtainMessage(SPEED, 1, -1).sendToTarget();
                                    state = false;
                                }
                                else{
                                    mHandler.obtainMessage(SPEED, -1, -1).sendToTarget();
                                    state = true;
                                }
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){};


                            }

                        }
                    }).start();

                }

                else if(view.getId() == R.id.btn_rst){
                    btn_name.setText("컨트롤러 리셋 버튼");
                    btn_contents.setText(getResources().getString(R.string.reset_contents));

                    running[2] = true;

                    new Thread(new Runnable() {
                        boolean state = false;

                        @Override
                        public void run() {
                            while(running[2]){
                                if(state) {
                                    mHandler.obtainMessage(RESET, 1, -1).sendToTarget();
                                    state = false;
                                }
                                else{
                                    mHandler.obtainMessage(RESET, -1, -1).sendToTarget();
                                    state = true;
                                }
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){};


                            }

                        }
                    }).start();

                }


                else if(view.getId() == R.id.btn_bluetooth){
                    btn_name.setText("블루투스 연결 초기회 버튼");
                    btn_contents.setText(getResources().getString(R.string.bluetooth_contents));

                    running[3] = true;

                    new Thread(new Runnable() {
                        boolean state = false;

                        @Override
                        public void run() {
                            while(running[3]){
                                if(state) {
                                    mHandler.obtainMessage(BLUETOOTH, 1, -1).sendToTarget();
                                    state = false;
                                }
                                else{
                                    mHandler.obtainMessage(BLUETOOTH, -1, -1).sendToTarget();
                                    state = true;
                                }
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){};


                            }

                        }
                    }).start();

                }

                else if(view.getId() == R.id.btn_power){
                    btn_name.setText("시동 버튼");
                    btn_contents.setText(getResources().getString(R.string.power_contents));

                    running[4] = true;

                    new Thread(new Runnable() {
                        boolean state = false;

                        @Override
                        public void run() {
                            while(running[4]){
                                if(state) {
                                    mHandler.obtainMessage(POWER, 1, -1).sendToTarget();
                                    state = false;
                                }
                                else{
                                    mHandler.obtainMessage(POWER, -1, -1).sendToTarget();
                                    state = true;
                                }
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){};


                            }

                        }
                    }).start();

                }

                else if(view.getId() == R.id.btn_tream1 || view.getId() == R.id.btn_tream2 || view.getId() == R.id.btn_tream3
                        || view.getId() == R.id.btn_tream4 || view.getId() == R.id.btn_tream5 || view.getId() == R.id.btn_tream6){
                    btn_name.setText("트림 조정, 컨트롤 버튼");
                    btn_contents.setText(getResources().getString(R.string.tream_contents));

                    running[5] = true;

                    new Thread(new Runnable() {
                        boolean state = false;

                        @Override
                        public void run() {
                            while(running[5]){
                                if(state) {
                                    mHandler.obtainMessage(TREAM, 1, -1).sendToTarget();
                                    state = false;
                                }
                                else{
                                    mHandler.obtainMessage(TREAM, -1, -1).sendToTarget();
                                    state = true;
                                }
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){};


                            }

                        }
                    }).start();

                }

                else if(view.getId() == R.id.btn_lcd){
                    btn_name.setText("LCD");
                    btn_contents.setText(getResources().getString(R.string.lcd_contents));

                    running[6] = true;

                    new Thread(new Runnable() {
                        boolean state = false;

                        @Override
                        public void run() {
                            while(running[6]){
                                if(state) {
                                    mHandler.obtainMessage(LCD, 1, -1).sendToTarget();
                                    state = false;
                                }
                                else{
                                    mHandler.obtainMessage(LCD, -1, -1).sendToTarget();
                                    state = true;
                                }
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){};


                            }

                        }
                    }).start();

                }

                else if(view.getId() == R.id.btn_A || view.getId() == R.id.btn_b
                        || view.getId() == R.id.btn_c ){

                    btn_name.setText("컨트롤 버튼");
                    btn_contents.setText(getResources().getString(R.string.digit_btn_contents));

                    running[7] = true;

                    new Thread(new Runnable() {
                        boolean state = false;

                        @Override
                        public void run() {
                            while(running[7]){
                                if(state) {
                                    mHandler.obtainMessage(DIGIT_BTN, 1, -1).sendToTarget();
                                    state = false;
                                }
                                else{
                                    mHandler.obtainMessage(DIGIT_BTN, -1, -1).sendToTarget();
                                    state = true;
                                }
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){};


                            }

                        }
                    }).start();

                }

                else if(view.getId() == R.id.btn_lcdchange){
                    btn_name.setText("LCD 내용 변경 버튼");
                    btn_contents.setText(getResources().getString(R.string.lcdchange_contents));

                    running[8] = true;

                    new Thread(new Runnable() {
                        boolean state = false;

                        @Override
                        public void run() {
                            while(running[8]){
                                if(state) {
                                    mHandler.obtainMessage(LCD_CHANGE, 1, -1).sendToTarget();
                                    state = false;
                                }
                                else{
                                    mHandler.obtainMessage(LCD_CHANGE, -1, -1).sendToTarget();
                                    state = true;
                                }
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){};


                            }

                        }
                    }).start();

                }

                else if(view.getId() == R.id.btn_btmode){
                    btn_name.setText("펌웨어 업로드 모드");
                    btn_contents.setText(getResources().getString(R.string.btmode_contents));

                    running[9] = true;

                    new Thread(new Runnable() {
                        boolean state = false;

                        @Override
                        public void run() {
                            while(running[9]){
                                if(state) {
                                    mHandler.obtainMessage(BTMODE, 1, -1).sendToTarget();
                                    state = false;
                                }
                                else{
                                    mHandler.obtainMessage(BTMODE, -1, -1).sendToTarget();
                                    state = true;
                                }
                                try {
                                    Thread.sleep(500);
                                }catch (InterruptedException e){};


                            }

                        }
                    }).start();

                }



            }
            return true;
        }
    };

    private void initAllBtnDrawable(){
        btn_speed.setImageDrawable(getResources().getDrawable(R.drawable.btn_rect));
        btn_rst.setImageDrawable(getResources().getDrawable(R.drawable.btn_rect));
        btn_bluetooth.setImageDrawable(getResources().getDrawable(R.drawable.btn_rect));
        btn_power.setImageDrawable(getResources().getDrawable(R.drawable.btn_rect));

        btn_thro1.setImageDrawable(getResources().getDrawable(R.drawable.btn_throttle));
        btn_thro2.setImageDrawable(getResources().getDrawable(R.drawable.btn_throttle));

        btn_tream1.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
        btn_tream2.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
        btn_tream3.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
        btn_tream4.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
        btn_tream5.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
        btn_tream6.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));

        btn_a.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
        btn_b.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
        btn_c.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));

        btn_lcdchange.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));
        btn_btmode.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle));

        btn_lcd.setImageDrawable(getResources().getDrawable(R.drawable.btn_lcd));



    }



}


