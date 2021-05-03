package com.cymetrics.persistence.rdbms.dao.dev;

import com.cymetrics.persistence.rdbms.entities.CountDo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import java.util.Optional;

public class JpaWithoutTransaction {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("dbcount");

    public Optional<CountDo> findById(int id){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        CountDo countDo = em.find(CountDo.class, id, LockModeType.PESSIMISTIC_WRITE);
        em.getTransaction().commit();
        em.close();
        return Optional.ofNullable(countDo);
    }

    public void update(CountDo countDo) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(countDo);
        em.getTransaction().commit();
        em.close();
    }
}
