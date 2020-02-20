package com.tencent.tga.liveplugin.live.right.schedule.bean

/**
 * Created by WeiLong on 2020/1/15.
 */
class TeamBean {
    var teamid:String?=""
    var team_name:String?=""
    var team_short_name:String?=""
    var team_logo:String?=""
    override fun toString(): String {
        return "TeamBean(teamid=$teamid, team_name=$team_name, team_short_name=$team_short_name, team_logo=$team_logo)"
    }
}