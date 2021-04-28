package com.cymetrics.application.services;

import com.cymetrics.domain.auth.aggregates.user.User;
import com.cymetrics.domain.auth.services.AuthenticationService;
import com.cymetrics.domain.auth.services.TokenService;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

public class LoginService {
    @Inject
    AuthenticationService authenticationService;

    @Inject
    TokenService tokenService;

    @Transactional("userTransactionManager")
    public Optional<String> getLoginToken(String email, String password) {
        Optional<User> result = this.authenticationService.authenticate(email, password);
        return this.tokenService.issueToken(result);
    }
}
