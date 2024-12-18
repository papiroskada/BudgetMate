package com.example.PersonalAccounting.aop;

import com.example.PersonalAccounting.util.exceptions.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Aspect
@Slf4j
public class ServiceAspect {

    @Around("ServicePointcuts.crudMethods()")
    public Object aroundCrudMethods(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (NullPointerException e) {
            log.error("Null user", e);
            throw new RuntimeException("Problem with authenticated user");
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Data lookup error");
        }
    }

    @Around("ServicePointcuts.paymentMethods()")
    public Object aroundPaymentMethods(ProceedingJoinPoint joinPoint) {
        try{
            return joinPoint.proceed();
        }catch (PaymentException e) {
            log.error("Payment on executed entity", e);
            throw new IllegalArgumentException(e.getMessage());
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Data lookup error");
        }
    }

    @Around("ServicePointcuts.statisticsMethods()")
    public Object aroundStatisticsMethods(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        }catch (NullPointerException e) {
            log.error("Null fileGenerator in StatisticsService", e);
            throw new RuntimeException("Ops :( Something went wrong");
        }catch (RuntimeException e) {
            log.error("IO streams exception in Statistics file generator", e);
            throw new RuntimeException("Ops :( Something went wrong");
        }catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Ops :( Something went wrong");
        }catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Data lookup error");
        }
    }
}
