package com.cymetrics.persistence.rdbms.dao;

import com.cymetrics.persistence.rdbms.entities.ScheduleDo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ScheduleDao  extends CrudRepository<ScheduleDo, String> {
    Optional<ScheduleDo> findByName(String name);
    void deleteByName(String name);
}
