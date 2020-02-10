package com.tencent.tga.liveplugin.base.task;



import com.tencent.common.log.tga.TLog;

/**
 * Created by asherchen on 2015/9/9.
 */
public abstract class Task
{
	public final static String tag = "Task";

	private long startTime;
	private long endTime;
//
//	private Map<String, Object> map;

	public void execute()
	{
		onPreExecute();
		run();
		onPostExecute();
	}

	protected abstract void run();

	protected void onPreExecute()
	{
		startTime = System.currentTimeMillis();
		TLog.v(tag, "Task(" + this + ")" + ":start time = " + startTime);
	}

	protected void onPostExecute()
	{
		endTime = System.currentTimeMillis();
		TLog.v(tag,"Task("+this+")"+":end time = "+endTime+" , execute time = "+(endTime - startTime));
	}
}
