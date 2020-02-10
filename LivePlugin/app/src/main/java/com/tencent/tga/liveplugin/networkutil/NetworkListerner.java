package com.tencent.tga.liveplugin.networkutil;

public class NetworkListerner
{
	public static interface Timeout
	{
		public void onTimeout();
	}
	
	public static interface AuthListerner extends Timeout
	{
		public void onAuthorizedOk(int result,
								   String errmsg,
								   String uuid,
								   String openid,
								   String auth_key,
								   String access_token,
								   int token_expires);
		
		public void onAuthorizedFailed(int result, String errmsg);
	}
	
	public static interface ConnectListerner extends Timeout
	{
		public void onConnected(int result, String errmsg);
	}
	
	public static interface DisconnectListerner extends Timeout
	{
		public void onDisconnected(int result, String errmsg);
	}
	
	public static interface HelloListerner extends Timeout
	{
		public void onHelloRsp(int result, int time, int needCloseTcp);
	}
}