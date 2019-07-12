package com.ecust.sijar.ProcessScheduleSimulation;


import android.os.Bundle;

import com.ecust.sijar.ProcessScheduleSimulation.Dispatch.ProcessAdapter;

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
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;




public class MainActivity extends AppCompatActivity {
    private rrFragment fragmentOne;
    private fcfsFragment fcfsFragment;
    private priFragment priFragment;



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

    //init（）用来初始化组件
    private void init(){
        fragmentOne=new rrFragment();
        fcfsFragment =new fcfsFragment();
        priFragment =new priFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content,fragmentOne).add(R.id.content, fcfsFragment).add(R.id.content, priFragment);//开启一个事务将fragment动态加载到组件
        transaction.hide(fragmentOne).hide(fcfsFragment).hide(priFragment);//隐藏fragment
        transaction.addToBackStack(null);//返回到上一个显示的fragment
        transaction.commit();//每一个事务最后操作必须是commit（），否则看不见效果
        showNav(R.id.navigation_rr);
    }


    private void showNav(int navid){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (navid){
            case R.id.navigation_rr:
                transaction.hide(fcfsFragment).hide(priFragment);
                transaction.show(fragmentOne);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.navigation_fcfs:
                transaction.hide(fragmentOne).hide(priFragment);
                transaction.show(fcfsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.navigation_spf:
                transaction.hide(fcfsFragment).hide(fragmentOne);
                transaction.show(priFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }


}

