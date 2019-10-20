package com.yingke.videoplayer.worker;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 工人任务基类 主要是子线程 callable任务
 *
 * @author tuke
 *
 * @param <T>
 */
public abstract class WorkerTask<T> implements Callable<T> {

	private static final String TAG = WorkerTask.class.getName();

    //任务名称
	private String taskName = "WorkerTask";
    //结果是否在主线程处理
	private boolean isResultOnMainThread = false ;

	public volatile boolean isCancelled = false;

	public WorkerTask(String taskName , boolean isResultOnMainThread) {
		this.taskName = taskName;
		this.isResultOnMainThread = isResultOnMainThread;
	}

	public WorkerTask(String taskName) {
		this.taskName = taskName;
		this.isResultOnMainThread = false;
	}

	/**
	 * 执行的内容
	 * @return
	 */
	protected final T executeWrapper() {

		T result = null;
		try {
			//没有执行取消
		    if (!isCancelled()) {
    			// 执行
    			result = execute();
    			// 执行完成后，从map中删除
    			Future<?> future = DefaultWorkerCenter.getInstance().removeTask(this);
    			if(future != null){
    				if(future.isCancelled()){
						// 被cancel了，不再通知结果
						Log.e(TAG, "task has been cancelled. task= " + getTaskName());
					}else {
						// 通知结果
						notifyResult(result,isResultOnMainThread);
					}
				}else {
					// 可能被其他地方删除了，也不通知结果
					Log.e(TAG, "task not found. task=" + getTaskName());
				}
		    } else {
				DefaultWorkerCenter.getInstance().removeTask(this);
				Log.e(TAG, "not execute for the task has been cancelled. task= " + getTaskName());
	        }
		} catch (Exception e) {
			Log.e(TAG, "task execute exception. ex=" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public final T call() throws Exception {
		return executeWrapper();
	}


	/**
	 * 子类实现
	 * @return
	 */
	protected abstract T execute();

	/**
	 * 子类实现
	 * @param result
	 */
	protected abstract void notifyResult(T result);

	/**
	 * 通知结果
	 * @param result       处理结果
	 * @param isMainThread 结果是否在主线程处理
	 */
	private void notifyResult(final T result, boolean isMainThread){

    	if(isMainThread){
    		//结果在 主线程处理
    		IWorkerCenter mCurWorkerCenter = WorkerCenter.getInstance().getmCurWorkerCenter();
    		if(mCurWorkerCenter != null){
				mCurWorkerCenter.submitMainThreadTask(new ThreadTask() {
					@Override
					public void onWork() {
						notifyResult(result);
					}
				});
			}
		}else {
    		//结果在 子线程处理
			notifyResult(result);
		}

	}

	public final String getTaskName() {
		return taskName;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

}
