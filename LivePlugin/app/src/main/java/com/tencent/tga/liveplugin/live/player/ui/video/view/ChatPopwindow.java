package com.tencent.tga.liveplugin.live.player.ui.video.view;

import com.ryg.dynamicload.internal.DLPluginLayoutInflater;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.base.util.LengthFilter;
import com.tencent.tga.liveplugin.base.util.ToastUtil;
import com.tencent.tga.liveplugin.live.LiveConfig;
import com.tencent.tga.liveplugin.live.common.broadcast.LiveEvent;
import com.tencent.tga.liveplugin.live.common.util.SoftUitl;
import com.tencent.tga.liveplugin.live.common.util.TextUitl;
import com.tencent.tga.liveplugin.live.common.views.ResizeLinerlayout;
import com.tencent.tga.liveplugin.live.liveView.event.LiveViewEvent;
import com.tencent.tga.plugin.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupWindow;

import java.text.MessageFormat;

/**
 * Created by lionljwang on 2016/8/2.
 */
public class ChatPopwindow {

    /***
     * 聊天输入类型，0为直播，1为点播
     */
    public int type = 0;

    public static final int INPUT_TYPE_LIVE_CHAT = 0;
    public static final int INPUT_TYPE_VIDEO_COMMENT = 1;

    /***
     * 最长聊天长度
     */
    public static int MAX_LEN=20;

    private Context mActivity;

    private View view;
    public PopupWindow popWindow;

    public EditText editText;

    private View mEmptyView;

    private Handler mHandler;

    public ChatPopwindow(Context activity,int maxLen){
        mActivity = activity;
        MAX_LEN = maxLen;
        view = DLPluginLayoutInflater.getInstance(activity).inflate(R.layout.popwindow_chat_window, null);
        popWindow = new PopupWindow(view, WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,false);
        editText =  view.findViewById(R.id.edit_text);
        editText.setHintTextColor(0xff8A7D6B);
        editText.setFilters(new InputFilter[]{new LengthFilter(MAX_LEN)});
        editText.setTypeface(LiveConfig.mFont);
        mEmptyView = view.findViewById(R.id.empty_view);

        popWindow.setFocusable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mEmptyView.setOnClickListener(v -> onDismiss(false));

        ResizeLinerlayout resizeLinerlayout =  view.findViewById(R.id.pop_chat_tainer);

        if (null == mHandler) mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(null !=  resizeLinerlayout) resizeLinerlayout.setOnResizeRelativeListener((w, h, oldw, oldh) -> onDismiss(false));
            }
        }, 500);

        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                onDismiss(true);
            }
            return false;
        });
    }

    public void setHitText(int id){
        if (editText!=null)
        {
            editText.setHint(id);
        }
    }

    public void onDismiss(boolean isSend){
        String string = TextUitl.delSpace(editText.getText().toString().trim());
        TLog.d(MessageFormat.format("mEditText setOnEditorActionListener...{0}", string));
        if (!TextUtils.isEmpty(string)) {
            switch (type)
            {
                case INPUT_TYPE_LIVE_CHAT:
                    LiveViewEvent.Companion.sendMsg(new LiveEvent.SendMsg(string,isSend, false, 0));
                    break;
                case INPUT_TYPE_VIDEO_COMMENT:
                    LiveViewEvent.Companion.sendVideoDanmu(string);
                    break;
                default:
                    break;
            }
        }else {
            if(isSend){
                ToastUtil.show(mActivity,"聊天内容不能为空");
            }
        }
        editText.clearFocus();
        editText.setText("");
        popWindow.dismiss();
    }

    public void show(View mBottom){
        popWindow.showAtLocation(mBottom, Gravity.BOTTOM, 0, -500);
        view.postDelayed(() -> SoftUitl.showSoftKeyBroad(mActivity, editText), 100);
    }
    public void show(View mBottom,String edit){
        if (popWindow.isShowing())
            return;
       show(mBottom);
        if (!TextUtils.isEmpty(edit))
        {
            editText.setText(edit);
            int len =edit.length();
            len = len>=MAX_LEN?MAX_LEN-1:len;
            editText.setSelection(len);
        }
    }
}
