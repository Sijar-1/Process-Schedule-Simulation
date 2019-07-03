package com.ecust.sijar.ProcessScheduleSimulation;


import android.os.Bundle;

import com.ecust.sijar.ProcessScheduleSimulation.fragment.FragmentTwo;
import com.ecust.sijar.ProcessScheduleSimulation.R;
import com.ecust.sijar.ProcessScheduleSimulation.fragment.rrFragment;
import com.ecust.sijar.ProcessScheduleSimulation.fragment.FragmentThree;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private rrFragment fragmentOne;
    private com.ecust.sijar.ProcessScheduleSimulation.fragment.FragmentTwo fragmentTwo;
    private FragmentThree fragmentThree;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    showNav(R.id.navigation_home);
                    return true;
                case R.id.navigation_dashboard:

                    showNav(R.id.navigation_dashboard);
                    return true;
                case R.id.navigation_notifications:

                    showNav(R.id.navigation_notifications);
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
    private void init(){
        fragmentOne=new rrFragment();
        fragmentTwo=new FragmentTwo();
        fragmentThree=new FragmentThree();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content,fragmentOne).add(R.id.content,fragmentTwo).add(R.id.content,fragmentThree);//开启一个事务将fragment动态加载到组件
        transaction.hide(fragmentOne).hide(fragmentTwo).hide(fragmentThree);//隐藏fragment
        transaction.addToBackStack(null);//返回到上一个显示的fragment
        transaction.commit();//每一个事务最后操作必须是commit（），否则看不见效果
        showNav(R.id.navigation_home);
    }


    private void showNav(int navid){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (navid){
            case R.id.navigation_home:
                transaction.hide(fragmentTwo).hide(fragmentThree);
                transaction.show(fragmentOne);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.navigation_dashboard:
                transaction.hide(fragmentOne).hide(fragmentThree);
                transaction.show(fragmentTwo);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.navigation_notifications:
                transaction.hide(fragmentTwo).hide(fragmentOne);
                transaction.show(fragmentThree);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
        }
    }

}

