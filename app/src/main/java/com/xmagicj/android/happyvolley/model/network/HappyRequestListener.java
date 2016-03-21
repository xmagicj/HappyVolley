package com.xmagicj.android.happyvolley.model.network;

import org.json.JSONObject;

/**
 * Created by Mumu
 * on 2015/11/16.
 */
public interface HappyRequestListener {
    void onCompleted(JSONObject response);

    void onError(String error);

    // 一般在项目中用的自定义Exception去处理
    //void onError(CustomException error);

    //void onCancel(CustomException error);
}
