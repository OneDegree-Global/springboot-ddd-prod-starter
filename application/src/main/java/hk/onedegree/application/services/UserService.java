package hk.onedegree.application.services;

import hk.onedegree.application.aspect.annotations.Authorize;
import hk.onedegree.application.exception.CreateUserFailsException;
import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.exceptions.DuplicatedEmailException;
import hk.onedegree.domain.auth.exceptions.InValidEmailException;
import hk.onedegree.domain.auth.exceptions.InValidPasswordException;
import hk.onedegree.domain.auth.services.UserAuthInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;

public class UserService {
    @Inject
    UserAuthInfoService userAuthInfoService;

    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    public Optional<User> createUser(String email, String password) throws CreateUserFailsException {

        try {
            return this.userAuthInfoService.createUser(email, password);
        } catch (DuplicatedEmailException | InValidEmailException | InValidPasswordException e) {
            logger.error("Create user fails: {}", e.getMessage());
            throw new CreateUserFailsException(e.getMessage());
        }
    }

    @Authorize
    public Optional<User> getUser(String token, String id){
        return this.userAuthInfoService.getUserById(id);
    }
}

