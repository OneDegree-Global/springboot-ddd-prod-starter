package com.cymetrics.persistence.rdbms.dao;

import com.cymetrics.persistence.rdbms.config.PersistenceUserConfiguration;
import com.cymetrics.persistence.rdbms.entities.UserDo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

@SpringBootApplication
@SpringBootTest
@EnableTransactionManagement
@Import(PersistenceUserConfiguration.class)
public class SpringDataJpaWithoutTransactionDev {

    @Autowired
    private UserDao userDao;


    @Test
    public void forDev(){
        UserDo userDo = userDao.findFirstByEmail("1234567");
        System.out.println(userDo);
        Optional optional = Optional.ofNullable(userDo);
        System.out.println(optional.isEmpty());
    }
}
