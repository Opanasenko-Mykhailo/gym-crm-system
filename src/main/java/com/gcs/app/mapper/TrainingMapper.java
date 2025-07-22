package com.gcs.app.mapper;

import com.gcs.app.facade.dto.TrainingCreateRequestDto;
import com.gcs.app.facade.dto.TrainingResponseDto;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.rest.TraineeTrainingGetResponse;
import com.gcs.app.rest.TrainerTrainingGetResponse;
import com.gcs.app.rest.TrainingCreateRequest;
import com.gcs.app.rest.TrainingTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingMapper {

    Training toEntity(TrainingCreateRequestDto dto);

    TrainingResponseDto toDto(Training training);

    TrainerTrainingGetResponse toTrainerTrainingRestModel(Training training);

    TraineeTrainingGetResponse toTraineeTrainingRestModel(Training training);

    TrainingCreateRequestDto toTrainingCreateRequestDto(TrainingCreateRequest trainingCreateRequest);

    @Mapping(source = "name", target = "trainingType")
    @Mapping(source = "id", target = "trainingTypeId")
    TrainingTypeResponse toTrainingTypeRestModel(TrainingType trainingType);
}
