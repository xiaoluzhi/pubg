package com.tencent.tga.liveplugin.live.right;

import com.loopj.android.tgahttp.Configs.Configs;
import com.ryg.dynamicload.internal.DLPluginManager;
import com.ryg.utils.LOG;
import com.tencent.protocol.tga.expressmsg.BusinessType;
import com.tencent.tga.imageloader.core.assist.FailReason;
import com.tencent.tga.imageloader.core.display.CircleBitmapDisplayer;
import com.tencent.tga.imageloader.core.listener.ImageLoadingListener;
import com.tencent.tga.liveplugin.base.util.DeviceUtils;
import com.tencent.tga.liveplugin.base.util.HexStringUtil;
import com.tencent.tga.liveplugin.base.util.ImageLoaderUitl;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.util.BitMapUitl;
import com.tencent.tga.liveplugin.live.common.views.CenterImageSpan;
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent;
import com.tencent.tga.liveplugin.live.right.chat.bean.ChatMsgBean;
import com.tencent.tga.liveplugin.live.right.chat.bean.ChatMsgEntity;
import com.tencent.tga.plugin.R;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

/**
 * Created by lionljwang on 2017/9/11.
 */
public class ChatUitl {

    public static ChatUitl mInstance = new ChatUitl();

    private long time = 0;
    private long difTime = 0;
    private ChatMsgBean chatMsgBean;

    private CenterImageSpan mManager,mRoomManager,mMSmall,mWSmall;

    private int size ;

    private int size2;

    CenterImageSpan centerImageSpan = null;
    public ChatMsgBean getMsg(final ChatMsgEntity entity) {
        return getMsg(entity, true);
    }

