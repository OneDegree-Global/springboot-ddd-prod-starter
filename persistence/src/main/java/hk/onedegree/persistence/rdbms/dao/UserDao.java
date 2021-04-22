package hk.onedegree.persistence.rdbms.dao;

import hk.onedegree.persistence.rdbms.entities.UserDo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserDao extends CrudRepository<UserDo, String> {
    UserDo findFirstByEmail(String lastName);
    UserDo findById(long id);
}
