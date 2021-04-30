package com.cymetrics.application.aspect;

import com.cymetrics.application.aspect.annotations.Retry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    Note that this aspect do not deal with transactional rollback, and only retry if exceptions were
    thrown, so advised function should have no side effects
*/

@Aspect
public class RetryAspect {

    private static Logger logger = LoggerFactory.getLogger(RetryAspect.class);

    @Pointcut("execution(@com.cymetrics.application.aspect.annotations.Retry  * *..*.*(..))")
    public void pointCut() {

    }

    @Around(value="@annotation(retry)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, Retry retry) throws Throwable{
        int retryCounter = 0;
        int retries = retry.retries();
        long baseInterval = retry.baseInterval();
        while(true){
            try {
                return proceedingJoinPoint.proceed();
            } catch (Exception retryException){
                if(retryCounter >= retries)
                    throw new Exception("Retries exceed limit times:"+retryException.toString());

                logger.error("Retry aspect catch exception, retry counter:"+retryCounter+" "+retryException.toString());
                try {
                    Thread.sleep((long) Math.pow(baseInterval, retryCounter));
                } catch(InterruptedException interruptedException){
                    Thread.currentThread().interrupt();
                    logger.info("Retry fail thread sleep got interrupted:"+ interruptedException.toString());
                }
            }
            retryCounter++;
        }
    }
}
