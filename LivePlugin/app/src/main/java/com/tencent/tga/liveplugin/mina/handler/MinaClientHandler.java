package com.tencent.tga.liveplugin.mina.handler;

import com.tencent.common.log.tga.TLog;
import com.tencent.tga.liveplugin.mina.interfaces.OnResponseListener;
import com.tencent.tga.liveplugin.mina.utils.MinaConstants;
import com.tencent.tga.net.mina.core.service.IoHandlerAdapter;
import com.tencent.tga.net.mina.core.session.IoSession;

public class MinaClientHandler extends IoHandlerAdapter {

	private static final String TAG = MinaConstants.TAG+"MinaClientHandler";
	private OnResponseListener onResponseListener;

	public MinaClientHandler(OnResponseListener listener) {
		this.onResponseListener = listener;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		TLog.i(TAG, "客户端发生异常 : "+cause.getMessage());
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		MessageHandleUtil.handleReceiveMessage(message,onResponseListener);
		super.messageReceived(session, message);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
		super.messageSent(session, message);
	}
}
