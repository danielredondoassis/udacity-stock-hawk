package com.udacity.stockhawk.ui;

import android.os.Build;

/**
 * Created by DT on 5/11/16.
 */
public class AndroidVersionHelper {

    public static boolean isLollipop(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1){
            return true;
        }
        return false;
    }

    public static boolean isKitKat(){

        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
            return true;
        }
        return false;
    }

    public static boolean isJellyBean(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2){
            return true;
        }
        return false;
    }

    public static boolean isJellyBeanMR1OrHigher(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            return true;
        }
        return false;
    }

    public static boolean isIceCream(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            return true;
        }
        return false;
    }

    public static boolean isJellyOrHigher() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        return false;
    }

    public static boolean isIceCreamOrHigher() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return true;
        }
        return false;
    }

    public static boolean isKitKatOrHigher(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            return true;
        }
        return false;
    }

    public static boolean isLollipopOrHigher(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return true;
        }
        return false;
    }

    public static boolean isMarshmallowOrHigher(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return true;
        }
        return false;
    }


    public static boolean isNougatOrHigher() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return true;
        }
        return false;
    }
}

