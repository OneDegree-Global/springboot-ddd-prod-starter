package hk.onedegree.application.aspect;

import hk.onedegree.application.aspect.annotations.Authorize;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AuthorizeAspect {
    @Pointcut("execution(@hk.onedegree.application.aspect.annotations.Authorize  * *..*.*(..)) && @annotation(authorize)")
    public void pointCut(Authorize authorize) {

    }

    @Before("pointCut(authorize)")
    public void before(Authorize authorize) {
        System.out.println("執行前，檢查權限" + authorize.operation());
    }

    @After("pointCut(authorize)")
    public void after(Authorize authorize) {
        System.out.println("執行後，檢查權限" + authorize.operation());
    }
}
