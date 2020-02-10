package com.tencent.tga.liveplugin.base.util;

import com.loopj.android.tgahttp.Configs.Configs;

public class HttpUtil {
    /**
    * 获取对应请求地址
    * @author hyqiao
    * @time 2018/8/17 16:18
    */
    public static String getHttpUrl(String req_keyword){
        String url = "";
        if(Configs.isUseTestIP){
            url  = "http://" + Configs.test_ip + "/app/hpjy/"+req_keyword;
        }else {
            url  = "https://" + Configs.domain_http + "/app/hpjy/"+req_keyword;
        }

        return url;
    }

    public static String getCommonHttpUrl(String req_keyword){
        String url = "";
        if(Configs.isUseTestIP){
            url  = "http://" + Configs.test_ip + "/app/platform/"+req_keyword;
        }else {
            url  = "https://" + Configs.domain_http + "/app/platform/"+req_keyword;
        }

        return url;
    }
}
