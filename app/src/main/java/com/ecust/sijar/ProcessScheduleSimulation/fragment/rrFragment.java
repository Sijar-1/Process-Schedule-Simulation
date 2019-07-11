
package com.ecust.sijar.ProcessScheduleSimulation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;


import com.ecust.sijar.ProcessScheduleSimulation.Dispatch.DispatchListener;
import com.ecust.sijar.ProcessScheduleSimulation.Dispatch.Process;
import com.ecust.sijar.ProcessScheduleSimulation.Dispatch.ProcessAdapterRR;
import com.ecust.sijar.ProcessScheduleSimulation.Dispatch.RRDispatch;
import com.ecust.sijar.ProcessScheduleSimulation.Listener.fromMainToRR;
import com.ecust.sijar.ProcessScheduleSimulation.Listener.rrFragmentListener;
import com.ecust.sijar.ProcessScheduleSimulation.R;


import java.util.ArrayList;
import java.util.List;


public class rrFragment extends androidx.fragment.app.Fragment implements fromMainToRR {

    private EditText etSlot;  //输入时间片长度
    private Button btnAdd;  //添加进程按钮
    private Button btnReset;  //重置按钮
    private Button btnStart;  //开始按钮
    private Button btnChart;  //btn_chart
    private TextView tvRuntime;  //已运行
    private int nowTime = 0;
    protected int mlock = 0;
    private RRDispatch dispatchMathod;
    private rrFragmentListener mrrFragmentListener;
    private ProcessAdapterRR processAdapterRR = null;
    private List<Process> processListRR = new ArrayList<Process>();
    private List<Process> copyListRR = new ArrayList<Process>();
    private Context mContext;


    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mContext = context;
            mrrFragmentListener = (rrFragmentListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rr, container, false);
        etSlot = (EditText) view.findViewById(R.id.et_slotrr);
        btnAdd = (Button) view.findViewById(R.id.btn_addrr);
        btnReset = (Button) view.findViewById(R.id.btn_resetrr);
        btnStart = (Button) view.findViewById(R.id.btn_startrr);
        ListView lvProcess = (ListView) view.findViewById(R.id.lv_process_rr);
        tvRuntime = (TextView) view.findViewById(R.id.tv_runtimerr);
        dispatchMathod = new RRDispatch();
        initData();
        processAdapterRR = new ProcessAdapterRR(this.getActivity(), processListRR);
        lvProcess.setAdapter(processAdapterRR);
        return view;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //添加进程
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mrrFragmentListener != null) {
                    // 添加进程时阻塞线程
                    if(dispatchMathod.isRunning()){
                        dispatchMathod.pause();
                    }
                    mrrFragmentListener.btnFunc("rr_add", "null");
                }
                Log.d("rr", "add.click");
            }

        });

        //重置  ----未完成
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 先终止线程
                dispatchMathod.stop();

                if (copyListRR.size() > 0) {
                    processListRR.clear();

                    for (Process p : copyListRR) {
                        processListRR.add(p);
                        Log.d("rr",p.getName());
                    }
                    Log.d("rr",processListRR.size()+"pro");
                    Log.d("rr",copyListRR.size()+"cop");
                   processAdapterRR.notifyDataSetChanged();
                    tvRuntime.setText("0 秒");
                       nowTime = 0;
                }
                mlock=0; //开始
                btnStart.setText("开始");
                etSlot.setFocusable(true);
                etSlot.requestFocus();
                etSlot.setFocusableInTouchMode(true);
                Log.d("rr", "reset.click");

            }
        });


        //开始
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 开始按钮 第一次执行 //在开始或者是充值后方可运行
                if (mlock==0) {
                    // 线程未启动时，执行方法体，避免创建多个进程
                    if (!dispatchMathod.isRunning()) {
                        Log.d("rr","开始");
                        // 先备份进程列表数据
                        copyList();
                        // 注册监听器
                        dispatchMathod.setDispatchListener(new DispatchListener() {
                            @Override
                            public void nowTime(int s) {
                                nowTime = s;
                                tvRuntime.setText(s + " 秒");  //已运行 时间片上
                                processAdapterRR.notifyDataSetChanged();
                            }
                        });

                        // 启动线程
                        int slot = checkSlot();
                        dispatchMathod.startThread(processListRR, slot);
                       // btnStart.setEnabled(false);
                        mlock=1;
                        btnStart.setText("暂停");
                        etSlot.setFocusable(false);
                        etSlot.setFocusableInTouchMode(false);
                    }


                }
                else
                    if(mlock==1){       //暂停，挂起程序
                        btnStart.setText("继续");
                        mlock=2;
                        // 阻塞线程
                        if(dispatchMathod.isRunning()){
                            dispatchMathod.pause();
                        }
                    }
                    else{     // 继续
                        btnStart.setText("暂停");
                        mlock=1;

                        // 如果线程还存在，则唤醒线程
                        if(dispatchMathod.isRunning()){
                            dispatchMathod.start();
                        }
                    }


            }
        });

    }


    // 开始时数据
    private void initData() {
        //process队列,以下为测试用
        Process p = new Process("a", 0, 10, 3, 6);
        processListRR.add(p);
        p = new Process("b", 4, 6, 2, 1);
        processListRR.add(p);
        p = new Process("c", 5, 5, 3, 2);
        processListRR.add(p);
        p = new Process("b1", 6, 7, 1, 2);
        processListRR.add(p);
        p = new Process("b2", 7, 3, 2, 1);
        processListRR.add(p);


    }

    @Override
    public void process(String name, int startTime, int CPUtime, int IOstart, int IOlast) {
        Process p = new Process(name, startTime, CPUtime, IOstart, IOlast);
        processListRR.add(p);
        processAdapterRR.notifyDataSetChanged();


        dispatchMathod.update(p);

        // 如果线程还存在，则唤醒线程
        if(dispatchMathod.isRunning()){
            dispatchMathod.start();
        }

        // 若已备份初始进程信息，插入新来的进程
        if(copyListRR.size()>0){
            try {
                copyListRR.add(p.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }


    private void copyList() {
        copyListRR.clear();
        for (Process p :processListRR) {
            try {
                copyListRR.add(p.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    // 检查时间片后，返回时间片大小
    private int checkSlot(){
        int slot = 0;
        if(!etSlot.getText().toString().equals("")){
            slot = Integer.valueOf(etSlot.getText().toString());

        }else{
            // 若不输入时间片大小，时间片调度算法时间片默认大小为2
            slot = 2;
        }
        return slot;
    }

}