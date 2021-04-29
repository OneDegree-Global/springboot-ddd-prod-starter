package com.cymetrics.persistence.rdbms.dao;

import com.cymetrics.persistence.rdbms.entities.UserDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.persistence.*;
import java.util.List;
import java.util.Optional;

public class UserDaoJpa {

    EntityManagerFactory emf;

    private static Logger logger = LoggerFactory.getLogger(UserDao.class);

    public Optional<UserDo> findById(String id){
        EntityManager em = emf.createEntityManager();
        UserDo userDo = em.find(UserDo.class, id);
        return Optional.ofNullable(userDo);
    }

    public Optional<UserDo> findByEmail(String email){
        EntityManager em = emf.createEntityManager();
        Query q = em.createNativeQuery("SELECT * FROM USER WHERE EMAIL = ? LIMIT 1");
        q.setParameter(1, email);
        List<UserDo> userDo = (List<UserDo>) q.getResultList();

        if (userDo.size() == 0) {
            return Optional.empty();
        }
//1710176365
        return Optional.ofNullable(userDo.get(0));
    }

    public void save(UserDo userDo) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(userDo);
        em.getTransaction().commit();
    }

    public void delete(UserDo userDo) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(userDo);
        em.getTransaction().commit();
    }
}
