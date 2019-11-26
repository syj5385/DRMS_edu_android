package com.drms.drmakersystem.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.drms.drmakersystem.Drone_Controller.ACCJoystick;
import com.drms.drmakersystem.Drone_Controller.DualJoystick;
import com.drms.drmakersystem.Drone_Controller.SingleJoystick;
import com.drms.drmakersystem.IconTextItem;
import com.drms.drmakersystem.IconTextListAdapter;
import com.drms.drmakersystem.Lev1_Controller.Cont1_1_Activity;
import com.drms.drmakersystem.R;

public class EduSelectActivity extends AppCompatActivity {

    private IconTextListAdapter adapter;
    private ListView round_list;
    private ImageView round_topic;
    private Intent lev_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_eduselect);

        Intent intent = getIntent();
        String level = intent.getStringExtra("LEVEL");

        round_topic = (ImageView)findViewById(R.id.topic);

        switch(level){
            case "1_1" :
                round_topic.setImageDrawable(getResources().getDrawable(R.drawable.level1_1));
                make_edu_list1();
                break;

            case "1_2" :
                round_topic.setImageDrawable(getResources().getDrawable(R.drawable.level1_2));
                make_edu_list1_2();
                break;

            case "2_1" :
                round_topic.setImageDrawable(getResources().getDrawable(R.drawable.level2));
                make_edu_list2();
                break;

            case "2_2" :
                round_topic.setImageDrawable(getResources().getDrawable(R.drawable.level2));
                make_edu_list2_2();
                break;

            case "3_1" :
                round_topic.setImageDrawable(getResources().getDrawable(R.drawable.level3));
                make_edu_list3();
                break;

            case "3_2" :
                round_topic.setImageDrawable(getResources().getDrawable(R.drawable.level1));
                make_edu_list3_2();
                break;

        }
    }

    void make_edu_list1(){
        round_list = (ListView) findViewById(R.id.round_list);
        adapter = new IconTextListAdapter(this);

        Resources res = getResources();

        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.zero),"DRS 소개",""));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.one_1),"여러가지 구조", "여러가지 프레임 사용방법을 익히고 구조물을 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.two_1),"선풍기", "휴대하고 다닐 수 있는 미니 선풍기를 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.three_1),"빙글빙글 모빌", "모터를 사용하여 속도와 방향이 제어되는 모빌을 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.four_1),"돌림판", "회전을 이용하여 여러가지 숫자와 사칙연산판을 이용해 게임을 합니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.five_1),"핸드 드라이기", "바라밍 나오는 핸드드라이기를 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.six_1),"핸드 믹서기", "여러가지를 섞을 수 있는 핸드 믹서기를 만들어 봅니다. "));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.seven_1),"컬링", "바람의 힘을 이용하여 컬링게임을 합니다. "));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.eight_1),"잔디 깎이", "마당에서 시용하는 잔디깎이를 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.nine_1),"스키모형", "직접 스키를 타듯이 핸드폰의 기울기 센서로 스키모형을 제어합니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.ten_1),"QUAD 드론", "QUAD드론을 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.eleven_1),"열기구", "QUAD드론을 응용하여 세계일주가 가능한 열기구를 만들어봅니다. "));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.twelve_1),"Y축 드론", "Y축드론을 만듭니다."));

        round_list.setAdapter(adapter);

        round_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    lev_intent = new Intent(getApplicationContext(), DRSexplainActivity.class);
