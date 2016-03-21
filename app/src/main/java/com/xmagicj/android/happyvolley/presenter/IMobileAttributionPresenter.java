package com.xmagicj.android.happyvolley.presenter;

/**
 * Created by Mumu
 * on 2016/3/21.
 */
public interface IMobileAttributionPresenter extends IBasePresenter {

    /**
     * 获取手机号归属地
     *
     * @param phoneNum phoneNum
     */
    void getMobileAttribution(String phoneNum);
}
