package com.ecust.sijar.ProcessScheduleSimulation.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ecust.sijar.ProcessScheduleSimulation.Dispatch.Process;
import com.ecust.sijar.ProcessScheduleSimulation.Dispatch.*;
import com.ecust.sijar.ProcessScheduleSimulation.R;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

public class priFragment extends Fragment implements AdapterView.OnItemClickListener{

    private EditText etSlot = null;
    private TextView tvRuntime = null;   //已运行
    private Button button_start_timer;   //开始按钮
    private Button btn_add_pri; //添加进程按钮
    private Button btnReset;   //重置按钮
    private int nowTime = 0;
    protected int mlock = 0;   //用于判断是不是第一次开始
    private ProcessAdapterPri processAdapter = null;
    private ProcessDispatch dispatchMathod;
    private LinkedList<Process> processList = new LinkedList<Process>();
    private LinkedList<Process> copyList = new LinkedList<Process>();

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

    }*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.priority, container, false);
        button_start_timer = (Button)view.findViewById(R.id.btn_startpri);
        btn_add_pri = (Button)view.findViewById(R.id.btn_addpri);
        btnReset = (Button) view.findViewById(R.id.btn_resetpri);
        ListView lvProcess = (ListView) view.findViewById(R.id.lv_processpri);
        tvRuntime = (TextView) view.findViewById(R.id.tv_allruntimepri);  //总运行时间
        dispatchMathod = new PriorityDispatch();
        initData();
        processAdapter = new ProcessAdapterPri(priFragment.this.getActivity(),processList);
        lvProcess.setAdapter(processAdapter);
        lvProcess.setOnItemClickListener(this);
        return view;

    }// onCreateView-end
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //添加进程
        btn_add_pri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加进程时阻塞线程
                if(dispatchMathod.isRunning()){
                    dispatchMathod.pause();
                }
                button_start_timer.setText("开始");  //开始
                showDialog();
            }
        });

        button_start_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = button_start_timer.getText().toString();//获取按钮字符串
                // 开始按钮 第一次执行，在开始或者是重置后方可运行
                if(mlock==0){
                    if(str.equals("开始")){ //切换按钮文字
                        // 线程未启动时，执行方法体，避免创建多个进程
                        if(!dispatchMathod.isRunning()) {
                            // 先备份进程列表数据
                            copyList();
                            // 注册监听器
                            dispatchMathod.setDispatchListener(new DispatchListener() {
                                @Override
                                public void nowTime(int s) {
                                    nowTime = s;
                                    tvRuntime.setText(s+" 秒");
                                    processAdapter.notifyDataSetChanged();  //会记住划到的位置，重新加载数据时不会改变位置只改变数据
                                }
                            });

                            // 启动线程
                            dispatchMathod.startThread(processList);
                            mlock=1;
                            button_start_timer.setText("暂停");
                        }
                    }
                }   //if （mlock==0） -end
                else{
                    if(mlock==1){
                        button_start_timer.setText("开始");
                        mlock=2;
                        // 阻塞线程
                        if(dispatchMathod.isRunning()){
                            dispatchMathod.pause();
                        }
                    }   //if （mlock==1） -end
                    else{   //继续
                        button_start_timer.setText("暂停");
                        mlock=1;
                        // 如果线程还存在，则唤醒线程
                        if(dispatchMathod.isRunning()){
                            dispatchMathod.start();
                        }
                    }
                }
            }
        });


        //重置
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 先终止线程
                dispatchMathod.stop();
                if (copyList.size() > 0) {
                    processList.clear();
                    //备份数据
                    for (Process p : copyList) {
                        processList.add(p);
                    }
                    processAdapter.notifyDataSetChanged();
                    tvRuntime.setText("0 秒");
                    nowTime = 0;
                }
                mlock=0;   //开始
                button_start_timer.setText("开始");
                Log.d("priority", "reset.click");
            }
        });
    }
    private void showDialog(){//添加进程弹出框
        View root = LayoutInflater.from(this.getActivity()).inflate(R.layout.dialog_addpri,null);
        final EditText etDialogName = (EditText) root.findViewById(R.id.et_dialog_namepri);
        final EditText etDialogPriority = (EditText) root.findViewById(R.id.et_dialog_prioritypri);
        final EditText etDialogstarttime = (EditText) root.findViewById(R.id.et_dialog_starttimepri);
        final EditText etDialogCPUruntime = (EditText) root.findViewById(R.id.et_dialog_CPUruntimepri);
        final EditText etDialogIOstarttime = (EditText) root.findViewById(R.id.et_dialog_IOstarttimepri);
        final EditText etDialogIOtime = (EditText) root.findViewById(R.id.et_dialog_IOtimepri);
        Button btnDialogSure = (Button) root.findViewById(R.id.btn_dialog_surepri);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setView(root);
        final AlertDialog dialog=builder.create();
        dialog.show();
        btnDialogSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etDialogName.getText().toString();
                // 检查输入信息正确性
                if(name.equals("")|| etDialogPriority.getText().toString().isEmpty()||
                        etDialogstarttime.getText().toString().equals("")||etDialogCPUruntime.getText().toString().equals("")
               ||etDialogIOstarttime.getText().toString().equals("")||etDialogIOtime.getText().toString().equals("")){
                    Toast.makeText(priFragment.this.getActivity(),"请输入完整信息",Toast.LENGTH_SHORT).show();
                    return ;
                }
                int priority=Integer.valueOf(etDialogPriority.getText().toString());
                int starttime=Integer.valueOf(etDialogstarttime.getText().toString());
                int CPUruntime=Integer.valueOf(etDialogCPUruntime.getText().toString());
                int IOstarttime=Integer.valueOf(etDialogIOstarttime.getText().toString());
                int IOtime=Integer.valueOf(etDialogIOtime.getText().toString());
                if(priority<1||priority>10){
                    Toast.makeText(priFragment.this.getActivity(),"优先级范围为1-10",Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(starttime<0){
                    Toast.makeText(priFragment.this.getActivity(),"开始时间不能小于0",Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(CPUruntime<=0){
                    Toast.makeText(priFragment.this.getActivity(),"CPU运行时间不能小于等于0",Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(IOstarttime<0||CPUruntime<IOstarttime){
                    Toast.makeText(priFragment.this.getActivity(),"IO开始时间不能小于0不能大于CPU运行时间",Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(IOtime<0){
                    Toast.makeText(priFragment.this.getActivity(),"IO持续时间不能小于0",Toast.LENGTH_SHORT).show();
                    return ;
                }

                Process process = new Process(priority,name,starttime,CPUruntime,IOstarttime,IOtime);
                processList.add(process);
                Log.d("pri","插入新进程");
                for(int i=0;i<processList.size();i++){
                    Log.d("prolist：",processList.get(i).getName()+" runcputimr="+processList.get(i).getRunCPUtime()+" cputtime="+processList.get(i).getCPUTime()+" state="+processList.get(i).getState());
                }
                processAdapter.notifyDataSetChanged();
                // 调整优先级列表
                dispatchMathod.InsertProcess(process,processList);
                dialog.dismiss();
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
            }//onClick end
        });
    }  //showDialog（）-end


    // 开始时数据
    private void initData(){
        Process p = new Process(1,"a",0,3,1,2);
        processList.add(p);
        p = new Process(3,"b",4,1,0,0);
        processList.add(p);
        p = new Process(6,"c",0,2,1,2);
        processList.add(p);
        p = new Process(5,"d",0,3,0,0);
        processList.add(p);
        p = new Process(7,"e",7,5,0,0);
        processList.add(p);
        p = new Process(1,"f",7,4,0,0);
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
        AlertDialog dialog = new AlertDialog.Builder(priFragment.this.getActivity())
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
