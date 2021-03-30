package hk.onedegree.domain.auth.repository;

import hk.onedegree.domain.auth.aggregates.user.User;

public interface UserRepository {
    public User findByEmail(String email);
    public void save(User user);
    public void delete(User user);
 }
