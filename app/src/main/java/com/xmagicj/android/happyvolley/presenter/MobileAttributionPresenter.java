package com.xmagicj.android.happyvolley.presenter;

import android.content.Context;

import com.xmagicj.android.happyvolley.R;
import com.xmagicj.android.happyvolley.model.entity.MobileInfo;
import com.xmagicj.android.happyvolley.model.network.request.GetMobileAttributionRequest;
import com.xmagicj.android.happyvolley.model.network.response.GetMobileAttributionResponse;
import com.xmagicj.android.happyvolley.view.IMobileAttributionView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mumu
 * on 2016/3/21.
 */
public class MobileAttributionPresenter implements IMobileAttributionPresenter {

    Context mContext;
    IMobileAttributionView mobileAttributionView;

    public MobileAttributionPresenter(Context mContext, IMobileAttributionView mobileAttributionView) {
        this.mContext = mContext.getApplicationContext();
        this.mobileAttributionView = mobileAttributionView;
    }

    @Override
    public void getMobileAttribution(String phoneNum) {
        if (!isPhoneNumValid(phoneNum)) {
            mobileAttributionView.setErrorMsg(mContext.getResources().getString(R.string.error_invalid_phone_num));
            return;
        }

        mobileAttributionView.showLoading();

        GetMobileAttributionRequest getMobileAttributionRequest = new GetMobileAttributionRequest(phoneNum);
        getMobileAttributionRequest.sendRequest(new GetMobileAttributionResponse() {
            @Override
            public void onSuccess(final MobileInfo mobileInfo) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mobileAttributionView.hideLoading();
                        mobileAttributionView.setData(mobileInfo);
                    }
                });
            }

            @Override
            public void onException(final String e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mobileAttributionView.hideLoading();
                        mobileAttributionView.setErrorMsg(e);
                    }
                });
            }
        });
    }

    /**
     * isPhoneNumValid
     *
     * @param phoneNum String
     * @return boolean
     */
    private boolean isPhoneNumValid(String phoneNum) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");
        Matcher m = p.matcher(phoneNum);

        return m.matches();

    }
}
