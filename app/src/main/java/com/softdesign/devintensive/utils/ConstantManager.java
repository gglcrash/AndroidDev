package com.softdesign.devintensive.utils;

/**
 * Created by gglcrash on 02.08.2016.
 */
public interface ConstantManager {
    String TAG_PREFIX="DEV";

    String EXTRA_MESSAGE="MYMSG";

    String EDIT_MODE_KEY="EDITKEY";

    String USER_PHONE_KEY = "USER_PHONE_KEY";
    String USER_MAIL_KEY ="USER_MAIL_KEY";
    String USER_PROF_KEY ="USER_PROF_KEY";
    String USER_REPO_KEY="USER_REPO_KEY";
    String USER_INFO_KEY="USER_INFO_KEY";
    String USER_PHOTO_KEY = "USER_PHOTO_KEY";

    int LOAD_PROFILE_PHOTO = 1;
    int REQUEST_CAMERA_PICTURE=101;
    int REQUEST_GALLERY_PICTURE=102;
    int PERMISSION_REQUEST_SETTINGS_CODE = 201;
    int  CAMERA_REQUSET_PERMISSION_CODE = 202;
    int  CALL_PHONE_PERMISSION_CODE = 203;
    int  SEND_EMAIL_PERMISSION_CODE = 204;
    int  OPEN_BROWSER_PERMISSION_CODE = 205;

}
