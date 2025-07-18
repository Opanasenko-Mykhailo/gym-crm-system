package com.gcs.app.mapper;

import com.gcs.app.facade.dto.TrainingCreateRequestDto;
import com.gcs.app.facade.dto.TrainingResponseDto;
import com.gcs.app.model.Training;
import com.gcs.app.rest.TrainerTrainingGetResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingMapper {

    Training toEntity(TrainingCreateRequestDto dto);

    TrainingResponseDto toDto(Training training);

    TrainerTrainingGetResponse toRestModel(Training training);
}
