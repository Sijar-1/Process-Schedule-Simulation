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


    private List<Process> mReadyQueue; //就绪队列，存放“待运行的进程
    private List<Process> mBlockeddQueue; //存放“到达时间未到的进程”
    private List<Process> mWaitQueue;



    // 抽象方法：启动线程
    @Override
    public void startThread(LinkedList<Process> l) {
        startThread(l, 1);
    }


    // 抽象方法：有时间片的启动线程
    @Override
    public void startThread(List<Process> l, final int slotSize) {
        listRR = l;
        index = 0;
        time = 0;   //已运行时间
        lock = true;
        isRunning = false;
        slotTimer = 0;

        listIndex = 0;
        mReadyQueue = new ArrayList<Process>();
        mBlockeddQueue = new ArrayList<Process>();
        mWaitQueue = new ArrayList<Process>();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InitRR(); // 初始化-----根据进程开始时间排序，只在最开始运行一次
                isRunning = true;

                while (lock) {
                    // 检查是否阻塞线程
                    pauseThread();
                    block(); //时间+1




                    if (mReadyQueue.size() != 0) {

                        listIndex = find(index); //实际list 表格中的序号
                        reflesh(index, listIndex, "进行");
                        slotTimer++;


                        //如果到了IO请求，阻塞
                        if (mReadyQueue.get(index).getIOstartTime() == mReadyQueue.get(index).getRunCPUtime()) {

                            reflesh(index, listIndex, "阻塞");

                        } else {
                            // 如果达到需要时间，则设置进程状态为完成
                            if (mReadyQueue.get(index).getRunCPUtime()+mReadyQueue.get(index).getIOtime() >= mReadyQueue.get(index).getCPUTime()) {

                                reflesh(index, listIndex, "完成");
                            } else {
                                // 如果到了时间片，则切换进程
                                if (slotTimer >= slotSize) {
                                    reflesh(index, listIndex, "时间片");

                                }
                            }

                        }


                    }

                    time++;  //已运行时间加1


                    /////////////////////////////////////
                    Log.d("rr", "队列--------------开始"+time+"----------------------------");
                    Log.d("rr", "等待队列");
                    for (Process p : mWaitQueue) {
                        Log.d("rr", p.getName()+":::"+p.getRunIOtime()+"::::"+p.getRunCPUtime());
                    }

                    Log.d("rr", "就绪队列");
                    for (Process p : mReadyQueue) {
                        Log.d("rr", p.getName()+":::"+p.getRunIOtime()+"::::"+p.getRunCPUtime());
                    }

                    Log.d("rr", "阻塞队列");
                    for (Process p : mBlockeddQueue) {
                        Log.d("rr", p.getName()+":::"+p.getRunIOtime()+"::::"+p.getRunCPUtime());
                    }

                    Log.d("rr", "队列------------------------------------------");
                    //////////////////////////////////////////////////////////////

                    addReady();


                    handler.sendEmptyMessage(time); //已运行时间——+1

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
        if (mReadyQueue.size() == 0 && mBlockeddQueue.size() == 0 && mWaitQueue.size() == 0) {
            lock = false;
            // time = 0;
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

    private int find(int mindex) {
        int mlistIndex = 0;
        for (int i = 0; i < listRR.size(); i++) {
            if (listRR.get(i).getName() == this.mReadyQueue.get(mindex).getName()) {
                mlistIndex = i;
            }
        }
        return mlistIndex;
    }

    private int find2(int mindex) {
        int mlistIndex = 0;
        for (int i = 0; i < listRR.size(); i++) {
            if (listRR.get(i).getName() == this.mBlockeddQueue.get(mindex).getName()) {
                mlistIndex = i;
            }
        }
        return mlistIndex;

    }

    private  int findp(Process p){

        int tmp=0;
        for(Process q:listRR){
            if (q.getName()==p.getName()){
                tmp=listRR.indexOf(q);
                break;
            }
        }

        return tmp;
    }


    //初始化-----根据进程开始时间排序
    private void InitRR() {
        Collections.sort(listRR, new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                return o1.getStartTime() - o2.getStartTime();
            }
        });

        copyList();
        initAdd();
    }

    private void initAdd() {
        List<Process> waitL = new ArrayList<Process>();

        copyTmp(waitL,0);
        for (Process p: waitL) {
            if (p.getStartTime() == 0) {
                p.setState("就绪");
                mReadyQueue.add(p);
                listRR.get(findp(p)).setState("就绪");
                mWaitQueue.remove(p);
            }
        }
    }


    private void addReady() {

        List<Process> waitL = new ArrayList<Process>();
        List<Process> blockL = new ArrayList<Process>();

        copyTmp(waitL,0);
        copyTmp(blockL,1);


        for (Process p: waitL) {
            if (p.getStartTime() == time) {
                p.setState("就绪");
                mReadyQueue.add(p);
                listRR.get(findp(p)).setState("就绪");
                mWaitQueue.remove(p);
            }
        }

        for (Process p: blockL) {
            if (p.getIOtime() == p.getRunIOtime()) {
                p.setState("就绪");
                mReadyQueue.add(p);
                listRR.get(findp(p)).setState("就绪");
                mBlockeddQueue.remove(p);
            }
        }
    }


    //阻塞

    private void block() {
        int nIndex = 0;
        for (int i = 0; i < mBlockeddQueue.size(); i++) {
            mBlockeddQueue.get(i).setRunIOtime(mBlockeddQueue.get(i).getRunIOtime() + 1);
            mBlockeddQueue.get(i).setState("阻塞");
            nIndex = find2(i);
            listRR.get(nIndex).setRunIOtime(listRR.get(nIndex).getRunIOtime() + 1);
            if (i == mBlockeddQueue.size() - 1) {
                nIndex = find2(i);
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
            // 寻找下一个运行进程
            checkProcess();

        }
        if (type.equals("时间片")) {
            Log.d("rr", "时间片");
            mReadyQueue.get(mIndex).setState("就绪");
            listRR.get(mListIndex).setState("就绪");
            Process temp = mReadyQueue.get(mIndex);
            mReadyQueue.remove(mIndex);
            mReadyQueue.add(temp);
            checkProcess();
        }

        if (type.equals("阻塞")) {
            Log.d("rr", "阻塞");
            mBlockeddQueue.add(mReadyQueue.get(index));
            mReadyQueue.remove(index);
            checkProcess();

        }

    }


    // 备份数据
    private void copyList() {
        for (Process p : listRR) {
            try {
                mWaitQueue.add(p.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyTmp(List<Process> dst,int type) {
        if(type==0){
            for (Process p : mWaitQueue)
                try {
                    dst.add(p.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
        }
        else{
            for (Process p : mBlockeddQueue)
                try {
                    dst.add(p.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
        }

    }

    public void update(Process p){
        mWaitQueue.add(p);
        Log.d("rr","------"+p.getName());
    }

}
