package hk.onedegree.persistence.rdbms.dao;

import hk.onedegree.persistence.rdbms.entities.UserDo;

import javax.persistence.*;
import java.util.Optional;

public class UserDao {
    private final static String persistenceUnit = "user";

    public Optional<UserDo> findById(String id){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit);
        EntityManager em = emf.createEntityManager();
        UserDo userDo = em.find(UserDo.class, id);
        return Optional.ofNullable(userDo);
    }

    public Optional<UserDo> findByEmail(String email){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit);
        EntityManager em = emf.createEntityManager();
        Query q = em.createNativeQuery("SELECT * FROM USER WHERE EMAIL = ?");
        q.setParameter(1, email);
        UserDo[] userDo = (UserDo[]) q.getSingleResult();

        if (userDo.length == 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(userDo[0]);
    }

    public void save(UserDo userDo) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit);
        EntityManager em = emf.createEntityManager();
        em.persist(userDo);
    }

    public void delete(UserDo userDo) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit);
        EntityManager em = emf.createEntityManager();
        em.remove(userDo);
    }
}
