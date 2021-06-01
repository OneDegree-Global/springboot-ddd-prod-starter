package com.cymetrics.persistence.mem;

import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.repository.ScheduleRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemScheduleRepository implements ScheduleRepository {

    ConcurrentHashMap<String, Schedule> nameScheduleMap = new ConcurrentHashMap<>();

    private static MemScheduleRepository instance = new MemScheduleRepository();
    public static MemScheduleRepository getInstance(){
        return instance;
    }

    @Override
    public List<Schedule> getAll() {
        return null;
    }

    @Override
    public Optional<Schedule> getByName(String name) {
        return Optional.ofNullable(nameScheduleMap.get(name));
    }

    @Override
    public Optional<Schedule> save(Schedule schedule) {
        nameScheduleMap.put(schedule.getName(), schedule);
        return Optional.ofNullable(schedule);
    }

    @Override
    public void deleteByName(String name) {
        nameScheduleMap.remove(name);
    }
}
