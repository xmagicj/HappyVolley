package com.xmagicj.android.happyvolley.view;

import android.app.Application;

/**
 * Created by Mumu
 * on 2015/11/5.
 */
public class MyApplication extends Application {
    private static MyApplication applicationContext;


    public static MyApplication getInstance() {
        return applicationContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
    }
}
