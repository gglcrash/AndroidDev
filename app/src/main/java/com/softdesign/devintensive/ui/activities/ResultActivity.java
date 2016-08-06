package com.softdesign.devintensive.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.ConstantManager;

import java.util.logging.LogRecord;

public class ResultActivity extends BaseActivity implements View.OnClickListener {

    Button mButtonOk, mButtonCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mButtonCall = (Button)findViewById(R.id.button_call);
        mButtonOk = (Button)findViewById(R.id.button_ok);
        mButtonCall.setOnClickListener(this);
        mButtonOk.setOnClickListener(this);

        Intent intent = getIntent();
        String message = intent.getStringExtra(ConstantManager.EXTRA_MESSAGE);
        EditText resultEditText = (EditText)findViewById(R.id.result_edittext);
        resultEditText.setText(message);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_ok:
                finish();
                break;
            case R.id.button_call:
/*                showProgress();
                runWithDelay();*/
                break;
        }
    }

  /*  private void runWithDelay(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //TODO: выполнить с задержкой
                hideProgress();
            }
        },3000);
    }
*/
}
