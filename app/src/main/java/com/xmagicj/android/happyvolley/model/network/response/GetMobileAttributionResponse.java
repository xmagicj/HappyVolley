package com.xmagicj.android.happyvolley.model.network.response;


import com.xmagicj.android.happyvolley.model.entity.MobileInfo;

/**
 * Created by Mumu
 * on 2015/11/16.
 */
public interface GetMobileAttributionResponse extends BaseResponse {
    void onSuccess(MobileInfo mobileInfo);
}
