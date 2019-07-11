package com.ecust.sijar.ProcessScheduleSimulation.Listener;

public interface fromMainToRR {
    public void process(String name, int startTime, int CPUtime, int IOstart, int IOlast);
    public void dismiss();
}
