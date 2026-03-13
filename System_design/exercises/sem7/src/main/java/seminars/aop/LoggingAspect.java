package seminars.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    @Around("@annotation(logExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint,
                                   LogExecutionTime logExecutionTime) throws Throwable {
        long start =  System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.printf("Метод %s выполнен за %d мс %n",
                joinPoint.getSignature().toShortString(), elapsedTime);
        return result;
    }
}
