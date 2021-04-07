package hk.onedegree.application.aspect;

import hk.onedegree.application.aspect.annotations.Authorize;
import hk.onedegree.application.aspect.exception.UnAuthorizeException;
import hk.onedegree.domain.auth.aggregates.user.User;
import hk.onedegree.domain.auth.services.AuthenticationService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import javax.inject.Inject;
import java.util.Optional;

@Aspect
public class AuthorizeAspect {
    @Inject
    AuthenticationService authenticationService;

    @Pointcut("execution(@hk.onedegree.application.aspect.annotations.Authorize  * *..*.*(..))")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) throws UnAuthorizeException {
        String jwt = joinPoint.getArgs()[0].toString();
        Optional<User> result = this.authenticationService.authenticate(jwt);
        if (result.isEmpty()) {
            throw new UnAuthorizeException("Permission denied");
        }
    }
}
