package com.drms.drmakersystem.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.drms.drmakersystem.IconTextItem;
import com.drms.drmakersystem.IconTextListAdapter;
import com.drms.drmakersystem.Lev1_Controller.Cont1_1_Activity;
import com.drms.drmakersystem.R;

public class ContHelp_3Activity extends AppCompatActivity {

    private ImageView btn_speed, btn_rst, btn_bluetooth, btn_power;

    private ImageView btn_thro1, btn_thro2;

    private ImageView btn_tream1, btn_tream2, btn_tream3, btn_tream4, btn_tream5, btn_tream6;

    private ImageView btn_lcd;

    private ImageView btn_a, btn_b, btn_c;

    private ImageView btn_lcdchange, btn_btmode;

    private ImageView btn_powersw;

    private TextView btn_name, btn_contents;

    private ScrollView lev_list_scroll;

    private ListView lev_list;


    private LinearLayout monitor;

    private Button lev_selection;
    private boolean monitor_state = false;


    private IconTextListAdapter adapter;

    public static final int LEV2_R2 = 1;
    public static final int LEV2_R3 = 2;
    public static final int LEV2_R4 = 3;
    public static final int LEV2_R5 = 4;
    public static final int LEV2_R6 = 5;
    public static final int LEV2_R7 = 6;
    public static final int LEV2_R8 = 7;
    public static final int LEV2_R9 = 8;
    public static final int LEV2_R10 = 9;
    public static final int LEV2_R11 = 10;
    public static final int LEV2_R12 = 11;

