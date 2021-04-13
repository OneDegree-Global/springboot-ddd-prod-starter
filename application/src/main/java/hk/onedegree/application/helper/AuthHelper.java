package hk.onedegree.application.helper;

import hk.onedegree.domain.auth.services.AuthenticationService;
import hk.onedegree.domain.auth.services.UserAuthInfoService;

import javax.inject.Inject;

public class AuthHelper {

    public static AuthenticationService authenticationService;

    @Inject
    public void setUserAuthInfoService(AuthenticationService authenticationService) {
        AuthHelper.authenticationService = authenticationService;
    }
}
