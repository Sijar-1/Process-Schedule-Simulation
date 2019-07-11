package com.ecust.sijar.ProcessScheduleSimulation.Dispatch;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ecust.sijar.ProcessScheduleSimulation.R;

import java.util.LinkedList;


/**
 * Created by Sijar on 2019/7/3.
 * 进程列表设配器
 */
public class ProcessAdapterPri extends BaseAdapter{

    private Context context;
    private LinkedList<Process> processList;

    public ProcessAdapterPri(Context context, LinkedList<Process> list){
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
        View root = LayoutInflater.from(context).inflate(R.layout.list_itempri,null);
        TextView tvName = (TextView) root.findViewById(R.id.item_name3);
        TextView tvPriority = (TextView) root.findViewById(R.id.item_priority3);
        TextView tvCPUtime = (TextView) root.findViewById(R.id.item_CPUtime3);  //cpu时间
        TextView tvIOtime = (TextView) root.findViewById(R.id.item_IOtime3);  //IO时间
        TextView tvEndTime=(TextView) root.findViewById(R.id.item_endtime3) ;
        TextView tvState = (TextView) root.findViewById(R.id.item_state3);
        TextView tvStartTime = (TextView) root.findViewById(R.id.item_starttime3);

        tvName.setText(processList.get(i).getName());
        tvPriority.setText(processList.get(i).getPriority()+"");
        tvCPUtime.setText(processList.get(i).getCPUTime()+"");
        tvIOtime.setText(processList.get(i).getIOtime()+"");
        tvEndTime.setText(processList.get(i).getEndTime()+"");
        tvState.setText(processList.get(i).getState());
        tvStartTime.setText(processList.get(i).getStartTime()+"");
        if(processList.get(i).getState().equals("进行")){
            root.setBackgroundColor(Color.parseColor("#D1EEEE"));
        }
        if(processList.get(i).getState().equals("就绪")
                ||processList.get(i).getState().equals("完成")){
            root.setBackgroundColor(Color.WHITE);
        }
        if(processList.get(i).getState().equals("阻塞")){
            root.setBackgroundColor(Color.parseColor("#FFC0CB"));
        }
        return root;
    }
}
