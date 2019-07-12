package com.ecust.sijar.ProcessScheduleSimulation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ecust.sijar.ProcessScheduleSimulation.Dispatch.Process;
import com.ecust.sijar.ProcessScheduleSimulation.Dispatch.fcfsDispatch;
import com.ecust.sijar.ProcessScheduleSimulation.Dispatch.*;
import com.ecust.sijar.ProcessScheduleSimulation.R;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class fcfsFragment extends Fragment implements AdapterView.OnItemClickListener{


    private TextView tvRuntime = null;   //已运行
    private Button btnStart;   //开始按钮
    private Button btnAdd;
    private Button btnReset;
    private Button btnStop;
    private int nowTime = 0;
    private ProcessAdapter processAdapter = null;
    private List<Process> processList = new ArrayList<Process>();
    private List<Process> copyList = new ArrayList<Process>();
    private ProcessDispatch dispatchMathod = new fcfsDispatch();
    private    int flag=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fcfs, container, false);

        btnStart = (Button)view.findViewById(R.id.btn_startfcfs);
        btnAdd = (Button)view.findViewById(R.id.btn_addfcfs);
        btnReset = (Button) view.findViewById(R.id.btn_resetfcfs);
        btnStop =(Button) view.findViewById(R.id.btn_stopfcfs);
        ListView lvProcess = (ListView) view.findViewById(R.id.lv_processfcfs);
        tvRuntime = (TextView) view.findViewById(R.id.tv_allruntime);  //总运行时间

        initData();
        processAdapter = new ProcessAdapter(getActivity(),processList);
        lvProcess.setAdapter(processAdapter);
        lvProcess.setOnItemClickListener(this);

        return view;

    }// onCreateView-end


    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //setContentView(R.layout.activity_main);

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // 添加进程时阻塞线程
                if(dispatchMathod.isRunning()){
                    dispatchMathod.pause();
                }
                // MainActivity myActivity = new MainActivity();
                View root = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add,null);
                final EditText etDialogName = (EditText) root.findViewById(R.id.et_dialog_name);
                final EditText etDialogPriority = (EditText) root.findViewById(R.id.et_dialog_priority);
                final EditText etDialogNeedtime = (EditText) root.findViewById(R.id.et_dialog_needtime);
                Button btnDialogSure = (Button) root.findViewById(R.id.btn_dialog_sure);

                final AlertDialog addDialog = new AlertDialog.Builder(getActivity())
                        .setView(root)
                        .create();
                addDialog.show();
                btnDialogSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = etDialogName.getText().toString();

                        int priority = Integer.valueOf(etDialogPriority.getText().toString());
                        int needTime = Integer.valueOf(etDialogNeedtime.getText().toString());

                        Process process = new Process(name,priority,needTime,0);
                        process.setStartTime(nowTime);
                        processList.add(process);
                        processAdapter.notifyDataSetChanged();
                        addDialog.dismiss();

                        // 若已备份初始进程信息，插入新来的进程
                        if(copyList.size()>0){
                            try {
                                copyList.add(process.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }


                        // 如果线程还存在，则唤醒线程
                        if(dispatchMathod.isRunning()){
                            dispatchMathod.start();
                        }
                    }
                });

            }
        }
        );
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 线程未启动时，执行方法体，避免创建多个进程
                flag=1;
                if(!dispatchMathod.isRunning()) {
                    // 先备份进程列表数据
                    copyList();
                    // 注册监听器
                    dispatchMathod.setDispatchListener(new DispatchListener() {
                        @Override
                        public void nowTime(int s) {
                            nowTime = s;
                            tvRuntime.setText(s+" 秒");
                            processAdapter.notifyDataSetChanged();
                        }
                    });

                    // 启动线程
                //    int slot = 3;
       //             dispatchMathod.startThread(processList,slot);
                    dispatchMathod.startThread(processList);
                    btnStart.setEnabled(false);
                }
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // 先终止线程
                flag=0;
                dispatchMathod.stop();

                // 如果有备份，则恢复原始信息
                if(copyList.size()>0) {
                    processList.clear();
                    for (Process p : copyList) {
                        processList.add(p);
                        p.setRunTime(0);
                    }

                    processAdapter.notifyDataSetChanged();
                    tvRuntime.setText("0 秒");
                    nowTime = 0;
                    btnStart.setEnabled(true);
                }

            }
        });


        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(dispatchMathod.isRunning()){

                    if(flag==1){
                        flag=2;
                        dispatchMathod.pause();
                        btnStop.setText(flag);
                    }
                    else {
                        flag=1;
                        dispatchMathod.start();
                        btnStop.setText("暂停");
                    }

                }



            }
        });
    }



    // 开始时数据
    private void initData(){
        Process p = new Process("a",5,5,0);
        processList.add(p);
        p = new Process("b",3,5,0);
        processList.add(p);
        p = new Process("c",6,5,0);
        processList.add(p);
        p = new Process("d",1,3,0);
        processList.add(p);
        p = new Process("e",3,2,0);
        processList.add(p);
    }
    // 备份数据
    private void copyList(){
        copyList.clear();
        for(Process p:processList){
            try {
                copyList.add(p.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }




    // 监听删除检查操作
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        AlertDialog dialog = new AlertDialog.Builder(fcfsFragment.this.getActivity())
                .setTitle("删除提示")
                .setMessage("删除该进程？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        processList.remove(i);
                        processAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消",null)
                .create();
        dialog.show();
    }
}
