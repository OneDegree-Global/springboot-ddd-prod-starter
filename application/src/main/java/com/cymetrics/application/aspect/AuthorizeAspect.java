package com.cymetrics.application.aspect;

import com.cymetrics.application.exception.UnAuthorizeException;
import com.cymetrics.domain.auth.aggregates.user.User;
import com.cymetrics.domain.auth.services.AuthenticationService;
import jdk.jfr.Name;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import javax.inject.Inject;
import java.util.Optional;

@Aspect
public class AuthorizeAspect {

    @Inject
    @Name("authenticationService")
    AuthenticationService authenticationService;

    @Pointcut("execution(@com.cymetrics.application.aspect.annotations.Authorize  * *..*.*(..))")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) throws UnAuthorizeException {
        String jwt = joinPoint.getArgs()[0].toString();
        Optional<User> result = authenticationService.authenticate(jwt);
        if (result.isEmpty()) {
            throw new UnAuthorizeException("Permission denied");
        }
    }
}
