package com.gcs.app.util;

import java.util.List;
import java.util.Random;

public class UserUtils {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateUsername(String firstName, String lastName, List<String> existUsernames) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int suffix = 1;

        while (isUsernameExists(username, existUsernames)) {
            username = baseUsername + suffix++;
        }

        return username;
    }

    private static boolean isUsernameExists(String username, List<String> existUsernames) {
        if (existUsernames.isEmpty()){
            return false;
        }
        return existUsernames.stream()
                .anyMatch(existing -> existing.equalsIgnoreCase(username));
    }

    public static String generateRandomPassword() {
        Random random = new Random();
        StringBuilder password = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            password.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return password.toString();
    }
}