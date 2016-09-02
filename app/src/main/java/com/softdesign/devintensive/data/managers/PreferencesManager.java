package com.softdesign.devintensive.data.managers;

import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevintensiveApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gglcrash on 08.08.2016.
 */
public class PreferencesManager {
    private SharedPreferences mSharedPreferences;

    private static final String[] USER_FIELDS = {
            ConstantManager.USER_PHONE_KEY,ConstantManager.USER_MAIL_KEY,ConstantManager.USER_PROF_KEY,
            ConstantManager.USER_REPO_KEY,ConstantManager.USER_INFO_KEY
    };

    public PreferencesManager(){
        this.mSharedPreferences= DevintensiveApplication.getSharedPreferences();
    }

    public void saveUserProfileData(List<String> userFields){
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        for(int i = 0;i<USER_FIELDS.length;i++){
            editor.putString(USER_FIELDS[i],userFields.get(i));
        }
        editor.apply();
    }

    public List<String> loadUserProfileData(){
        List<String> userFields = new ArrayList<>();
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_PHONE_KEY,""));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_MAIL_KEY,""));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_PROF_KEY,""));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_REPO_KEY,""));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_INFO_KEY,""));
        return userFields;
    }

    public void saveUserPhoto(Uri uri){
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString(ConstantManager.USER_PHOTO_KEY,uri.toString());
        editor.apply();
    }

    public Uri loadUserPhoto(){
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY,
                "android.resource://com.softdesign.devintensive/drawable/user_photo"));
    }
}
