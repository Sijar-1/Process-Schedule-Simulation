package com.ecust.sijar.ProcessScheduleSimulation.Dispatch;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.ecust.sijar.ProcessScheduleSimulation.R;

import java.util.List;

/**
 * Created by Veronica on 2019/7/4.
 * 进程列表设配器 - 时间轮转调度算法
 */

public class ProcessAdapterRR extends BaseAdapter {

    private Context context;
    private List<Process> processList;

    // 通过构造方法将数据源与数据适配器关联起来
    // context:要使用当前的Adapter的界面对象
    public ProcessAdapterRR(Context context, List<Process> list) {

        this.context = context;
        processList = list;

    }

    @Override
    //ListView需要显示的数据数量
    public int getCount() {
        return processList.size();
    }

    @Override
    //指定的索引对应的数据项
    public Object getItem(int i) {
        return processList.get(i);
    }

    @Override
    //指定的索引对应的数据项ID
    public long getItemId(int i) {
        return i;
    }

    @Override
    //返回每一项的显示内容
    public View getView(int i, View view, ViewGroup viewGroup) {
        //将布局文件转化为View对象
        View root = LayoutInflater.from(context).inflate(R.layout.list_item_rr, null);
        TextView tvName = (TextView) root.findViewById(R.id.item_namerr);
        TextView tvProcessTime = (TextView) root.findViewById(R.id.item_process_timerr);  //进程  开始：持续
        TextView tvIOtime = (TextView) root.findViewById(R.id.item_IOtimerr);  //IO  开始：持续
        TextView tvCPU = (TextView) root.findViewById(R.id.item_CPUrr);
        TextView tvIO = (TextView) root.findViewById(R.id.item_IOrr);
        TextView tvTotal = (TextView) root.findViewById(R.id.item_totalrr); //实际运行时间  processtime+等待
        TextView tvState = (TextView) root.findViewById(R.id.item_staterr);

        tvName.setText(processList.get(i).getName());
        tvProcessTime.setText(processList.get(i).getStartTime() + ":" + processList.get(i).getCPUTime());
        tvIOtime.setText(processList.get(i).getIOstartTime() + ":" + processList.get(i).getIOtime());
        tvCPU.setText(processList.get(i).getRunCPUtime() + "");
        tvIO.setText(processList.get(i).getRunIOtime() + "");
        tvTotal.setText(processList.get(i).getEndTime() + "");
        tvState.setText(processList.get(i).getState());

        //如果进程状态变为“进行”，则该行颜色变为绿色
        if (processList.get(i).getState().equals("进行")) {
            root.setBackgroundColor(Color.parseColor("#D1EEEE"));
        }
        //如果进程状态变为“就绪”或者是“完成”，则该行颜色变为白色
        if (processList.get(i).getState().equals("就绪")
                || processList.get(i).getState().equals("完成")) {
            root.setBackgroundColor(Color.WHITE);
        }
        //如果进程状态变为“阻塞”，则该行颜色变为红色
        if (processList.get(i).getState().equals("阻塞")) {
            root.setBackgroundColor(Color.parseColor("#FFC0CB"));
        }

        return root;
    }
}
