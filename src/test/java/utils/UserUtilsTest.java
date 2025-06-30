package utils;

import com.gcs.app.util.UserUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserUtilsTest {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String BASE_USERNAME = "John.Doe";
    private static final String BASE_USERNAME_LOWER = "john.doe";
    private static final String USERNAME_WITH_SUFFIX_1 = "John.Doe1";
    private static final String USERNAME_WITH_SUFFIX_2 = "John.Doe2";

    @Test
    void generateUsername_withNonExistingUsername_returnsBaseUsername() {
        Set<String> existingUsernames = Collections.emptySet();
        String actual = UserUtils.generateUsername(FIRST_NAME, LAST_NAME, existingUsernames);
        assertEquals(BASE_USERNAME, actual);
    }

    @Test
    void generateUsername_withExistingUsername_appendsSuffix() {
        Set<String> existingUsernames = Set.of(BASE_USERNAME, BASE_USERNAME_LOWER);
        String actual = UserUtils.generateUsername(FIRST_NAME, LAST_NAME, existingUsernames);
        assertEquals(USERNAME_WITH_SUFFIX_1, actual);
    }

    @Test
    void generateUsername_withMultipleExistingUsernames_appendsHigherSuffix() {
        Set<String> existingUsernames = Set.of(BASE_USERNAME, BASE_USERNAME_LOWER, USERNAME_WITH_SUFFIX_1, BASE_USERNAME_LOWER + "1");
        String actual = UserUtils.generateUsername(FIRST_NAME, LAST_NAME, existingUsernames);
        assertEquals(USERNAME_WITH_SUFFIX_2, actual);
    }

    @Test
    void generateUsername_withEmptyNames_returnsDot() {
        String firstName = "";
        String lastName = "";
        Set<String> existingUsernames = Collections.emptySet();
        String expected = ".";
        String actual = UserUtils.generateUsername(firstName, lastName, existingUsernames);
        assertEquals(expected, actual);
    }

    @Test
    void generateRandomPassword_returnsTenCharacterPassword() {
        String actual = UserUtils.generateRandomPassword();
        assertEquals(10, actual.length());
        assertTrue(actual.chars().allMatch(c -> CHARS.indexOf(c) >= 0), "Password should only contain characters from CHARS");
    }
}