package com.tencent.tga.liveplugin.live.right.schedule.bean;

import com.tencent.protocol.tga.ppkdc_schedule.MatchItem;
import com.tencent.tga.liveplugin.networkutil.PBDataUtils;

import java.util.List;

public class MatchDayInfoBean {


    /**
     * result : 0
     * match_day_list : [{"match_day_time":1560960000,"match_list":[{"guest_team_name":"小瑶队","host_team_id":"KPL2019S1_zyry",
     * "subscribe_state":0,"host_team_logo":"https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20190424/07e4450733dc638474e2bd09a89d937c.png",
     * "match_id":"KPL2019S1M17W7D1","guest_team_id":"KPL2019S1_xyd","record_vid_list":[],"stage":null,"guest_team_short":null,"region":"","match_state":4,
     * "host_team_short":null,"host_team_score":0,"guest_team_score":0,"match_time":"1561028400",
     * "guest_team_logo":"https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20190412/8f330d8a6f98be860edb9799c3682e08.png"},{"guest_team_name":"xq",
     * "host_team_id":"KPL2019S1_edg.m","subscribe_state":0,"host_team_logo":"https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20180828/4a2460144e23aaf58bf5efba784dc1bd.png",
     * "match_id":"KPL2019S1M12W2D1","guest_team_id":"KPL2019S1_xq","record_vid_list":["s0537uy3ihk","s0537uy3ihk"],"stage":null,"guest_team_short":"xq","region":"",
     * "match_state":1,"host_team_short":null,"host_team_score":0,"guest_team_score":0,"match_time":"1561023540",
     * "guest_team_logo":"https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20180828/2b15830d881b27c9bd60ac80999f0684.png"},
     * {"guest_team_name":"xq","host_team_id":"KPL2019S1_edg.m","subscribe_state":0,
     * "host_team_logo":"https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20180828/4a2460144e23aaf58bf5efba784dc1bd.png",
     * "match_id":"KPL2019S1M12W2D1","guest_team_id":"KPL2019S1_xq","record_vid_list":["s0537uy3ihk","s0537uy3ihk"],"stage":null,
     * "guest_team_short":"xq","region":"","match_state":1,"host_team_short":"edgm","host_team_score":0,"guest_team_score":0,"match_time":"1561023540",
     * "guest_team_logo":"https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20180828/2b15830d881b27c9bd60ac80999f0684.png"}]}]
     */

    private int result;
    private List<MatchDayListBean> match_day_list;
    private String msg;
    private String is_finish;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIs_finish() {
        return is_finish;
    }

