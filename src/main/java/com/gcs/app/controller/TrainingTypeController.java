package com.gcs.app.controller;

import com.gcs.app.facade.GymFacade;
import com.gcs.app.rest.TrainingTypeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.gcs.app.controller.ApiConstant.BASE_PATH;

@RestController
@RequestMapping(BASE_PATH + "/training-types")
@RequiredArgsConstructor
public class TrainingTypeController {

    private final GymFacade facade;

    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getTrainingTypes() {
        List<TrainingTypeResponse> trainingTypes = facade.getAllTrainingTypes();

        return ResponseEntity.ok(trainingTypes);
    }
}
