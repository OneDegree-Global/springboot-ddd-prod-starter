package com.cymetrics.domain.auth.repository;

import com.cymetrics.domain.auth.aggregates.user.User;
import com.cymetrics.domain.auth.exceptions.RepositoryOperatorException;

import java.util.Optional;

public interface UserRepository {
    public Optional<User> findByEmail(String email) throws RepositoryOperatorException;
    public Optional<User> findById(String id) throws RepositoryOperatorException;
    public void save(User user) throws RepositoryOperatorException;
    public void delete(User user) throws RepositoryOperatorException;
 }
