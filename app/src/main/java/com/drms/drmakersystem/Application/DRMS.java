package com.drms.drmakersystem.Application;

import android.app.Application;

/**
 * Created by comm on 2018-02-21.
 */

public class DRMS extends Application {
    public static final int LEV2_1  = 10;
    public static final int LEV2_2  = 11;
    public static final int LEV2_3  = 12;
    public static final int LEV2_4  = 13;
    public static final int LEV2_5  = 14;
    public static final int LEV2_6  = 15;
    public static final int LEV2_7  = 16;
    public static final int LEV2_8  = 17;
    public static final int LEV2_9  = 18;
    public static final int LEV2_10  = 19;
    public static final int LEV2_11  = 20;
    public static final int LEV2_12  = 21;


    private int[] lev2_r2 = new int[7];

    public void setLev2_r2(int index, int value){
        lev2_r2[index] = value;
    }

    public void setLev2_r2(int[] lev2_r2){
        this.lev2_r2 = lev2_r2;
    }

    public int[] getLev2_r2(){
        return lev2_r2;
    }

    private String currentNotice = "";
    private boolean hideNotice = false;

    public void setCurrentNotice(String currentNotice){
        this.currentNotice = currentNotice;
    }

    public String getCurrentNotice(){
        return currentNotice;
    }

    public void setHideNotice(boolean hideNotice){
        this.hideNotice = hideNotice;
    }

    public boolean isHideNotice(){
        return hideNotice;

    }


}
