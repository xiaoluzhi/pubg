package com.tencent.tga.liveplugin.live.right.schedule.bean;

import com.squareup.tga.Message;
import com.tencent.protocol.tga.ppkdc_schedule.MatchItem;

import java.util.List;

import okiotga.ByteString;

/**
 * Created by hyqiao on 2017/4/6.
 */

public class RaceInfoBean extends Message {
    public Integer source_id;
    public ByteString race_name;
    public List<MatchItem> per_match_list;

    /*public RaceInfoBean(RaceInfo raceInfo) {
        this.source_id = raceInfo.source_id;
        this.race_name = raceInfo.race_name;
        this.per_match_list = Message.copyOf(raceInfo.per_match_list);
    }*/
}
