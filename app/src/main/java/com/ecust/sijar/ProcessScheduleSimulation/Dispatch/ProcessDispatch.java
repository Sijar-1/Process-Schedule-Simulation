package com.ecust.sijar.ProcessScheduleSimulation.Dispatch;


import java.util.LinkedList;
import java.util.List;


/**
 * Created by Sijar on 2019/7/3.
 * 调度算法的抽象父类
 */
public abstract class ProcessDispatch {

    protected DispatchListener listener;
    protected Thread thread = null;
    protected boolean suspend = false;
    protected String control = "";
    protected boolean isRunning = false;
    protected boolean lock = true;
    protected int index;
    protected int length;
    protected int time;
    protected LinkedList<Process> list;   //所有进程的队列
    protected List<Process> listRR;   //RR新增

    // 抽象方法：启动线程
    public abstract void startThread(LinkedList<Process> l);


    // 抽象方法：有优先级的启动线程
  //  public abstract void startThread(LinkedList<Process> l, int time1);

    // 抽象方法：有时间片的启动线程
    public abstract void startThread(List<Process> l,int slot);




    // 注册监听器
    public void setDispatchListener(DispatchListener listener) {
        this.listener = listener;
    }

    // 检查是否线程是否挂起
    public boolean isSuspend() {
        return suspend;
    }

    // 阻塞线程
    public void pause() {
        suspend = true;
    }

    // 启动线程
    public void start() {
        if (suspend) {
            synchronized (control) {
                control.notify();
                suspend = false;
            }
        }
    }

    // 检查线程是否存在
    public boolean isRunning() {
        return isRunning;
    }

    // 终止线程
    public void stop() {
        if (isRunning()) {
            lock = false;
        }
    }

    // 被优先级调度算法覆盖的动态插入进程算法
    public void InsertProcess(Process p) {
        return;
    }
    public void InsertProcess(Process p,LinkedList<Process> plist) {
        return;
    }
}
