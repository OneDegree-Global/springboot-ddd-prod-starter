package hk.onedegree.application.aspect;

import com.cymetrics.domain.scheduling.service.ScheduleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import javax.inject.Inject;
import java.util.Optional;

@Aspect
public class SchedulerAspect {
    @Inject
    ScheduleService authenticationService;

    @Pointcut("execution(@hk.onedegree.application.aspect.annotations.Scheduler  * *..*.*(..))")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {

    }
}
