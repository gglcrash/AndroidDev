package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "MainActivity";

    private boolean mCurrentEditMode;

    private ImageView mAvatarImageView;

    private BitmapDrawable mBmpAvatarRounded;
    private DataManager mDataManager;

    private NavigationView mNavigationView;
    private EditText mEditTextMobile, mEditTextEmail, mEditTextProfile, mEditTextRepo, mEditTextInfo;
    private Toolbar mToolbar;
    private Button mButtonDone;
    private CoordinatorLayout mCoordinatorLayout;
    private DrawerLayout mNavigationDrawer;
    private FloatingActionButton mFab;
    private List<EditText> mUserInfo;

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

        mButtonDone = (Button) findViewById(R.id.done_btn);
        mButtonDone.setOnClickListener(this);

        //скругление аватара
        mNavigationView = (NavigationView)findViewById(R.id.navigation_view);
        mAvatarImageView = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.circle_avatar);
        mBmpAvatarRounded = (BitmapDrawable) getResources().getDrawable(R.drawable.user_photo_mini);
        mAvatarImageView.setImageDrawable(new RoundedAvatarDrawable(mBmpAvatarRounded.getBitmap()));

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_layout);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mEditTextMobile = (EditText) findViewById(R.id.mobile_edittext);
        mEditTextEmail = (EditText) findViewById(R.id.email_edittext);
        mEditTextProfile = (EditText) findViewById(R.id.profile_edittext);
        mEditTextRepo = (EditText) findViewById(R.id.repo_edittext);
        mEditTextInfo = (EditText) findViewById(R.id.info_edittext);

        mUserInfo = new ArrayList<>();
        mUserInfo.add(mEditTextMobile);
        mUserInfo.add(mEditTextEmail);
        mUserInfo.add(mEditTextProfile);
        mUserInfo.add(mEditTextRepo);
        mUserInfo.add(mEditTextInfo);

        saveUserInfoValue();

        mFab.setOnClickListener(this);
        setupToolbar();
        setupDrawer();
        loadUserInfoValue();

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
            case R.id.done_btn:
                onDone();
                break;
            case R.id.fab:
                if (mCurrentEditMode) {
                    changeEditMode(false);
                    mCurrentEditMode = false;
                } else {
                    changeEditMode(true);
                    mCurrentEditMode = true;
                }
                break;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);

        Log.d(TAG, "onSaveInstanceState");
    }

    private void onDone() {
        Intent intent = new Intent(this, ResultActivity.class);

        String message = "Mobile: " + mEditTextMobile.getText().toString() + "\n" +
                "E-Mail: " + mEditTextEmail.getText().toString() + "\n" +
                "Profile: " + mEditTextProfile.getText().toString() + "\n" +
                "Repo: " + mEditTextRepo.getText().toString() + "\n" +
                "Info: " + mEditTextInfo.getText().toString();

        intent.putExtra(ConstantManager.EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
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
        } else {
            saveUserInfoValue();
            mFab.setImageResource(R.drawable.ic_edit_24dp);
            for (EditText userValue : mUserInfo) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
        }
    }

    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for(int i=0;i<userData.size();i++){
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
}
