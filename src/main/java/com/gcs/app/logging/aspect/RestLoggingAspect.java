package com.gcs.app.logging.aspect;

import com.gcs.app.rest.UserCreationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RestLoggingAspect {

    private final HttpServletRequest request;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {
    }

    @Before("restController()")
    public void logRequest(JoinPoint joinPoint) {
        MethodSignature method = (MethodSignature) joinPoint.getSignature();

        log.info("REST Request [{} {}] Method: {}, Args: {}",
                request.getMethod(),
                request.getRequestURI(),
                method.getMethod().getName(),
                Arrays.toString(joinPoint.getArgs())
        );
    }

    @AfterReturning(pointcut = "restController()", returning = "result")
    public void logResponse(Object result) {
        if (result == null) {
            log.info("REST Response: OK");
            return;
        }

        log.info("REST Response: {}", maskSensitiveData(result));
    }

    @AfterThrowing(pointcut = "restController()", throwing = "ex")
    public void logException(Exception ex) {
        log.error("REST Error: {}", ex.getMessage(), ex);
    }

    private String maskSensitiveData(Object result) {
        if (result == null) {
            return "null";
        }

        if (result instanceof ResponseEntity<?> responseEntity) {
            Object body = responseEntity.getBody();
            return "ResponseEntity { status: " + responseEntity.getStatusCode() +
                    ", body: " + maskSensitiveData(body) + " }";
        }

        if (result instanceof UserCreationResponse response) {
            return String.format("UserCreationResponse { username: %s, password: **** }", response.getUsername());
        }

        return result.toString();
    }
}