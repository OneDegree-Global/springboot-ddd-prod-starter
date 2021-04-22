package hk.onedegree.persistence.rdbms.dao;

import hk.onedegree.persistence.rdbms.config.PersistenceUserConfiguration;
import hk.onedegree.persistence.rdbms.entities.UserDo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

@SpringBootTest
@EnableTransactionManagement
@Import(PersistenceUserConfiguration.class)
public class Dev {

    @Autowired
    private UserDao userDao;


    @Test
    public void forDev(){
        // save a few customers
//        UserDo userDo = userDao.findFirstByEmail("1234567");
//        System.out.println(userDo);
//        Optional optional = Optional.ofNullable(userDo);
//        System.out.println(optional.isEmpty());
    }
}
