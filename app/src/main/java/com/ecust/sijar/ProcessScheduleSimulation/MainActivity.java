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


import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements rrFragmentListener {
    private fcfsFragment fcfsFragment;
    private priFragment priFragment;

    //RR
    final rrFragment mrrFragment = new rrFragment();
    private fromMainToRR mfromMainToRR;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_rr:

                    showNav(R.id.navigation_rr);
                    return true;
                case R.id.navigation_fcfs:

                    showNav(R.id.navigation_fcfs);
                    return true;
                case R.id.navigation_spf:

                    showNav(R.id.navigation_spf);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    //init（）用来初始化组件
    private void init() {
        fcfsFragment = new fcfsFragment();
        priFragment = new priFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content, mrrFragment).add(R.id.content, fcfsFragment).add(R.id.content, priFragment);//开启一个事务将fragment动态加载到组件
        transaction.hide(mrrFragment).hide(fcfsFragment).hide(priFragment);//隐藏fragment
        transaction.addToBackStack(null);//返回到上一个显示的fragment
        transaction.commit();//每一个事务最后操作必须是commit（），否则看不见效果
        showNav(R.id.navigation_rr);
    }


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


    //RR
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

            final AlertDialog addDialog = new AlertDialog.Builder(this)
                    .setView(root)
                    .create();
            addDialog.show();
            btnDialogSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mfromMainToRR = mrrFragment;
                    mfromMainToRR.process(
                            etDialogName.getText().toString(),
                            Integer.valueOf(etDialogStartTime.getText().toString()),
                            Integer.valueOf(etDialogNeedtime.getText().toString()),
                            Integer.valueOf(etDialogIOstart.getText().toString()),
                            Integer.valueOf(etDialogIOlast.getText().toString())
                    );
                    addDialog.dismiss();


                }
            });
        }


    }
}

