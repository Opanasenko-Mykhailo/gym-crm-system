package com.gcs.app.mapper;

import com.gcs.app.dto.TraineeCreateRequestDto;
import com.gcs.app.dto.TraineeResponseDto;
import com.gcs.app.dto.TraineeUpdateRequestDto;
import com.gcs.app.model.Trainee;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TraineeMapper {

    Trainee toEntity(TraineeCreateRequestDto dto);

    Trainee toUpdateEntity(TraineeUpdateRequestDto dto);

    TraineeResponseDto toDto(Trainee trainee);
}
