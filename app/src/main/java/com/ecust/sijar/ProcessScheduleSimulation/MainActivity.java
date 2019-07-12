package com.ecust.sijar.ProcessScheduleSimulation;


import android.app.AlertDialog;
import android.os.Bundle;

import com.ecust.sijar.ProcessScheduleSimulation.Listener.fromMainToRR;
import com.ecust.sijar.ProcessScheduleSimulation.Listener.rrFragmentListener;
import com.ecust.sijar.ProcessScheduleSimulation.fragment.fcfsFragment;
import com.ecust.sijar.ProcessScheduleSimulation.fragment.rrFragment;
import com.ecust.sijar.ProcessScheduleSimulation.fragment.priFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements rrFragmentListener {
    private fcfsFragment fcfsFragment;
    private priFragment priFragment;

    //时间片轮转算法
    final rrFragment mrrFragment = new rrFragment();
    private fromMainToRR mfromMainToRR;

    //绑定监听器，实现点击底部三个按钮“时间片轮转”，“先来先服务”，“优先级” 切换对应的界面（Fragment）
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_rr:
                    //切换到“时间片轮转算法”界面
                    showNav(R.id.navigation_rr);
                    return true;
                case R.id.navigation_fcfs:
                    //切换到“先来先服务”界面
                    showNav(R.id.navigation_fcfs);
                    return true;
                case R.id.navigation_spf:
                    //切换到“优先级”界面
                    showNav(R.id.navigation_spf);
                    return true;
            }
            return false;
        }
    };

    /**
     * 函数名：void onCreate(Bundle savedInstanceState)
     * 作用：是在Activity创建时被系统调用，是一个Activity生命周期的开始；
     *       它主要做这个activity启动时一些必要的初始化工作
     * **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * 函数名：void init()
     * 作用：用来初始化组件，并设置时间片轮转算法页面为APP打开显示的页面
     *      其中FragmentManager 是用来管理Activity 中的Fragment；
     *      而FragmentTransaction是在使用Fragment时，通过用户交互来执行一些动作，
     *      比如增加、移除、替换等。所有这些改变构成一个集合，这个集合被叫做一个transaction；
     * **/

    private void init() {

        fcfsFragment = new fcfsFragment();
        priFragment = new priFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content, mrrFragment).add(R.id.content, fcfsFragment).add(R.id.content, priFragment);//开启一个事务将fragment动态加载到组件
        transaction.hide(mrrFragment).hide(fcfsFragment).hide(priFragment);//隐藏fragment
        transaction.addToBackStack(null);//禁止回到上一个显示的fragment
        transaction.commit();//应用操作
        //显示时间片轮转算法的界面
        showNav(R.id.navigation_rr);
    }


    /**
     * 函数名：void showNav(int navid)
     * 作用：根据按钮显示相应页面（Fragment）
     *       其中 navigation_rr  时间片轮转算法页面，navigation_fcfs 先来先服务算法页面，navigation_spf 优先级算法页面
     * **/
    private void showNav(int navid) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (navid) {
            case R.id.navigation_rr:
                transaction.hide(fcfsFragment).hide(priFragment);
                transaction.show(mrrFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.navigation_fcfs:
                transaction.hide(mrrFragment).hide(priFragment);
                transaction.show(fcfsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.navigation_spf:
                transaction.hide(fcfsFragment).hide(mrrFragment);
                transaction.show(priFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
        }
    }


    /**
     * 函数名：vbtnFunc(String btn, String content)
     * 实现接口：rrFragmentListener
     * 作用：接口回调，一旦在 时间片轮转算法中点击添加进行，则触发以下函数
     *       实现创建弹出框，需输入：进程名称，进程开始时间，进程持续时间，IO开始时间，IO持续时间
     *       若有一项输入为空，则不可创建。点击“取消”按钮可取消创建进程
     * **/
    @Override
    public void btnFunc(String btn, String content) {
        //添加进程
        if (btn.equals("rr_add")) {
            View root = LayoutInflater.from(this).inflate(R.layout.dialog_add_rr, null);
            final EditText etDialogName = (EditText) root.findViewById(R.id.et_dialog_namerr);
            final EditText etDialogNeedtime = (EditText) root.findViewById(R.id.et_dialog_needtimerr);
            final EditText etDialogStartTime = (EditText) root.findViewById(R.id.et_dialog_startTimerr);
            final EditText etDialogIOstart = (EditText) root.findViewById(R.id.et_dialog_IOstartrr);
            final EditText etDialogIOlast = (EditText) root.findViewById(R.id.et_dialog_IOlastrr);
            Button btnDialogSure = (Button) root.findViewById(R.id.btn_dialog_surerr);
            Button btnDialogCancel = (Button) root.findViewById(R.id.btn_dialog_cancelrr);

            //弹出框
            final AlertDialog addDialog = new AlertDialog.Builder(this)
                    .setView(root)
                    .create();
            addDialog.show();
            addDialog.setCancelable(false);  //点击框外无法关闭弹出框
            btnDialogSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!TextUtils.isEmpty(etDialogName.getText().toString().trim())&&
                            !TextUtils.isEmpty(etDialogStartTime.getText().toString().trim())&&
                            !TextUtils.isEmpty(etDialogNeedtime.getText().toString().trim())&&
                            !TextUtils.isEmpty(etDialogIOstart.getText().toString().trim())&&
                            !TextUtils.isEmpty(etDialogIOstart.getText().toString().trim())
                    ){
                        mfromMainToRR = mrrFragment;
                        mfromMainToRR.process(
                                etDialogName.getText().toString(),
                                Integer.valueOf(etDialogStartTime.getText().toString()),
                                Integer.valueOf(etDialogNeedtime.getText().toString()),
                                Integer.valueOf(etDialogIOstart.getText().toString()),
                                Integer.valueOf(etDialogIOstart.getText().toString())
                        );
                        addDialog.dismiss();

                    }
                    else{
                        Toast.makeText(MainActivity.this,"请输入完整信息",Toast.LENGTH_SHORT).show();

                    }


                }
            });

            btnDialogCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addDialog.dismiss();
                    mfromMainToRR = mrrFragment;
                    mfromMainToRR.dismiss();
                }
            });
        }
    }
}

