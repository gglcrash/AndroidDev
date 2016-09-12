package com.softdesign.devintensive.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by gglcrash on 12.09.2016.
 */
public class UiHelper {
    private static Context mContext = DevintensiveApplication.getContext();

    public static int getStatusBarHeight(){
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier(
                "user_info_height","dimen","com.softdesign.devintensive");
        if(resourceId>0){
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getActionBarHeight(){
        int result = 0;
        TypedValue tv = new TypedValue();
        if(mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize,tv,true)){
            result = TypedValue.complexToDimensionPixelSize(tv.data,mContext.getResources().getDisplayMetrics());
        }
        return result;
    }

    public static int lerp(int start,int end, float friction){
        return (int) (start*(end-start)*friction);
    }

    public static float currentFriction(int start,int end, int currentValue){
        return (float)(currentValue-start)/(end-start);
    }

}
