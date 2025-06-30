package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.mapper.TrainerMapper;
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
    private final TrainerMapper trainerMapper;

    @Override
    public Trainer createTrainer(TrainerCreateRequestDto trainerCreateRequestDto) {
        Trainer trainer = trainerMapper.toEntity(trainerCreateRequestDto);
        log.info("Creating trainer: {} {}", trainer.getFirstName(), trainer.getLastName());

        String username = generateUsername(trainer.getFirstName(), trainer.getLastName(), trainerDao.getAllUsernames());
        String password = generateRandomPassword();

        Trainer trainerWithCredentials = trainer.toBuilder()
                .username(username)
                .password(password)
                .isActive(true)
                .build();

        Trainer createdTrainer = trainerDao.create(trainerWithCredentials);
        log.debug("Trainer created: {}", createdTrainer);

        return createdTrainer;
    }

    public Trainer updateTrainer(TrainerUpdateRequestDto trainerUpdateRequestDto) {
        Trainer updatedTrainer = trainerMapper.toUpdateEntity(trainerUpdateRequestDto);

        Long userId = updatedTrainer.getUserId();
        log.info("Updating trainer with userId: {}", userId);

        validateTrainerExists(userId);

        Trainer trainerWithId = updatedTrainer.toBuilder().userId(userId).build();
        Trainer savedTrainer = trainerDao.update(trainerWithId);
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
            throw new ServiceException(String.format("Trainer with userId %d not found", userId));
        }

        return trainer;
    }
}