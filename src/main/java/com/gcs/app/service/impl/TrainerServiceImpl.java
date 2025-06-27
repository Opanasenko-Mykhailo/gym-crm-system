package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.model.Trainer;
import com.gcs.app.service.TrainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.gcs.app.util.UserUtils.generateRandomPassword;
import static com.gcs.app.util.UserUtils.generateUsername;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDao;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        log.info("Creating trainer: {} {}", trainer.getFirstName(), trainer.getLastName());

        trainer.setUsername(generateUsername(trainer.getFirstName(), trainer.getLastName(), trainerDao.getAllUsernames()));
        trainer.setPassword(generateRandomPassword());
        trainer.setIsActive(true);

        Trainer createdTrainer = trainerDao.create(trainer);
        log.debug("Trainer created: {}", createdTrainer);

        return createdTrainer;
    }

    @Override
    public Trainer updateTrainer(Long userId, Trainer updatedTrainer) {
        log.info("Updating trainer with userId: {}", userId);

        validateTrainerExists(userId);

        updatedTrainer.setUserId(userId);
        updatedTrainer.setUsername(generateUsername(updatedTrainer.getFirstName(), updatedTrainer.getLastName(), trainerDao.getAllUsernames()));

        Trainer savedTrainer = trainerDao.update(updatedTrainer);
        log.debug("Trainer updated: {}", savedTrainer);

        return savedTrainer;
    }

    @Override
    public Trainer getTrainer(Long userId) {
        log.info("Retrieving trainer with userId: {}", userId);

        Trainer trainer = validateTrainerExists(userId).orElseThrow(() -> new ServiceException(String.format("Trainer with userId {} not found", userId)));

        log.debug("Trainer retrieved: {}", trainer);
        return trainer;
    }

    private Optional<Trainer> validateTrainerExists(Long userId) {
        Optional<Trainer> trainer = trainerDao.get(userId);

        if (trainer.isEmpty()) {
            throw new ServiceException(String.format("Trainer with userId {} not found", userId));
        }

        return trainer;
    }
}