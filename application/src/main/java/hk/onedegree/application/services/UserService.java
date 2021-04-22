package hk.onedegree.application.services;

import hk.onedegree.application.aspect.annotations.Authorize;
import hk.onedegree.application.exception.CreateUserFailsException;
import hk.onedegree.application.exception.RetrieveUserInfoFailsException;
import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.exceptions.DuplicatedEmailException;
import hk.onedegree.domain.auth.exceptions.InValidEmailException;
import hk.onedegree.domain.auth.exceptions.InValidPasswordException;
import hk.onedegree.domain.auth.exceptions.RepositoryOperatorException;
import hk.onedegree.domain.auth.services.UserAuthInfoService;
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
            this.userAuthInfoService.createUser("6666666@gmail.com", password);
        } catch (DuplicatedEmailException | InValidEmailException | InValidPasswordException | RepositoryOperatorException e) {
            logger.error("Create user fails: {}", e.getMessage());
            throw new CreateUserFailsException(e.getMessage());
        }

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
