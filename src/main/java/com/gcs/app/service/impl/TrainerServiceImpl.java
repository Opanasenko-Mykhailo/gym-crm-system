package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.dao.UserDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.PasswordChangeRequestDto;
import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.mapper.TrainerMapper;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.User;
import com.gcs.app.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

import static com.gcs.app.util.UserUtils.generateRandomPassword;
import static com.gcs.app.util.UserUtils.generateUsername;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDao;
    private final UserDao userDao;
    private final TrainerMapper trainerMapper;

    @Override
    public Trainer createTrainer(@Valid TrainerCreateRequestDto trainerCreateRequestDto) {
        Trainer trainer = trainerMapper.toEntity(trainerCreateRequestDto);
        log.info("Creating trainer: {} {}", trainer.getUser().getFirstName(), trainer.getUser().getLastName());

        Trainer trainerWithCredentials = trainer.toBuilder()
                .user(userWithCredentials(trainer.getUser()))
                .build();

        Trainer createdTrainer = trainerDao.create(trainerWithCredentials);
        log.debug("Trainer created: {}", createdTrainer);

        return createdTrainer;
    }

    public Trainer updateTrainer(@Valid TrainerUpdateRequestDto trainerUpdateRequestDto) {
        Trainer updatedTrainer = trainerMapper.toUpdateEntity(trainerUpdateRequestDto);

        Long userId = updatedTrainer.getId();
        log.info("Updating trainer with userId: {}", userId);

        validateTrainerExists(userId);

        Trainer trainerWithId = updatedTrainer.toBuilder().id(userId).build();
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

    @Override
    public Trainer getByUsername(String username) {
        log.info("Getting trainer by username: {}", username);

        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainer not found with username: %s", username)));
    }

    @Override
    public boolean authenticateTrainer(String username, String password) {
        log.info("Authenticating trainer with username: {}", username);

        return trainerDao.findByUsername(username)
                .map(t -> {
                    boolean matches = t.getUser().getPassword().equals(password);
                    log.info("Trainer {} authentication {}", username, matches ? "successful" : "failed: wrong password");

                    return matches;
                })
                .orElseGet(() -> {
                    log.warn("Trainer not found for username: {}", username);

                    return false;
                });
    }

    @Override
    public void changePassword(@Valid PasswordChangeRequestDto dto) {
        log.info("Changing password for username: {}", dto.getUsername());

        Trainer trainer = trainerDao.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ServiceException("User not found: " + dto.getUsername()));

        if (!trainer.getUser().getPassword().equals(dto.getOldPassword())) {
            log.warn("Old password does not match for username: {}", dto.getUsername());
            throw new ServiceException("Old password is incorrect");
        }

        User updatedUser = trainer.getUser().toBuilder()
                .password(dto.getNewPassword())
                .build();

        Trainer updatedTrainer = trainer.toBuilder()
                .user(updatedUser)
                .build();

        trainerDao.update(updatedTrainer);
        log.info("Password changed successfully for username: {}", dto.getUsername());
    }

    private Optional<Trainer> validateTrainerExists(Long userId) {
        Optional<Trainer> trainer = trainerDao.get(userId);

        if (trainer.isEmpty()) {
            throw new ServiceException(String.format("Trainer with userId %d not found", userId));
        }

        return trainer;
    }

    private User userWithCredentials(User user) {
        String username = generateUsername(user.getFirstName(), user.getLastName(), userDao.findAllUsernames());
        String password = generateRandomPassword();

        return user.builder()
                .username(username)
                .password(password)
                .isActive(true)
                .build();
    }
}