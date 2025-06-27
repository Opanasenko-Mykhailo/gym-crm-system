package com.gcs.app.mapper;

import com.gcs.app.dto.TrainingCreateRequestDto;
import com.gcs.app.dto.TrainingResponseDto;
import com.gcs.app.model.Training;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingMapper {

    Training toEntity(TrainingCreateRequestDto dto);

    TrainingResponseDto toDto(Training training);
}
