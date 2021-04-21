package com.cymetrics.domain.scheduling.repository;

import com.cymetrics.domain.scheduling.aggregates.Scheduler;

import java.util.ArrayList;

public interface SchedulerRepository {
    public ArrayList<Scheduler> getSchedulers();
    public void save(Scheduler scheduler);
    public void delete(String name);
}
