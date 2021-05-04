package com.cymetrics.application.aspect;

import com.cymetrics.application.aspect.annotations.Retry;
import com.cymetrics.application.exception.RetryExceedLimitException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    Note that this aspect do not deal with transactional rollback, and only retry if exceptions were
    thrown, so advised function should have no side effects
*/

@Aspect
public class RetryAspect {

    private static Logger logger = LoggerFactory.getLogger(RetryAspect.class);

    @Around(value="execution(@com.cymetrics.application.aspect.annotations.Retry  * *..*.*(..))  && @annotation(retry)")
    public void around(ProceedingJoinPoint proceedingJoinPoint, Retry retry) throws RetryExceedLimitException {
        int retryCounter = 0;
        int retries =  retry.retries();
        long baseInterval = retry.baseInterval();

        while(true){
            try {
                proceedingJoinPoint.proceed();
                return;
            } catch (Throwable retryException){
                if(retryCounter >= retries)
                    throw new RetryExceedLimitException("Retries exceed limit times:"+retryException.toString());

                logger.error("Retry aspect catch exception, retry counter:"+retryCounter+" "+retryException.toString());
                try {
                    Thread.interrupted();
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
