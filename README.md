# HappyVolley
在非UI线程返回Response且可以配置并发线程数的Volley(未改动源码)

## 前言
我知道...我懂
大家现在都用 OKHttp 或者 Retrofit
为啥这里还要发布和二次封装Volley呢?

原因有三
- 以往的成果还是做个记录.
- Volley在没有上传下载等类似Task的情况下 还是有生存空间的.
- 哈哈~秘密...这个以后再谈...

## 解决问题
1. Volley默认是在UI Thread返回Response,导致如果还需要对数据进行操作(比如DB,筛选,排序等相对耗时任务),则需要另起Task(非UI Thread)去做.这样就存在新的同步问题,甚至踏入callback深渊.
**解决**
通过传入ExecutorService来让其成为单线程模型,Response依然在非UI Thread返回.这样数据层可以继续对其进行操作.
当结果返回到业务层的时候 再通知GUI更新即可(Handler EventBus等随意了).
2. Volley默认采用4个线程作为线程池控制,无法修改.
**我们通过Volley构造方法**
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
去创建一个新的RequestQueue对象即可解决这个问题.


