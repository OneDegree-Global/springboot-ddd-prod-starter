package com.cymetrics.persistence.rdbms.dao.dev;

import com.cymetrics.persistence.rdbms.entities.CountDo;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaWithoutTransactionDev {
    JpaWithoutTransaction jpaWithoutTransaction = new JpaWithoutTransaction();
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("dbcount");


    @Test
    public void test(){
        //jpaWithoutTransaction.em = em;
        CountDo countDo = jpaWithoutTransaction.findById(1).get();
        countDo.inc();
        jpaWithoutTransaction.update(countDo);
        System.out.println(countDo.getCount());

    }
}
