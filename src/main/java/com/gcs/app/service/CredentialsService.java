package com.gcs.app.service;

import java.util.Set;

public interface CredentialsService {
    String generateRandomPassword();
    String encodePassword(String rawPassword);
    String generateUsername(String firstName, String lastName, Set<String> existingUsernames);
    boolean isPasswordCorrect(String rawPassword, String encodedPassword);
}
