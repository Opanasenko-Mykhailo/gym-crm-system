package com.gcs.app.util;

import com.gcs.app.model.User;

import java.util.Map;
import java.util.Random;

public class UserUtils {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateUsername(String firstName, String lastName, Map<Long, ? extends User> storage) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int suffix = 1;

        while (isUsernameExists(username, storage)) {
            username = baseUsername + suffix++;
        }

        return username;
    }

    private static boolean isUsernameExists(String username, Map<Long, ? extends User> storage) {
        return storage.values().stream()
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
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