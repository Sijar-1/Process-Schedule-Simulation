package com.ecust.sijar.ProcessScheduleSimulation.Dispatch;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

//import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Sijar on 2019/7/3.
 * 优先级检查调度算法
 */
public class PriorityDispatch extends ProcessDispatch {
    private int listIndex;
    //就绪队列
    private LinkedList<Process> readyList;
    // 按优先级排列的进程就绪队列
    private LinkedList<Process> processReadyList;
    //存放未到达开始时间的添加的进程队列
    private LinkedList<Process> waitList;
    //存放等待IO用完的进程队列
    private LinkedList<Process> blockedList;
    //抽象方法：启动线程,写入当前总运行时间
    @Override
    public void startThread(LinkedList<Process> l ) {
        list = l;
        index = 0;
        time = 0;  //总体已运行时间
        lock = true;
        isRunning = false;

        listIndex=0;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                isRunning = true;
                while (lock) {
                    // 检查是否阻塞
                    pauseThread();
                    // 初始化优先级进程就绪列表,只在最开始运行一次
                    initPriorityList1();
                    block();
                    time++;//已运行时间加一
                    if(processReadyList.size()!=0){
                        for(int i=0;i<processReadyList.size();i++){
                            Log.d("processReadyList all:",processReadyList.get(i).getName()+" (name) start= "
                                    +processReadyList.get(i).getStartTime()+" runCPUT="+processReadyList.get(i).getRunCPUtime()+
                                    " cputime="+processReadyList.get(i).getCPUTime()+"index="+index+"i="+i);
                        }
                        listIndex=find(index);
                        changeState(index,listIndex,"进行");
                        //如果到了IO请求，阻塞
                        Log.d("processReady index",processReadyList.get(index).getName()+"  "
                                +processReadyList.get(index).getStartTime()+" runCPUT "+processReadyList.get(index).getRunCPUtime()+
                                " cput="+processReadyList.get(index).getCPUTime()+"state="+processReadyList.get(index).getState()+"index="+index+"listIndex="+listIndex);
                        if (processReadyList.get(index).getIOstartTime() == processReadyList.get(index).getRunCPUtime()&&processReadyList.get(index).getIOtime()!=0) {
                            changeState(index, listIndex, "阻塞");
                        } else
                            if (processReadyList.get(index).getRunCPUtime()>= processReadyList.get(index).getCPUTime()) {
                                changeState(index, listIndex, "完成");
                            }
                    }
                    handler.sendEmptyMessage(time);
                    addReadyBlock();

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
//初始化，按优先级给就绪队列排序
    private void initPriorityList1() {
        processReadyList = new LinkedList<Process>();
        readyList=new LinkedList<Process>();
        waitList=new LinkedList<Process>();
        blockedList=new LinkedList<Process>();
        initWaitReady(); //初始化等待和就绪队列
        for(int i=0;i<readyList.size();i++){
            Log.d("pri --1",readyList.get(i).getName()+"  ,,"+readyList.get(i).getStartTime());
        }
        processReadyList.add(readyList.get(0));
        // 从列表尾向头遍历，按优先级大小，选择合适的位置插入
        for (int i = 1; i < readyList.size(); i++) {
            // 若优先级小于列表的第一个检查，则排在第一位，抢占资源  （优先级越小越重要）
            for (int j = processReadyList.size() - 1; j >= 0; j--) {
                if (readyList.get(i).getPriority() < processReadyList.get(0).getPriority()) {
                    processReadyList.add(0, readyList.get(i));
                    break;
                }
                // 若优先级大于或等于当前进程的优先级，则排在当前进程的后面
                if (readyList.get(i).getPriority() >= processReadyList.get(j).getPriority()) {
                    processReadyList.add(j + 1, readyList.get(i));
                    break;
                }
            }
        }
    }
    //初始化，已到开始时间的都加到就绪队列里，未到的都加到等待队列里
    private void initWaitReady(){
        Collections.sort(list, new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                //按开始时间正序排序
                return o1.getStartTime() - o2.getStartTime();
            }
        });
        copyList1();
        initReady();//初始化就绪队列
    }
    //备份数据,且全加入等待队列
    private void copyList1() {
        for (Process p : list) {
            try {
                waitList.add(p.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }
    private void initReady() {  //初始化就绪队列，把开始时间到了的都加入进去
        List<Process> waitL = new LinkedList<Process>();
        copyTmp(waitL,0);
        for (Process p: waitL) {
            if (p.getStartTime() <= time) {
                p.setState("就绪");
                readyList.add(p);
                list.get(findp(p)).setState("就绪");
                waitList.remove(p);
            }
        }
    }
    private void block() {
        int nIndex = 0;
        for (int i = 0; i < blockedList.size(); i++) {
            blockedList.get(i).setRunIOtime(blockedList.get(i).getRunIOtime() + 1);
            blockedList.get(i).setState("阻塞");
            nIndex = find2(i);
            list.get(nIndex).setRunIOtime(list.get(nIndex).getRunIOtime() + 1);
            if (i == blockedList.size() - 1) {
                nIndex = find2(i);
                list.get(nIndex).setState("阻塞");
            }
        }

    }
    //阻塞列表序列对应的list的真正index
    private int find2(int mindex) {
        int mlistIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName() == this.blockedList.get(mindex).getName()) {
                mlistIndex = i;
            }
        }
        return mlistIndex;

    }
    private void PriorityList(Process p) {
        handler.sendEmptyMessage(time); //已运行时间——+1
        initWaitReady(); //等待和就绪队列
        // 为新的进程选择合适的队列位置插入
        for (int j = processReadyList.size() - 1; j >= 0; j--) {
            if (p.getPriority() < processReadyList.get(0).getPriority()) {
                processReadyList.add(0, p);
            }
            if (p.getPriority() >= processReadyList.get(j).getPriority()) {
                processReadyList.add(j + 1, p);
            }
        }
    }
//找到队列中最终真正的index
    private int find(int mindex) {
        int mlistIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName() == this.processReadyList.get(mindex).getName()) {
                mlistIndex = i;
            }
        }
        return mlistIndex;
    }


    private void changeState(int mIndex, int mListIndex, String type) {
        if (type.equals("进行")) {
            processReadyList.get(mIndex).setState("进行");
            processReadyList.get(mIndex).setRunCPUtime(processReadyList.get(mIndex).getRunCPUtime() + 1);
            processReadyList.get(mIndex).setRunTime(processReadyList.get(mIndex).getRunTime() + 1);
            list.get(mListIndex).setState("进行");
            for(int i=0;i<list.size();i++){
                Log.d("list all:name=",list.get(i).getName()+"runcputimr="+list.get(i).getRunCPUtime()+"cputtime="+list.get(i).getCPUTime()+"state="+list.get(i).getState());
            }

            list.get(mListIndex).setRunCPUtime(list.get(mListIndex).getRunCPUtime() + 1);
            list.get(mListIndex).setRunTime(list.get(mListIndex).getRunTime() + 1);
        }
        if (type.equals("完成")) {
            processReadyList.get(mIndex).setState("完成");
            processReadyList.get(mIndex).setEndTime(time);
            list.get(mListIndex).setState("完成");
            list.get(mListIndex).setEndTime(time);
            processReadyList.remove(mIndex);
            readyList.remove(mIndex);
            // 寻找下一个运行进程
            checkProcess();

        }
        if (type.equals("阻塞")) {
            processReadyList.get(mIndex).setState("阻塞");
            processReadyList.get(mIndex).setRunIOtime(processReadyList.get(mIndex).getRunIOtime() + 1);
            processReadyList.get(mIndex).setRunTime(processReadyList.get(mIndex).getRunTime() + 1);
            list.get(mListIndex).setState("阻塞");
            list.get(mIndex).setRunIOtime(list.get(mIndex).getRunIOtime() + 1);
            list.get(mIndex).setRunTime(list.get(mIndex).getRunTime() + 1);
            blockedList.add(processReadyList.get(mIndex));
            processReadyList.remove(mIndex);
            readyList.remove(mIndex);
            checkProcess();
        }

    }

    // 动态插入新的进程
    @Override
  /* public void InsertProcess(Process p) {
        // 为新的进程选择合适的队列位置插入
        for (int j = processReadyList.size() - 1; j >= 0; j--) {
            if (p.getPriority() < processReadyList.get(0).getPriority()) {
                processReadyList.add(0, p);
            }
            if (p.getPriority() >= processReadyList.get(j).getPriority()) {
                processReadyList.add(j + 1, p);
            }
        }
    }*/
   public void InsertProcess(Process p){
       waitList.add(p);
       PriorityList(p);
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
     //异步消息处理，一旦time改变，通过handler消息处理更新函数中nowtime，触发回调函数，更新已运行时间
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            listener.nowTime(msg.what);
        }
    };


    //更新，已到开始时间的都加到就绪队列里，未到的都加到等待队列里
    private void addReadyBlock(){
        LinkedList<Process> waitL=new LinkedList<Process>();
        LinkedList<Process> blockedL=new LinkedList<Process>();
        copyTmp(waitL,0);
        copyTmp(blockedL,1);
        //迭代器
        for (Process p: waitL) {
            if (p.getStartTime() <= time) {
                p.setState("就绪");
                readyList.add(p);
                list.get(findp(p)).setState("就绪");
                for (int j = processReadyList.size() - 1; j >= 0; j--) {
                    if (p.getPriority() < processReadyList.get(0).getPriority()) {
                        processReadyList.add(0, p);
                    }
                    if (p.getPriority() >= processReadyList.get(j).getPriority()) {
                        processReadyList.add(j + 1, p);
                    }
                }
                waitList.remove(p);
            }
        }
        for (Process p: blockedL) {
            if (p.getIOtime() == p.getRunIOtime()) {
                p.setState("就绪");
                readyList.add(p);
                list.get(findp(p)).setState("就绪");
                for (int j = processReadyList.size() - 1; j >= 0; j--) {
                    if (p.getPriority() < processReadyList.get(0).getPriority()) {
                        processReadyList.add(0, p);
                    }
                    if (p.getPriority() >= processReadyList.get(j).getPriority()) {
                        processReadyList.add(j + 1, p);
                    }
                }
                blockedList.remove(p);
            }
        }


    }


    private void copyTmp(List<Process> dst,int type) {
        if(type==0){
            for (Process p : waitList)
                try {
                    dst.add(p.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
        }
        else{
            for (Process p : blockedList)
                try {
                    dst.add(p.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
        }

    }
    // 寻找进程列表中的就绪进程
    private void checkProcess() {
        if (processReadyList.size() == 0 && blockedList.size() == 0 && waitList.size() == 0) {
            lock = false;
        }
       // index++;
    }
    private  int findp(Process p){

        int tmp=0;
        for(Process q:list){
            if (q.getName()==p.getName()){
                tmp=list.indexOf(q);
                break;
            }
        }

        return tmp;
    }


}
