package com.softdesign.devintensive.ui.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG= ConstantManager.TAG_PREFIX+"MainActivity";

    protected EditText mEditText;
    protected Button mRedButton, mGreenButton;
    protected String mColorMode;

    /**
     * вызывается при создании активити (изменения конфигурации либо возврата к ней после
     * уничтожения.
     *
     * в методе инициализируется/производится:
     * UI, статические данные, связь данных со списками (иниц. адаптеров)
     *
     * Длительные операции тут не запускать!
     *
     * @param savedInstanceState - объект со значениями, сохраненными в Bundle - состояние UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        mRedButton = (Button) findViewById(R.id.red_btn);
        mGreenButton = (Button) findViewById(R.id.green_btn);
        mEditText = (EditText) findViewById(R.id.textView);

        mRedButton.setOnClickListener(this);
        mGreenButton.setOnClickListener(this);

        if(savedInstanceState == null){
            //первый запуск активити
        } else {
            //активити создается не впервые
        }
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
     *
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
        switch (v.getId()){
            case R.id.green_btn:
                mEditText.setBackgroundColor(Color.GREEN);
                break;
            case R.id.red_btn:
                mEditText.setBackgroundColor(Color.RED);
                break;

        }
    }
}
