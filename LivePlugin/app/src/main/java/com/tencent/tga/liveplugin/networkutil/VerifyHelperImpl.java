package com.tencent.tga.liveplugin.networkutil;

import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.base.net.VerifyHelper;

// not used ==> said by jammywmluo

public class VerifyHelperImpl implements VerifyHelper
{

	@Override
	public Request getSTRequest(boolean withLogin) {
		
		return null;
	}

	@Override
	public int onSTReponse(Message msg) {
		
		return 0;
	}
	
}