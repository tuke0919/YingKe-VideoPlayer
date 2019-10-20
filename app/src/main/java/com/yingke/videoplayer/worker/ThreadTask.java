package com.yingke.videoplayer.worker;

/**
 * <p>
 * 功能 线程回调接口 主要是主线程任务 和 子线程队列任务  无需返回结果
 * </p>
 * <p>
 *
 * @author tuke 时间 18/7/28 下午12:03
 * @version 1.0
 *          <p>
 *          最后修改人 无
 */
public interface ThreadTask {

    /**
     * 线程切换回调接口
     */
    public void onWork();
}