//                    startActivity(lev_intent);
                    Toast.makeText(getApplicationContext(),"DRS 소개를 준비 중 입니다.",Toast.LENGTH_SHORT).show();
                }
                else if(position == 1){
                    lev_intent = new Intent(getApplicationContext(),Cont1_1_Activity.class);
                    startActivity(lev_intent);
//                    Toast.makeText(getApplicationContext(),"오픈 준비 중입니다 ^^",Toast.LENGTH_SHORT).show();
                }
                else {
                    lev_intent = new Intent(getApplicationContext(), Level1Activity.class);
                    switch (position) {
                        case 2:
                            lev_intent.putExtra("level1", "R2");
                            break;

                        case 3:
                            lev_intent.putExtra("level1", "R3");
                            break;

                        case 4:
                            lev_intent.putExtra("level1", "R4");
                            break;

                        case 5:
                            lev_intent.putExtra("level1", "R5");
                            break;

                        case 6:
                            lev_intent.putExtra("level1", "R6");
                            break;

                        case 7:
                            lev_intent.putExtra("level1", "R7");
                            break;

                        case 8:
                            lev_intent.putExtra("level1", "R8");
                            break;

                        case 9:
                            lev_intent.putExtra("level1", "R9");
                            break;

                        case 10:
                            lev_intent.putExtra("level1", "R10");
                            break;

                        case 11:
                            lev_intent.putExtra("level1", "R11");
                            break;

                        case 12:
                            lev_intent.putExtra("level1", "R12");
                            break;

                        default:
                            break;
                    }
                    if(adapter != null)
                        adapter = null;
                    startActivity(lev_intent);
                }
            }
        });
    }

    void make_edu_list1_2(){
        round_list = (ListView) findViewById(R.id.round_list);
        adapter = new IconTextListAdapter(this);

        Resources res = getResources();


        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.one_1),"두더지를 잡아라", "지레의 원리를 이용한 집게를 이용하여, 빠르게 움직이는 두더지를 잡아봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.two_1),"나는 사격왕", "고무줄의 탄성과 지레의 원리를 이용한 고무줄 총을 이용해 회전하는 목표물을 맞추어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.three_1),"바나나 보트", "스마트폰의 기울기를 이용해 나아가는 보트를 만들어봅니다. "));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.four_1),"QUAD 드론(조종기)", "프로펠러가 4개인 QUAD 드론을 controller를 이용해 날려봅니다."));

        round_list.setAdapter(adapter);

        round_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                lev_intent = new Intent(getApplicationContext(), Level1Activity.class);
                switch (position) {
                    case 0:
                        lev_intent.putExtra("level1", "R1_1");
                        break;

                    case 1:
                        lev_intent.putExtra("level1", "R1_2");
                        break;

                    case 2:
                        lev_intent.putExtra("level1", "R1_3");
                        break;

                    case 3:
                        lev_intent.putExtra("level1", "R1_4");
                        break;

                    default:
                        break;
                }
                if(adapter != null)
                    adapter = null;
                startActivity(lev_intent);
            }

        });
    }


    void make_edu_list2(){
        round_list = (ListView) findViewById(R.id.round_list);
        adapter = new IconTextListAdapter(this);

        Resources res = getResources();

        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.zero),"DRS 소개", ""));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.one_2),"조종기 사용방법", "핸드폰과 조종기를 비교하고 쿼드 드론으을 만들어 조종합니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.two_2),"복권 당첨", "빠르게 돌아가는 광고판을 만들어 자신이 만든 광고판이 어떻게 보이는지 봅니다. "));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.three_2),"얼굴 만들기", "모터를 이용해 여러가지 표정을 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.four_2),"기차", "프로펠러의 힘을 이용하여 나아가는 기차를 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.five_2),"오르골", "주위의 빛 세기에 따라 작동하는 오르골을 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.six_2),"착시 현상", "모터의 뢰전을 이용해 착시현상이 일어날 수 있는 모형을 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.seven_2),"HEX 드론", "프로펠러가 6개인 Hex드론을 만들어 날려봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.eight_2),"비행기", "프로펠러의 힘을 이용해 빠르게 나아가는 비행기 모형을 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.nine_2),"트랙터", "모터의 힘으로 나아가고 동작할 수 있는 트랙터를 만들어 봅니다. "));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.ten_2),"스피드 자동차", "프로펠러의 힘을 이용해 나아가는 자동차를 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.eleven_2),"밀어내기", "부딪혀도 부서지지 않는 튼튼한 모형을 만들어 상대방을 경기장 밖으로 밀어내는 게임을 합니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.twelve_2),"HEX 드론(조종기)","실제 조종기를 이용하여 HEX드론을 조종해 봅니다."));

        round_list.setAdapter(adapter);

        round_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lev_intent = new Intent(getApplicationContext(), Level2Activity.class);

                if (position == 0) {
                    lev_intent = new Intent(getApplicationContext(), DRSexplainActivity.class);
                    Toast.makeText(getApplicationContext(),"DRS 소개를 준비 중 입니다.",Toast.LENGTH_SHORT).show();
//                    startActivity(lev_intent);
                }

                else if(position == 1){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(EduSelectActivity.this);
                    LinearLayout title_layout = new LinearLayout(EduSelectActivity.this);
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    title_layout.setLayoutParams(params1);
                    title_layout.setPadding(100,100,100,20);

                    title_layout.setBackgroundColor(getResources().getColor(R.color.dialogColor));

                    ImageView title = new ImageView(EduSelectActivity.this);
                    title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    title.setImageDrawable(getResources().getDrawable(R.drawable.drs_controller));

                    title_layout.addView(title);


                    dialog.setCustomTitle(title_layout);
                    dialog.setView(R.layout.cont_help_dialog);

                    dialog.show();
                }
                else {
                    switch (position) {
                        case 2:
                            lev_intent.putExtra("level2", "R2");
                            break;

                        case 3:
                            lev_intent.putExtra("level2", "R3");
                            break;

                        case 4:
                            lev_intent.putExtra("level2", "R4");
                            break;

                        case 5:
                            lev_intent.putExtra("level2", "R5");
                            break;

                        case 6:
                            lev_intent.putExtra("level2", "R6");
                            break;

                        case 7:
                            lev_intent.putExtra("level2", "R7");
                            break;

                        case 8:
                            lev_intent.putExtra("level2", "R8");
                            break;

                        case 9:
                            lev_intent.putExtra("level2", "R9");
                            break;

                        case 10:
                            lev_intent.putExtra("level2", "R10");
                            break;

                        case 11:
                            lev_intent.putExtra("level2", "R11");
                            break;

                        case 12:
                            lev_intent.putExtra("level2", "R12");
                            break;

                        default:
                            break;
                    }

                    if (adapter != null)
                        adapter = null;
                    startActivity(lev_intent);
                }
            }
        });
    }

    void make_edu_list2_2(){
        round_list = (ListView) findViewById(R.id.round_list);
        adapter = new IconTextListAdapter(this);

        Resources res = getResources();


        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.one_2),"Push! Push!", "준비 중 입니다 "));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.two_2),"호버 크래프트", "준비 중 입니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.three_2),"전투기 시뮬레이션", "준비 중 입니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.four_2),"종이 자르기", "준비 중 입니다."));

        round_list.setAdapter(adapter);

        round_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                lev_intent = new Intent(getApplicationContext(), Level1Activity.class);
                switch (position) {
                    case 0:
                        lev_intent.putExtra("level2_2", "R1");
                        Toast.makeText(getApplicationContext(),"준비중",Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        lev_intent.putExtra("level2_2", "R2");
                        Toast.makeText(getApplicationContext(),"준비중",Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        lev_intent.putExtra("level2_2", "R3");
                        Toast.makeText(getApplicationContext(),"준비중",Toast.LENGTH_SHORT).show();
                        break;

                    case 3:
                        lev_intent.putExtra("level2_2", "R4");
                        Toast.makeText(getApplicationContext(),"준비중",Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
                if(adapter != null)
                    adapter = null;
//                startActivity(lev_intent);
            }

        });
    }

    public void onContClick1(View v){
        Intent intent = new Intent(getApplicationContext(),ContHelp_1Activity.class);
        startActivity(intent);

    }

    public void onContClick2(View v){
//        Intent intent = new Intent(getApplicationContext(),ContHelp_2Activity.class);
//        startActivity(intent);
        Toast.makeText(getApplicationContext(),"DRS 조종기 업로드 방법을 준비중 입니다",Toast.LENGTH_SHORT ).show();

    }

    public void onContClick3(View v){
//        Intent intent = new Intent(getApplicationContext(),ContHelp_3Activity.class);
//        startActivity(intent);
        Toast.makeText(getApplicationContext(),"단계별 조종기에 대한 설명을 준비중입니다.",Toast.LENGTH_SHORT ).show();

    }

    void make_edu_list3(){
        round_list = (ListView) findViewById(R.id.round_list);
        adapter = new IconTextListAdapter(this);

        Resources res = getResources();
        adapter.addItem(new IconTextItem(res.getDrawable(R.mipmap.ic_launcher),"감시카메라", "Explanation"));
        adapter.addItem(new IconTextItem(res.getDrawable(R.mipmap.ic_launcher),"Round2", "Content1"));
        adapter.addItem(new IconTextItem(res.getDrawable(R.mipmap.ic_launcher),"Round3", "Content1"));
        adapter.addItem(new IconTextItem(res.getDrawable(R.mipmap.ic_launcher),"Round4", "Content1"));
        adapter.addItem(new IconTextItem(res.getDrawable(R.mipmap.ic_launcher),"Round5", "Content1"));


        round_list.setAdapter(adapter);

//        round_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent lev3_intent = new Intent(EducationActivity.this,Level3Activity.class);
//                switch(position){
//                    case 0 :
//                        lev3_intent.putExtra("level3","R1");
//                        break;
//
//                    case 1 :
//                        lev3_intent.putExtra("level3","R2");
//                        break;
//
//                    case 2 :
//                        lev3_intent.putExtra("level3","R3");
//                        break;
//
//                    case 3 :
//                        lev3_intent.putExtra("level3","R4");
//                        break;
//
//                    case 4 :
//                        lev3_intent.putExtra("level3","R5");
//                        break;
//
//                }
//                startActivity(lev3_intent);
//
//            }
//        });
    }

    void make_edu_list3_2(){
        round_list = (ListView) findViewById(R.id.round_list);
        adapter = new IconTextListAdapter(this);

        Resources res = getResources();


        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.one_1),"여러가지 구조", "여러가지 프레임 사용방법을 익히고 구조물을 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.two_1),"선풍기", "휴대하고 다닐 수 있는 미니 선풍기를 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.three_1),"빙글빙글 모빌", "모터를 사용하여 속도와 방향이 제어되는 모빌을 만들어 봅니다."));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.four_1),"돌림판", "회전을 이용하여 여러가지 숫자와 사칙연산판을 이용해 게임을 합니다."));

        round_list.setAdapter(adapter);

        round_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                lev_intent = new Intent(getApplicationContext(), Level1Activity.class);
                switch (position) {
                    case 1:
                        lev_intent.putExtra("level3_2", "R1");
                        break;

                    case 2:
                        lev_intent.putExtra("level3_2", "R2");
                        break;

                    case 3:
                        lev_intent.putExtra("level3_2", "R3");
                        break;

                    case 4:
                        lev_intent.putExtra("level3_2", "R4");
                        break;



                    default:
                        break;
                }
                if(adapter != null)
                    adapter = null;
                startActivity(lev_intent);
            }

        });
    }

}
