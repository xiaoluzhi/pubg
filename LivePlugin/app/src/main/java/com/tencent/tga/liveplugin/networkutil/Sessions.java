package com.tencent.tga.liveplugin.networkutil;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.protocol.tga.auth.auth_cmd_types;
import com.tencent.protocol.tga.auth.auth_subcmd;
import com.tencent.tga.liveplugin.mina.MessageStruct.CSHead;

import java.io.UnsupportedEncodingException;

public class Sessions {
	
//	// acoount
//	public long uin;
//
//	// oicq rsp
//	public boolean logined;
//	public byte[] stKey;	// key
//	public byte[] sigKey;	// signature
	
	// auth rsp
	public boolean authorized = false;
	public byte[] uuid;
	public byte[] openid;
	public byte[] auth_key;
	public byte[] access_token;
	public int token_expires;
	public byte[] decrypt_key;
	// conn rsp
	public boolean connected;
	
	
	private static Sessions _instance;
	
	public static Sessions globalSession() {
		if(_instance == null){
			_instance = new Sessions();
		}
		return _instance;
	}

	public void unInit () {
		_instance = null;
	}

	
	private Sessions() {
	}

	/**
	* 返回uid byte[]
	* @author hyqiao
	* @time 2016/11/4 14:35
	*/
	public byte[] getUid(){
		if (uuid == null || uuid.length ==0){
			//LoginActivity.launch(TGAApplication.getInstance());
			try {
				return  "uuid".getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return uuid;
	}

	/**
	 * 返回uid String
	 * @author hyqiao
	 * @time 2016/11/4 14:35
	 */
	public String getUserId()  {
		if (uuid == null || uuid.length ==0){
			//LoginActivity.launch(TGAApplication.getInstance());
			return  "uid_null";
		}
		try {
			return new String(uuid, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return "uid_null";
		}
	}


	public String getOpenId(){
		return new String(getOpenid());
	}

	public byte[] getOpenid() {
		if (uuid == null || uuid.length ==0){
			//LoginActivity.launch(TGAApplication.getInstance());
			try {
				return  "openid".getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return openid;
	}

	public byte[] getAccess_token() {
		if (access_token == null || access_token.length ==0){
			//LoginActivity.launch(TGAApplication.getInstance());
			try {
				return  "access_token".getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return access_token;
	}

	public static byte[] getDecryptKey(CSHead cshead){
		if((cshead.command == auth_cmd_types.CMD_AUTH.getValue()) && (cshead.subcmd == auth_subcmd.SUBCMD_AUTH_TOKEN.getValue())){
			return Sessions.globalSession().getDefaultDecrypt_key();
		}else {
			return Sessions.globalSession().getDecrypt_key();
		}
	}

	public byte[] getDecrypt_key() {
		if (decrypt_key == null || decrypt_key.length ==0){
			//LoginActivity.launch(TGAApplication.getInstance());
			try {
				return  "".getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return decrypt_key;
	}

	public byte[] getDefaultDecrypt_key() {
		try {
			return Configs.PB_KEY.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;

	}
}
