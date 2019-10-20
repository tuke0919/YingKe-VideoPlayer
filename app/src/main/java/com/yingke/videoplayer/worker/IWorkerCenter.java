package com.yingke.videoplayer.worker;

import java.util.concurrent.Future;

/**
 * 工人中心接口定义
 *
 * @author tuke
 */
public interface IWorkerCenter {

	/**
	 * 使用方禁止调用，后续会禁止
	 * @param task
	 * @return
	 */
	public <T> Future<?> submitTask(WorkerTask<T> task);

	/**
	 * 子线程执行任务，
	 * @param task
	 */
	public <T> void submitNormalTask(WorkerTask<T> task);

	/**
	 * 子线程延迟执行任务
	 * @param task
	 * @param delayMS  单位：ms
	 */
	public <T> void submitNormalTaskDelay(WorkerTask<T> task, long delayMS);

	/**
	 * 子线程执行任务，并阻塞调用线程等待执行完成  get
	 * @param task
	 */
	public <T> T submitBlockTask(WorkerTask<T> task);


	/**
	 * 子线程执行队列任务，根据提交的先后顺序执行，无任何方式的结果通知  threadHandler实现
	 * @param task
	 */
	public void submitQueneTask(ThreadTask task);

	/**
	 * 子线程延时执行队列任务，根据提交的先后顺序执行，无任何方式的结果通知  threadHandler实现
	 * @param task
	 * @param delayMS  单位：ms
	 */
	public void submitQueneTaskDelay(ThreadTask task, long delayMS);


	/**
	 * 主线程(UI线程)执行任务，无任何方式的结果通知  mainhandler实现
	 * @param task
	 */
	public  void submitMainThreadTask(ThreadTask task);

	/**
	 * 主线程(UI线程)执行任务，无任何方式的结果通知 mainhandler实现
	 * @param task
	 * @param delayMS  单位：ms
	 */
	public  void submitMainThreadTaskDelay(ThreadTask task, long delayMS);


	/**
	 * 移除任务
	 *
	 * @param task 需要移除的任务
	 * @param <T>
	 * @return
	 */
	public <T> Future<?> removeTask(WorkerTask<T> task);
	
	/**
	 * 取消目标任务
	 * @param task  需要取消的任务
	 * @param force 是否强制停止正在执行的目标任务
	 * @return
	 */
	public <T> boolean cancelTask(WorkerTask<T> task, boolean force);
}
