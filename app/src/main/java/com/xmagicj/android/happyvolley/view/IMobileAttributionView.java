package com.xmagicj.android.happyvolley.view;

import com.xmagicj.android.happyvolley.model.entity.MobileInfo;

/**
 * Created by Mumu
 * on 2016/3/21.
 */
public interface IMobileAttributionView {

    void setData(MobileInfo mobileInfo);

    void setErrorMsg(String errorMsg);

    void showLoading();

    void hideLoading();
}
