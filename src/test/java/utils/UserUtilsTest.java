package utils;

import com.gcs.app.util.UserUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserUtilsTest {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Test
    void generateUsername_withNonExistingUsername_returnsBaseUsername() {
        String firstName = "John";
        String lastName = "Doe";
        Set<String> existingUsernames = Collections.emptySet();
        String expected = "John.Doe";

        String result = UserUtils.generateUsername(firstName, lastName, existingUsernames);

        assertEquals(expected, result);
    }

    @Test
    void generateUsername_withExistingUsername_appendsSuffix() {
        String firstName = "John";
        String lastName = "Doe";
        Set<String> existingUsernames = Set.of("John.Doe", "john.doe");
        String expected = "John.Doe1";

        String result = UserUtils.generateUsername(firstName, lastName, existingUsernames);

        assertEquals(expected, result);
    }

    @Test
    void generateUsername_withMultipleExistingUsernames_appendsHigherSuffix() {
        String firstName = "John";
        String lastName = "Doe";
        Set<String> existingUsernames = Set.of("John.Doe", "John.Doe1", "john.doe", "john.doe1");
        String expected = "John.Doe2";

        String result = UserUtils.generateUsername(firstName, lastName, existingUsernames);

        assertEquals(expected, result);
    }

    @Test
    void generateUsername_withEmptyNames_returnsDot() {
        String firstName = "";
        String lastName = "";
        Set<String> existingUsernames = Collections.emptySet();
        String expected = ".";

        String result = UserUtils.generateUsername(firstName, lastName, existingUsernames);

        assertEquals(expected, result);
    }

    @Test
    void generateRandomPassword_returnsTenCharacterPassword() {
        String result = UserUtils.generateRandomPassword();

        assertEquals(10, result.length());
        assertTrue(result.chars().allMatch(c -> CHARS.indexOf(c) >= 0), "Password should only contain characters from CHARS");
    }
}