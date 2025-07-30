package com.gcs.app.repository.impl;

import com.gcs.app.facade.dto.TraineeTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import com.gcs.app.model.Training;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@DBRider
@DBUnit(cacheConnection = true, leakHunter = true, caseSensitiveTableNames = false, schema = "PUBLIC")
@Import(TrainingQueryRepositoryImpl.class)
class TrainingQueryRepositoryImplTest {

    @Autowired
    private TrainingQueryRepositoryImpl trainingQueryRepository;

    @Test
    @DataSet(value = "dataset/trainee-data-criteria.xml", cleanBefore = true, cleanAfter = true)
    void findTrainingsForTrainee_whenCriteriaMatch_returnsTrainings() {
        TraineeTrainingSearchCriteriaDto criteria = new TraineeTrainingSearchCriteriaDto();
        criteria.setUsername("sophia.martinez");  // username з нового файлу
        criteria.setFromDate(LocalDate.of(2020, 1, 1));
        criteria.setToDate(LocalDate.of(2030, 1, 1));
        criteria.setTrainerName("Coach Smith");  // тренер зі нового файлу
        criteria.setTrainingTypeName("Pilates"); // тип тренування зі нового файлу

        List<Training> trainings = trainingQueryRepository.findTrainingsForTrainee(criteria);

        assertFalse(trainings.isEmpty());
        assertTrue(trainings.stream().allMatch(t -> "sophia.martinez".equals(t.getTrainee().getUser().getUsername())));
    }

    @Test
    @DataSet(value = "dataset/trainee-data-criteria.xml", cleanBefore = true, cleanAfter = true)
    void findTrainingsForTrainee_whenNoCriteria_returnsEmpty() {
        TraineeTrainingSearchCriteriaDto criteria = new TraineeTrainingSearchCriteriaDto();
        criteria.setUsername("non.existing");

        List<Training> trainings = trainingQueryRepository.findTrainingsForTrainee(criteria);

        assertTrue(trainings.isEmpty());
    }

    @Test
    @DataSet(value = "dataset/trainee-data-criteria.xml", cleanBefore = true, cleanAfter = true)
    void findTrainingsForTrainee_whenPartialCriteriaMatch_returnsFilteredTrainings() {
        TraineeTrainingSearchCriteriaDto criteria = new TraineeTrainingSearchCriteriaDto();
        criteria.setUsername("sophia.martinez");
        criteria.setTrainerName("Coach");

        List<Training> trainings = trainingQueryRepository.findTrainingsForTrainee(criteria);

        assertFalse(trainings.isEmpty());
        assertTrue(trainings.stream().allMatch(t -> "sophia.martinez".equals(t.getTrainee().getUser().getUsername())));
        assertTrue(trainings.stream().anyMatch(t -> t.getTrainer().getUser().getFirstName().toLowerCase().contains("coach")));
    }

    @Test
    @DataSet(value = "dataset/trainee-data-criteria.xml", cleanBefore = true, cleanAfter = true)
    void findTrainingsForTrainee_whenDateRangeExcludesTrainings_returnsEmpty() {
        TraineeTrainingSearchCriteriaDto criteria = new TraineeTrainingSearchCriteriaDto();
        criteria.setUsername("sophia.martinez");
        criteria.setFromDate(LocalDate.of(2030, 1, 1));
        criteria.setToDate(LocalDate.of(2040, 1, 1));

        List<Training> trainings = trainingQueryRepository.findTrainingsForTrainee(criteria);

        assertTrue(trainings.isEmpty());
    }

    @Test
    @DataSet(value = "dataset/trainee-data-criteria.xml", cleanBefore = true, cleanAfter = true)
    void findTrainingsForTrainer_whenCriteriaMatch_returnsTrainings() {
        TrainerTrainingSearchCriteriaDto criteria = new TrainerTrainingSearchCriteriaDto();
        criteria.setUsername("coach.smith");
        criteria.setFromDate(LocalDate.of(2020, 1, 1));
        criteria.setToDate(LocalDate.of(2030, 1, 1));
        criteria.setTraineeName("Sophia Martinez");

        List<Training> trainings = trainingQueryRepository.findTrainingsForTrainer(criteria);

        assertFalse(trainings.isEmpty());
        assertTrue(trainings.stream().allMatch(t -> "coach.smith".equals(t.getTrainer().getUser().getUsername())));
    }

    @Test
    @DataSet(value = "dataset/trainee-data-criteria.xml", cleanBefore = true, cleanAfter = true)
    void findTrainingsForTrainer_whenNoMatch_returnsEmpty() {
        TrainerTrainingSearchCriteriaDto criteria = new TrainerTrainingSearchCriteriaDto();
        criteria.setUsername("trainer.ghost");

        List<Training> trainings = trainingQueryRepository.findTrainingsForTrainer(criteria);

        assertTrue(trainings.isEmpty());
    }
}
