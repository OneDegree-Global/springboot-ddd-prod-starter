package com.cymetrics.persistence.rdbms.dao.dev;

import com.cymetrics.persistence.rdbms.entities.CountDo;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.*;
import java.util.Optional;

public class JpaWithSpringData {

    @Inject
    private EntityManager em;

    public Optional<CountDo> findById(int id){
        CountDo countDo = em.find(CountDo.class, id, LockModeType.PESSIMISTIC_WRITE);
        return Optional.ofNullable(countDo);
    }

    public void update(CountDo countDo) {
        em.persist(countDo);
    }
}
