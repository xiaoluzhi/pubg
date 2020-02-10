package com.tencent.tga.liveplugin.mina;

import com.loopj.android.tgahttp.Configs.Configs;
import com.tencent.common.log.tga.TLog;
import com.tencent.tga.base.net.Message;
import com.tencent.tga.base.net.Request;
import com.tencent.tga.liveplugin.mina.MessageStruct.ConnectTypeNotice;
import com.tencent.tga.liveplugin.mina.MessageStruct.RspNotice;
import com.tencent.tga.liveplugin.mina.codec.MinaCodecFactory;
import com.tencent.tga.liveplugin.mina.handler.IoListener;
import com.tencent.tga.liveplugin.mina.handler.MinaClientHandler;
import com.tencent.tga.liveplugin.mina.handler.REQ_RESULT_CODE;
import com.tencent.tga.liveplugin.mina.handler.RequestUtil;
import com.tencent.tga.liveplugin.mina.interfaces.OnResponseListener;
import com.tencent.tga.liveplugin.mina.utils.MinaConstants;
import com.tencent.tga.net.mina.core.buffer.IoBuffer;
import com.tencent.tga.net.mina.core.future.ConnectFuture;
import com.tencent.tga.net.mina.core.future.ReadFuture;
import com.tencent.tga.net.mina.core.service.IoConnector;
import com.tencent.tga.net.mina.core.service.IoService;
import com.tencent.tga.net.mina.core.session.IdleStatus;
import com.tencent.tga.net.mina.core.session.IoSession;
import com.tencent.tga.net.mina.filter.codec.ProtocolCodecFilter;
import com.tencent.tga.net.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MinaThread extends Thread {
	private static final String TAG = MinaConstants.TAG+"MinaThread";

	public final static String Domain = "101.227.153.22";

	public static int REQ_TIMEOUT_PERIOD = 10000;

	public static int SOCKET_CONNECT_TIMEOUT = 2000;

	public static int SOCKET_CONNECT_MAX_LIMIT = 3;
	private IoSession session = null;
	private IoConnector connector = null;
	ConnectFuture future;

	private ArrayList<String> ipList = new ArrayList<>();
	private ArrayList<Integer> portList = new ArrayList<>();

	private ExecutorService fixedThreadPool;

	public MinaThread(ArrayList<String> ipList, ArrayList<Integer> portList) {
		this.ipList.addAll(ipList);
		this.portList.addAll(portList);
		fixedThreadPool = Executors.newFixedThreadPool(4);
		connector = new NioSocketConnector();//TCP
		//connector = new NioDatagramConnector();//UDP
	}

	private String getIp(){
		if(ipList != null && ipList.size() != 0){
			Random r = new Random();
			int index = r.nextInt(ipList.size());
			return ipList.get(index);
		}else {
			throw new NullPointerException();
		}

	}

	private int getPort(){
		if(portList != null && portList.size() != 0){
			Random r = new Random();
			int index = r.nextInt(portList.size());
			return portList.get(index);
		}else {
			throw new NullPointerException();
		}
	}

	private OnResponseListener onResponseListener;
	public void setOnResponseListener(OnResponseListener listener){
		this.onResponseListener = listener;
	}
	@Override
	public void run() {
		super.run();

		// TODO Auto-generated method stub]
		try{
			TLog.e(TAG,"客户端链接开始...");
			//connector = new NioSocketConnector();
			TLog.e(TAG,""+101);
			// 设置链接超时时间
			if(connector == null)
				return;
			connector.setConnectTimeoutMillis(SOCKET_CONNECT_TIMEOUT);

			//自定义编解码器
			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaCodecFactory()));
			connector.setHandler(new MinaClientHandler(onResponseListener));
			//设置默认连接远程服务器的IP地址和端口
			connector.setDefaultRemoteAddress(new InetSocketAddress(getIp(), getPort()));
			// 监听客户端是否断线
			connector.addListener(ioListener);
			//开始连接
			socket_connect();

			if (session != null && session.isConnected()) {
				session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
				TLog.e(TAG,"客户端断开111111...");
				// connector.dispose();//彻底释放Session,退出程序时调用不需要重连的可以调用这句话，也就是短连接不需要重连。长连接不要调用这句话，注释掉就OK。
			}
			TLog.e(TAG,""+119);
		}catch (Exception e){
			TLog.e(TAG,"Run error : "+e.getMessage());
		}
	}

	//重新跑线程
	private synchronized void reRun(){
		try {
			TLog.e(TAG,"reRun");
			if (connector != null){
				connector.removeListener(ioListener);
				connector.getFilterChain().remove(ProtocolCodecFilter.class);
			}
			if(future != null){
				future.cancel();
			}
			if(session != null){
				session.closeNow();
			}
			if(this.isAlive()){
				return;
			}

			run();
		}catch (Exception e){
			TLog.e(TAG,"reRun error : "+e.getMessage());
		}
	}

	private void socket_connect(){
		TLog.e(TAG,"socket_connect");
		try {
			future = connector.connect();
			future.awaitUninterruptibly();// 等待连接创建完成
			TLog.e(TAG,future == null?"future null":"future not null");

			session = future.getSession();// 获得session

			TLog.e(TAG,session == null?"session null":"session not null");

			//session.getIdleCount(IdleStatus.BOTH_IDLE);
			//判断是否连接服务器成功
			if (session != null && session.isConnected()) {
				session.getConfig().setUseReadOperation(true);

			} else {
				TLog.e(TAG,"写数据失败");
			}

			TLog.e(TAG,""+11);
		}catch (Exception e){
			TLog.e(TAG,"客户端链接异常...:"+e.getMessage());
			if(onResponseListener != null){
				onResponseListener.onConnectType(new ConnectTypeNotice(ConnectTypeNotice.ConnectType.CONNECT_FAIL));
			}
		}
	}


	private IoListener ioListener = new IoListener(){
		@Override
		public void serviceActivated(IoService arg0) throws Exception {
			super.serviceActivated(arg0);
			TLog.e(TAG,"serviceActivated");
		}

		@Override
		public void serviceDeactivated(IoService arg0) throws Exception {
			super.serviceDeactivated(arg0);
			TLog.e(TAG,"serviceDeactivated");
		}

		@Override
		public void serviceIdle(IoService arg0, IdleStatus arg1) throws Exception {
			super.serviceIdle(arg0, arg1);
			TLog.e(TAG,"serviceIdle");
		}

		@Override
		public void sessionClosed(IoSession arg0) throws Exception {
			super.sessionClosed(arg0);
			TLog.e(TAG,"sessionClosed");
		}

		@Override
		public void sessionCreated(IoSession arg0) throws Exception {
			super.sessionCreated(arg0);
			TLog.e(TAG,"sessionCreated");
			if(onResponseListener != null){
				onResponseListener.onConnectType(new ConnectTypeNotice(ConnectTypeNotice.ConnectType.CONNECT_SUCC));
			}
		}

		@Override
		public void sessionDestroyed(IoSession arg0) throws Exception {
			super.sessionDestroyed(arg0);
			TLog.e(TAG,"sessionDestroyed");
			// TODO: 2017/8/30 通知socket断线
			if(onResponseListener != null){
				onResponseListener.onConnectType(new ConnectTypeNotice(ConnectTypeNotice.ConnectType.DISCONNECT));
			}
		}
	};


	public int sendRequest(final int command, final int subcmd, final int seq, final byte[] payload){
		TLog.e(TAG,"socket command:" + command + " subcmd:" + subcmd + " seq is : " + seq);

		fixedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if(session != null && session.isConnected()){
						TLog.e(TAG,"sendRequest...1");
						session.write(IoBuffer.wrap(RequestUtil.getRequest(command,subcmd,seq,payload)));
						//接收
						ReadFuture readFuture = session.read();
						TLog.e(TAG,"sendRequest...3");
						if (readFuture.awaitUninterruptibly(REQ_TIMEOUT_PERIOD/2, TimeUnit.MILLISECONDS)) {
							TLog.e(TAG,"socket from ip:" + session.getLocalAddress() + " to ip:" + session.getRemoteAddress());
						}else {
							TLog.e(TAG,"sendRequest...ERROR_CODE_TIMEOUT :" + command + " subcmd:" + subcmd + " seq is : " + seq);
							if(onResponseListener != null){
								onResponseListener.onResponse(new RspNotice(REQ_RESULT_CODE.ERROR_CODE_TIMEOUT,
										Request.createEncryptRequest(command,subcmd,null,null,null,null),
										Message.createMessage(command,subcmd, Configs.CLIENT_TYPE,seq,null,null,null,REQ_RESULT_CODE.ERROR_CODE_TIMEOUT)));
							}
						}

						TLog.e(TAG,"sendRequest...5");
					}else {
						TLog.e(TAG,"sendRequest...ERROR_NOT_CONN");
						reRun();
						if(onResponseListener != null){
							onResponseListener.onResponse(new RspNotice(REQ_RESULT_CODE.ERROR_NOT_CONN,
									Request.createEncryptRequest(command,subcmd,null,null,null,null),
									Message.createMessage(command,subcmd, Configs.CLIENT_TYPE,seq,null,null,null,REQ_RESULT_CODE.ERROR_NOT_CONN)));
						}
					}
				}catch (Exception e){
					TLog.e(TAG,"sendRequest error : "+e.getMessage());
					if(onResponseListener != null){
						onResponseListener.onResponse(new RspNotice(REQ_RESULT_CODE.ERROR_CONN_EXCEPTION,
								Request.createEncryptRequest(command,subcmd,null,null,null,null),
								Message.createMessage(command,subcmd, Configs.CLIENT_TYPE,seq,null,null,null,REQ_RESULT_CODE.ERROR_CONN_EXCEPTION)));
					}
				}
			}
		});


		return 0;
	}

	public void release(){
		if (session != null && session.isConnected()) {
			session.closeNow();
			session = null;
			TLog.e(TAG,"session.close");
		}
		if(connector!=null){
			connector.dispose();//彻底释放Session,退出程序时调用不需要重连的可以调用这句话，也就是短连接不需要重连。长连接不要调用这句话，注释掉就OK。
			connector = null;
			TLog.e(TAG,"connector.dispose");
		}
		if(fixedThreadPool != null && !fixedThreadPool.isShutdown()){
			fixedThreadPool.shutdownNow();
		}
		fixedThreadPool = null;
	}
}