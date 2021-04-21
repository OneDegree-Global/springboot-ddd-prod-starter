package hk.onedegree.persistence.rdbms.dao;

import hk.onedegree.persistence.rdbms.RdbmsUserRepository;
import hk.onedegree.persistence.rdbms.entities.UserDo;
import jdk.jfr.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.*;
import java.util.List;
import java.util.Optional;

public class UserDao {

//    @Inject
//    @Name("userEntityManager")
//    EntityManagerFactory emf;

    @PersistenceContext
    private EntityManager em;

    private static Logger logger = LoggerFactory.getLogger(UserDao.class);

    public Optional<UserDo> findById(String id){
        UserDo userDo = em.find(UserDo.class, id);
        return Optional.ofNullable(userDo);
    }

    public Optional<UserDo> findByEmail(String email){
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
        em.persist(userDo);
    }

    public void delete(UserDo userDo) {
        em.remove(userDo);
    }
}
