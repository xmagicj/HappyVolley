package com.xmagicj.android.happyvolley.model.network.request;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RedirectError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.xmagicj.android.happyvolley.model.network.HappyRequestListener;
import com.xmagicj.android.happyvolley.model.network.HappyRequestQueue;
import com.xmagicj.android.happyvolley.model.network.response.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mumu
 * on 2015/11/16.
 */
public abstract class BaseRequest {
    public final static String TAG = BaseRequest.class.getSimpleName();
    public final static String REQUEST_PREFIX = "";
    public final static int TIME_OUT = 10 * 1000;
    static Gson gson = new Gson();

    private JsonObjectRequest jsonObjectRequest;
    private Map<String, String> paramsMap = new HashMap<>();
    /**
     * 请求前缀 这个项目默认是 ""
     */
    private String mRequestPrefix = REQUEST_PREFIX;
    /**
     * 请求url地址
     */
    private String mURL;
    /**
     * 请求的标示,用来判定是否是同一个请求,来进行取消或其他操作
     */
    private String mTag;
    /**
     * 请求body
     */
    private String mRequestBody;
    /**
     * 超时
     */
    private int mTimeout = TIME_OUT;

    public abstract String initURL();

    public abstract Map<String, String> initParams();

    public abstract void sendRequest(BaseResponse response);

    public final void buildRequest(final HappyRequestListener listener) {
        setURL(initURL());
        setParamsMap(initParams());
        //项目中请求也是用的json 这里例子没有这么复杂 就注释掉了
        //final JSONObject jsonObject = new JSONObject(getParamsMap());
        //setRequestBody(getRequestPrefix() + jsonObject.toString());
        if (TextUtils.isEmpty(getTag())) {
            //setTag(getURL() + " " + getRequestBody());
            setTag(getURL() + "?" + getParamsMap().toString());
        }

        // 这个鬼淘宝的请求 Get + getHeaders()没用,Response Data始终是空的
        // 重点也不是这个地方.所以手动拼装下url.
        String suffix = "";
        for (String headerName : getParamsMap().keySet()) {
            suffix = suffix + pickUpHeader(headerName, getParamsMap().get(headerName));
        }
        String finalURL = getURL() + suffix;

        cancelRequest(getTag());
        // 项目中请求也是用的json
        // 这里例子没有这么复杂 就注释掉了 直接用get + url 的方式
        //jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getURL(), getRequestBody(), new Response.Listener<JSONObject>() {
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, finalURL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onCompleted(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.getMessage());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
            }

            /**
             * 针对这个例子 做的特殊处理
             * 淘宝返回的json字串前缀有特殊字符需要去掉
             *
             * @param response NetworkResponse
             * @return Response
             */
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    jsonString = jsonString.substring(jsonString.indexOf('{'));

                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };
        // 设置超时 重试等参数
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(getTimeout(),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // 加入到请求队列中
        HappyRequestQueue.getInstance().addRequest(jsonObjectRequest, getTag());
    }


    public String pickUpHeader(String key, String value) {
        return "?" + key + "=" + value;
    }

    /**
     * 判断是否有网络 或者 server无响应等非Client端请求参数异常
     *
     * @param error VolleyError
     * @return isConnectionError
     */
    public boolean isConnectionError(VolleyError error) {
        if (error == null) {
            return false;
        }
        if (error instanceof TimeoutError) {
            return true;
        }
        if (error instanceof NoConnectionError) {
            return true;
        }
        if (error instanceof NetworkError) {
            return true;
        }
        if (error instanceof ServerError) {
            return true;
        }
        if (error instanceof RedirectError) {
            return true;
        }
        if (error instanceof AuthFailureError) {
            return true;
        }
        return false;
    }

    /**
     * 强制取消所有请求
     */
    public static void cancelAllRequest() {
        HappyRequestQueue.getInstance().getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                Log.w(TAG, "Cancel All Request");
                return true;
            }
        });
    }

    /**
     * 根据tag来取消符合tag的请求
     *
     * @param tag String
     */
    public static void cancelRequest(final String tag) {
        // 取消已经存在的请求，防止重复请求
        HappyRequestQueue.getInstance().getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                boolean cancel = tag.equals(request.getTag());
                if (cancel) {
                    Log.w(TAG, "Cancel Old Request: " + tag);
                }
                return cancel;
            }
        });
    }

    public Map<String, String> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(Map<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }

    public int getTimeout() {
        return mTimeout;
    }

    public void setTimeout(int mTimeout) {
        this.mTimeout = mTimeout;
    }

    public String getURL() {
        return mURL;
    }

    public void setURL(String mURL) {
        this.mURL = mURL;
    }

    public JsonObjectRequest getJsonObjectRequest() {
        return jsonObjectRequest;
    }

    public void setJsonObjectRequest(JsonObjectRequest jsonObjectRequest) {
        this.jsonObjectRequest = jsonObjectRequest;
    }

    public String getRequestBody() {
        return mRequestBody;
    }

    public void setRequestBody(String mRequestBody) {
        this.mRequestBody = mRequestBody;
    }

    public String getRequestPrefix() {
        return mRequestPrefix;
    }

    public void setRequestPrefix(String mRequestPrefix) {
        this.mRequestPrefix = mRequestPrefix;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String mTag) {
        this.mTag = mTag;
    }
}
