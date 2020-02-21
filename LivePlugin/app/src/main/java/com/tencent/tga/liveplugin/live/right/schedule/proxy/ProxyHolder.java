package com.tencent.tga.liveplugin.live.right.schedule.proxy;

/**
 * Created by agneswang on 2017/1/10.
 */
public class ProxyHolder {

    public ProxyHolder()
    {
        currentMatchProxy= new CurrentMatchProxy();
        currentMatchProxyParma = new CurrentMatchProxy.Param();

        getRoomListProxy = new GetRoomListProxy();
        getRoomListProxyParam = new GetRoomListProxy.Param();

        matchSubscribeProxy = new MatchSubscribeProxy();
        matchSubscribeProxyParam = new MatchSubscribeProxy.Param();
    }
    public CurrentMatchProxy currentMatchProxy;
    public CurrentMatchProxy.Param currentMatchProxyParma;

    public GetRoomListProxy getRoomListProxy;
    public GetRoomListProxy.Param getRoomListProxyParam;

    public MatchSubscribeProxy matchSubscribeProxy;
    public MatchSubscribeProxy.Param matchSubscribeProxyParam;
}
