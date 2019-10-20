package com.yingke.videoplayer.worker;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;


/**
 * <p/>
 * 功能 :处理子线程队列任务
 * <p/>
 * <p>
 * 最后修改人 无
 */
public class QueueTaskHandler {

    /**
     * 默认消息
     */
    private static final int MSG_DEFAULT = 1;

    /**
     * 线程Handler
     */
    private HandlerThread mHandlerThread = null;

    /**
     * 处理消息的Handler
     */
    private Handler mHandler = null;

    public QueueTaskHandler() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {

        if (mHandlerThread != null ) {
            return;
        }
        mHandlerThread = new HandlerThread("CommonHandlerThread");
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //线程中处理消息
                switch (msg.what){
                    case MSG_DEFAULT:
                        if(msg.obj != null && msg.obj instanceof ThreadTask ){
                            ((ThreadTask) msg.obj).onWork();
                        }
                        break;
                    default:

                        break;
                }
            }
        };
    }


    /**
     * 执行队列任务
     * @param task
     */
    public void executeQueueWork(ThreadTask task){
        sendMessage(MSG_DEFAULT,task);
    }

    /**
     * 延迟发送队列任务
     * @param task
     * @param delayMills 延迟时间
     */
    public void executeQueueWorkDelay(ThreadTask task,long delayMills){
        sendMessageDelay(MSG_DEFAULT,task,delayMills);
    }


    /**
     * 发送消息
     * @param what
     * @return
     */
    public boolean sendMessage(int what) {
        return sendMessage(what, 0, 0, null, 0);
    }

    /**
     * 发送消息
     * @param what
     * @param task
     * @return
     */
    public boolean sendMessage(int what , ThreadTask task){
        return sendMessage(what, 0, 0, task, 0);
    }

    /**
     * 发送延迟消息
     * @param what
     * @param task      任务
     * @param delayTime 延时时间
     * @return
     */
    public boolean sendMessageDelay(int what , ThreadTask task ,long delayTime){
        return sendMessage(what, 0, 0, task, delayTime);
    }

    /**
     * 发送消息
     * @param what
     * @param arg1
     * @param arg2
     * @param obj
     * @param delayTime
     * @return
     */
    public boolean sendMessage(int what, int arg1, int arg2, Object obj, long delayTime) {
        if ( mHandler == null ) {
            return false;
        }
        Message message = mHandler.obtainMessage(what);
        message.arg1 = arg1;
        message.arg2 = arg2;
        if ( obj != null ) {
            message.obj = obj;
        }
        if ( delayTime <= 0 ) {
            mHandler.sendMessage(message);
        } else {
            mHandler.sendMessageDelayed(message, delayTime);
        }
        return true;
    }



    public Handler getHandler() {
        return mHandler;
    }

    public Looper getLooper() {
        if ( mHandlerThread == null ) {
            return null;
        }
        return mHandlerThread.getLooper();
    }


}
