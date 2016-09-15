package com.softdesign.devintensive.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private static final String TAG = ConstantManager.TAG_PREFIX + "MainActivity";

    private boolean mCurrentEditMode;

    private BitmapDrawable mBmpAvatarRounded;
    private DataManager mDataManager;

    @BindView(R.id.navigation_view) NavigationView mNavigationView;
    @BindView(R.id.mobile_edittext) EditText mEditTextMobile;
    @BindView(R.id.email_edittext) EditText mEditTextEmail;
    @BindView(R.id.profile_edittext) EditText mEditTextProfile;
    @BindView(R.id.repo_edittext) EditText mEditTextRepo;
    @BindView(R.id.info_edittext) EditText mEditTextInfo;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.main_coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.navigation_drawer) DrawerLayout mNavigationDrawer;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder) RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.appbar_layout) AppBarLayout mAppBarLayout;
    @BindView(R.id.user_photo_img) AppCompatImageView mProfileImage;
    @BindView(R.id.call_image) ImageView mCallByPhoneImage;
    @BindView(R.id.email_image) ImageView mSendEmailImage;
    @BindView(R.id.open_profile_image) ImageView mProfileLinkImage;
    @BindView(R.id.open_repo_image) ImageView mRepoLinkImage;

    private ImageView mAvatarImageView;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private List<EditText> mUserInfo;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    /**
     * Длительные операции тут не запускать!
     *
     * @param savedInstanceState - объект со значениями, сохраненными в Bundle - состояние UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDataManager = DataManager.getInstance();

        //скругление аватара
        mAvatarImageView = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.circle_avatar);
        mBmpAvatarRounded = (BitmapDrawable) getResources().getDrawable(R.drawable.user_photo_mini);
        mAvatarImageView.setImageDrawable(new RoundedAvatarDrawable(mBmpAvatarRounded.getBitmap()));

        mUserInfo = new ArrayList<>();
        mUserInfo.add(mEditTextMobile);
        mUserInfo.add(mEditTextEmail);
        mUserInfo.add(mEditTextProfile);
        mUserInfo.add(mEditTextRepo);
        mUserInfo.add(mEditTextInfo);

        setupToolbar();
        setupDrawer();

        loadUserInfoValue();
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_photo)
                .into(mProfileImage);

        if (savedInstanceState == null) {
            //первый запуск активити
        } else {
            //активити создается не впервые
            mCurrentEditMode = savedInstanceState.getBoolean(ConstantManager.EDIT_MODE_KEY, false);
            changeEditMode(mCurrentEditMode);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * обработка кликов
     * заменится с ButterKnife
     */

    @OnClick(R.id.profile_placeholder)
    public void profilePlaceholderClick(){
        showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
    }

    @OnClick(R.id.call_image)
    public void onCallClick(){
        callByPhone(mEditTextMobile.getText().toString());
    }

    @OnClick(R.id.fab)
    public void onFabClick(){
        if (mCurrentEditMode) {
            changeEditMode(false);
            mCurrentEditMode = false;
        } else {
            changeEditMode(true);
            mCurrentEditMode = true;
        }
    }

    @OnClick(R.id.email_image)
    public void onEmailSend(){
        sendEmail(mEditTextEmail.getText().toString());
    }

    @OnClick(R.id.open_profile_image)
    public void onProfileClick(){
        openLink(mEditTextProfile.getText().toString());
    }

    @OnClick(R.id.open_repo_image)
    public void onRepoClick(){
        openLink(mEditTextRepo.getText().toString());
    }


    /**
     * соханение текущего значения режима редактирования
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);

        Log.d(TAG, "onSaveInstanceState");
    }


    private void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * установка тулбара
     */
    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * установка вьюхи, выезжающей слева
     */
    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    showSnackBar(item.getTitle().toString());
                    item.setChecked(true);
                    mNavigationDrawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            });
        }
    }

    /**
     * переключает режим редактирования информации
     *
     * @param mode если true - редактирование, false - просмотр
     */
    private void changeEditMode(boolean mode) {
        if (mode) {
            mFab.setImageResource(R.drawable.ic_done_24dp);
            for (EditText userValue : mUserInfo) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }

            mEditTextMobile.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditTextMobile, InputMethodManager.SHOW_IMPLICIT);

            showProfilePlaceholder();
            lockToolbar();
            mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        } else {
            saveUserInfoValue();
            mFab.setImageResource(R.drawable.ic_edit_24dp);
            for (EditText userValue : mUserInfo) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
            hideProfilePlaceholder();
            unlockToolbar();
            mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white));
        }
    }

    /**
     * восстановление сохраненных данных
     */

    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfo.get(i).setText(userData.get(i));
        }
    }

    /**
     * соъхранение введенных данных
     */

    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfo) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    /**
     * обработка нажатия back при открытой левой вьюхе
     */

    @Override
    public void onBackPressed() {
        if (mNavigationView.isShown()) {
            mNavigationDrawer.closeDrawer(Gravity.LEFT);
        } else {
            moveTaskToBack(true);
        }
    }

    /**
     * загрузка фото из галлереи
     */

    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");

        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.choose_photo)),
                ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    /**
     * загрузка фото из камеры и обработка разрешений
     */

    private void loadPhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                //TODO: обработать ошибку
            }

            if (mPhotoFile != null) {
                //TODO: передать файл в интент
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        }else{
            ActivityCompat.requestPermissions(this,new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            },ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);


            Snackbar.make(mCoordinatorLayout, getString(R.string.get_permissions_text), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.allow_permission_text), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    /**
     * звонок по номеру телефона при нажатии на кнопку + разрешения
     * @param mobile - номер телефона
     */

    private void callByPhone(String mobile){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            if(mobile.length()>8&&mobile.length()<18) {
                Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
                startActivity(dialIntent);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{ android.Manifest.permission.CALL_PHONE },
                    ConstantManager.CALL_PHONE_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout, getString(R.string.get_permissions_text), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.allow_permission_text), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    /**
     * отправка письма при нажатии на кнопку
     * @param email - адрес почты
     */

    private void sendEmail(String email){

        if(email.length()>4) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts(
                    "mailto", email, null));
            emailIntent.setType("message/rfc822");

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
    }

    /**
     * открыть ссылку в браузере
     * @param link - ссылка
     */

    private void openLink(String link){
        if(link.length()>3) {
            //todo: обработать парсер
                Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browseIntent);
        }
    }

    /**
     * ответ на запрос получения разрешений
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length==2){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //todo: обработать "разрешение получено", например, вывести msg или обработать логикой
            }
            if(grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //todo: обработать "разрешение получено", например, вывести msg или обработать логикой
            }
        }
        if(requestCode==ConstantManager.CALL_PHONE_PERMISSION_CODE){
            //todo: обработать
        }
    }

    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbarLayout.setLayoutParams(mAppBarParams);
    }

    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
    }


    /**
     * получение результата из другой активити (камера или галлерея)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    InsertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    InsertProfileImage(mSelectedImage);
                }
        }
    }

    /**
     * установить фото по Uri
     * @param image - изображение
     */

    private void InsertProfileImage(Uri image) {
        Picasso.with(this)
                .load(image)
                .into(mProfileImage);

        mDataManager.getPreferencesManager().saveUserPhoto(image);
    }

    /**
     * создани диалогового окна
     */

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_photo_load), getString(R.string.user_photo_capture), getString(R.string.user_photo_reset), getString(R.string.user_photo_cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_photo_change_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // загрузить из галллереи
                                loadPhotoFromGallery();
                                break;
                            case 1:
                                // загрузить из камеры

                                loadPhotoFromCamera();
                                break;
                            case 2:
                                //сбросить фото
                                resetPhoto();
                                break;
                            case 3:
                                //отмена
                                dialog.cancel();
                                break;
                        }
                    }
                });
                return builder.create();


            default:
                return null;
        }
    }

    /**
     * сбросить фото на стандартное
     */

    private void resetPhoto() {
        Picasso.with(this)
                .load(R.drawable.user_photo)
                .into(mProfileImage);

        mDataManager.getPreferencesManager().saveUserPhoto(
                Uri.parse("android.resource://com.softdesign.devintensive/drawable/user_photo")
        );
    }

    /**
     * создание файла для сохранения в него изображения с камеры
     */

    private File createImageFile()
            throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN,System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "images/jpeg");
        values.put(MediaStore.MediaColumns.DATA,image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        return image;
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

}
