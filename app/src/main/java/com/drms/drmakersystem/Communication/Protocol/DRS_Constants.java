package com.drms.drmakersystem.Communication.Protocol;

/**
 * Created by yeongjunsong on 2017. 6. 15..
 */

public class DRS_Constants {

    // DRS Protocal Header
    private static final String DRS_header = "#DRS<";

    // LEVEL ROUND command

// Level & week
    public static final int LEV2_R1 = 1;
    public static final int LEV2_R2  = 2;
    public static final int LEV2_R3  = 3;
    public static final int LEV2_R4  = 4;
    public static final int LEV2_R5  = 5;
    public static final int LEV2_R6  = 6;
    public static final int LEV2_R7  = 7;
    public static final int LEV2_R8  = 8;
    public static final int LEV2_R9  = 9;
    public static final int LEV2_R10 = 10;
    public static final int LEV2_R11 = 11;
    public static final int LEV2_R12 = 12;



//////////////////////////

// Common Command (100 ~ 110)
    public static final int VBAT   =   100;

///////////////////////////////


// Level Command  (110 ~ )

    public static final int LEV2_R1_STATE1 = 110;
    public static final int LEV2_R1_STATE2 = 111;

    public static final int LEV2_R2_STATE1 = 112;
    public static final int LEV2_R2_STATE2 = 113;

    public static final int LEV2_R3_STATE1 = 114;
    public static final int LEV2_R3_STATE2 = 115;

    public static final int LEV2_R4_STATE1  = 116;
    public static final int LEV2_R4_STATE2 = 117;

    public static final int LEV2_R5_STATE1 = 118;
    public static final int LEV2_R5_STATE2 =119;

    public static final int LEV2_R6_STATE1 =120;
    public static final int LEV2_R6_STATE2 =121;

    public static final int LEV2_R7_STATE1 =122;
    public static final int LEV2_R7_STATE2 =123;

    public static final int LEV2_R8_STATE1 =124;
    public static final int LEV2_R8_STATE2 =125;

    public static final int LEV2_R9_STATE1 =126;
    public static final int LEV2_R9_STATE2 =127;

    public static final int LEV2_R10_STATE1 =128;
    public static final int LEV2_R10_STATE2 =129;

    public static final int LEV2_R11_STATE1 =130;
    public static final int LEV2_R11_STATE2 =131;

    public static final int LEV2_R12_STATE1 =132;
    public static final int LEV2_R12_STATE2 =133;



// POWER
    public static final int POWER_ON = 200;
    public static final int POWER_OFF =100;


}
