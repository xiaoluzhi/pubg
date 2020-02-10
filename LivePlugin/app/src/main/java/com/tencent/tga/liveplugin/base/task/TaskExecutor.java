package com.tencent.tga.liveplugin.base.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asherchen on 2015/9/9.
 */
public abstract class TaskExecutor
{
	private List<Task> mTasks;
	private int mCurrentTaskIndex;

	public TaskExecutor()
	{
		mTasks = new ArrayList<Task>();
	}

	public void addTask(Task task)
	{
		mTasks.add(task);
	}

	protected Task getCurrentTask()
	{
		if (mCurrentTaskIndex < mTasks.size())
		{
			return mTasks.get(mCurrentTaskIndex);
		}
		else
		{
			return null;
		}
	}

	protected Task nextTask()
	{
		Task task = getCurrentTask();
		mCurrentTaskIndex ++;
		return task;
	}

	protected boolean hasMoreTask()
	{
		return mCurrentTaskIndex < mTasks.size();
	}

	public abstract void execute();
}
