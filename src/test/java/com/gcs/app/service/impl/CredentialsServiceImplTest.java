package com.gcs.app.service.impl;

import com.gcs.app.service.common.CredentialsService;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CredentialsServiceImplTest {

    private final CredentialsService service = new CredentialsService();

    @Test
    void generateRandomPassword_returnsPasswordOfLength10() {
        String password = service.generateRandomPassword();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void generateRandomPassword_returnsDifferentPasswords() {
        String pwd1 = service.generateRandomPassword();
        String pwd2 = service.generateRandomPassword();

        assertNotEquals(pwd1, pwd2);
    }

    @Test
    void encodePassword_and_isPasswordCorrect_workCorrectly() {
        String rawPassword = "mySecret123";
        String encoded = service.encodePassword(rawPassword);

        assertNotNull(encoded);
        assertFalse(encoded.isEmpty());
        assertTrue(service.isPasswordCorrect(rawPassword, encoded));
        assertFalse(service.isPasswordCorrect("wrongPassword", encoded));
    }

    @Test
    void generateUsername_returnsBaseUsernameIfNotExists() {
        String firstName = "john";
        String lastName = "doe";
        Set<String> existing = Set.of("alice.smith");

        String username = service.generateUsername(firstName, lastName, existing);

        assertEquals("john.doe", username);
    }

    @Test
    void generateUsername_appendsSuffixIfExists() {
        String firstName = "john";
        String lastName = "doe";
        Set<String> existing = Set.of("john.doe", "john.doe1", "john.doe2");

        String username = service.generateUsername(firstName, lastName, existing);

        assertEquals("john.doe3", username);
    }

    @Test
    void generateUsername_caseInsensitiveCheck() {
        String firstName = "john";
        String lastName = "doe";
        Set<String> existing = Set.of("John.Doe", "john.doe1");

        String username = service.generateUsername(firstName, lastName, existing);

        assertEquals("john.doe2", username);
    }
}
