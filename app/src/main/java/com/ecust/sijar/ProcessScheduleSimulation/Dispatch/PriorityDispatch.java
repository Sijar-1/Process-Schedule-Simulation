package com.ecust.sijar.ProcessScheduleSimulation.Dispatch;

import android.os.Handler;
import android.os.Message;

//import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Sijar on 2019/7/3.
 * 优先级检查调度算法
 */
public class PriorityDispatch extends ProcessDispatch {

    // 按优先级排列的进程列表
    private LinkedList<Process> processList;
    //抽象方法：启动线程,写入当前总运行时间
    @Override
    public void startThread(LinkedList<Process> l ) {
        list = l;
        index = 0;
        time = 0;
        lock = true;
        isRunning = false;
        // 初始化优先级进程列表
        initPriorityList();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                while (lock) {
                    // 检查是否阻塞
                    pauseThread();
                    length = processList.size() - 1;
                    time++;

                    processList.get(index).setState("进行");
                    processList.get(index).setRunTime(processList.get(index).getRunTime() + 1);

                    // 如果达到需要时间，则设置检查状态为完成
                    if (processList.get(index).getRunTime() >= processList.get(index).getRunCPUtime()) {   //  if (processList.get(index).getRunTime() >= processList.get(index).getNeedTime())   _Sijar
                        processList.get(index).setState("完成");
                        processList.get(index).setEndTime(time);
                        index++;
                    }

                    handler.sendEmptyMessage(time);
                    // 遍历完进程列表，结束线程
                    if (index > length) {
                        lock = false;
                        time = 0;
                    }
                    try {
                        thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isRunning = false;

            }
        });
        thread.start();
    }
    //抽象方法：时间片启动线程,不用管
    @Override
   public void startThread(List<Process> l , int slot) {
        startThread(l,1);
    }

    private void initPriorityList() {

        processList = new LinkedList<>();
        processList.add(list.get(0));

        // 从列表尾向头遍历，按优先级大小，选择合适的位置插入
        for (int i = 1; i < list.size(); i++) {
            // 若优先级小于列表的第一个检查，则排在第一位，抢占资源
            for (int j = processList.size() - 1; j >= 0; j--) {
                if (list.get(i).getPriority() < processList.get(0).getPriority()) {
                    processList.add(0, list.get(i));
                    break;
                }
                // 若优先级大于或等于当前进程的优先级，则排在当前进程的后面
                if (list.get(i).getPriority() >= processList.get(j).getPriority()) {
                    processList.add(j + 1, list.get(i));
                    break;
                }
            }
        }
    }

    // 动态插入新的进程
    @Override
    public void InsertProcess(Process p) {
        // 为新的进程选择合适的队列位置插入
        for (int j = processList.size() - 1; j >= 0; j--) {
            if (p.getPriority() < processList.get(0).getPriority()) {
                processList.add(0, p);
            }
            if (p.getPriority() >= processList.get(j).getPriority()) {
                processList.add(j + 1, p);
            }
        }
    }

    // 阻塞线程
    public void pauseThread() {
        if (suspend) {
            synchronized (control) {
                try {
                    control.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            listener.nowTime(msg.what);
        }
    };
}
