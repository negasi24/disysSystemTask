package com.disys.systemtask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class OopsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oops);
        btn_retry = (Button) findViewById(R.id.btn_retry);
        btn_retry.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_retry:

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
