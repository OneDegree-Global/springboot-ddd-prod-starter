package hk.onedegree.persistence;

import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.repository.UserRepository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemUserRepository implements UserRepository {
    ConcurrentHashMap<String, User> emailUserMap = new ConcurrentHashMap<>();

    private static MemUserRepository instance = new MemUserRepository();
    public static MemUserRepository getInstance(){
        return instance;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(this.emailUserMap.get(email));
    }

    @Override
    public void save(User user) {
        this.emailUserMap.put(user.getEmail(), user);
    }

    @Override
    public void delete(User user) {
        this.emailUserMap.remove(user.getEmail());
    }
}
