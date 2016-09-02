package com.softdesign.devintensive.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
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
import java.util.jar.Manifest;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "MainActivity";

    private boolean mCurrentEditMode;

    private ImageView mAvatarImageView;

    private BitmapDrawable mBmpAvatarRounded;
    private DataManager mDataManager;

    private NavigationView mNavigationView;
    private EditText mEditTextMobile, mEditTextEmail, mEditTextProfile, mEditTextRepo, mEditTextInfo;
    private Toolbar mToolbar;
    private CoordinatorLayout mCoordinatorLayout;
    private DrawerLayout mNavigationDrawer;
    private FloatingActionButton mFab;
    private List<EditText> mUserInfo;
    private RelativeLayout mProfilePlaceholder;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private AppBarLayout mAppBarLayout;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;
    private AppCompatImageView mProfileImage;
    private ImageView mCallByPhoneImage, mSendEmailImage, mProfileLinkImage, mRepoLinkImage;

    /**
     * вызывается при создании активити (изменения конфигурации либо возврата к ней после
     * уничтожения.
     * <p/>
     * в методе инициализируется/производится:
     * UI, статические данные, связь данных со списками (иниц. адаптеров)
     * <p/>
     * Длительные операции тут не запускать!
     *
     * @param savedInstanceState - объект со значениями, сохраненными в Bundle - состояние UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");


        mDataManager = DataManager.getInstance();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        //скругление аватара
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mAvatarImageView = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.circle_avatar);
        mBmpAvatarRounded = (BitmapDrawable) getResources().getDrawable(R.drawable.user_photo_mini);
        mAvatarImageView.setImageDrawable(new RoundedAvatarDrawable(mBmpAvatarRounded.getBitmap()));

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mEditTextMobile = (EditText) findViewById(R.id.mobile_edittext);
        mEditTextEmail = (EditText) findViewById(R.id.email_edittext);
        mEditTextProfile = (EditText) findViewById(R.id.profile_edittext);
        mEditTextRepo = (EditText) findViewById(R.id.repo_edittext);
        mEditTextInfo = (EditText) findViewById(R.id.info_edittext);
        mProfilePlaceholder = (RelativeLayout) findViewById((R.id.profile_placeholder));
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mProfileImage = (AppCompatImageView) findViewById(R.id.user_photo_img);
        mCallByPhoneImage = (ImageView) findViewById(R.id.call_image);
        mSendEmailImage = (ImageView) findViewById(R.id.email_image);
        mProfileLinkImage = (ImageView) findViewById(R.id.open_profile_image);
        mRepoLinkImage = (ImageView) findViewById(R.id.open_repo_image);


        mUserInfo = new ArrayList<>();
        mUserInfo.add(mEditTextMobile);
        mUserInfo.add(mEditTextEmail);
        mUserInfo.add(mEditTextProfile);
        mUserInfo.add(mEditTextRepo);
        mUserInfo.add(mEditTextInfo);

        //saveUserInfoValue();

        mProfilePlaceholder.setOnClickListener(this);
        mFab.setOnClickListener(this);
        mCallByPhoneImage.setOnClickListener(this);
        mSendEmailImage.setOnClickListener(this);
        mProfileLinkImage.setOnClickListener(this);
        mRepoLinkImage.setOnClickListener(this);


        setupToolbar();
        setupDrawer();

        loadUserInfoValue();
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_photo)
                .into(mProfileImage);

        if (savedInstanceState == null) {
            //первый запуск активити
            showSnackBar("Активити запускается впервые");
        } else {
            //активити создается не впервые
            mCurrentEditMode = savedInstanceState.getBoolean(ConstantManager.EDIT_MODE_KEY, false);
            changeEditMode(mCurrentEditMode);
            showSnackBar("Активити уже создавалось");
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
     * вызывается при старте активити перед моментом того, как UI станет доступен пользователю
     * как правило в нем происзодит регистрация подписки на события, остановка которых была
     * произведена в onStop()
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    /**
     * вызывается когда активити становится доступна пользователю для взаимодействия
     * в нем как правило происзодит запууск анимаций/аудио/видео/запуск BroadcastReciever,
     * необходимых для реализации UI логики/запуск выполнения потоков и т.п.
     * должен быть максмально ЛЕГКОВЕСНЫМ для лучшей отзывчивости UI
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    /**
     * вызывается когда текущая активити теряет фокус, но остается видимой (всплывает диалоговое
     * окно, либо наше частично перекрывается другой активити и т.п.)
     * <p/>
     * в нем реализуется соханение легковесных UI данных/анимаций/аудио/видео и т.д.
     */
    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
    }

    /**
     * метод вызывается когда активити становится невидим для пользователя.
     * в данном методе происходит отписка от событий, остановка сложных анимаций, сложные
     * операции по сохранению данных/прерывание запущенных потоков и т.п.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /**
     * вызывается при окончании работы активити (когда это происходит системно или после
     * вызова finish() )
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    /**
     * вызывается при рестарте активити/позобновлении работы после вызова метда onStop()
     * в нем реализуется спецфическая логика, которая должна быть реализована именно при
     * рестарте активности (например, запрос к серверу, который необходимо вызвать при
     * возвращении из другой активности (обновление данныз, подписка на определенное событие,
     * проинициализированное на другом экране и т.д.) )
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab:
                if (mCurrentEditMode) {
                    changeEditMode(false);
                    mCurrentEditMode = false;
                } else {
                    changeEditMode(true);
                    mCurrentEditMode = true;
                }
                break;

            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;

            case R.id.call_image:
                callByPhone(mEditTextMobile.getText().toString());
                break;

            case R.id.email_image:
                sendEmail(mEditTextEmail.getText().toString());
                break;

            case R.id.open_profile_image:
                openLink(mEditTextProfile.getText().toString());
                break;

            case R.id.open_repo_image:
                openLink(mEditTextRepo.getText().toString());
                break;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);

        Log.d(TAG, "onSaveInstanceState");
    }


    private void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

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
     * @param mode если 1 - редактирование, 0 - просмотр
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

    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfo.get(i).setText(userData.get(i));
        }
    }

    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfo) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationView.isShown()) {
            mNavigationDrawer.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");

        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.choose_photo)),
                ConstantManager.REQUEST_GALLERY_PICTURE);
    }

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
            },ConstantManager.CAMERA_REQUSET_PERMISSION_CODE);


            Snackbar.make(mCoordinatorLayout, getString(R.string.get_permissions_text), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.allow_permission_text), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

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

    private void openLink(String link){
        if(link.length()>2) {
            Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browseIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==ConstantManager.CAMERA_REQUSET_PERMISSION_CODE && grantResults.length==2){
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
     *
     * @param requestCode
     * @param resultCode
     * @param data
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


    private void InsertProfileImage(Uri image) {
        Picasso.with(this)
                .load(image)
                .into(mProfileImage);

        mDataManager.getPreferencesManager().saveUserPhoto(image);
    }

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

    private void resetPhoto() {
        Picasso.with(this)
                .load(R.drawable.user_photo)
                .into(mProfileImage);

        mDataManager.getPreferencesManager().saveUserPhoto(
                Uri.parse("android.resource://com.softdesign.devintensive/drawable/user_photo")
        );
    }

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
