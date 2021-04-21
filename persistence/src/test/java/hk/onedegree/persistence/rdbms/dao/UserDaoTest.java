package hk.onedegree.persistence.rdbms.dao;

import hk.onedegree.persistence.rdbms.entities.UserDo;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class UserDaoTest {
    @Test
    public void forDev(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");
        UserDao userDao = new UserDao();

        UserDo userDo = new UserDo();
        userDo.setId("33333");
        userDo.setEmail("genchilu@gmail.com");
        userDo.setPassword("3333333333");

        //userDao.findByEmail("genchilu@gmail.com");
        userDao.save(userDo);

    }
}
