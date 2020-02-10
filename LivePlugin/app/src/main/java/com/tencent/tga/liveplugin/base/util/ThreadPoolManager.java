package com.tencent.tga.liveplugin.base.util;

import com.tencent.common.log.tga.TLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hyqiao on 2018/3/26.
 */

public class ThreadPoolManager {

    private final static String TAG = "ThreadPoolManager";
    private static ThreadPoolManager mThreadPoolManager;
    public synchronized static ThreadPoolManager getInstance(){
        if(mThreadPoolManager == null){
            mThreadPoolManager = new ThreadPoolManager();
        }
        return mThreadPoolManager;
    }

    private ExecutorService mExecutorService;
    public ThreadPoolManager() {
        mExecutorService = Executors.newFixedThreadPool(4);
    }

    public void executeRunnable(Runnable r){
        try {
            if(r == null)
                return;
            if(mExecutorService == null)
                return;
            mExecutorService.execute(r);
        }catch (Exception e){
            TLog.e(TAG,"executeRunnable error : "+e.getMessage());
        }

    }

    public void unInit(){
        try {
            if(mExecutorService != null){
                mExecutorService.shutdown();
            }
            mThreadPoolManager = null;
        }catch (Exception e){
            TLog.e(TAG,"unInit error : "+e.getMessage());
        }
    }
}