    public ChatMsgBean procNorMsg(final ChatMsgEntity entity) {
       String content = String.format("   %s：%s", entity.name, entity.text.trim());
        if (centerImageSpan == null) {
            content = content.trim();
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
        ForegroundColorSpan span = new ForegroundColorSpan(0xFFB9A560);
        spannableStringBuilder.setSpan(span, 0, entity.name.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        try {
            if (centerImageSpan != null) {
                spannableStringBuilder.setSpan(centerImageSpan, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(span, 0, entity.name.trim().length() + 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            } else {
                spannableStringBuilder.setSpan(span, 0, entity.name.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        chatMsgBean.text = spannableStringBuilder;
        chatMsgBean.textColor = Color.WHITE;


        return chatMsgBean;
    }

    public ChatMsgBean procSelMsg(final ChatMsgEntity entity){
        String content = String.format("   %s：%s", entity.name, entity.text.trim());
        if (centerImageSpan == null) {
            content = content.trim();
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
        ForegroundColorSpan span = new ForegroundColorSpan(0xFFFA9B0A);
        try {
            if (centerImageSpan != null && spannableStringBuilder.length()>entity.name.length() + 4) {
                spannableStringBuilder.setSpan(centerImageSpan, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(span, 0, entity.name.trim().length() + 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            } else {
                spannableStringBuilder.setSpan(span, 0, entity.name.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        chatMsgBean.text = spannableStringBuilder;
        chatMsgBean.textColor = 0xffffffff;

        return chatMsgBean;
    }

    public synchronized ChatMsgBean getMsg(final ChatMsgEntity entity, boolean isFilter) {
        if (LiveConfig.mLiveContext == null)
            return null;

        chatMsgBean = new ChatMsgBean();
        centerImageSpan = null;
        try {
            if (size == 0)
            {
                size = DeviceUtils.dip2px(LiveConfig.mLiveContext, 12);
                size2 = DeviceUtils.dip2px(LiveConfig.mLiveContext, 18);
            }

            final String content;
            if (entity.msgType == BusinessType.BUSINESS_TYPE_ROOM_CHAT_NOTIFY.getValue()) {//普通聊天消息
                if (entity.isSel) {
                    procSelMsg(entity);
                } else if (!TextUtils.isEmpty(entity.name) && !TextUtils.isEmpty(entity.text)) {
                    if (isFilter) {
                        difTime = System.currentTimeMillis() - time;
                        if (difTime < 100) {
                            return null;
                        }
                        time = System.currentTimeMillis();
                    }

                    procNorMsg(entity);

                }
            } else if (entity.msgType == BusinessType.BUSINESS_TYPE_ROOM_OPERATION_MSG.getValue()) {//运营消息
                if (entity.subType == 4) {//系统消息
                    content = String.format("   %s", entity.text.trim());
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
                    spannableStringBuilder.setSpan(getmManager(), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    chatMsgBean.text = spannableStringBuilder;
                    chatMsgBean.textColor = 0xFFFA9B0A;
                } else if (entity.subType == 1) {//房管消息
                    content =String.format("   房管：%s",entity.text.trim());
//                    content = String.format("   %s: %s", entity.name.trim(), entity.text.trim());
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
                    spannableStringBuilder.setSpan(getmRoomManager(), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    chatMsgBean.text = spannableStringBuilder;
                    chatMsgBean.textColor = 0xFFFA9B0A;
                } else if (entity.subType == 2||entity.subType == 5) {//2 男解说消息 5 女解说消息
                    content = String.format("   %s: %s", entity.name.trim(), entity.text.trim());
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);

                    if (entity.subType == 2)//男解说
                        spannableStringBuilder.setSpan(getmMSmall(), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    else
                        spannableStringBuilder.setSpan(getmWSmall(), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    chatMsgBean.text = spannableStringBuilder;

                    if (entity.subType == 2)//男解说
                        chatMsgBean.textColor = 0xFF1ce2ff;
                    else
                        chatMsgBean.textColor = 0xFFff496b;
                } else if (entity.subType == 3||entity.subType == 6){ //3男嘉宾消息 6女嘉宾消息
                    LOG.e(ChatUitl.class.getSimpleName(),"entity.subType"+entity.subType+" entity.showType "+entity.showType);
                    if (!TextUtils.isEmpty(entity.nickUrl)) {
                        ImageLoaderUitl.loadimage(entity.nickUrl, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {
                            }
                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                String content = String.format("   %s: %s", entity.name.trim(), entity.text.trim());
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
                                CircleBitmapDisplayer.CircleDrawable drawable = new CircleBitmapDisplayer.CircleDrawable(bitmap,0x000000,2);
                                drawable.setBounds(0, 0, size2, size2);
                                CenterImageSpan span = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
                                spannableStringBuilder.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                                chatMsgBean.text = spannableStringBuilder;
                                if (entity.subType == 3)
                                    chatMsgBean.textColor = 0xFF1ce2ff;
                                else
                                    chatMsgBean.textColor = 0xFFff496b;
                                if (Configs.Debug)
                                    LOG.e(ChatUitl.class.getSimpleName(),"entity.subType"+entity.subType+" entity.showType "+entity.showType);
                                LiveViewEvent.Companion.addChatMsg(chatMsgBean);
                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {
                            }
                        });
                    }
                    return null;
                }else if(entity.subType == 7){
                    if (!TextUtils.isEmpty(entity.nickUrl)) {
                        ImageLoaderUitl.loadimage(entity.nickUrl, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {
                            }
                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                String content = String.format("   %s: %s", entity.name.trim(), entity.text.trim());
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
                                CircleBitmapDisplayer.CircleDrawable drawable = new CircleBitmapDisplayer.CircleDrawable(bitmap,0x000000,2);
                                drawable.setBounds(0, 0, size2, size2);

                                CenterImageSpan span = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
                                spannableStringBuilder.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                                chatMsgBean.text = spannableStringBuilder;
                                chatMsgBean.textColor = HexStringUtil.hexString2colorInt(entity.color);
                                LiveViewEvent.Companion.addChatMsg(chatMsgBean);
                                if (Configs.Debug)
                                    LOG.e(ChatUitl.class.getSimpleName(),"entity.subType"+entity.subType+" entity.showType "+entity.showType);
                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {
                            }
                        });
                    }
                    return null;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return chatMsgBean;
    }

    public CenterImageSpan getmManager() {
        if (mManager == null) {
            Drawable drawable = BitMapUitl.zoomDrawable(DLPluginManager.getInstance(LiveConfig.mLiveContext).mPluginContext.getResources().getDrawable(R.drawable.friend_fight_notify), size, size);
            drawable.setBounds(0, 0, size, size);
            mManager = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        }
        return mManager;
    }


    public CenterImageSpan getmRoomManager() {
        if (mRoomManager == null) {
            Drawable drawable = BitMapUitl.zoomDrawable(DLPluginManager.getInstance(LiveConfig.mLiveContext).mPluginContext.getResources().getDrawable(R.drawable.chat_room_manger_icon), size, size);
            drawable.setBounds(0, 0, size, size);
            mRoomManager = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        }
        return mRoomManager;
    }

    public CenterImageSpan getmMSmall() {
        if (mMSmall == null) {
            Drawable drawable = BitMapUitl.zoomDrawable(DLPluginManager.getInstance(LiveConfig.mLiveContext).mPluginContext.getResources().getDrawable(R.drawable.chat_small_m_gloze_icon), size, size);
            drawable.setBounds(0, 0, size, size);
            mMSmall = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        }
        return mMSmall;
    }

    public CenterImageSpan getmWSmall() {
        if (mWSmall == null) {
            Drawable drawable = BitMapUitl.zoomDrawable(DLPluginManager.getInstance(LiveConfig.mLiveContext).mPluginContext.getResources().getDrawable(R.drawable.chat_small_w_gloze_icon), size, size);
            drawable.setBounds(0, 0, size, size);
            mWSmall = new CenterImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        }
        return mWSmall;
    }

}
