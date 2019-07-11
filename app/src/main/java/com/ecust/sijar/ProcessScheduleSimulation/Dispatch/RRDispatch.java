package com.ecust.sijar.ProcessScheduleSimulation.Dispatch;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Veronica on 2019/7/5.
 * 时间片调度算法
 */
public class RRDispatch extends ProcessDispatch {

    private int slotTimer;
    private int listIndex;
    private boolean mLock;


    private List<Process> mReadyQueue; //就绪队列
    private List<Process> mBlockedQueue; //阻塞队列
    private List<Process> mWaitQueue;  //等待队列

    // 抽象方法：启动线程
    @Override
    public void startThread(LinkedList<Process> l) {
        startThread(l, 1);
    }


    // 抽象方法：有时间片的启动线程
    @Override
    public void startThread(List<Process> l, final int slotSize) {
        //ProcessDispatch
        listRR = l;  // 进程队列
        index = 0;   //就绪队列的头序号
        time = 0;   //已运行时间
        lock = true;  //进程是否已运行完毕
        isRunning = false;
        mLock = true;
        slotTimer = 0;   //在一个时间片内运行的时间

        //RRDispatch
        listIndex = 0;   //进程队列的序号
        mReadyQueue = new ArrayList<Process>();   //就绪队列
        mBlockedQueue = new ArrayList<Process>();   //阻塞队列
        mWaitQueue = new ArrayList<Process>();     //等待队列

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InitRR(); // 初始化-----根据进程开始时间排序，只在最开始运行一次
                isRunning = true;
                while (lock) {
                    // 检查是否阻塞线程
                    pauseThread();

                    if (mReadyQueue.size() != 0 || mBlockedQueue.size() != 0 || mWaitQueue.size() != 0) {
                        block(); //时间+1
                        if (mReadyQueue.size() != 0) {

                            listIndex = findp(mReadyQueue.get(index)); //实际list 表格中的序号
                            reflesh(index, listIndex, "进行");  //修改状态为进行，getRunCPUtime+1
                            slotTimer++;

                            //如果到了IO请求，阻塞
                            if (mReadyQueue.get(index).getIOstartTime() == mReadyQueue.get(index).getRunCPUtime()
                                    && mReadyQueue.get(index).getRunIOtime() == 0) {

                                reflesh(index, listIndex, "阻塞");  //进程加入阻塞队列，并从就绪队列中

                            } else {
                                // 如果达到需要时间，则设置进程状态为完成
                                if (mReadyQueue.get(index).getRunCPUtime() + mReadyQueue.get(index).getIOtime() >= mReadyQueue.get(index).getCPUTime()) {

                                    reflesh(index, listIndex, "完成");
                                } else {
                                    // 如果到了时间片，则切换进程
                                    if (slotTimer >= slotSize) {
                                        reflesh(index, listIndex, "时间片");

                                    }
                                }

                            }  //else
                        }
                        time++;  //已运行时间加1
                        handler.sendEmptyMessage(time); //已运行时间——+1
                        // logPrint();
                        addReady();
                        logPrint();
                    } else {
                        checkProcess();  //结束
                    }


                    try {
                        thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }  //循环结束
                isRunning = false;
            }
        });
        thread.start();
    }

    // 寻找进程列表中的就绪进程
    private void checkProcess() {
        slotTimer = 0;
        if (mReadyQueue.size() == 0 && mBlockedQueue.size() == 0 && mWaitQueue.size() == 0) {
            lock = false;
        }


    }

    // 检查是否在线程
    private void pauseThread() {
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


    private int findp(Process p) {

        int tmp = -1;
        for (Process q : listRR) {
            if (q.getName() == p.getName()) {
                tmp = listRR.indexOf(q);
                break;
            }
        }

        return tmp;
    }

    /**
     * void InitRR（）
     * 作用：1. 根据进程开始时间进行排序（从小到大）
     * 2. 将进程队列添加到等待队列
     * 3. 若等待队列中已有进程开始时间==0，即立即开始，则将该进程加入就绪队列，并从等待队列中删除
     * 注：只在第一次启动线程时运行
     **/
    private void InitRR() {
        if (listRR.size() != 0) {
            // 根据进程开始时间排序
            Collections.sort(listRR, new Comparator<Process>() {
                @Override
                public int compare(Process o1, Process o2) {
                    return o1.getStartTime() - o2.getStartTime();
                }
            });
            //插入到等待队列中
            copyList();
            //等待队列中已准备好的进程插入到就绪队列中
            initAdd();
        } else {
            lock = false;
        }
    }


    /**
     * void initAdd（）
     * 作用：若等待队列中已有进程开始时间==0，即立即开始，
     * 则进程状态从“等待”改为“就绪”，并将该进程加入就绪队列且从等待队列中删除
     * 注： 只在第一次启动线程时运行
     **/
    private void initAdd() {

        List<Process> waitL = new ArrayList<Process>();
        copyList(waitL, "W");
        for (Process p : waitL) {
            if (p.getStartTime() == 0) {
                p.setState("就绪");
                mReadyQueue.add(p);
                listRR.get(findp(p)).setState("就绪");
                if (mLock) {
                    mLock = false;
                    mWaitQueue.remove(waitL.indexOf(p));
                    mLock = true;
                }

            }
        }
    }

    /**
     * void addReady（）
     * 作用：若等待队列中已有进程已到达进程开始时间（即开始时间==已运行时间），进程从等待队列中移除，进入就绪队列中，并将进程状态“等待”改为“就绪”
     * 若阻塞队列中已有进程结束IO（即IO持续时间==IO运行时间），进程从阻塞队列中移除，进入就绪队列中，并将进程状态“阻塞”改为“等待”
     * 注： 1. 在线程运行后进行（即循环时运行）
     * 2. 若在等待队列和阻塞队列中同时有进程准备好，默认先插入等待队列中进程
     **/

    private void addReady() {
        List<Process> waitL = new ArrayList<Process>();
        List<Process> blockL = new ArrayList<Process>();

        if (mLock) {
            Log.d("rr","mmm");
            mLock = false;
          //  Log.d("rr",waitL.size()+""+mWaitQueue.size());
            for (Process p : mWaitQueue) {
                if (p.getStartTime() == time) {
                    p.setState("就绪");
                    mReadyQueue.add(p);
                    listRR.get(findp(p)).setState("就绪");
                    waitL.add(p);
                }
            }
            if(waitL.size()!=0) {
                for (Process p : waitL) {
                    mWaitQueue.remove(p);
                }
            }
            mLock = true;
        }

        for (Process p : mBlockedQueue) {
            if (p.getRunIOtime() == p.getIOtime()) {
                if ((p.getRunIOtime() + p.getRunCPUtime()) != p.getCPUTime()) {
                    p.setState("就绪");
                    mReadyQueue.add(p);
                    blockL.add(p);
                    listRR.get(findp(p)).setState("就绪");
                } else {
                    listRR.get(findp(p)).setState("完成");
                    listRR.get(findp(p)).setEndTime(time);
                     blockL.add(p);
                }
            }
        }

        if(blockL.size()!=0){
            for(Process p : blockL){
                mBlockedQueue.remove(p);
            }
        }
    }


    //阻塞

    private void block() {
        int nIndex = 0;
        for (int i = 0; i < mBlockedQueue.size(); i++) {
            mBlockedQueue.get(i).setRunIOtime(mBlockedQueue.get(i).getRunIOtime() + 1);
            mBlockedQueue.get(i).setState("阻塞");
            nIndex = findp(mBlockedQueue.get(i));
            listRR.get(nIndex).setRunIOtime(listRR.get(nIndex).getRunIOtime() + 1);
            if (i == mBlockedQueue.size() - 1) {
                nIndex = findp(mBlockedQueue.get(i));
                listRR.get(nIndex).setState("阻塞");

            }
        }

    }

    //处理
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            listener.nowTime(msg.what);
        }
    };

    /**
     * void reflesh(int mIndex, int mListIndex, String type)
     * 参数：1. mIndex    要改变得就绪队列序号
     * 2. mListIndex   要改变的进程队列list(显示的列表)的序号
     * 3. type      进程转变的状态
     * 作用：修改进程PCB中的状态、CPU运行时间
     * type  ==
     * 1. 进行    进程状态改为“进行”，并且CPU运行状态+1
     * 2. 完成    进程状态改为“完成”，并从就绪队列中删除
     * 3. 时间片  进程状态改为“就绪”， 进程插入到就绪队列末尾
     * 4. 阻塞    进程状态改为“阻塞”， 进程从就绪队列中删除，并加入到阻塞队列中
     **/

    private void reflesh(int mIndex, int mListIndex, String type) {
        if (type.equals("进行")) {
            mReadyQueue.get(mIndex).setState("进行");
            mReadyQueue.get(mIndex).setRunCPUtime(mReadyQueue.get(mIndex).getRunCPUtime() + 1);
            listRR.get(mListIndex).setState("进行");
            listRR.get(mListIndex).setRunCPUtime(listRR.get(mListIndex).getRunCPUtime() + 1);


        }
        if (type.equals("完成")) {
            mReadyQueue.get(mIndex).setState("完成");
            mReadyQueue.get(mIndex).setEndTime(time);
            listRR.get(mListIndex).setState("完成");
            listRR.get(mListIndex).setEndTime(time);
            mReadyQueue.remove(mIndex);
            checkProcess();

        }
        if (type.equals("时间片")) {

            mReadyQueue.get(mIndex).setState("就绪");
            listRR.get(mListIndex).setState("就绪");
            Process temp = mReadyQueue.get(mIndex);
            mReadyQueue.remove(mIndex);
            mReadyQueue.add(temp);
            checkProcess();
        }

        if (type.equals("阻塞")) {

            mBlockedQueue.add(mReadyQueue.get(index));
            mReadyQueue.remove(index);
            checkProcess();

        }

    }

    /**
     * void copyList()
     * 作用：1. 将所有进程加入到等待队列中
     * 背景：因为list 与adapter 绑定，在运行过程中进程顺序会改变，避免listView 顺序调整，故使用浅克隆，复制队列到等待队列
     **/
    private void copyList() {
        if(mLock) {
            mLock = false;
            for (Process p : listRR) {
                try {
                    mWaitQueue.add(p.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
            mLock = true;
        }
    }

    private void copyList(List<Process> dst, String type) {
        if (type.equals("W")) {
            if (mLock) {
                mLock = false;
                for (Process p : mWaitQueue)
                    try {
                        dst.add(p.clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                mLock = true;
            }
        }
        if (type.equals("B")) {
            for (Process p : mBlockedQueue)
                try {
                    dst.add(p.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
        }

    }

    public void update(Process p) {
        while (mLock) {
            mLock = false;
            mWaitQueue.add(p);
            Log.d("rr", "------" + p.getName());
        }
        mLock = true;
    }

    public void logPrint() {
        /////////////////////////////////////
        Log.d("rr", "队列--------------开始" + time + "----------------------------");
        Log.d("rr", "等待队列");
        for (Process p : mWaitQueue) {
            Log.d("rr", p.getName() + ":::" + p.getRunIOtime() + "::::" + p.getRunCPUtime() + "::::" +p.getStartTime());
        }

        Log.d("rr", "就绪队列");
        for (Process p : mReadyQueue) {
            Log.d("rr", p.getName() + ":::" + p.getRunIOtime() + "::::" + p.getRunCPUtime());
        }

        Log.d("rr", "阻塞队列");
        for (Process p : mBlockedQueue) {
            Log.d("rr", p.getName() + ":::" + p.getRunIOtime() + "::::" + p.getRunCPUtime());
        }

        Log.d("rr", "队列------------------------------------------");
        //////////////////////////////////////////////////////////////
    }

}
