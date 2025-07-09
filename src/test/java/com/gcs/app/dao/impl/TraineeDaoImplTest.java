package com.gcs.app.dao.impl;

import com.gcs.app.dao.AbstractRepositoryTest;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.User;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "dataset/trainee-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
class TraineeDaoImplTest extends AbstractRepositoryTest<TraineeDaoImpl> {

    private static final String EXISTING_USERNAME = "john.doe";
    private static final String NON_EXISTENT_USERNAME = "non.existing.username";

    @Test
    void findByUsername_whenTraineeExists_returnsTrainee() {
        Optional<Trainee> result = dao.findByUsername(EXISTING_USERNAME);

        assertTrue(result.isPresent());
        assertEquals(EXISTING_USERNAME, result.get().getUser().getUsername());
        assertEquals("John", result.get().getUser().getFirstName());
    }

    @Test
    void findByUsername_whenTraineeDoesNotExist_returnsEmptyOptional() {
        Optional<Trainee> result = dao.findByUsername(NON_EXISTENT_USERNAME);

        assertFalse(result.isPresent());
    }

    @Test
    void update_whenTraineeExists_mergesAndReturnsTrainee() {
        Trainee existing = dao.findByUsername(EXISTING_USERNAME).orElseThrow();
        User updatedUser = existing.getUser().toBuilder()
                .firstName("UpdatedName")
                .build();

        Trainee updated = existing.toBuilder()
                .user(updatedUser)
                .address("Updated Address")
                .build();

        Trainee result = dao.update(updated);

        assertEquals("UpdatedName", result.getUser().getFirstName());
        assertEquals("Updated Address", result.getAddress());
    }

    @Test
    void update_whenTraineeDoesNotExist_throwsException() {
        User user = User.builder()
                .id(2L)
                .firstName("Ghost")
                .lastName("User")
                .username("ghost.user")
                .password("password")
                .isActive(true)
                .build();

        Trainee trainee = Trainee.builder()
                .id(999L)
                .user(user)
                .address("Phantom Address")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> dao.update(trainee));
        assertEquals("Trainee with id 999 not found", exception.getMessage());
    }

    @Test
    void deleteByUsername_whenTraineeExists_removesTrainee() {
        dao.deleteByUsername(EXISTING_USERNAME);

        Optional<Trainee> afterDelete = dao.findByUsername(EXISTING_USERNAME);
        assertFalse(afterDelete.isPresent());
    }

    @Test
    void deleteByUsername_whenTraineeDoesNotExist_throwsException() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> dao.deleteByUsername(NON_EXISTENT_USERNAME));
        assertEquals("Trainee with username 'non.existing.username' not found", exception.getMessage());
    }

}
