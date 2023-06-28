package com.pokotilov.finaltask.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

    @Pointcut("@annotation(LogExecution)")
    public void withinLogExecutionClass() {}

    @Pointcut("execution(* (@LogExecution *).*(..))")
    public void annotatedWithLogExecutionMethod() {}

    @Pointcut("withinLogExecutionClass() || annotatedWithLogExecutionMethod()")
    public void logExecution() {}

    @Before("logExecution()")
    public void logClassExecution(JoinPoint jp) {
        String className = jp.getTarget().getClass().getSimpleName();
        String methodName = ((MethodSignature) jp.getSignature()).getMethod().getName();

        String dto = Arrays.toString(jp.getArgs());
        String output = dto.replaceAll("(?<=password=)[^\\s]+", "*****,");

        log.info("Entering: " + className + " " + methodName + "  w/args: " + output);
    }

    @AfterReturning(pointcut = "logExecution()", returning = "result")
    public void logClassExecution(JoinPoint jp, Object result) {
        String className = jp.getTarget().getClass().getSimpleName();
        String methodName = ((MethodSignature) jp.getSignature()).getMethod().getName();

        String dto;
        if (methodName.equals("authenticate") || methodName.equals("register")) {
            dto = "token";
        } else {
            dto = result.toString();
        }

        log.info("Exiting " + className + " " + methodName + "  w/result: " + dto);
    }
}
