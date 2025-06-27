package com.gcs.app.mapper;

import com.gcs.app.dto.TrainingDto;
import com.gcs.app.model.Training;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingMapper {
    TrainingDto toDto(Training training);
    Training toEntity(TrainingDto dto);
}
