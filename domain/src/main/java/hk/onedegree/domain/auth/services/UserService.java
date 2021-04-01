package hk.onedegree.domain.auth.services;

import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.exceptions.InValidEmailException;
import hk.onedegree.domain.auth.exceptions.InValidPasswordException;
import hk.onedegree.domain.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    @Inject
    UserRepository userRepository;

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    public Optional<User> createUser(String email, String password){
        String id = UUID.randomUUID().toString();
        User user;

        if (!this.userRepository.findByEmail(email).isEmpty()) {
            logger.error("Email already exist: {}", email);
            return Optional.empty();
        }

        try {
            user = new User(id, email);
        } catch (InValidEmailException e) {
            logger.error("create user fails, e: ", e);
            return Optional.empty();
        }

        try {
            user.setPassword(password);
        } catch (InValidPasswordException e) {
            logger.error("Set password fails, e: ", e);
            return Optional.empty();
        }

        this.userRepository.save(user);
        return Optional.of(user);
    }
}
