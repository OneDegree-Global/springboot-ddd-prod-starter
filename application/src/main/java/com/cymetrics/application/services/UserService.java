package com.cymetrics.application.services;

import com.cymetrics.application.aspect.annotations.Authorize;
import com.cymetrics.application.exception.CreateUserFailsException;
import com.cymetrics.application.exception.RetrieveUserInfoFailsException;
import com.cymetrics.domain.auth.aggregates.user.User;
import com.cymetrics.domain.auth.exceptions.DuplicatedEmailException;
import com.cymetrics.domain.auth.exceptions.InValidEmailException;
import com.cymetrics.domain.auth.exceptions.InValidPasswordException;
import com.cymetrics.domain.auth.exceptions.RepositoryOperatorException;
import com.cymetrics.domain.auth.services.UserAuthInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

public class UserService {
    @Inject
    UserAuthInfoService userAuthInfoService;

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Transactional("userTransactionManager")
    public Optional<User> createUser(String email, String password) throws CreateUserFailsException {

        try {
            return this.userAuthInfoService.createUser(email, password);
        } catch (DuplicatedEmailException | InValidEmailException | InValidPasswordException | RepositoryOperatorException e) {
            logger.error("Create user fails: {}", e.getMessage());
            throw new CreateUserFailsException(e.getMessage());
        }
    }

    @Authorize
    @Transactional("userTransactionManager")
    public Optional<User> getUser(String token, String id) throws RetrieveUserInfoFailsException {

        try {
            return this.userAuthInfoService.getUserById(id);
        } catch (RepositoryOperatorException e) {
            logger.error("Get user fails: {}", e.getMessage());
            throw new RetrieveUserInfoFailsException(e.getMessage());
        }
    }
}
