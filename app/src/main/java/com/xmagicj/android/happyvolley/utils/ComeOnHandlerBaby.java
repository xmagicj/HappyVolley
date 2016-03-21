package com.xmagicj.android.happyvolley.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Mumu
 * on 2015/12/11.
 */
public class ComeOnHandlerBaby {

    public static ComeOnHandlerBaby getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final ComeOnHandlerBaby INSTANCE = new ComeOnHandlerBaby();
    }

    /**
     * <pre>
     * mHandler.post(new Runnable() {
     * @@Override
     * public void run() {
     *   uiThreadDoSomething();
     * }
     * });
     * </pre>
     */
    Handler mHandler = new Handler(Looper.getMainLooper()); // 若在UIThread中初始化 new Handler(); 就够了

    public Handler getHandler() {
        return mHandler;
    }

}
