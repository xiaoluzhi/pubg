package com.tencent.tga.liveplugin.base.util;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.imageloader.cache.disc.impl.UnlimitedDiskCache;
import com.tencent.tga.imageloader.cache.disc.naming.Md5FileNameGenerator;
import com.tencent.tga.imageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.tencent.tga.imageloader.core.DisplayImageOptions;
import com.tencent.tga.imageloader.core.ImageLoader;
import com.tencent.tga.imageloader.core.ImageLoaderConfiguration;
import com.tencent.tga.imageloader.core.assist.FailReason;
import com.tencent.tga.imageloader.core.assist.ImageSize;
import com.tencent.tga.imageloader.core.assist.QueueProcessingType;
import com.tencent.tga.imageloader.core.assist.ViewScaleType;
import com.tencent.tga.imageloader.core.download.BaseImageDownloader;
import com.tencent.tga.imageloader.core.imageaware.NonViewAware;
import com.tencent.tga.imageloader.core.listener.ImageLoadingListener;
import com.tencent.tga.imageloader.utils.StorageUtils;
import com.tencent.tga.liveplugin.base.LivePluginConstant;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.Random;

import master.flame.danmaku.tga.danmaku.model.BaseDanmaku;


/**
 * Created by lionljwang on 2016/8/5.
 */
public class ImageLoaderUitl {
    private static String TAG = "ImageLoaderUitl";
    private volatile static ImageLoaderUitl mInstance;

    private DisplayImageOptions options;

    public synchronized static ImageLoaderUitl getmInstance()
    {
        if (mInstance == null)
           mInstance = new ImageLoaderUitl();

        return mInstance;
    }

