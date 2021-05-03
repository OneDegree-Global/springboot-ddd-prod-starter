package com.cymetrics.persistence.rdbms.dao.dev;

import com.cymetrics.persistence.rdbms.entities.CountDo;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;


public interface ISpringData extends CrudRepository<CountDo, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    CountDo findById(int id);
}
