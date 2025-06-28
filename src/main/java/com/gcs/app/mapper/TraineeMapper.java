package com.gcs.app.mapper;

import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeResponseDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.model.Trainee;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TraineeMapper {

    Trainee toEntity(TraineeCreateRequestDto dto);

    Trainee toUpdateEntity(TraineeUpdateRequestDto dto);

    TraineeResponseDto toDto(Trainee trainee);
}
