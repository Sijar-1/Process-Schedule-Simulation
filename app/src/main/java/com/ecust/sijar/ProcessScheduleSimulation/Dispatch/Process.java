package com.ecust.sijar.ProcessScheduleSimulation.Dispatch;

import java.util.List;
/**
 * Created by Sijar on 2019/7/3.
 * PCB信息对象类
 */
public class Process implements Cloneable{

    private String name="未命名";    //进程标识符
    private int priority=10;          //进程优先数
    private int round=1;             //进程轮转时间片
    private int runTime=0;           //进程运行时间（动态的）
    private int CPUTime=1;           //进程占用CPU时间（不变的）
    private int IOstartTime=0;         //I/O相对CPU开始时间
    private int IOtime=0;            //I/O持续时间
    private int count=0;            //计数器
    private int needTime=0;          //进程到完成还要的CPU时间（按课程设计表示的意思）
    private String state="就绪";       //进程状态
    private int startTime = 0;      //进程开始时间
    private int endTime = 0;         //进程结束时间  ，可以不要
    private List<Process> next;    //链指针

    public Process(String name,int priority,int needTime){
        this.name = name;
        this.priority = priority;
        this.needTime = needTime;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRound() {
        return round;
    }
    public void setRound(int round) { this.round = round; }

    public int getRunTime() {
        return runTime;
    }
    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public int getCPUTime() {
        return CPUTime;
    }

    public void setCPUTime(int CPUTime) {
        this.CPUTime = CPUTime;
    }

    public int getIOstartTime() {
        return IOstartTime;
    }

    public int getIOtime() {
        return IOtime;
    }

    public void setIOtime(int IOtime) {
        this.IOtime = IOtime;
    }

    public void setIOstartTime(int IOstartTime) {
        this.IOstartTime = IOstartTime;
    }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public int getNeedTime() { return needTime; }
    public void setNeedTime(int needTime) { this.needTime = needTime; }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public int getEndTime() {
        return endTime;
    }
    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getStartTime() {
        return startTime;
    }
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public List<Process> getNext() { return next; }
    public void setNext(List<Process> next) { this.next = next; }
    @Override
    protected Process clone() throws CloneNotSupportedException {
        try {
            Process p = (Process) super.clone();
            p.name = this.name;
            p.priority = this.priority;
            p.round=this.round;
            p.runTime = this.runTime;
            p.CPUTime=this.CPUTime;
            p.IOstartTime=this.IOstartTime;
            p.IOtime=this.IOtime;
            p.count=this.count;
            p.needTime = this.needTime;
            p.state = this.state;
            p.startTime = this.startTime;
            p.endTime = this.endTime;
            p.next=this.next;
            return p;
        }catch (Exception e){

        }
        return null;
    }
}
