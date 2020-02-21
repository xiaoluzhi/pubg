package com.tencent.tga.liveplugin.live.right.schedule.bean;


import java.util.List;

public class MatchDayInfoBean {

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

            private int subscribe_state;//订阅状态， 0 没订阅 1 已订阅
            private String match_id;
            private String stage;//赛事类型： yxs=预选赛, cgs=常规赛,jhs=季后赛,bys=表演赛,tts=淘汰赛
            private int match_state;//1: 赛事未开始, 2:赛事已取消, 3 : 赛事进行中, 4:赛事已结束
            private String match_time;
            private List<String> record_vid_list;//排好序的回放vid列表， 最多7个
            private String has_cheer;//是否配置助威
            private String match_sub_title;//副标题， 每天的日期后面展示第一场比赛的副标题,回放视频标题里也会展示
            private String match_main_title;//主标题， 回放视频标题里会展示
            private String roomid;//房间id

            public String getRoomid() {
                return roomid;
            }

            public void setRoomid(String roomid) {
                this.roomid = roomid;
            }

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

            public MatchListBean(int subscribe_state, String match_id, String stage,  int match_state,
                                 String match_time, List<String> record_vid_list,
                                 String has_cheer,  String match_sub_title,String match_main_title,String roomid){

                this.subscribe_state = subscribe_state;
                this.match_id = match_id;
                this.stage = stage;
                this.match_state = match_state;
                this.match_time = match_time;
                this.record_vid_list = record_vid_list;
                this.has_cheer = has_cheer;
                this.match_sub_title = match_sub_title;
                this.match_main_title = match_main_title;
                this.roomid = roomid;

            }

            public int getSubscribe_state() {
                return subscribe_state;
            }

            public void setSubscribe_state(int subscribe_state) {
                this.subscribe_state = subscribe_state;
            }


            public String getMatch_id() {
                return match_id;
            }

            public void setMatch_id(String match_id) {
                this.match_id = match_id;
            }


            public String getStage() {
                return stage;
            }

            public void setStage(String stage) {
                this.stage = stage;
            }

            public int getMatch_state() {
                return match_state;
            }

            public void setMatch_state(int match_state) {
                this.match_state = match_state;
            }


            public String getMatch_time() {
                return match_time;
            }

            public void setMatch_time(String match_time) {
                this.match_time = match_time;
            }


            public List<String> getRecord_vid_list() {
                return record_vid_list;
            }

            public void setRecord_vid_list(List<String> record_vid_list) {
                this.record_vid_list = record_vid_list;
            }

            public String getHas_cheer() {
                return has_cheer;
            }

            public void setHas_cheer(String has_cheer) {
                this.has_cheer = has_cheer;
            }

        }
    }
}
