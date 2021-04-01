package hk.onedegree.application.aspect;

import hk.onedegree.domain.auth.aggregates.user.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;

@Component
@Aspect
public class LoginStateChecker {
    @Before("execution(* hk.onedegree.application.services.OtherService.*(..))")
    public void validateToken(JoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        System.out.println(String.format("%s.%s(%s)",
                target.getClass().getName(), methodName, Arrays.toString(args)));
//        Optional<User> result = this.authenticationService.authenticate(token);
//        return !result.isEmpty();
    }
}
