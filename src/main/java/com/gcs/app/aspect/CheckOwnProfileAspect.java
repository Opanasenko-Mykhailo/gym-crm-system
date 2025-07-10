package com.gcs.app.aspect;

import com.gcs.app.model.User;
import com.gcs.app.security.CheckOwnProfile;
import com.gcs.app.service.AuthContextHolder;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class CheckOwnProfileAspect {

    private final AuthContextHolder authContextHolder;

    @Around("@annotation(checkOwnProfile)")
    public Object checkUsernameMatch(ProceedingJoinPoint joinPoint, CheckOwnProfile checkOwnProfile) throws Throwable {
        User currentUser = authContextHolder.getCurrentUser();

        if (currentUser == null) {
            throw new AccessDeniedException("User is not authenticated");
        }

        String currentUsername = currentUser.getUsername();

        String usernameParamName = checkOwnProfile.usernameParam();
        String usernameToCheck = extractUsername(joinPoint, usernameParamName)
                .orElseThrow(() -> new AccessDeniedException("Username parameter is null or missing"));

        if (!usernameToCheck.equalsIgnoreCase(currentUsername)) {
            throw new AccessDeniedException("You can only update your own profile");
        }

        return joinPoint.proceed();
    }

    private Optional<String> extractUsername(ProceedingJoinPoint joinPoint, String usernameParamName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {

            if (parameterNames[i].equals(usernameParamName)) {
                Object paramValue = args[i];
                return Optional.ofNullable(paramValue)
                        .flatMap(val -> {

                            if (val instanceof String) {
                                return Optional.of((String) val);
                            }

                            try {
                                Field usernameField = val.getClass().getDeclaredField("username");
                                usernameField.setAccessible(true);

                                return Optional.ofNullable((String) usernameField.get(val));
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                return Optional.empty();
                            }
                        });
            }
        }
        return Optional.empty();
    }
}
