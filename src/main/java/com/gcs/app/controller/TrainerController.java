package com.gcs.app.controller;

import com.gcs.app.dto.AuthResponse;
import com.gcs.app.dto.StatusUpdateRequest;
import com.gcs.app.dto.TrainerProfileResponse;
import com.gcs.app.dto.TrainerRegistrationRequest;
import com.gcs.app.dto.TrainerUpdateRequest;
import com.gcs.app.dto.TrainingResponse;
import com.gcs.app.facade.GymFacade;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final GymFacade gymFacade;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerTrainer(
            @Valid @RequestBody TrainerRegistrationRequest request) {
        return ResponseEntity.ok(gymFacade.createTrainer(request));
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(
            @PathVariable String username) {
        return ResponseEntity.ok(gymFacade.getTrainerByUsername(username));
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> updateTrainerProfile(
            @PathVariable String username,
            @Valid @RequestBody TrainerUpdateRequest request) {

        return ResponseEntity.ok(gymFacade.updateTrainer(request, username));
    }

    @PatchMapping("/{username}/change-activation-status")
    public ResponseEntity<Void> changeActivationStatus(
            @PathVariable String username,
            @Valid @RequestBody StatusUpdateRequest request) {
        gymFacade.setTrainerActive(username, request.getIsActive());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingResponse>> getTrainerTrainings(
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String traineeName) {

        var criteria = new TrainerTrainingSearchCriteriaDto();
        criteria.setUsername(username);
        criteria.setFromDate(periodFrom);
        criteria.setToDate(periodTo);
        criteria.setTraineeName(traineeName);

        return ResponseEntity.ok(gymFacade.getTrainerTrainings(criteria));
    }
}
