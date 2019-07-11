package com.ecust.sijar.ProcessScheduleSimulation.Dispatch;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
    private LinkedList<Process> readyList=new LinkedList<Process>();
    // 按优先级排列的进程就绪队列
    private LinkedList<Process> processReadyList=new LinkedList<Process>();
    //存放未到达开始时间的添加的进程队列
    private LinkedList<Process> waitList=new LinkedList<Process>();
    //存放等待IO用完的进程队列
    private LinkedList<Process> blockedList=new LinkedList<Process>();
    private  LinkedList<Process> waitL;
    private  LinkedList<Process> blockedL;
    private  LinkedList<Process> readyL;
    //抽象方法：启动线程,写入当前总运行时间
    @Override
    public void startThread(LinkedList<Process> l ) {
        list = l;
        index = 0;
        time = 0;  //总体已运行时间
        lock = true;
        isRunning = false;
     //   processReadyList = new LinkedList<Process>();
      //  readyList=new LinkedList<Process>();
     //   waitList=new LinkedList<Process>();
       // blockedList=new LinkedList<Process>();
        listIndex=0;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 初始化优先级进程就绪列表,只在最开始运行一次
                initPriorityList1();
                Log.d("rr", "initPriorityList1之后队列--------------开始"+time+"-----------");
                Log.d("pri", "等待队列");
                for (Process p : waitList) {
                    Log.d("rr", p.getName()+" starttime="+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+"::runiotime="+p.getRunIOtime()+":::runcputime:="+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                }

                Log.d("rr", "就绪队列");
                for (Process p : processReadyList) {
                    Log.d("rr", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime"+p.getRunIOtime()+"::::runcputime"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                }

                Log.d("rr", "阻塞队列");
                for (Process p : blockedList) {
                    Log.d("rr", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime"+p.getRunIOtime()+":::runcputime"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                }
                Log.d("rr", "原始队列");
                for (Process p : list) {
                    Log.d("pri", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime="+p.getRunIOtime()+":::runcputime:"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                }
                Log.d("rr", "initPriorityList1之后队列---------------------结束---------");
                isRunning = true;
                while (lock) {//循环，改变优先级顺序的就绪队列第一个进程的状态
                    pauseThread();// 检查是否阻塞（点击暂停按钮suspend改变此函数被阻塞）
                    block();    //把阻塞队列里的进程的状态设置为阻塞,IOruntime+1
                    Log.d("rr", "block之后队列--------------开始"+time+"-----------");
                    Log.d("pri", "等待队列");
                    for (Process p : waitList) {
                        Log.d("rr", p.getName()+" starttime="+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+"::runiotime="+p.getRunIOtime()+":::runcputime:="+p.getRunCPUtime()+" state="+p.getState());
                    }

                    Log.d("rr", "就绪队列");
                    for (Process p : processReadyList) {
                        Log.d("rr", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime"+p.getRunIOtime()+"::::runcputime"+p.getRunCPUtime()+" state="+p.getState());
                    }

                    Log.d("rr", "阻塞队列");
                    for (Process p : blockedList) {
                        Log.d("rr", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime"+p.getRunIOtime()+":::runcputime"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                    }
                    Log.d("rr", "原始队列");
                    for (Process p : list) {
                        Log.d("pri", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime="+p.getRunIOtime()+":::runcputime:"+p.getRunCPUtime()+" state="+p.getState());
                    }
                    Log.d("rr", "block之后队列---------------------结束---------");

                    if(processReadyList.size()!=0){
                        listIndex=find(index);//listIndex为对应的原始list里的index,index=0
                           changeState(index,listIndex,"进行");
                        Log.d("rr", "判断进行之后队列--------------开始"+time+"-----------");
                        Log.d("pri", "等待队列");
                        for (Process p : waitList) {
                            Log.d("rr", p.getName()+" starttime="+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+"::runiotime="+p.getRunIOtime()+":::runcputime:="+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                        }

                        Log.d("rr", "优先级就绪队列");
                        for (Process p : processReadyList) {
                            Log.d("rr", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime"+p.getRunIOtime()+"::::runcputime"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                        }

                        Log.d("rr", "阻塞队列");
                        for (Process p : blockedList) {
                            Log.d("rr", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime"+p.getRunIOtime()+":::runcputime"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                        }
                        Log.d("rr", "原始队列");
                        for (Process p : list) {
                            Log.d("pri", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime="+p.getRunIOtime()+":::runcputime:"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                        }
                        Log.d("rr", "判断进行之后队列---------------------结束---------");
                        //如果到了IO请求，阻塞
                        if (processReadyList.get(index).getIOstartTime() == processReadyList.get(index).getRunCPUtime()&&processReadyList.get(index).getIOtime()!=0) {
                            changeState(index, listIndex, "阻塞");
                        } else
                            if (processReadyList.get(index).getRunCPUtime()== processReadyList.get(index).getCPUTime()) {
                                changeState(index, listIndex, "完成");
                            }

                    }//if（processReadyList.size()!=0)  -end
                    time++;//已运行时间加一
                    Log.d("rr", "队列----判断完阻塞和完成了----------开始"+time+"---------------------------------------------------------------------------------------");
                    Log.d("pri", "等待队列");
                    for (Process p : waitList) {
                        Log.d("rr", p.getName()+" starttime="+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+"::runiotime="+p.getRunIOtime()+":::runcputime:="+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                    }

                    Log.d("rr", "优先级就绪队列");
                    for (Process p : processReadyList) {
                        Log.d("rr", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime"+p.getRunIOtime()+"::::runcputime"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                    }

                    Log.d("rr", "阻塞队列");
                    for (Process p : blockedList) {
                        Log.d("rr", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime"+p.getRunIOtime()+":::runcputime"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                    }
                    Log.d("rr", "原始队列");
                    for (Process p : list) {
                        Log.d("pri", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime="+p.getRunIOtime()+":::runcputime:"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                    }
                    Log.d("rr", "队列----------------------结束-----------------------------------------------------------------------------------");

                    addReadyBlock();//更新，已到开始时间的都加到就绪队列里，IO完成后的也加到就绪队列里

                    Log.d("rr", "队列----更新了addreadyblock---------开始"+time+"---------------------------------------------------------------------------------------");
                    Log.d("pri", "等待队列");
                    for (Process p : waitList) {
                        Log.d("rr", p.getName()+" starttime="+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+"::runiotime="+p.getRunIOtime()+":::runcputime:="+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                    }

                    Log.d("rr", "优先级就绪队列");
                    for (Process p : processReadyList) {
                        Log.d("rr", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime"+p.getRunIOtime()+"::::runcputime"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                    }

                    Log.d("rr", "阻塞队列");
                    for (Process p : blockedList) {
                        Log.d("rr", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime"+p.getRunIOtime()+":::runcputime"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                    }
                    Log.d("rr", "原始队列");
                    for (Process p : list) {
                        Log.d("pri", p.getName()+" start"+p.getStartTime()+" iotime="+p.getIOtime()+" runtime="+p.getRunTime()+":::runiotime="+p.getRunIOtime()+":::runcputime:"+p.getRunCPUtime()+" state="+p.getState()+" processrunning="+p.getProcessrunning());
                    }
                    Log.d("rr", "队列----更新了addreadyblock'------------------结束--------------------------------------------------");

                    handler.sendEmptyMessage(time);
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
    }//startThread()-end

    //抽象方法：时间片启动线程,不用管
    @Override
   public void startThread(List<Process> l , int slot) {
        startThread(l,1);
    }
//初始化，得到等待队列，就绪队列，优先级排序就绪队列，阻塞队列
    private void initPriorityList1() {
        //按开始时间给原始list排序
        Collections.sort(list, new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                //按开始时间正序排序
                return o1.getStartTime() - o2.getStartTime();
            }
        });
        copyList1();
        initReady();//初始化就绪队列，阻塞队列
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
    private void initReady() { //开始时间到的都加入就绪队列，IO开始的加入阻塞队列
        waitL = new LinkedList<Process>();
        readyL=new LinkedList<Process>();
        copyTmp(waitL,0);
        for (Process p: waitL) {
            if (p.getStartTime() <= time) {
                p.setState("就绪");
                readyList.add(p);
                list.get(findp(p)).setState("就绪");
                waitList.remove(findwait(p));
            }
        }
        copyTmp(readyL,2);
        for(Process p:readyL){
            if(p.getIOstartTime()<=p.getRunCPUtime()&&p.getIOtime()!=0){
                p.setState("阻塞");
                blockedList.add(p);
                list.get(findp(p)).setState("阻塞");
                readyList.remove(findready(p));
            }
        }
    }
    //把阻塞队列里的进程的状态设置为阻塞
    private void block() {
        int nIndex = 0;
        for (int i = 0; i < blockedList.size(); i++) {
            blockedList.get(i).setRunIOtime(blockedList.get(i).getRunIOtime() + 1);
            blockedList.get(i).setRunTime(blockedList.get(i).getRunTime()+1);
            blockedList.get(i).setState("阻塞");
            nIndex = find2(i);
            list.get(nIndex).setRunIOtime(list.get(nIndex).getRunIOtime() + 1);
            list.get(nIndex).setRunTime(list.get(nIndex).getRunTime()+1);
            if (i == blockedList.size() - 1) { //因为每一秒都会运行block，所以只用把新加入的对应的list的状态变为阻塞
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

//找到原始队列中最终对应的真正的index
    private int find(int mindex) {
        int mlistIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName() == this.processReadyList.get(mindex).getName()) {
                mlistIndex = i;
            }
        }
        return mlistIndex;
    }
    private int findwait(Process p) {
        int mlistIndex = 0;
        for (int i = 0; i < waitList.size(); i++) {
            if (waitList.get(i).getName() == p.getName()) {
                mlistIndex = i;
            }
        }
        return mlistIndex;
    }
    private int findready(Process p) {
        int mlistIndex = 0;
        for (int i = 0; i < readyList.size(); i++) {
            if (readyList.get(i).getName() == p.getName()) {
                mlistIndex = i;
            }
        }
        return mlistIndex;
    }
    private int findblocked(Process p) {
        int mlistIndex = 0;
        for (int i = 0; i < blockedList.size(); i++) {
            if (blockedList.get(i).getName() == p.getName()) {
                mlistIndex = i;
            }
        }
        return mlistIndex;
    }

    private void changeState(int mIndex, int mListIndex, String type) {
        if (type.equals("进行")) {
            if(list.get(mListIndex).getProcessrunning()==0){
               //初次变进行便是真正开始运行时间
                processReadyList.get(mIndex).setRealStartTime(time);
                list.get(mListIndex).setRealStartTime((time));
                processReadyList.get(mIndex).setProcessrunning(1);
                list.get(mListIndex).setProcessrunning(1);
            }
                processReadyList.get(mIndex).setState("进行");
                processReadyList.get(mIndex).setRunCPUtime(processReadyList.get(mIndex).getRunCPUtime() + 1);
                processReadyList.get(mIndex).setRunTime(processReadyList.get(mIndex).getRunTime() + 1);
                list.get(mListIndex).setState("进行");
                list.get(mListIndex).setRunCPUtime(list.get(mListIndex).getRunCPUtime() + 1);
                list.get(mListIndex).setRunTime(list.get(mListIndex).getRunTime() + 1);

        }
        if (type.equals("完成")) {
            processReadyList.get(mIndex).setState("完成");
            processReadyList.get(mIndex).setEndTime(time+1);
            list.get(mListIndex).setState("完成");
            list.get(mListIndex).setEndTime(time+1);
            readyList.remove(findready(list.get(mListIndex)));
            processReadyList.remove(mIndex);
            // 判断是否存在进程
            checkProcess();

        }
        if (type.equals("阻塞")) {
         //   processReadyList.get(mIndex).setState("阻塞");
       //     processReadyList.get(mIndex).setRunIOtime(processReadyList.get(mIndex).getRunIOtime() + 1);
           // processReadyList.get(mIndex).setRunTime(processReadyList.get(mIndex).getRunTime() + 1);
          //  list.get(mListIndex).setState("阻塞");
            //list.get(mListIndex).setRunIOtime(list.get(mListIndex).getRunIOtime() + 1);
            //list.get(mListIndex).setRunTime(list.get(mListIndex).getRunTime() + 1);
            blockedList.add(processReadyList.get(mIndex));
            readyList.remove(findready(processReadyList.get(mIndex)));
            processReadyList.remove(mIndex);
            processReadyList.get(mIndex).setProcessrunning(1);
            list.get(mListIndex).setProcessrunning(1);
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
   public void InsertProcess(Process p,LinkedList<Process> plist){

       list=plist;
        //按开始时间给原始list排序
        Collections.sort(list, new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                //按开始时间正序排序
                return o1.getStartTime() - o2.getStartTime();
            }
        });
        waitList=list;
      //  readyList=new LinkedList<Process>();
       // copyList1();
        initReady();//初始化就绪队列，阻塞队列
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

    // 阻塞线程
    public void pauseThread() {
        if (suspend) {//如果线程挂起暂停了
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


    //更新，已到开始时间的都加到就绪队列里，IO完成后的也加到就绪队列里
    private void addReadyBlock(){
       waitL=new LinkedList<Process>();
       blockedL=new LinkedList<Process>();
       copyTmp(waitL,0);
       copyTmp(blockedL,1);
        //迭代器 
        for (Process p: waitL) {
            if (p.getStartTime() == time) {
                p.setState("就绪");
                readyList.add(p);
                list.get(findp(p)).setState("就绪");
                if(processReadyList.get(0).getState()=="进行"&&p.getPriority() < processReadyList.get(0).getPriority()){
                    processReadyList.add(1, p);
                }else{
                    for (int j = processReadyList.size() - 1; j >= 0; j--) {
                        if (p.getPriority() < processReadyList.get(0).getPriority()) {
                            processReadyList.add(0, p);break;
                        }
                        if (p.getPriority() >= processReadyList.get(j).getPriority()) {
                            processReadyList.add(j + 1, p);break;
                        }
                    }
                }
                waitList.remove(findwait(p));
            }

               /* for (int j = processReadyList.size() - 1; j >= 0; j--) {
                    if (p.getPriority() < processReadyList.get(0).getPriority()) {
                        processReadyList.add(0, p);
                    }
                    if (p.getPriority() >= processReadyList.get(j).getPriority()) {
                        processReadyList.add(j + 1, p);
                    }
                }*/

        }
        for (Process p: blockedL) {
            if (p.getIOtime() < p.getRunIOtime()) {
                p.setState("就绪");
                readyList.add(p);
                list.get(findp(p)).setState("就绪");
                //加入优先级排序的就绪队列
                if(processReadyList.get(0).getState()=="进行"&&p.getPriority() < processReadyList.get(0).getPriority()){
                    processReadyList.add(1, p);
                }else{
                    for (int j = processReadyList.size() - 1; j >= 0; j--) {
                        if (p.getPriority() < processReadyList.get(0).getPriority()) {
                            processReadyList.add(0, p);break;
                        }
                        if (p.getPriority() >= processReadyList.get(j).getPriority()) {
                            processReadyList.add(j + 1, p);break;
                        }
                    }
                }
            blockedList.remove(findblocked(p));
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
        else if(type==1){
            for (Process p : blockedList)
                try {
                    dst.add(p.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
        }
        else{
            for(Process p:readyList)
                try{
                    dst.add(p.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
        }

    }
    // 判断是否存在进程
    private void checkProcess() {
        if (processReadyList.size() == 0 && blockedList.size() == 0 && waitList.size() == 0) {
            lock = false;
        }
    }



}
