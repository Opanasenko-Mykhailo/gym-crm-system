package com.gcs.app.dao.impl;

import com.gcs.app.dao.AbstractRepositoryTest;
import com.gcs.app.model.User;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "dataset/user-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
class UserDaoImplTest extends AbstractRepositoryTest<UserDaoImpl> {

    private static final String EXISTING_USERNAME = "john.doe";
    private static final String NON_EXISTENT_USERNAME = "ghost.user";

    @Test
    void findByUsername_whenUserExists_returnsUser() {
        Optional<User> result = dao.findByUsername(EXISTING_USERNAME);

        assertTrue(result.isPresent());
        assertEquals(EXISTING_USERNAME, result.get().getUsername());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void findByUsername_whenUserDoesNotExist_returnsEmptyOptional() {
        Optional<User> result = dao.findByUsername(NON_EXISTENT_USERNAME);

        assertFalse(result.isPresent());
    }

    @Test
    void update_whenUserExists_mergesAndReturnsUser() {
        User existing = dao.findByUsername(EXISTING_USERNAME).orElseThrow();
        User updated = existing.toBuilder()
                .firstName("UpdatedName")
                .isActive(false)
                .build();

        User result = dao.update(updated);

        assertEquals("UpdatedName", result.getFirstName());
        assertFalse(result.getIsActive());
    }

    @Test
    void findAllUsernames_returnsAllUsernames() {
        Set<String> usernames = dao.findAllUsernames();

        assertTrue(usernames.contains("john.doe"));
        assertEquals(1, usernames.size());
    }
}
