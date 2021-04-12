package hk.onedegree.application.services;

import hk.onedegree.application.exception.CreateUserFailsException;
import hk.onedegree.domain.auth.exceptions.DuplicatedEmailException;
import hk.onedegree.domain.auth.exceptions.InValidEmailException;
import hk.onedegree.domain.auth.exceptions.InValidPasswordException;
import hk.onedegree.domain.auth.services.UserAuthInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class UserService {
    @Inject
    UserAuthInfoService userAuthInfoService;

    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    public void createUser(String email, String password) throws CreateUserFailsException {

        try {
            this.userAuthInfoService.createUser(email, password);
        } catch (DuplicatedEmailException | InValidEmailException | InValidPasswordException e) {
            logger.error("Create user fails: {}", e.getMessage());
            throw new CreateUserFailsException(e.getMessage());
        }
    }
}
