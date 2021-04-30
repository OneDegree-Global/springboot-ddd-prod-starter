package com.cymetrics.domain.auth.services;

import com.cymetrics.domain.auth.aggregates.user.User;
import com.cymetrics.domain.auth.exceptions.DuplicatedEmailException;
import com.cymetrics.domain.auth.exceptions.InValidEmailException;
import com.cymetrics.domain.auth.exceptions.InValidPasswordException;
import com.cymetrics.domain.auth.exceptions.RepositoryOperatorException;
import com.cymetrics.domain.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

public class UserAuthInfoService {
    @Inject
    UserRepository userRepository;

    private static Logger logger = LoggerFactory.getLogger(UserAuthInfoService.class);

    public Optional<User> createUser(String email, String password) throws DuplicatedEmailException, InValidEmailException, InValidPasswordException, RepositoryOperatorException {
        String id = UUID.randomUUID().toString();
        User user;

        if (!this.userRepository.findByEmail(email).isEmpty()) {
            logger.error("Email already exist: {}", email);
            throw  new DuplicatedEmailException("Email already exist: " + email);
        }

        user = new User(id, email);

        user.setPassword(password);

        this.userRepository.save(user);
        return Optional.of(user);
    }

    public Optional<User> getUserById(String id) throws RepositoryOperatorException {
        return this.userRepository.findById(id);
    }
}
