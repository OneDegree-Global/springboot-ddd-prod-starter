package hk.onedegree.domain.auth.repository;

import hk.onedegree.domain.auth.aggregates.user.User;

import java.util.Optional;

public interface UserRepository {
    public Optional<User> findByEmail(String email);
    public Optional<User> findById(String id);
    public void save(User user);
    public void delete(User user);
 }
