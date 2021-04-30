package com.cymetrics.persistence.mem;

import com.cymetrics.domain.scheduling.aggregates.Schedule;
import com.cymetrics.domain.scheduling.repository.ScheduleRepository;

import java.util.ArrayList;
import java.util.Optional;

public class MemScheduleRepository implements ScheduleRepository {
    @Override
    public ArrayList<Schedule> getAll() {
        return null;
    }

    @Override
    public Optional<Schedule> getByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Schedule> save(Schedule schedule) {
        return null;
    }

    @Override
    public void deleteByName(String name) {

    }
}
