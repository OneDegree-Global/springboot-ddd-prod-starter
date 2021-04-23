package hk.onedegree.persistence.rdbms.dao;

import hk.onedegree.persistence.rdbms.entities.UserDo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserDao extends CrudRepository<UserDo, String> {
    UserDo findFirstByEmail(String email);
    Optional<UserDo> findById(String id);
}
