package com.cymetrics.web.springboot.controller;

import com.cymetrics.persistence.rdbms.dao.dev.*;
import com.cymetrics.persistence.rdbms.entities.CountDo;
import com.cymetrics.web.springboot.controller.dev.NotThreadSafeCounter;
import com.cymetrics.web.springboot.controller.dev.ThreadSafeCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("dev")
public class DevController {

    @Inject
    NotThreadSafeCounter notThreadSafeCounter;

    @Inject
    ThreadSafeCounter threadSafeCounter;

    @Inject
    JpaWithoutTransaction jpaWithoutTransaction;

    @Inject
    JpaWithTransaction jpaWithTransaction;

    @Inject
    JpaWithTransactionThreadSafe jpaWithTransactionThreadSafe;

    @Inject
    ISpringData iSpringData;

    @Inject
    JpaWithSpringData jpaWithSpringDataBean;

    private static Logger logger = LoggerFactory.getLogger(DevController.class);

    @GetMapping("/notthreadsafe/inc")
    public String notThreadSafeInc() {
        return Integer.toString(notThreadSafeCounter.incCount());
    }

    @GetMapping("/threadsafe/inc")
    public String threadSafeInc() {
        return Integer.toString(threadSafeCounter.incCount());
    }

    @GetMapping("/jpa/nontransaction")
    public String jpaNonTransaction() {
        CountDo countDo = jpaWithoutTransaction.findById(1).get();
        int cur = countDo.getCount();
        countDo.inc();
        jpaWithoutTransaction.update(countDo);
        int newer = countDo.getCount();

        return String.format("origin count: %d, inc count: %d", cur, newer);

    }

    @GetMapping("/jpa/transaction/notthreadsafe")
    public String jpaTransactionNotThreadSafe() {
        jpaWithTransaction.begixTx();

        CountDo countDo = jpaWithTransaction.findById(1).get();
        int cur = countDo.getCount();
        countDo.inc();
        jpaWithTransaction.update(countDo);
        int newer = countDo.getCount();

        jpaWithTransaction.endTx();
        return String.format("origin count: %d, inc count: %d", cur, newer);

    }

    @GetMapping("/jpa/transaction/threadsafe")
    public String jpaTransactionThreadSafe() {
        jpaWithTransactionThreadSafe.begixTx();

        CountDo countDo = jpaWithTransactionThreadSafe.findById(1).get();
        int cur = countDo.getCount();
        countDo.inc();
        jpaWithTransactionThreadSafe.update(countDo);
        int newer = countDo.getCount();

        jpaWithTransactionThreadSafe.endTx();
        logger.info("origin count: {}, inc count: {}", cur, newer);
        return String.format("origin count: %d, inc count: %d", cur, newer);
    }

    @GetMapping("/jpa/spring")
    @Transactional("devTransactionManager")
    public String jpaSpring() {
        CountDo countDo = iSpringData.findById(1);
        int cur = countDo.getCount();
        countDo.inc();
        iSpringData.save(countDo);
        int newer = countDo.getCount();

        logger.info("origin count: {}, inc count: {}", cur, newer);
        return String.format("origin count: %d, inc count: %d", cur, newer);
    }

    @GetMapping("/jpa/spring/test")
    @Transactional("devTransactionManager")
    public String jpaSpringNoTransaction() {
        CountDo countDo1 = new CountDo();
        countDo1.setId(2);

        CountDo countDo2 = new CountDo();
        countDo2.setId(3);

        iSpringData.save(countDo1);
        iSpringData.save(countDo2);

        return "";
    }

    @GetMapping("/jpa/mixSpring")
    @Transactional("devTransactionManager")
    public String jpaMixSpring() {
        CountDo countDo1 = new CountDo();
        countDo1.setId(2);

        CountDo countDo2 = new CountDo();
        countDo2.setId(3);

        jpaWithSpringDataBean.update(countDo1);
        jpaWithSpringDataBean.update(countDo2);

        return "";
    }

}
