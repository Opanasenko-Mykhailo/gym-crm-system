package com.gcs.app.security.aspect;

import com.gcs.app.model.User;
import com.gcs.app.security.MatchEntityOwner;
import com.gcs.app.service.common.AuthContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.nio.file.AccessDeniedException;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.lang.String.format;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MatchEntityOwnerAspect {

    private final AuthContextHolder authContextHolder;

    @Around("@annotation(matchEntityOwner)")
    public Object checkUsernameMatch(ProceedingJoinPoint joinPoint, MatchEntityOwner matchEntityOwner) throws Throwable {
        User currentUser = authContextHolder.getCurrentUser();

        if (currentUser == null || StringUtils.isBlank(currentUser.getUsername())) {
            throw new AccessDeniedException("User was not authenticated as current session User or his username is missing");
        }

        String currentUsername = currentUser.getUsername();
        String usernameParamName = matchEntityOwner.usernameParam();

        String entityOwner = extractUsername(joinPoint, usernameParamName)
                .orElseThrow(() -> new AccessDeniedException("Username parameter is null or missing"));

        if (!entityOwner.equalsIgnoreCase(currentUsername)) {
            throw new AccessDeniedException(format("User %s has not permission for operation regarding %s entity", currentUsername, entityOwner));
        }

        log.info("User {} has permission to requested operation", currentUsername);
        return joinPoint.proceed();
    }

    private Optional<String> extractUsername(ProceedingJoinPoint joinPoint, String usernameParamName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        return IntStream.range(0, parameterNames.length)
                .filter(i -> parameterNames[i].equals(usernameParamName))
                .mapToObj(i -> args[i])
                .findFirst()
                .flatMap(this::extractUsername);
    }

    private Optional<String> extractUsername(Object inputDto) {
        if (inputDto instanceof String username) {
            return Optional.of(username);
        }

        try {
            Field usernameField = inputDto.getClass().getDeclaredField("username");
            usernameField.setAccessible(true);
            Object fieldValue = usernameField.get(inputDto);

            return Optional.ofNullable((String) fieldValue)
                    .filter(StringUtils::isNotBlank);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return Optional.empty();
        }
    }
}
