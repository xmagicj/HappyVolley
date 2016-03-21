# HappyVolley
在非UI线程返回Response且可以配置并发线程数的Volley(未改动源码)
采用MVP标准架构搭建Demo

## 前言
我知道...我懂
大家现在都用 **OKHttp** 或者 **Retrofit** <br />
为啥这里还要发布和二次封装 **Volley** 呢?

**原因有三**
- 以往的成果还是做个记录.
- Volley在没有上传下载等类似Task的情况下 还是有生存空间的.
- 哈哈~秘密...这个以后再谈...

## 解决什么问题
1. Volley默认是在UI Thread返回Response,导致如果还需要对数据进行操作(比如DB,筛选,排序等相对耗时任务),则需要另起Task(非UI Thread)去做.这样就存在新的同步问题,甚至踏入callback深渊.<br />
**解决**
通过传入ExecutorService来让其成为单线程模型,Response依然在非UI Thread返回.这样数据层可以继续对其进行操作.
当结果返回到业务层的时候 再通知GUI更新即可(Handler EventBus等随意了).
2. Volley默认采用4个线程作为线程池控制,无法修改.<br />
**解决**
我们通过Volley构造方法<br />
```
     /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param cache A Cache to use for persisting responses to disk
     * @param network A Network interface for performing HTTP requests
     * @param threadPoolSize Number of network dispatcher threads to create
     * @param delivery A ResponseDelivery interface for posting responses and errors
     */
        public RequestQueue(Cache cache, Network network, int threadPoolSize,
            ResponseDelivery delivery) {
        mCache = cache;
        mNetwork = network;
        mDispatchers = new NetworkDispatcher[threadPoolSize];
        mDelivery = delivery;
        }
```
去创建一个新的RequestQueue对象即可解决这个问题.

## 工程描述
本来是说就放个HappyRequestQueue类好了.反正大家也看得懂.
后来想想算了咱们还是做个Demo吧.
谁让哥做人这么厚道呢...

那就开始写呗...
啪啪啪~

工程一定要基于Android Studio才溜得飞起
MVP架构搞起来 各种Niubility... <br />
![github](https://github.com/xmagicj/HappyVolley/blob/master/happy_volley_mvp.png "happy_volley_mvp")  

网络请求库的Demo 肯定要请求点啥啊...恩对~
但是...天呐~~~哪有API来做Demo测试呢?

好在我机智...找到淘宝有个手机号的API接口 不用像百度那样非要appkey
> https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=手机号 <br />

坑爹的是这个接口不能用POST,以往封装好的JSONRequest无法直接使用...
不过这也难不倒哥...改就是了 , GET分分钟搞定...
虽然显得不够优雅.
但是我们的重点不是这一层啊...

做完后的样子...<br />
![github](https://github.com/xmagicj/HappyVolley/blob/master/happy_volley.png "happy_volley")  


## 具体代码
```
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

    private static class SingletonHolder {
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
        //**重点就是在这里new ExecutorDelivery(executorService),是不是很简单**
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir),
                network,
                DEFAULT_NETWORK_THREAD_POOL_SIZE,
                new ExecutorDelivery(executorService));
        queue.start();
        return queue;
    }

}

```

链接
-----------------------------------
1.[https://github.com/xmagicj/HappyVolley](https://github.com/xmagicj/HappyVolley)<br />
