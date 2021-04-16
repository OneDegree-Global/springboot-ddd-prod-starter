package hk.onedegree.persistence.rdbms.entities;

import hk.onedegree.persistence.rdbms.dao.UserDao;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class UserDaoTest {
    @Test
    public void forDev(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dev");
        EntityManager em = emf.createEntityManager();

        UserDo userDo = new UserDo();
        userDo.setId("33333");
        userDo.setEmail("333@gmail.com");
        userDo.setPassword("3333333333");
        em.getTransaction().begin();
        em.persist(userDo);
        em.getTransaction().commit();

    }
}