    public void setIs_finish(String is_finish) {
        this.is_finish = is_finish;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<MatchDayListBean> getMatch_day_list() {
        return match_day_list;
    }

    public void setMatch_day_list(List<MatchDayListBean> match_day_list) {
        this.match_day_list = match_day_list;
    }

    public static class MatchDayListBean {
        /**
         * match_day_time : 1560960000
         * match_list : [{"guest_team_name":"小瑶队","host_team_id":"KPL2019S1_zyry","subscribe_state":0,
         * "host_team_logo":"https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20190424/07e4450733dc638474e2bd09a89d937c.png",
         * "match_id":"KPL2019S1M17W7D1","guest_team_id":"KPL2019S1_xyd","record_vid_list":[],"stage":null,"guest_team_short":null,"region":"",
         * "match_state":4,"host_team_short":null,"host_team_score":0,"guest_team_score":0,"match_time":"1561028400",
         * "guest_team_logo":"https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20190412/8f330d8a6f98be860edb9799c3682e08.png"},
         * {"guest_team_name":"xq","host_team_id":"KPL2019S1_edg.m","subscribe_state":0,
         * "host_team_logo":"https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20180828/4a2460144e23aaf58bf5efba784dc1bd.png",
         * "match_id":"KPL2019S1M12W2D1","guest_team_id":"KPL2019S1_xq","record_vid_list":["s0537uy3ihk","s0537uy3ihk"],"stage":null,
         * "guest_team_short":"xq","region":"","match_state":1,"host_team_short":null,"host_team_score":0,"guest_team_score":0,
         * "match_time":"1561023540","guest_team_logo":"https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20180828/2b15830d881b27c9bd60ac80999f0684.png"},
         * {"guest_team_name":"xq","host_team_id":"KPL2019S1_edg.m","subscribe_state":0,
         * "host_team_logo":"https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20180828/4a2460144e23aaf58bf5efba784dc1bd.png",
         * "match_id":"KPL2019S1M12W2D1","guest_team_id":"KPL2019S1_xq","record_vid_list":["s0537uy3ihk","s0537uy3ihk"],"stage":null,
         * "guest_team_short":"xq","region":"","match_state":1,"host_team_short":"edgm","host_team_score":0,"guest_team_score":0,"match_time":"1561023540",
         * "guest_team_logo":"https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20180828/2b15830d881b27c9bd60ac80999f0684.png"}]
         */

        private int match_day_time;
        private List<MatchListBean> match_list;

        public int getMatch_day_time() {
            return match_day_time;
        }

        public void setMatch_day_time(int match_day_time) {
            this.match_day_time = match_day_time;
        }

        public List<MatchListBean> getMatch_list() {
            return match_list;
        }

        public void setMatch_list(List<MatchListBean> match_list) {
            this.match_list = match_list;
        }

        public static class MatchListBean {
            /**
             * guest_team_name : 小瑶队
             * host_team_id : KPL2019S1_zyry
             * subscribe_state : 0
             * host_team_logo : https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20190424/07e4450733dc638474e2bd09a89d937c.png
             * match_id : KPL2019S1M17W7D1
             * guest_team_id : KPL2019S1_xyd
             * record_vid_list : []
             * stage : null
             * guest_team_short : null
             * region :
             * match_state : 4
             * host_team_short : null
             * host_team_score : 0
             * guest_team_score : 0
             * match_time : 1561028400
             * guest_team_logo : https://imgcache-1251786003.image.myqcloud.com/media/gzhoss/image/20190412/8f330d8a6f98be860edb9799c3682e08.png
             */

            private String guest_team_name;
            private String host_team_name;
            private String host_team_id;
            private int subscribe_state;//订阅状态， 0 没订阅 1 已订阅
            private String host_team_logo;
            private String match_id;
            private String guest_team_id;
            private String stage;//赛事类型： yxs=预选赛, cgs=常规赛,jhs=季后赛,bys=表演赛,tts=淘汰赛
            private String guest_team_short;
            private String region;
            private int match_state;//1: 赛事未开始, 2:赛事已取消, 3 : 赛事进行中, 4:赛事已结束
            private String host_team_short;
            private int host_team_score;
            private int guest_team_score;
            private String match_time;
            private String guest_team_logo;
            private List<String> record_vid_list;//排好序的回放vid列表， 最多7个
            private String has_cheer;
            private String card_url;//卡片url, 非卡片不回
            private String card_title;//卡片标题, 非卡片不回
            private String match_sub_title;//副标题， 每天的日期后面展示第一场比赛的副标题,回放视频标题里也会展示
            private String match_main_title;//主标题， 回放视频标题里会展示

            public String getMatch_main_title() {
                return match_main_title;
            }

            public void setMatch_main_title(String match_main_title) {
                this.match_main_title = match_main_title;
            }

            public String getMatch_sub_title() {
                return match_sub_title;
            }

            public void setMatch_sub_title(String match_sub_title) {
                this.match_sub_title = match_sub_title;
            }

            public MatchListBean(String guest_team_name, String host_team_name, String host_team_id, int subscribe_state, String host_team_logo, String match_id,
                                 String guest_team_id, String stage, String guest_team_short, String region, int match_state, String host_team_short,
                                 int host_team_score, int guest_team_score, String match_time, String guest_team_logo, List<String> record_vid_list,
                                 String has_cheer, String card_url, String card_title, String match_sub_title,String match_main_title){
                this.guest_team_name = guest_team_name;
                this.host_team_name = host_team_name;
                this.host_team_id = host_team_id;
                this.subscribe_state = subscribe_state;
                this.host_team_logo = host_team_logo;
                this.match_id = match_id;
                this.guest_team_id = guest_team_id;
                this.stage = stage;
                this.guest_team_short = guest_team_short;
                this.region = region;
                this.match_state = match_state;
                this.host_team_short = host_team_short;
                this.host_team_score = host_team_score;
                this.guest_team_score = guest_team_score;
                this.match_time = match_time;
                this.guest_team_logo = guest_team_logo;
                this.record_vid_list = record_vid_list;
                this.has_cheer = has_cheer;
                this.card_url =  card_url;
                this.card_title = card_title;
                this.match_sub_title = match_sub_title;
                this.match_main_title = match_main_title;

            }
            public String getGuest_team_name() {
                return guest_team_name;
            }

            public void setGuest_team_name(String guest_team_name) {
                this.guest_team_name = guest_team_name;
            }

            public String getHost_team_id() {
                return host_team_id;
            }

            public void setHost_team_id(String host_team_id) {
                this.host_team_id = host_team_id;
            }

            public int getSubscribe_state() {
                return subscribe_state;
            }

            public void setSubscribe_state(int subscribe_state) {
                this.subscribe_state = subscribe_state;
            }

            public String getHost_team_logo() {
                return host_team_logo;
            }

            public void setHost_team_logo(String host_team_logo) {
                this.host_team_logo = host_team_logo;
            }

            public String getMatch_id() {
                return match_id;
            }

            public void setMatch_id(String match_id) {
                this.match_id = match_id;
            }

            public String getGuest_team_id() {
                return guest_team_id;
            }

            public void setGuest_team_id(String guest_team_id) {
                this.guest_team_id = guest_team_id;
            }

            public String getStage() {
                return stage;
            }

            public void setStage(String stage) {
                this.stage = stage;
            }

            public String getGuest_team_short() {
                return guest_team_short;
            }

            public void setGuest_team_short(String guest_team_short) {
                this.guest_team_short = guest_team_short;
            }

            public String getRegion() {
                return region;
            }

            public void setRegion(String region) {
                this.region = region;
            }

            public int getMatch_state() {
                return match_state;
            }

            public void setMatch_state(int match_state) {
                this.match_state = match_state;
            }

            public String getHost_team_short() {
                return host_team_short;
            }

            public void setHost_team_short(String host_team_short) {
                this.host_team_short = host_team_short;
            }

            public int getHost_team_score() {
                return host_team_score;
            }

            public void setHost_team_score(int host_team_score) {
                this.host_team_score = host_team_score;
            }

            public int getGuest_team_score() {
                return guest_team_score;
            }

            public void setGuest_team_score(int guest_team_score) {
                this.guest_team_score = guest_team_score;
            }

            public String getMatch_time() {
                return match_time;
            }

            public void setMatch_time(String match_time) {
                this.match_time = match_time;
            }

            public String getGuest_team_logo() {
                return guest_team_logo;
            }

            public void setGuest_team_logo(String guest_team_logo) {
                this.guest_team_logo = guest_team_logo;
            }

            public List<String> getRecord_vid_list() {
                return record_vid_list;
            }

            public void setRecord_vid_list(List<String> record_vid_list) {
                this.record_vid_list = record_vid_list;
            }

            public String getHost_team_name() {
                return host_team_name;
            }

            public void setHost_team_name(String host_team_name) {
                this.host_team_name = host_team_name;
            }

            public String getHas_cheer() {
                return has_cheer;
            }

            public void setHas_cheer(String has_cheer) {
                this.has_cheer = has_cheer;
            }

            public String getCard_url() {
                return card_url;
            }

            public void setCard_url(String card_url) {
                this.card_url = card_url;
            }

            public String getCard_title() {
                return card_title;
            }

            public void setCard_title(String card_title) {
                this.card_title = card_title;
            }
        }
    }
}
