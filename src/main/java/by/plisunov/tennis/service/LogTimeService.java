package by.plisunov.tennis.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogTimeService {


    private static final Logger logger = LoggerFactory.getLogger(LogTimeService.class);

    @Around("@annotation(LogTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        logger.info("TIME: " + joinPoint.getSignature().getName() + " executed in " + executionTime + "ms");

        return proceed;
    }
}
