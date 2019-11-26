package com.drms.drmakersystem.etc;

import android.graphics.Bitmap;

/**
 * Created by jjun on 2018. 2. 23..
 */

public class Icon {


    Bitmap img1, img2,img3;

    public Icon(Bitmap img1){
        this.img1 = img1;
    }

    public Icon(Bitmap img1, Bitmap img2) {
        this.img1 = img1;
        this.img2 = img2;
    }

    public Icon(Bitmap img1, Bitmap img2, Bitmap img3) {
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
    }

    public Bitmap getImg1(){
        return img1;
    }
    public Bitmap getImg2(){
        return img2;
    }
    public Bitmap getImg3(){
        return img3;
    }

    public void setImg(Bitmap img1){
        this.img1 = img1;
    }

    public void setImg(Bitmap img1, Bitmap img2){
        this.img1 = img1;
        this.img2 = img2;
    }

    public void setImg(Bitmap img1, Bitmap img2, Bitmap img3){
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
    }
}
