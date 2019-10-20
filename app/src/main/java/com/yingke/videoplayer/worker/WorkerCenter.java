package com.yingke.videoplayer.worker;

import android.util.Log;
import java.util.concurrent.Future;

/**
 * 工人中心策略控制类
 *
 * @author tuke
 *
 */
public class WorkerCenter implements IWorkerCenter {

	public static final String TAG = WorkerCenter.class.getSimpleName();

	private static WorkerCenter sInstance = null;
	private static final Object sInstanceLock = new Object();

	/** 当前的工人中心实现 */
	private IWorkerCenter mCurWorkerCenter = null;

	private WorkerCenter() {
		setWorkerCenter(DefaultWorkerCenter.getInstance());
	}
	
	/**
	 * 初始化工人中心
	 * 
	 * @param center 传入null表示采用导航SDK内部线程池方案
	 */
	public static void init(IWorkerCenter center) {
		if ( sInstance == null ) {
			synchronized ( sInstanceLock ) {
				if ( sInstance == null ) {
					sInstance = new WorkerCenter();
				}
			}
		}
		if ( center != null ) {
			//使用 外面的 worker center
			sInstance.setWorkerCenter(center);
		} else {
			//使用默认的 worker center
			sInstance.setWorkerCenter(DefaultWorkerCenter.getInstance());

		}
		
	}
	
	/**
	 * 设置具体的工人中心策略实现
	 * @param center
	 */
	private void setWorkerCenter(IWorkerCenter center) {
		if ( center == null ) {
			return;
		}

		mCurWorkerCenter = center;
	}

	/**
	 * 单例
	 * @return
	 */
	public static WorkerCenter getInstance() {
		if ( sInstance == null ) {
			synchronized( sInstanceLock ) {
				if ( sInstance == null ) {
					sInstance = new WorkerCenter();
				}
			} 
		}
		return sInstance;
	}


	@Override
	public <T> Future<?> submitTask(WorkerTask<T> task) {

		if(mCurWorkerCenter != null){
			return  mCurWorkerCenter.submitTask(task);
		}
		log();
		return null;
	}

	@Override
	public <T> void submitNormalTask(WorkerTask<T> task) {
         if(mCurWorkerCenter != null){
			 mCurWorkerCenter.submitNormalTask(task);
		 }else {
			 log();
		 }
	}

	@Override
	public <T> void submitNormalTaskDelay(WorkerTask<T> task, long delayMS) {
           if(mCurWorkerCenter != null){
			   mCurWorkerCenter.submitNormalTaskDelay(task ,delayMS);
		   }else {
			   log();
		   }
	}

	@Override
	public <T> T submitBlockTask(WorkerTask<T> task) {
		if(mCurWorkerCenter != null){
			return mCurWorkerCenter.submitBlockTask(task);
		}
		log();
		return null;
	}

	@Override
	public void submitQueneTask(ThreadTask task) {
		if(mCurWorkerCenter != null){
			mCurWorkerCenter.submitQueneTask(task);
		}else {
			log();
		}
	}

	@Override
	public void submitQueneTaskDelay(ThreadTask task, long delayMS) {
		if(mCurWorkerCenter != null){
			mCurWorkerCenter.submitQueneTaskDelay(task,delayMS);
		}else {
			log();
		}

	}

	@Override
	public void submitMainThreadTask(ThreadTask task) {
		if(mCurWorkerCenter != null){
			mCurWorkerCenter.submitMainThreadTask(task);
		}else {
			log();
		}

	}

	@Override
	public void submitMainThreadTaskDelay(ThreadTask task, long delayMS) {
		if(mCurWorkerCenter != null){
			mCurWorkerCenter.submitMainThreadTaskDelay(task, delayMS);
		}else {
			log();
		}

	}

	@Override
	public <T> Future<?> removeTask(WorkerTask<T> task) {
		if(mCurWorkerCenter != null){
			return mCurWorkerCenter.removeTask(task);
		}
		log();
		return null;
	}

	@Override
	public <T> boolean cancelTask(WorkerTask<T> task, boolean force) {
		if(mCurWorkerCenter != null){
			return mCurWorkerCenter.cancelTask(task,force);
		}
		log();
		return false;
	}

	public IWorkerCenter getmCurWorkerCenter() {
		return mCurWorkerCenter;
	}

	public void log(){
		Log.e(TAG, "worker center is null.");
	}

}
