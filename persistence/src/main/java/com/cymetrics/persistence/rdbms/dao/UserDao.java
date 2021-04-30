package com.cymetrics.persistence.rdbms.dao;

import com.cymetrics.persistence.rdbms.entities.UserDo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserDao extends CrudRepository<UserDo, String> {
    UserDo findFirstByEmail(String email);
    Optional<UserDo> findById(String id);
}
