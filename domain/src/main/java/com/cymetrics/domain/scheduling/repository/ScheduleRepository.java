package com.cymetrics.domain.scheduling.repository;

import com.cymetrics.domain.scheduling.aggregates.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {
    public List<Schedule> getAll();
    public Optional<Schedule> getByName(String name);
    public Optional<Schedule> save(Schedule schedule);
    public void deleteByName(String name);
}
