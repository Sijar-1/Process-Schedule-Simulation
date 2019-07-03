package com.ecust.sijar.ProcessScheduleSimulation;

import android.app.Application;
import android.content.Context;

/**
 * Created by Sijar on 2019/7/3.
 */
public class App extends Application {

    private static Context context;

    public void App(){
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
