package com.gcs.app.service.impl;

import com.gcs.app.service.CredentialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CredentialsServiceImpl implements CredentialsService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String generateRandomPassword() {
        Random random = new Random();
        StringBuilder password = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            password.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return password.toString();
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public String generateUsername(String firstName, String lastName, Set<String> existingUsernames) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int suffix = 1;

        while (isUsernameExists(username, existingUsernames)) {
            username = baseUsername + suffix++;
        }

        return username;
    }

    @Override
    public boolean isPasswordCorrect(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private boolean isUsernameExists(String username, Set<String> existingUsernames) {
        if (existingUsernames.isEmpty()) {
            return false;
        }

        return existingUsernames.stream()
                .anyMatch(existing -> existing.equalsIgnoreCase(username));
    }
}
