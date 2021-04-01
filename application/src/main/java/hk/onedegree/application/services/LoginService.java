package hk.onedegree.application.services;

import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.services.AuthenticationService;
import hk.onedegree.domain.auth.services.TokenService;

import javax.inject.Inject;
import java.util.Optional;

public class LoginService {
    @Inject
    AuthenticationService authenticationService;

    @Inject
    TokenService tokenService;

    public String getLoginToken(String email, String password) {
        Optional<User> result = this.authenticationService.authenticate(email, password);
        String token = this.tokenService.issueToken(result);

        return token;
    }

    public boolean validateToken(String token) {
        Optional<User> result = this.authenticationService.authenticate(token);
        return !result.isEmpty();
    }
}
