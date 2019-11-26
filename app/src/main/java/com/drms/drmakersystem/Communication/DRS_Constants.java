package com.drms.drmakersystem.Communication;

/**
 * Created by yeongjunsong on 2017. 6. 15..
 */

public class DRS_Constants {

    // DRS Protocal Header
    private static final String DRS_header = "#DRS<";

    // LEVEL ROUND command
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

    public static final int LEV3_R1 = 12;
    public static final int LEV3_R2 = 13;
    public static final int LEV3_R3 = 14;
    public static final int LEV3_R4 = 15;
    public static final int LEV3_R5 = 16;
    public static final int LEV3_R6 = 17;
    public static final int LEV3_R7 = 18;
    public static final int LEV3_R8 = 19;
    public static final int LEV3_R9 = 20;
    public static final int LEV3_R10 = 21;
    public static final int LEV3_R11 = 22;
    public static final int LEV3_R12 = 23;

    public static final int LEV4_R1 = 24;
    public static final int LEV4_R2 = 25;
    public static final int LEV4_R3 = 26;
    public static final int LEV4_R4 = 27;
    public static final int LEV4_R5 = 28;
    public static final int LEV4_R6 = 29;
    public static final int LEV4_R7 = 30;
    public static final int LEV4_R8 = 31;
    public static final int LEV4_R9 = 32;
    public static final int LEV4_R10 = 33;
    public static final int LEV4_R11 = 34;
    public static final int LEV4_R12 = 35;



    // Common Command

    public static final int POWER_ON = 50;
    public static final int POWER_OFF = 51;
    public static final int MOTOR_INIT = 52;
    public static final int CURRENT_VBAT = 53;
    public static final int NOTHING = 54;




    ////////////////////// Each Level - Round Command///////////////////////////////

    // Level2 Round2 Lotto
    public static final int LOTTO_STATE = 60;
    public static final int LEV2_R2_STATE2 = 61;

    public static final int FACE_STATE = 62;
    public static final int LEV2_R3_STATE2 = 63;

    public static final int TRAIN_RC_DATA = 64;
    public static final int LEV2_R4_STATE2 = 65;

    public static final int MUSIC_RC_DATA = 66;
    public static final int LEV2_R5_STATE2 = 67;

    public static final int ANIMAL_STATE = 68;
    public static final int LEV2_R6_STATE2 = 69;

    public static final int PLANE_RC_DATA = 70;
    public static final int LEV2_R8_STATE2 = 71;

    public static final int TRACTOR_RC_DATA = 72;
    public static final int LEV2_R9_STATE2 = 73;

    public static final int CAR_RC_DATA = 74;
    public static final int LEV2_R10_STATE2 = 75;

    public static final int BUMPER_RC_DATA = 76;
    public static final int LEV2_R11_STATE2 = 77;

}
