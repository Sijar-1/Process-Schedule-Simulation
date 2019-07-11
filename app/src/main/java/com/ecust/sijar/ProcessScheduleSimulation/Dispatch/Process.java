package com.ecust.sijar.ProcessScheduleSimulation.Dispatch;

import java.util.LinkedList;
/**
 * Created by Sijar on 2019/7/3.
 * PCB信息对象类
 */
public class Process implements Cloneable{

    private String name="未命名";    //进程标识符  RR
    private int priority=10;          //进程优先数
    private int round=1;             //进程轮转时间片
    private int runTime=0;           //进程运行时间（动态的）  RR
    private int CPUTime=1;           //进程占用CPU时间（不变的） RR
    private int IOstartTime=0;         //I/O相对CPU开始时间   RR
    private int IOtime=0;            //I/O持续时间  RR
    private int count=0;            //计数器
    private int runCPUtime=0;          //进程运行了的CPU时间    RR
    private int runIOtime=0;          //运行IO时间  RR新增
    private String state="等待";       //进程状态   RR   sijar
    private int startTime = 0;      //进程开始时间  RR
    private int endTime;         //进程结束时间  ，可以不要  RR
    private int realstartTime;        //真正开始运行时间

/**
 *
 *
    //先来先服务算法创建进程
    public Process(String name,int startTime,int  CPUTime,int IOstartTime,int IOtime){
        this.name = name;
        this.startTime=startTime;
        this.CPUTime = CPUTime;
        this.IOstartTime=IOstartTime;
        this.IOtime=IOtime;
    }

 **/
    //时间片轮转算法-----重载
    public Process(String name,int startTime, int processTime,int IOstart,int IOlast){
        this.name = name;
        this.startTime = startTime;
        this.CPUTime = processTime;
        this.IOstartTime = IOstart;
        this.IOtime = IOlast;
        this.state = "等待";
    }


    //优先级算法创建进程
    public Process(int priority,String name,int startTime,int  CPUTime,int IOstartTime,int IOtime){
        this.name = name;
        this.priority = priority;
        this.startTime=startTime;
        this.CPUTime = CPUTime;
        this.IOstartTime=IOstartTime;
        this.IOtime=IOtime;
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

    public void setRound(int round) {
        this.round = round;
    }

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

    public int getRunCPUtime() {
        return runCPUtime;
    }

    public void setRunCPUtime(int runCPUtime) {
        this.runCPUtime = runCPUtime;
    }

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

    public int getRealstartTime() {
        return realstartTime;
    }

    public void setRealstartTime(int realstartTime) {
        this.realstartTime = realstartTime;
    }

    @Override
    public Process clone() throws CloneNotSupportedException {
        try {
            Process p = (Process) super.clone();
            return p;
        }catch (Exception e){

        }
        return null;
    }

    public int getRunIOtime() {
        return runIOtime;
    }

    public void setRunIOtime(int runIOtime) {
        this.runIOtime = runIOtime;
    }
}
