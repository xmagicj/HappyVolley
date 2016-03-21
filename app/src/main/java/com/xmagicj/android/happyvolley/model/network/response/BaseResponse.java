package com.xmagicj.android.happyvolley.model.network.response;

/**
 * Created by Mumu
 * on 2015/11/16.
 */
public interface BaseResponse {
//    /**
//     * 请求异常
//     *
//     * @param e CustomException
//     */
//    void onException(CustomException e);

//    /**
//     * 请求取消
//     *
//     * @param e
//     */
//    void onCancel(CustomException e);

    void onException(String e);

}
