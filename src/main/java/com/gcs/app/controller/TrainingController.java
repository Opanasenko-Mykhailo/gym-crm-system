package com.gcs.app.controller;

import com.gcs.app.facade.GymFacade;
import com.gcs.app.rest.TrainingCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gcs.app.controller.ApiConstant.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH + "/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final GymFacade gymFacade;

    @PostMapping
    public ResponseEntity<Void> addTraining(@Valid @RequestBody TrainingCreateRequest request) {
        gymFacade.createTraining(request);

        return ResponseEntity.ok().build();
    }
}