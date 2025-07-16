package com.gcs.app.mapper;

import com.gcs.app.rest.TrainingResponse;
import com.gcs.app.facade.dto.TrainingCreateRequestDto;
import com.gcs.app.facade.dto.TrainingResponseDto;
import com.gcs.app.model.Training;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingMapper {

    Training toEntity(TrainingCreateRequestDto dto);

    TrainingResponseDto toDto(Training training);

    TrainingResponse toRestModel(TrainingResponseDto dto);
}
