package com.xmagicj.android.happyvolley.model.network.request;

import com.xmagicj.android.happyvolley.model.entity.MobileInfo;
import com.xmagicj.android.happyvolley.model.network.HappyRequestListener;
import com.xmagicj.android.happyvolley.model.network.response.BaseResponse;
import com.xmagicj.android.happyvolley.model.network.response.GetMobileAttributionResponse;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mumu
 * on 2015/11/17.
 */
public class GetMobileAttributionRequest extends BaseRequest {
    public final String url = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm";
    public static final String TEL = "tel";
    private GetMobileAttributionResponse getMobileAttributionResponse;
    private String mTel;

    public GetMobileAttributionRequest(String phoneNum) {
        mTel = phoneNum;
    }

    @Override
    public String initURL() {
        return url;
    }

    @Override
    public Map<String, String> initParams() {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(TEL, mTel);
        return paramsMap;
    }

    @Override
    public void sendRequest(BaseResponse response) {
        getMobileAttributionResponse = (GetMobileAttributionResponse) response;
        buildRequest(new HappyRequestListener() {
            @Override
            public void onCompleted(JSONObject response) {
                if (null != response) {
                    MobileInfo mobileInfo = gson.fromJson(response.toString(), MobileInfo.class);
                    if (null != mobileInfo) {
                        getMobileAttributionResponse.onSuccess(mobileInfo);
                        return;
                    }
                }
                getMobileAttributionResponse.onException("result error");
            }

            @Override
            public void onError(String error) {
                getMobileAttributionResponse.onException(error);
            }
        });

    }

}
