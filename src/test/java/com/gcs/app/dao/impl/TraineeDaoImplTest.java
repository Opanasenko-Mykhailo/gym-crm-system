package com.gcs.app.dao.impl;

import com.gcs.app.dao.AbstractRepositoryTest;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.User;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataSet(value = "dataset/trainee-data.xml", cleanBefore = true, cleanAfter = true, transactional = true)
class TraineeDaoImplTest extends AbstractRepositoryTest<TraineeDaoImpl> {

    private static final Long EXISTING_TRAINEE_ID = 1L;
    private static final Long NON_EXISTENT_TRAINEE_ID = 999L;

    @Override
    protected TraineeDaoImpl initDao() {
        return new TraineeDaoImpl(sessionFactory);
    }

    @Test
    void get_whenTraineeExists_returnsTrainee() {
        Optional<Trainee> result = dao.get(EXISTING_TRAINEE_ID);

        assertTrue(result.isPresent());
        assertEquals("john.doe", result.get().getUser().getUsername());
        assertEquals("John", result.get().getUser().getFirstName());
    }

    @Test
    void get_whenTraineeDoesNotExist_returnsEmptyOptional() {
        Optional<Trainee> result = dao.get(NON_EXISTENT_TRAINEE_ID);
        assertFalse(result.isPresent());
    }

    @Test
    void update_whenTraineeExists_mergesAndReturnsTrainee() {
        Trainee existing = dao.get(EXISTING_TRAINEE_ID).orElseThrow();
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
                .id(NON_EXISTENT_TRAINEE_ID)
                .user(user)
                .address("Phantom Address")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> dao.update(trainee));

        assertEquals("Trainee with id 999 not found", exception.getMessage());
    }

    @Test
    void delete_whenTraineeExists_removesTrainee() {
        dao.delete(EXISTING_TRAINEE_ID);

        assertFalse(dao.get(EXISTING_TRAINEE_ID).isPresent());
    }

    @Test
    void delete_whenTraineeDoesNotExist_throwsException() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> dao.delete(NON_EXISTENT_TRAINEE_ID));

        assertEquals("Trainee with id 999 not found", exception.getMessage());
    }
}