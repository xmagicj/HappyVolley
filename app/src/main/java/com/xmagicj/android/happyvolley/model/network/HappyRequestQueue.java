package com.xmagicj.android.happyvolley.model.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.RequestFuture;
import com.xmagicj.android.happyvolley.view.MyApplication;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Mumu
 * on 2015/11/16.
 */
public class HappyRequestQueue {
    public final String TAG = "RequesterDefaultTag";
    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "volley";
    /**
     * Number of network request dispatcher threads to start.
     */
    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 1;

    RequestQueue mRequestQueue;
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    /**
     * getInstance
     *
     * @return RequestQueue
     */
    public static HappyRequestQueue getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
     */
    private static class SingletonHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        static final HappyRequestQueue INSTANCE = new HappyRequestQueue();
    }

    /**
     * 异步请求
     *
     * @param request Request
     * @param <T>     Request
     */
    public <T> void addRequest(Request<T> request) {
        addRequest(request, null);
    }

    /**
     * 异步请求
     *
     * @param request Request
     * @param tag     tag用于区分是否同一个请求
     * @param <T>     Request
     */
    public <T> void addRequest(Request<T> request, Object tag) {
        request.setTag(tag == null ? TAG : tag);
        getRequestQueue().add(request);
    }

    /**
     * 同步请求
     *
     * @param request Request
     * @param <T>     Request
     * @return JSONObject
     */
    public <T> JSONObject addSyncRequest(Request<T> request) {
        return addSyncRequest(request, null);
    }

    /**
     * 同步请求
     *
     * @param request Request
     * @param tag     tag用于区分是否同一个请求
     * @param <T>     Request
     * @return JSONObject
     */
    public <T> JSONObject addSyncRequest(Request<T> request, Object tag) {
        request.setTag(tag == null ? TAG : tag);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        getRequestQueue().add(request);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public com.android.volley.RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            //mRequestQueue = Volley.newRequestQueue(MyApplication.getInstance().getApplicationContext());
            mRequestQueue = newRequestQueue(MyApplication.getInstance().getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * 自定义Volley请求Queue
     *
     * @param context Context
     * @return RequestQueue
     */
    public RequestQueue newRequestQueue(Context context) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir),
                network,
                DEFAULT_NETWORK_THREAD_POOL_SIZE,
                new ExecutorDelivery(executorService));
        queue.start();
        return queue;
    }

}
