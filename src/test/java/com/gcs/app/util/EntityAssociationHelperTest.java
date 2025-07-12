package com.gcs.app.util;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.User;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityAssociationHelperTest {

    private static final String TRAINEE_USERNAME = "trainee_user";
    private static final String TRAINER1_USERNAME = "trainer_one";
    private static final String TRAINER2_USERNAME = "trainer_two";

    @Test
    void addTrainerToTrainee_addsTrainerAndUpdatesBothSides() {
        Trainee trainee = createTrainee();
        Trainer trainer = createTrainer(TRAINER1_USERNAME);

        EntityAssociationHelper.addTrainerToTrainee(trainee, trainer);

        assertEquals(Set.of(trainer), trainee.getTrainers());
        assertEquals(Set.of(trainee), trainer.getTrainees());
    }

    @Test
    void removeTrainerFromTrainee_removesTrainerAndUpdatesBothSides() {
        Trainee trainee = createTrainee();
        Trainer trainer = createTrainer(TRAINER1_USERNAME);
        EntityAssociationHelper.addTrainerToTrainee(trainee, trainer);

        EntityAssociationHelper.removeTrainerFromTrainee(trainee, trainer);

        assertTrue(trainee.getTrainers().isEmpty());
        assertTrue(trainer.getTrainees().isEmpty());
    }

    @Test
    void setTraineeTrainers_replacesOldTrainersWithNewOnesAndSynchronizesAssociations() {
        Trainee trainee = createTrainee();
        Trainer oldTrainer = createTrainer(TRAINER1_USERNAME);
        Trainer newTrainer = createTrainer(TRAINER2_USERNAME);

        EntityAssociationHelper.addTrainerToTrainee(trainee, oldTrainer);
        EntityAssociationHelper.setTraineeTrainers(trainee, Set.of(newTrainer));

        assertEquals(Set.of(newTrainer), trainee.getTrainers());
        assertFalse(oldTrainer.getTrainees().contains(trainee));
        assertEquals(Set.of(trainee), newTrainer.getTrainees());
    }

    private static User createUser(String username) {
        return User.builder()
                .username(username)
                .firstName("Test")
                .lastName("User")
                .password("secret")
                .isActive(true)
                .build();
    }

    private static Trainee createTrainee() {
        return Trainee.builder()
                .user(createUser(EntityAssociationHelperTest.TRAINEE_USERNAME))
                .trainers(new HashSet<>())
                .build();
    }

    private static Trainer createTrainer(String username) {
        return Trainer.builder()
                .user(createUser(username))
                .trainees(new HashSet<>())
                .build();
    }
}
