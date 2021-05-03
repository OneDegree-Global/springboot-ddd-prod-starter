package com.cymetrics.persistence.rdbms.dao.dev;

import com.cymetrics.persistence.rdbms.entities.CountDo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import java.util.Optional;

public class JpaWithTransaction {

    EntityManager em = Persistence.createEntityManagerFactory("count").createEntityManager();

    public void begixTx(){
        this.em.getTransaction().begin();
    }

    public void endTx(){
        this.em.getTransaction().commit();
    }

    public Optional<CountDo> findById(int id){
        CountDo countDo = em.find(CountDo.class, id, LockModeType.PESSIMISTIC_WRITE);
        return Optional.ofNullable(countDo);
    }

    public void update(CountDo countDo) {
        em.merge(countDo);
    }
}
