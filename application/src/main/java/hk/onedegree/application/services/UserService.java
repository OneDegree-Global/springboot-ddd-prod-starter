package hk.onedegree.application.services;

import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.services.UserAuthInfoService;

import javax.inject.Inject;
import java.util.Optional;

public class UserService {
    @Inject
    UserAuthInfoService userAuthInfoService;

    public boolean createUser(String email, String password){
        Optional<User> result = this.userAuthInfoService.createUser(email, password);
        return !result.isEmpty();
    }
}
