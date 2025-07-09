package com.gcs.app.service.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.dao.UserDao;
import com.gcs.app.exception.ServiceException;
import com.gcs.app.facade.dto.PasswordChangeRequestDto;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.mapper.TraineeMapper;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.User;
import com.gcs.app.service.TraineeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.gcs.app.util.UserUtils.generateRandomPassword;
import static com.gcs.app.util.UserUtils.generateUsername;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class TraineeServiceImpl implements TraineeService {

    private final TraineeDao traineeDao;
    private final UserDao userDao;
    private final TraineeMapper traineeMapper;

    @Override
    public Trainee createTrainee(@Valid TraineeCreateRequestDto requestDto) {
        Trainee trainee = traineeMapper.toEntity(requestDto);

        log.info("Creating trainee: {} {}", trainee.getUser().getFirstName(), trainee.getUser().getLastName());

        Trainee traineeWithCredentials = trainee.toBuilder()
                .user(userWithCredentials(trainee.getUser()))
                .build();

        Trainee createdTrainee = traineeDao.create(traineeWithCredentials);
        log.debug("Trainee created: {}", createdTrainee);

        return createdTrainee;
    }

    @Override
    public Trainee updateTrainee(@Valid TraineeUpdateRequestDto dto) {
        String username = dto.getUsername();
        Trainee existing = traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee with username %s not found", username)));

        Trainee updated = traineeMapper.update(existing, dto);

        return traineeDao.update(updated);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        log.info("Deleting trainee with username: {}", username);

        traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee with username %s not found", username)));

        traineeDao.deleteByUsername(username);
        log.debug("Trainee with username {} deleted", username);
    }

    @Override
    public Trainee getByUsername(String username) {
        log.info("Getting trainee by username: {}", username);

        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new ServiceException(String.format("Trainee not found with username: %s", username)));
    }

    @Override
    public void changePassword(@Valid PasswordChangeRequestDto dto) {
        log.info("Changing password for username: {}", dto.getUsername());

        Trainee trainee = traineeDao.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ServiceException("User not found: " + dto.getUsername()));

        if (!trainee.getUser().getPassword().equals(dto.getOldPassword())) {
            throw new ServiceException("Old password is incorrect");
        }

        User updatedUser = trainee.getUser().toBuilder()
                .password(dto.getNewPassword())
                .build();
        Trainee updatedTrainee = trainee.toBuilder()
                .user(updatedUser)
                .build();

        traineeDao.update(updatedTrainee);
        log.info("Password changed successfully for username: {}", dto.getUsername());
    }

    private User userWithCredentials(User user) {
        String username = generateUsername(user.getFirstName(), user.getLastName(), userDao.findAllUsernames());
        String password = generateRandomPassword();

        return User.builder()
                .username(username)
                .password(password)
                .isActive(true)
                .build();
    }
}