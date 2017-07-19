package com.liompei.zx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liompei.zxlog.Zx;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verbose:
                Zx.v();
                break;
            case R.id.btn_debug:
                Zx.d();
                break;
            case R.id.btn_info:
                Zx.i();
                break;
            case R.id.btn_warn:
                Zx.w();
                break;
            case R.id.btn_error:
                Zx.e();
                break;
            case R.id.btn_toast:
                Zx.show("Toast");
                break;
        }
    }
}
