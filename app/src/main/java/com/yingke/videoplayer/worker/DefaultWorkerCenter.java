package com.yingke.videoplayer.worker;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 默认工人中心实现
 *
 * @author tuke
 *
 */
public  class DefaultWorkerCenter implements IWorkerCenter {

	private static final String TAG = DefaultWorkerCenter.class.getSimpleName();

	// 线程池定义
	private static final int THREADPOOL_COUNT = 3;
	private static final int MSG_NORMAL_TASK_DELAY = 1;

	//单例
	private static DefaultWorkerCenter sInstance;

	private DefaultWorkerCenter(){};

    public static DefaultWorkerCenter getInstance(){
    	if(sInstance == null){
    		synchronized (DefaultWorkerCenter.class){
    			if(sInstance == null){
					sInstance = new DefaultWorkerCenter();
				}
			}
		}
		return sInstance;
	}

	/**
	 * 保存task - future
	 */
	protected Map<WorkerTask<?>, Future<?>> taskFutures = new ConcurrentHashMap<WorkerTask<?>, Future<?>>();

	/**
	 * 主线程任务处理
	 */
	private MainTaskHandler mMainHandler = new MainTaskHandler();

	/**
	 * 子线程队列任务处理
	 */
	private QueueTaskHandler mQueueHandler = new QueueTaskHandler();

	/**
	 * 线程池任务处理
	 */
	private ExecutorService mExecutorService = Executors.newFixedThreadPool(THREADPOOL_COUNT);;


	/**
	 * 普通延迟任务的处理器
	 */
	private Handler mNormalTaskDelayHandler = new Handler(mQueueHandler.getLooper()){
		@Override
		public void handleMessage(Message msg) {

			if(msg.what == MSG_NORMAL_TASK_DELAY){
				if(msg.obj != null && msg.obj instanceof WorkerTask){
					submitNormalTask((WorkerTask<Object>) msg.obj);
				}
			}else {
				Log.e( TAG," task is not normal delay task" );
			}
		}
	};


	/**
	 * 提交主线程任务
	 * @param task
	 */
	@Override
	public void submitMainThreadTask(ThreadTask task) {
		if(task != null ){
			mMainHandler.executeMainWork(task);
		}
	}

	/**
	 * 提交主线程 延迟任务
	 * @param task
	 * @param delayMS 单位：ms
	 */
	@Override
	public void submitMainThreadTaskDelay(ThreadTask task, long delayMS) {
		if(task != null ){
			mMainHandler.executeMainWorkDelay(task,delayMS);
		}
	}

	/**
	 * 提交子线程 队列任务
	 * @param task
	 */
	@Override
	public void submitQueneTask(ThreadTask task) {
		if(task != null ){
			mQueueHandler.executeQueueWork(task);
		}
	}

	/**
	 * 提交子线程 队列任务 延迟
	 * @param task
	 * @param delayMS 单位：ms
	 */
	@Override
	public void submitQueneTaskDelay(ThreadTask task, long delayMS) {
		if(task != null ){
			mQueueHandler.executeQueueWorkDelay(task,delayMS);
		}
	}

	/**
	 * 提交阻塞 任务 到线程池
	 * @param task
	 * @param <T>
	 */
	@Override
	public <T> T submitBlockTask(WorkerTask<T> task) {
        if(!checkTask(task)){
        	return null;
		}
		//提交线程池
		Future<T> future =  submitTask(task);
		try {
			if(future != null){
				//执行 阻塞 直到 任务执行完毕 主线程？
				return future.get();
			}
		} catch (InterruptedException e) {
			Log.e(TAG,"submitBlockTask has InterruptedException");
			e.printStackTrace();

		} catch (ExecutionException e) {
			Log.e(TAG,"submitBlockTask has ExecutionException");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 线程池 提交任务 并保存task - future
	 * @param task
	 * @param <T>
	 * @return
	 */
	@Override
	public <T> Future<T> submitTask(WorkerTask<T> task) {

		if(!checkTask(task)){
			return null;
		}
		if(mExecutorService != null){

			Future<T> future = mExecutorService.submit(task);

			//保存task - future
			if(future != null){
				taskFutures.put(task,future);
			}
			return future;
		}
		return null;
	}

	/**
	 * 子线程执行任务
	 *
	 * @param task
	 * @param <T>
	 */
	@Override
	public <T> void submitNormalTask(WorkerTask<T> task) {

		submitTask(task);

	}

	/**
	 * 子线程执行任务 延迟
	 *
	 * @param task
	 * @param delayMS 单位：ms
	 * @param <T>
	 */
	@Override
	public <T> void submitNormalTaskDelay(WorkerTask<T> task, long delayMS) {

		Message message = mNormalTaskDelayHandler.obtainMessage();
		message.what = MSG_NORMAL_TASK_DELAY;
		message.obj = task;
		mNormalTaskDelayHandler.sendMessageDelayed(message , delayMS);

	}


	/**
	 * 检查任务
	 * @param task
	 * @return
	 */
	private boolean checkTask(WorkerTask<?> task){
		if(task == null){
			return false;
		}
		return true;
	}

	/**
	 * task-future 移除任务
	 * @param task 需要移除的任务
	 * @param <T>
	 * @return
	 */
	@Override
	public <T> Future<?> removeTask(WorkerTask<T> task) {
		if(task != null && taskFutures.containsKey(task)){
			return taskFutures.remove(task);
		}
		return null;
	}

	@Override
	public <T> boolean cancelTask(WorkerTask<T> task, boolean force) {
		if(!checkTask(task)){
			return false;
		}

		boolean result = false;

		try {
			//无论取消成不成功，都置位true，不让它执行
			task.isCancelled = true;

			//执行任务完成后 就remove了
			if(taskFutures.containsKey(task)){
				Future<?> future = taskFutures.get(task);
				// 强制取消任务
				// 运行前 取消-true 运行前已经被取消 - false  运行中取消- true  运行完成取消 - false
				result = future.cancel(force);
			}
		}catch (Exception e){
			result = false;
		}
		return result;
	}

}
