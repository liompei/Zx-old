package com.liompei.zx;

import android.app.Application;

import com.liompei.zxlog.Zx;

/**
 * Created by Liompei
 * Time 2017/7/19 0:49
 * 1137694912@qq.com
 * remark:
 */

public class App  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //设置全局TAG,并打开日志
        Zx.initLog("Zx",true);
        //打开Toast
        Zx.initToast(getApplicationContext(),true);
    }

}
