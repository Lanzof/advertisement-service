package com.pokotilov.finaltask.aop;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Slf4j
@Component
public class LoggingAspect {
    @Before("@annotation(LogExecution)")
    public void logExecution(JoinPoint jp) {
        String className = jp.getTarget().getClass().getSimpleName();
        String methodName = ((MethodSignature) jp.getSignature()).getMethod().getName();

        log.info("entering: " + className +  " " + methodName + "  w/args: " + Arrays.toString(jp.getArgs()));
    }

    @Before("execution(* (@LogExecution *).*(..))")
    public void logClassExecution(JoinPoint jp) {
        String className = jp.getTarget().getClass().getSimpleName();
        String methodName = ((MethodSignature) jp.getSignature()).getMethod().getName();

        log.info("entering: " + className + " " + methodName + "  w/args: " + Arrays.toString(jp.getArgs()));
    }
}