    private int  CURRENT_LEV_ROUND = 0;


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
                        btn_thro1.setImageDrawable(getResources().getDrawable(R.drawable.btn_throttle_on_a));
                         btn_thro2.setImageDrawable(getResources().getDrawable(R.drawable.btn_throttle_on_b));
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
                        btn_a.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on_a));
                        btn_b.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on_b));
                        btn_c.setImageDrawable(getResources().getDrawable(R.drawable.btn_circle_on_c));
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
        setContentView(R.layout.activity_cont_help3);

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

        monitor = (LinearLayout)findViewById(R.id.monitor_contents);

        btn_name = (TextView)findViewById(R.id.btn_name);
        btn_contents = (TextView)findViewById(R.id.btn_contents);

        lev_list_scroll = (ScrollView)findViewById(R.id.lev_list_scroll);
        lev_list = (ListView)findViewById(R.id.lev_list);

        lev_selection = (Button)findViewById(R.id.lev_selection);

        make_edu_list2();

        lev_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!monitor_state){
                    lev_list_scroll.setVisibility(View.VISIBLE);
                    monitor.setVisibility(View.GONE);
                    monitor_state =true;
                    for(int i=0 ; i<10; i++){
                        running[i] = false;
                    }
                }
                else{
                    lev_list_scroll.setVisibility(View.GONE);
                    monitor.setVisibility(View.VISIBLE);
                    monitor_state = false;


                }

            }
        });


    }

    void make_edu_list2(){
//        lev_list = (ListView) findViewById(R.id.lev_list);
        adapter = new IconTextListAdapter(this);

        Resources res = getResources();

        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.two_2),"복권 당첨", ""));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.three_2),"얼굴 만들기", ""));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.four_2),"기차", ""));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.five_2),"오르골", ""));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.six_2),"착시 현상", ""));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.seven_2),"HEX 드론", ""));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.eight_2),"비행기", ""));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.nine_2),"트랙터", ""));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.ten_2),"스피드 자동차", ""));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.eleven_2),"밀어내기", ""));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.twelve_2),"HEX 드론(조종기)",""));

        lev_list.setAdapter(adapter);

        lev_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        lev_selection.setText("복권 당첨");
                        CURRENT_LEV_ROUND = LEV2_R2;
                        break;

                    case 1:
                        lev_selection.setText("얼굴 만들기");
                        CURRENT_LEV_ROUND = LEV2_R3;
                        break;

                    case 2:
                        lev_selection.setText("기차");
                        CURRENT_LEV_ROUND = LEV2_R4;
                        break;

                    case 3:
                        lev_selection.setText("오르골");
                        CURRENT_LEV_ROUND = LEV2_R5;
                        break;

                    case 4:
                        lev_selection.setText("착시 현상");
                        CURRENT_LEV_ROUND = LEV2_R6;
                        break;

                    case 5:
                        lev_selection.setText("HEX 드론");
                        CURRENT_LEV_ROUND = LEV2_R7;
                        break;

                    case 6:
                        lev_selection.setText("비행기");
                        CURRENT_LEV_ROUND = LEV2_R8;
                        break;

                    case 7:
                        lev_selection.setText("트랙터");
                        CURRENT_LEV_ROUND = LEV2_R9;
                        break;

                    case 8:
                        lev_selection.setText("스피드 자동차");
                        CURRENT_LEV_ROUND = LEV2_R10;
                        break;

                    case 9:
                        lev_selection.setText("밀어내기");
                        CURRENT_LEV_ROUND = LEV2_R11;
                        break;

                    case 10:
                        lev_selection.setText("HEX 드론(조종기)");
                        CURRENT_LEV_ROUND = LEV2_R12;
                        break;


                    default:
                        break;

                }
                lev_list_scroll.setVisibility(View.GONE);
                monitor.setVisibility(View.VISIBLE);
                monitor_state = false;

            }
        });
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
            else if(motionEvent.getAction() == MotionEvent.ACTION_UP && !monitor_state){

                if(view.getId() == R.id.btn_thro1 || view.getId() == R.id.btn_thro2){
                    btn_name.setText("쓰로틀(throttle)");

                    switch(CURRENT_LEV_ROUND){
                        case LEV2_R2 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r2_thro_content));
                            break;

                        case LEV2_R3 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r3_thro_content));
                            break;

                        case LEV2_R4 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r4_thro_content));
                            break;

                        case LEV2_R5 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r5_thro_content));
                            break;

                        case LEV2_R6 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r6_thro_content));
                            break;

                        case LEV2_R7 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r12_thro_content));
                            break;

                        case LEV2_R8 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r8_thro_content));
                            break;

                        case LEV2_R9 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r9_thro_content));
                            break;

                        case LEV2_R10 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r10_thro_content));
                            break;

                        case LEV2_R11 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r11_thro_content));
                            break;

                        case LEV2_R12 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r12_thro_content));
                            break;
                    }

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

                    switch(CURRENT_LEV_ROUND){
                        case LEV2_R2 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r2_speed_content));
                            break;

                        case LEV2_R3 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r3_speed_content));
                            break;

                        case LEV2_R4 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r4_speed_content));
                            break;

                        case LEV2_R5 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r5_speed_content));
                            break;

                        case LEV2_R6 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r6_speed_content));
                            break;

                        case LEV2_R7 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r12_speed_content));
                            break;

                        case LEV2_R8 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r8_speed_content));
                            break;

                        case LEV2_R9 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r9_speed_content));
                            break;

                        case LEV2_R10 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r10_speed_content));
                            break;

                        case LEV2_R11 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r11_speed_content));
                            break;

                        case LEV2_R12 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r12_speed_content));
                            break;
                    }

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
                    switch(CURRENT_LEV_ROUND){
                        case LEV2_R2 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r2_tream_content));
                            break;

                        case LEV2_R3 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r3_tream_content));
                            break;

                        case LEV2_R4 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r4_tream_content));
                            break;

                        case LEV2_R5 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r5_tream_content));
                            break;

                        case LEV2_R6 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r6_tream_content));
                            break;

                        case LEV2_R7 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r12_tream_content));
                            break;

                        case LEV2_R8 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r8_tream_content));
                            break;

                        case LEV2_R9 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r9_tream_content));
                            break;

                        case LEV2_R10 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r10_tream_content));
                            break;

                        case LEV2_R11 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r11_tream_content));
                            break;

                        case LEV2_R12 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r12_tream_content));
                            break;
                    }
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
                    switch(CURRENT_LEV_ROUND){
                        case LEV2_R2 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r2_digit_btn_content));
                            break;

                        case LEV2_R3 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r3_digit_btn_content));
                            break;

                        case LEV2_R4 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r4_digit_btn_content));
                            break;

                        case LEV2_R5 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r5_digit_btn_content));
                            break;

                        case LEV2_R6 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r6_digit_btn_content));
                            break;

                        case LEV2_R7 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r12_digit_btn_content));
                            break;

                        case LEV2_R8 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r8_digit_btn_content));
                            break;

                        case LEV2_R9 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r9_digit_btn_content));
                            break;

                        case LEV2_R10 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r10_digit_btn_content));
                            break;

                        case LEV2_R11 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r11_digit_btn_content));
                            break;

                        case LEV2_R12 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r12_digit_btn_content));
                            break;
                    }

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
                    switch(CURRENT_LEV_ROUND){
                        case LEV2_R2 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r2_lcd_change_contents));
                            break;

                        case LEV2_R3 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r3_lcd_change_contents));
                            break;

                        case LEV2_R4 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r4_lcd_change_contents));
                            break;

                        case LEV2_R5 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r5_lcd_change_contents));
                            break;

                        case LEV2_R6 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r6_lcd_change_contents));
                            break;

                        case LEV2_R7 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r12_lcd_change_contents));
                            break;

                        case LEV2_R8 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r8_lcd_change_contents));
                            break;

                        case LEV2_R9 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r9_lcd_change_contents));
                            break;

                        case LEV2_R10 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r10_lcd_change_contents));
                            break;

                        case LEV2_R11 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r11_lcd_change_contents));
                            break;

                        case LEV2_R12 :
                            btn_contents.setText(getResources().getString(R.string.lev2_r12_lcd_change_contents));
                            break;
                    }

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