    public void init(Context context){
        try{
            //设置缓存的路径
            File cacheDir = StorageUtils.getOwnCacheDirectory(context, LivePluginConstant.IMAGELOADER_cache);
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
            builder.threadPoolSize(3)//线程池内加载的数量
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                    .memoryCacheSize(2 * 1024 * 1024)
                    .discCacheSize(50 * 1024 * 1024)
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .discCacheFileCount(100) //缓存的文件数量
                    .discCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
                    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                    .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)); // connectTimeout (5 s), readTimeout (30 s)超时时间
            if(Configs.Debug){
                builder.writeDebugLogs();
            }
            ImageLoaderConfiguration config = builder.build();
            if(ImageLoader.getInstance().isInited()){
                TLog.e(TAG,"ImageLoader has inited");
            }else{
                TLog.e(TAG,"ImageLoader is initing");
                ImageLoader.getInstance().init(config);
            }
            LiveViewEvent.Companion.imageLoaderInit();
        }catch (Exception e){
            TLog.e(TAG,"ImageLoader init failed : "+e.getMessage());
        }
    }
    /**
     * 比较常用的配置方案
     * @return
     */
    public DisplayImageOptions getDisplayConfig() {
        if (options ==null){
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .build();
        }
        return options;
    }
    private static Random mRandom = new Random();
    public static void loadRoundImageForImageView(String url, final ImageView iv){
        String imageuri = System.currentTimeMillis() + mRandom.nextLong() + "";
        NonViewAware non = new NonViewAware(imageuri, new ImageSize(500, 500), ViewScaleType.CROP);
        if(!ImageLoader.getInstance().isInited()){
            if (null != LiveConfig.mLiveContext) {
                ImageLoaderUitl.getmInstance().init(LiveConfig.mLiveContext);
            } else {
                return;
            }
        }
        try{
            ImageLoader.getInstance().displayImage(url, non, ImageLoaderUitl.getmInstance().getDisplayConfig(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap == null)
                    return;
                iv.setBackground(null);
                Bitmap bm = ImageTools.toRoundCorner(Bitmap.createScaledBitmap(bitmap, 50, 50, false), 50);
                iv.setImageBitmap(bm);
            }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }catch (Exception e){
            TLog.e(TAG,"loadImageForViewGroup ImageLoader exception");
        }

    }
    public static void loadimage(String url, final ImageView iv ,ImageLoadingListener imageLoadingListener){
        String imageuri = System.currentTimeMillis() + mRandom.nextLong() + "";
        NonViewAware non = new NonViewAware(imageuri, new ImageSize(500, 500), ViewScaleType.FIT_INSIDE);
        if(!ImageLoader.getInstance().isInited()){
            if (null != LiveConfig.mLiveContext) {
                ImageLoaderUitl.getmInstance().init(LiveConfig.mLiveContext);
            } else {
                return;
            }
        }
        try{
            ImageLoader.getInstance().displayImage(url, non, ImageLoaderUitl.getmInstance().getDisplayConfig(), imageLoadingListener);
        }catch (Exception e){
            TLog.e(TAG,"loadimage ImageLoader exception");
        }
    }

    public static void loadimage(final BaseDanmaku danmaku, final String url, ImageLoadingListener imageLoadingListener){
        String imageuri = System.currentTimeMillis() + mRandom.nextLong() + "";
        NonViewAware non = new NonViewAware(imageuri, new ImageSize(500, 500), ViewScaleType.FIT_INSIDE);
        if(!ImageLoader.getInstance().isInited()){
            if (null != LiveConfig.mLiveContext) {
                ImageLoaderUitl.getmInstance().init(LiveConfig.mLiveContext);
            } else {
                return;
            }
        }
        try{
            ImageLoader.getInstance().displayImage(url, non, ImageLoaderUitl.getmInstance().getDisplayConfig(), imageLoadingListener);
        }catch (Exception e){
            TLog.e(TAG,"loadimage ImageLoader exception");
        }
    }

    public static void loadimage(final String url,ImageLoadingListener imageLoadingListener){
        String imageuri = System.currentTimeMillis() + mRandom.nextLong() + "";
        NonViewAware non = new NonViewAware(imageuri, new ImageSize(500, 500), ViewScaleType.FIT_INSIDE);
        if(!ImageLoader.getInstance().isInited()){
              if (null != LiveConfig.mLiveContext) {
                    ImageLoaderUitl.getmInstance().init(LiveConfig.mLiveContext);
                } else {
                    return;
                }
        }
        try{
            ImageLoader.getInstance().displayImage(url, non, ImageLoaderUitl.getmInstance().getDisplayConfig(), imageLoadingListener);
        }catch (Exception e){
            TLog.e(TAG,"loadimage ImageLoader exception");
        }
    }


    public static void loadLargeimage(String url, final ImageView iv ,ImageLoadingListener imageLoadingListener){
        String imageuri = System.currentTimeMillis() + mRandom.nextLong() + "";
        NonViewAware non = new NonViewAware(imageuri, new ImageSize(4000, 4000), ViewScaleType.FIT_INSIDE);
        if(!ImageLoader.getInstance().isInited()){
              if (null != LiveConfig.mLiveContext) {
                    ImageLoaderUitl.getmInstance().init(LiveConfig.mLiveContext);
                } else {
                    return;
                }
        }
        try{
            ImageLoader.getInstance().displayImage(url, non, ImageLoaderUitl.getmInstance().getDisplayConfig(), imageLoadingListener);
        }catch (Exception e){
            TLog.e(TAG,"loadimage ImageLoader exception");
        }
    }

    public static void loadimage(String url, final ImageView iv){
        loadimage(url,iv,new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap == null)
                    return;
                iv.setImageBitmap(bitmap);
                if (Configs.Debug)
                    TLog.e(TAG,"wide height :  "+bitmap.getWidth()+"  "+bitmap.getHeight());
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
        }
        });
    }

    public static void loadimageNoRepetate(String url, final ImageView iv){
        iv.setTag(url);
        loadimage(url,iv,new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap == null)
                    return;
                if(iv.getTag().toString().equals(s)){
                    iv.setImageBitmap(bitmap);
                }
                if (Configs.Debug)
                    TLog.e(TAG,"wide height :  "+bitmap.getWidth()+"  "+bitmap.getHeight());
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
    }

    public static void loadImageForViewGroup(String url, final ViewGroup viewGroup){
        //主要针对一个页面中同一链接的图片展示两次
        if(!ImageLoader.getInstance().isInited()){
            if (null != LiveConfig.mLiveContext) {
                ImageLoaderUitl.getmInstance().init(LiveConfig.mLiveContext);
            } else {
                return;
            }
        }
        try{
            ImageLoader.getInstance().loadImage(url, ImageLoaderUitl.getmInstance().getDisplayConfig(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap == null)
                        return;
                    viewGroup.setBackground(new BitmapDrawable(bitmap));
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }catch (Exception e){
            TLog.e(TAG,"loadImageForViewGroup ImageLoader exception");
        }

    }


    public static void loadImageForImageView(String url, final ImageView imageView){
        String imageuri = System.currentTimeMillis() + mRandom.nextLong() + "";
        NonViewAware non = new NonViewAware(imageuri, new ImageSize(500, 500), ViewScaleType.CROP);
        if(!ImageLoader.getInstance().isInited()){
            if (null != LiveConfig.mLiveContext) {
                ImageLoaderUitl.getmInstance().init(LiveConfig.mLiveContext);
            } else {
                return;
            }
        }
        try{
            ImageLoader.getInstance().displayImage(url, non, ImageLoaderUitl.getmInstance().getDisplayConfig(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap == null)
                        return;
                    imageView.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }catch (Exception e){
            TLog.e(TAG,"loadImageForViewGroup ImageLoader exception");
        }
    }

    public static void loadImageFromNet(String url,final ImageLoadSuccess imageLoadSuccess){
        try{
            String imageuri = System.currentTimeMillis() + mRandom.nextLong() + "";
            NonViewAware non = new NonViewAware(imageuri, new ImageSize(500, 500), ViewScaleType.CROP);
            if(!ImageLoader.getInstance().isInited()){
                if (null != LiveConfig.mLiveContext) {
                    ImageLoaderUitl.getmInstance().init(LiveConfig.mLiveContext);
                } else {
                    return;
                }
            }
            ImageLoader.getInstance().displayImage(url, non, ImageLoaderUitl.getmInstance().getDisplayConfig(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap == null)
                        return;
                    if (imageLoadSuccess != null){
                        imageLoadSuccess.loadSucc();
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }catch (Exception e){
            TLog.e(TAG,"loadImageFromNet ImageLoader exception");
        }
    }

    public interface ImageLoadSuccess{
        void loadSucc();
    }

    public void unInit(){
        try {
            ImageLoader.getInstance().destroy();
            mInstance = null;
        }catch (Exception e){
            TLog.e(TAG,"ImageLoaderUitl unInit error : "+e.getMessage());
        }
    }
}
