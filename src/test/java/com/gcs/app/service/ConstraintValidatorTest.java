package com.gcs.app.service;

import com.gcs.app.config.TestConfig;
import com.gcs.app.facade.dto.PasswordChangeRequestDto;
import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class ConstraintValidatorTest {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private UserService userService;

    @Test
    void givenValidPasswordChangeDto_whenChangePassword_thenShouldThrowServiceExceptionButNotValidation() {
        PasswordChangeRequestDto dto = new PasswordChangeRequestDto();
        dto.setUsername("emma.jackson");
        dto.setOldPassword("OldPass1!");
        dto.setNewPassword("NewPass1!");

        Exception ex = assertThrows(Exception.class, () -> userService.changePassword(dto));
        assertFalse(ex instanceof ConstraintViolationException);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidPasswordChangeDtos")
    void givenInvalidPasswordChangeDto_whenChangePassword_thenShouldThrowConstraintViolationException(PasswordChangeRequestDto dto, String expectedMessage) {
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> userService.changePassword(dto));
        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    @Test
    void givenValidTraineeCreateDto_whenCreateTrainee_thenShouldNotThrowException() {
        TraineeCreateRequestDto dto = new TraineeCreateRequestDto();
        dto.setFirstName("James");
        dto.setLastName("Wilson");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setAddress("123 Main St");

        assertDoesNotThrow(() -> traineeService.createTrainee(dto));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTraineeCreateDtos")
    void givenInvalidTraineeCreateDto_whenCreateTrainee_thenShouldThrowConstraintViolationException(TraineeCreateRequestDto dto, String expectedMessage) {
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> traineeService.createTrainee(dto));
        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    @Test
    void givenValidTraineeTrainingDto_whenGetTraineeTrainings_thenShouldNotThrowException() {
        TraineeTrainingSearchCriteriaDto dto = new TraineeTrainingSearchCriteriaDto();
        dto.setUsername("sophia.martin");
        dto.setFromDate(LocalDate.now().minusDays(5));
        dto.setToDate(LocalDate.now());
        dto.setTrainerName("William Brown");
        dto.setTrainingTypeName("Type A");

        assertDoesNotThrow(() -> traineeService.getTraineeTrainings(dto));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTraineeTrainingDtos")
    void givenInvalidTraineeTrainingDto_whenGetTraineeTrainings_thenShouldThrowConstraintViolationException(TraineeTrainingSearchCriteriaDto dto, String expectedMessage) {
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> traineeService.getTraineeTrainings(dto));
        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    @Test
    void givenValidTrainerTrainingDto_whenGetTrainerTrainings_thenShouldNotThrowException() {
        TrainerTrainingSearchCriteriaDto dto = new TrainerTrainingSearchCriteriaDto();
        dto.setUsername("liam.thompson");
        dto.setFromDate(LocalDate.now().minusDays(5));
        dto.setToDate(LocalDate.now());
        dto.setTraineeName("Olivia Davis");

        assertDoesNotThrow(() -> trainerService.getTrainerTrainings(dto));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTrainerTrainingDtos")
    void givenInvalidTrainerTrainingDto_whenGetTrainerTrainings_thenShouldThrowConstraintViolationException(TrainerTrainingSearchCriteriaDto dto, String expectedMessage) {
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> trainerService.getTrainerTrainings(dto));
        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    @Test
    void givenValidTraineeUpdateDto_whenUpdateTrainee_thenShouldThrowServiceExceptionButNotValidation() {
        TraineeUpdateRequestDto dto = new TraineeUpdateRequestDto();
        dto.setFirstName("Noah");
        dto.setLastName("Taylor");
        dto.setUsername("noah.taylor");
        dto.setPassword("ValidPass1!");
        dto.setIsActive(true);
        dto.setDateOfBirth(LocalDate.of(1985, 5, 15));
        dto.setAddress("456 Another St");

        Exception ex = assertThrows(Exception.class, () -> traineeService.updateTrainee(dto));
        assertFalse(ex instanceof ConstraintViolationException);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTraineeUpdateDtos")
    void givenInvalidTraineeUpdateDto_whenUpdateTrainee_thenShouldThrowConstraintViolationException(TraineeUpdateRequestDto dto, String expectedMessage) {
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> traineeService.updateTrainee(dto));
        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    private static Stream<Arguments> provideInvalidPasswordChangeDtos() {
        PasswordChangeRequestDto blankUsername = new PasswordChangeRequestDto();
        blankUsername.setUsername("");
        blankUsername.setOldPassword("OldPass1!");
        blankUsername.setNewPassword("NewPass1!");

        PasswordChangeRequestDto blankOldPassword = new PasswordChangeRequestDto();
        blankOldPassword.setUsername("emma.jackson");
        blankOldPassword.setOldPassword("");
        blankOldPassword.setNewPassword("NewPass1!");

        PasswordChangeRequestDto blankNewPassword = new PasswordChangeRequestDto();
        blankNewPassword.setUsername("emma.jackson");
        blankNewPassword.setOldPassword("OldPass1!");
        blankNewPassword.setNewPassword("");

        PasswordChangeRequestDto invalidNewPassword = new PasswordChangeRequestDto();
        invalidNewPassword.setUsername("emma.jackson");
        invalidNewPassword.setOldPassword("OldPass1!");
        invalidNewPassword.setNewPassword("short");

        return Stream.of(
                Arguments.of(blankUsername, "Username is required"),
                Arguments.of(blankOldPassword, "Old password is required"),
                Arguments.of(blankNewPassword, "New password is required"),
                Arguments.of(invalidNewPassword, "New password must be at least 8 characters long")
        );
    }

    private static Stream<Arguments> provideInvalidTraineeCreateDtos() {
        TraineeCreateRequestDto blankFirstName = new TraineeCreateRequestDto();
        blankFirstName.setFirstName("");
        blankFirstName.setLastName("Wilson");
        blankFirstName.setDateOfBirth(LocalDate.of(1990, 1, 1));
        blankFirstName.setAddress("123 Main St");

        TraineeCreateRequestDto blankLastName = new TraineeCreateRequestDto();
        blankLastName.setFirstName("James");
        blankLastName.setLastName("");
        blankLastName.setDateOfBirth(LocalDate.of(1990, 1, 1));
        blankLastName.setAddress("123 Main St");

        TraineeCreateRequestDto futureDob = new TraineeCreateRequestDto();
        futureDob.setFirstName("James");
        futureDob.setLastName("Wilson");
        futureDob.setDateOfBirth(LocalDate.now().plusDays(1));
        futureDob.setAddress("123 Main St");

        TraineeCreateRequestDto shortAddress = new TraineeCreateRequestDto();
        shortAddress.setFirstName("James");
        shortAddress.setLastName("Wilson");
        shortAddress.setDateOfBirth(LocalDate.of(1990, 1, 1));
        shortAddress.setAddress("123");

        return Stream.of(
                Arguments.of(blankFirstName, "First name is required"),
                Arguments.of(blankLastName, "Last name is required"),
                Arguments.of(futureDob, "Date of birth must be in the past or today"),
                Arguments.of(shortAddress, "Address must be between 5 and 255 characters")
        );
    }

    private static Stream<Arguments> provideInvalidTraineeTrainingDtos() {
        TraineeTrainingSearchCriteriaDto blankUsername = new TraineeTrainingSearchCriteriaDto();
        blankUsername.setUsername("");
        blankUsername.setFromDate(LocalDate.now().minusDays(1));
        blankUsername.setToDate(LocalDate.now());

        TraineeTrainingSearchCriteriaDto tooLongTrainerName = new TraineeTrainingSearchCriteriaDto();
        tooLongTrainerName.setUsername("sophia.martin");
        tooLongTrainerName.setFromDate(LocalDate.now().minusDays(1));
        tooLongTrainerName.setToDate(LocalDate.now());
        tooLongTrainerName.setTrainerName("a".repeat(101));

        TraineeTrainingSearchCriteriaDto tooLongTrainingTypeName = new TraineeTrainingSearchCriteriaDto();
        tooLongTrainingTypeName.setUsername("sophia.martin");
        tooLongTrainingTypeName.setFromDate(LocalDate.now().minusDays(1));
        tooLongTrainingTypeName.setToDate(LocalDate.now());
        tooLongTrainingTypeName.setTrainingTypeName("a".repeat(101));

        return Stream.of(
                Arguments.of(blankUsername, "Username is required"),
                Arguments.of(tooLongTrainerName, "Trainer name must be at most 100 characters"),
                Arguments.of(tooLongTrainingTypeName, "Training type name must be at most 100 characters")
        );
    }

    private static Stream<Arguments> provideInvalidTrainerTrainingDtos() {
        TrainerTrainingSearchCriteriaDto blankUsername = new TrainerTrainingSearchCriteriaDto();
        blankUsername.setUsername("");
        blankUsername.setFromDate(LocalDate.now().minusDays(1));
        blankUsername.setToDate(LocalDate.now());
        blankUsername.setTraineeName("Ava Clark");

        TrainerTrainingSearchCriteriaDto tooLongUsername = new TrainerTrainingSearchCriteriaDto();
        tooLongUsername.setUsername("a".repeat(51));
        tooLongUsername.setFromDate(LocalDate.now().minusDays(1));
        tooLongUsername.setToDate(LocalDate.now());

        TrainerTrainingSearchCriteriaDto futureFromDate = new TrainerTrainingSearchCriteriaDto();
        futureFromDate.setUsername("liam.thompson");
        futureFromDate.setFromDate(LocalDate.now().plusDays(1));
        futureFromDate.setToDate(LocalDate.now());

        TrainerTrainingSearchCriteriaDto futureToDate = new TrainerTrainingSearchCriteriaDto();
        futureToDate.setUsername("liam.thompson");
        futureToDate.setFromDate(LocalDate.now().minusDays(1));
        futureToDate.setToDate(LocalDate.now().plusDays(2));

        TrainerTrainingSearchCriteriaDto tooLongTraineeName = new TrainerTrainingSearchCriteriaDto();
        tooLongTraineeName.setUsername("liam.thompson");
        tooLongTraineeName.setFromDate(LocalDate.now().minusDays(1));
        tooLongTraineeName.setToDate(LocalDate.now());
        tooLongTraineeName.setTraineeName("a".repeat(101));

        return Stream.of(
                Arguments.of(blankUsername, "Username is required"),
                Arguments.of(tooLongUsername, "Username must be at most 50 characters"),
                Arguments.of(futureFromDate, "From date must be in the past or present"),
                Arguments.of(futureToDate, "To date must be in the past or present"),
                Arguments.of(tooLongTraineeName, "Trainee name must be at most 100 characters")
        );
    }

    private static Stream<Arguments> provideInvalidTraineeUpdateDtos() {
        TraineeUpdateRequestDto blankFirstName = new TraineeUpdateRequestDto();
        blankFirstName.setFirstName("");
        blankFirstName.setLastName("Taylor");
        blankFirstName.setUsername("noah.taylor");
        blankFirstName.setPassword("ValidPass1!");
        blankFirstName.setIsActive(true);
        blankFirstName.setDateOfBirth(LocalDate.of(1985, 5, 15));
        blankFirstName.setAddress("456 Another St");

        TraineeUpdateRequestDto blankLastName = new TraineeUpdateRequestDto();
        blankLastName.setFirstName("Noah");
        blankLastName.setLastName("");
        blankLastName.setUsername("noah.taylor");
        blankLastName.setPassword("ValidPass1!");
        blankLastName.setIsActive(true);
        blankLastName.setDateOfBirth(LocalDate.of(1985, 5, 15));
        blankLastName.setAddress("456 Another St");

        TraineeUpdateRequestDto blankUsername = new TraineeUpdateRequestDto();
        blankUsername.setFirstName("Noah");
        blankUsername.setLastName("Taylor");
        blankUsername.setUsername("");
        blankUsername.setPassword("ValidPass1!");
        blankUsername.setIsActive(true);
        blankUsername.setDateOfBirth(LocalDate.of(1985, 5, 15));
        blankUsername.setAddress("456 Another St");

        TraineeUpdateRequestDto invalidPassword = new TraineeUpdateRequestDto();
        invalidPassword.setFirstName("Noah");
        invalidPassword.setLastName("Taylor");
        invalidPassword.setUsername("noah.taylor");
        invalidPassword.setPassword("nopass");
        invalidPassword.setIsActive(true);
        invalidPassword.setDateOfBirth(LocalDate.of(1985, 5, 15));
        invalidPassword.setAddress("456 Another St");

        TraineeUpdateRequestDto nullIsActive = new TraineeUpdateRequestDto();
        nullIsActive.setFirstName("Noah");
        nullIsActive.setLastName("Taylor");
        nullIsActive.setUsername("noah.taylor");
        nullIsActive.setPassword("ValidPass1!");
        nullIsActive.setIsActive(null);
        nullIsActive.setDateOfBirth(LocalDate.of(1985, 5, 15));
        nullIsActive.setAddress("456 Another St");

        TraineeUpdateRequestDto futureDob = new TraineeUpdateRequestDto();
        futureDob.setFirstName("Noah");
        futureDob.setLastName("Taylor");
        futureDob.setUsername("noah.taylor");
        futureDob.setPassword("ValidPass1!");
        futureDob.setIsActive(true);
        futureDob.setDateOfBirth(LocalDate.now().plusDays(1));
        futureDob.setAddress("456 Another St");

        TraineeUpdateRequestDto blankAddress = new TraineeUpdateRequestDto();
        blankAddress.setFirstName("Noah");
        blankAddress.setLastName("Taylor");
        blankAddress.setUsername("noah.taylor");
        blankAddress.setPassword("ValidPass1!");
        blankAddress.setIsActive(true);
        blankAddress.setDateOfBirth(LocalDate.of(1985, 5, 15));
        blankAddress.setAddress("");

        return Stream.of(
                Arguments.of(blankFirstName, "First name is required"),
                Arguments.of(blankLastName, "Last name is required"),
                Arguments.of(blankUsername, "Username is required"),
                Arguments.of(invalidPassword, "Password must be at least 8 characters long"),
                Arguments.of(nullIsActive, "Active status is required"),
                Arguments.of(futureDob, "Date of birth must be in the past or today"),
                Arguments.of(blankAddress, "Address is required")
        );
    }
}