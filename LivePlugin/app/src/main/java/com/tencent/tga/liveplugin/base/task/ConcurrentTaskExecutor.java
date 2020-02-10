package com.tencent.tga.liveplugin.base.task;

/**
 * Created by asherchen on 2015/9/9.
 * 这个是一个伪并行
 */
public class ConcurrentTaskExecutor extends TaskExecutor
{
	@Override
	public void execute()
	{
		while (hasMoreTask())
		{
			Task task = nextTask();
			try
			{
				if (task !=null)
					task.execute();
			}
			catch (Exception e)
			{
				//不要吃异常，很难发现问题
				e.printStackTrace();
			}
		}
	}
}
