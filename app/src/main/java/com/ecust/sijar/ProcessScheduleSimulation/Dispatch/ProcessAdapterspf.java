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
 * Created by wjb on 2016/6/4.
 * 进程列表设配器
 */
public class ProcessAdapterspf extends BaseAdapter{

    private Context context;
    private List<Process> processList;

    public ProcessAdapterspf(Context context, List<Process> list){
        this.context = context;
        processList = list;
    }
    @Override
    public int getCount() {
        return processList.size();
    }   //好像可以去掉，可以设置在PCB类里的成员变量

    @Override
    public Object getItem(int i) {
        return processList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View root = LayoutInflater.from(context).inflate(R.layout.list_itemspf,null);
        TextView tvName = (TextView) root.findViewById(R.id.item_name3);
        TextView tvPriority = (TextView) root.findViewById(R.id.item_priority3);
      //  TextView tvRound=(TextView)root.findViewById(R.id.item_round);
        TextView tvRuntime = (TextView) root.findViewById(R.id.item_runtime3);
    //    TextView tvCount = (TextView) root.findViewById(R.id.item_count);
     //   TextView tvNeedTime = (TextView) root.findViewById(R.id.item_need_time);
        TextView tvState = (TextView) root.findViewById(R.id.item_state3);
        TextView tvStartTime = (TextView) root.findViewById(R.id.item_starttime3);
      //  TextView tvEndTime = (TextView) root.findViewById(R.id.item_endtime);

        tvName.setText(processList.get(i).getName());
        tvPriority.setText(processList.get(i).getPriority()+"");
     //   tvRound.setText(processList.get(i).getRound()+"s");
        tvRuntime.setText(processList.get(i).getRunTime()+"s");
     //   tvCount.setText(processList.get(i).getCount());
     //   tvNeedTime.setText(processList.get(i).getNeedTime()+"s");
        tvState.setText(processList.get(i).getState());
        if(processList.get(i).getState().equals("进行")){
            root.setBackgroundColor(Color.parseColor("#D1EEEE"));
        }
        if(processList.get(i).getState().equals("就绪")
                ||processList.get(i).getState().equals("完成")){
            root.setBackgroundColor(Color.WHITE);
        }
        tvStartTime.setText(processList.get(i).getStartTime());
     //   tvEndTime.setText(processList.get(i).getEndTime());
        return root;
    }
}
