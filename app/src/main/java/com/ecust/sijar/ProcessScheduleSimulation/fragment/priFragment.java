package com.ecust.sijar.ProcessScheduleSimulation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ecust.sijar.ProcessScheduleSimulation.R;


public class priFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.priority, container, false);
        final Button button_start_timer = (Button)view.findViewById(R.id.btn_startpri);
        button_start_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = button_start_timer.getText().toString();//获取按钮字符串
                if(str.equals("开始")){ //切换按钮文字
                    button_start_timer.setText("暂停");
                }
                else{
                    button_start_timer.setText("开始");
                }
            }
        });


        return view;

    }
}