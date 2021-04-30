package com.cymetrics.domain.scheduling.repository;

import com.cymetrics.domain.scheduling.aggregates.Schedule;

import java.util.ArrayList;
import java.util.Optional;

public interface ScheduleRepository {
    public ArrayList<Schedule> getAll();
    public Optional<Schedule> getByName(String name);
    public Optional<Schedule> save(Schedule schedule);
    public void deleteByName(String name);
}
