package com.tencent.tga.liveplugin.base.util;

import android.text.InputFilter;
import android.text.Spanned;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lionljwang on 2016/4/13.
 */
public class LengthFilter  implements InputFilter {
    private final int mMax;

    public LengthFilter(int max) {
        mMax = max;
    }

    /**
     * 过滤文本
     *
     * @param source 新增加的文本
     * @param start
     * @param end
     * @param dest   原文本
     * @param dstart
     * @param dend
     * @return 返回处理过的新文本，返回null表示不做任何处理
     */
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        // 为空表示没增加
        if (source == null) {
            return null;
        }

        // 打印下新增加文本的HEX
        //byte[] bytes = source.toString().getBytes();
        //String hex = Hex.bytesToHexes(bytes);

        // 总长度减现有的长度，得到当前新增加的文本需要保留的长度
        int keep = mMax - (dest.length() - (dend - dstart));

        //TLog.e("lenght",String.format("keep = %s mMax = %s dstart = %s getDestLength(dest) = %s dstart= %s ",keep,mMax,dstart,getDestLength(dest),dstart));

        if (keep <= 0) { // 为0表示，新增加的文本直接删除
            return "";
        } else if (keep >= end - start) { // 保留的长度比新增加文本的长度长，直接插入文本
            return null;
        } else if (isQQFace(source) || isEmoji(source)) {// 如果是QQ表情或者emoji表情， 直接加入
            return null;
        } else {                            // 截断多余的
            keep += start;
            if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                --keep;
                if (keep == start) {
                    return "";
                }
            }

            return source.subSequence(start, keep);
        }
    }

    public int getMax() {
        return mMax;
    }

    private static boolean isQQFace(CharSequence sequence) {
       // return FaceUtil.isQQFace(sequence);
        return false;
    }

    private static boolean isEmoji(CharSequence sequence) {
        if (sequence == null) {
            return false;
        }

        if (sequence.length() != 2) {
            return false;
        }

        Pattern pEmoji = getEmojiPattern();
        Matcher m = pEmoji.matcher(sequence);
        return m.find();
    }

    /**
     * 获取带表情文本的长度，一个表情的长度为1
     */
    private static int getDestLength(CharSequence sequence) {
        if (sequence == null) {
            return 0;
        }

        // 把QQ表情替换成单个字符
        Pattern pQQFace = Pattern.compile("\\[(.|..|...)\\]");
        Matcher m = pQQFace.matcher(sequence);
        String result = m.replaceAll("-");

        // 把emoji表情替换成单个字符
        Pattern pEmoji = getEmojiPattern();
        m = pEmoji.matcher(result);
        result = m.replaceAll("-");

        //TLog.e("lenght",String.format("resultt= %s ",result.length()));
        return result.length();
    }

    private static Pattern getEmojiPattern(){
        Pattern pEmoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        return pEmoji;
    }
}
