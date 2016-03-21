package com.xmagicj.android.happyvolley.presenter;

import android.os.Handler;

import com.xmagicj.android.happyvolley.utils.ComeOnHandlerBaby;

/**
 * 所有的Presenter继承自该类
 * Created by Mumu
 * on 2015/11/19.
 */
public interface IBasePresenter {
    Handler mHandler = ComeOnHandlerBaby.getInstance().getHandler();
}
