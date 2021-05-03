package com.cymetrics.persistence.rdbms.dao.dev;

import com.cymetrics.persistence.rdbms.entities.CountDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import java.util.Optional;

public class JpaWithTransactionThreadSafe {
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("dbcount");
    ThreadLocal<EntityManager> threadLocalEm = new ThreadLocal<>();

    private static Logger logger = LoggerFactory.getLogger(JpaWithTransactionThreadSafe.class);

    public void begixTx(){
        EntityManager em = threadLocalEm.get();
        if(em==null) {
            em = emf.createEntityManager();
            threadLocalEm.set(em);
        }
        logger.info("begin em: " + em);
        em.getTransaction().begin();
    }

    public void endTx(){
        EntityManager em = threadLocalEm.get();
        em.getTransaction().commit();
        em.close();
        threadLocalEm.remove();
        logger.info("end em: " + em);
    }

    public Optional<CountDo> findById(int id){
        EntityManager em = threadLocalEm.get();
        CountDo countDo = em.find(CountDo.class, id, LockModeType.PESSIMISTIC_WRITE);
        return Optional.ofNullable(countDo);
    }

    public void update(CountDo countDo) {
        EntityManager em = threadLocalEm.get();
        em.merge(countDo);
    }
}
