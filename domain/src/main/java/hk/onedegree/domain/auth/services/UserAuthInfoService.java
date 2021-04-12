package hk.onedegree.domain.auth.services;

import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.exceptions.DuplicatedEmailException;
import hk.onedegree.domain.auth.exceptions.InValidEmailException;
import hk.onedegree.domain.auth.exceptions.InValidPasswordException;
import hk.onedegree.domain.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

public class UserAuthInfoService {
    @Inject
    UserRepository userRepository;

    private static Logger logger = LoggerFactory.getLogger(UserAuthInfoService.class);

    public Optional<User> createUser(String email, String password) throws DuplicatedEmailException, InValidEmailException, InValidPasswordException {
        String id = UUID.randomUUID().toString();
        User user;

        if (!this.userRepository.findByEmail(email).isEmpty()) {
            logger.error("Email already exist: {}", email);
            throw  new DuplicatedEmailException(email);
        }

        user = new User(id, email);

        user.setPassword(password);

        this.userRepository.save(user);
        return Optional.of(user);
    }
}
