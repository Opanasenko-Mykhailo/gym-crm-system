package com.gcs.app.mapper;

import com.gcs.app.dto.TraineeDto;
import com.gcs.app.model.Trainee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TraineeMapper {
    TraineeDto toDto(Trainee trainee);
    Trainee toEntity(TraineeDto dto);
}
