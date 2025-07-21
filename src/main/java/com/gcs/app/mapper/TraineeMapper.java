package com.gcs.app.mapper;

import com.gcs.app.facade.dto.TraineeCreateRequestDto;
import com.gcs.app.facade.dto.TraineeResponseDto;
import com.gcs.app.facade.dto.TraineeUpdateRequestDto;
import com.gcs.app.model.Trainee;
import com.gcs.app.rest.TraineeAssignedTrainersUpdateResponse;
import com.gcs.app.rest.TraineeCreateRequest;
import com.gcs.app.rest.TraineeGetResponse;
import com.gcs.app.rest.TraineeUpdateRequest;
import com.gcs.app.rest.TraineeUpdateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {TrainerMapper.class})
public interface TraineeMapper {

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    Trainee toEntity(TraineeCreateRequestDto dto);

    TraineeResponseDto toDto(Trainee trainee);

    Trainee update(@MappingTarget Trainee trainee, TraineeUpdateRequestDto dto);

    TraineeCreateRequestDto toCreateRequestDto(TraineeCreateRequest request);

    TraineeUpdateRequestDto toUpdateRequestDto(TraineeUpdateRequest request);

    TraineeGetResponse toRestModel(Trainee trainee);

    TraineeUpdateResponse toUpdateRestModel(Trainee trainee);

    TraineeAssignedTrainersUpdateResponse toAssignedTrainersRestModel(Trainee trainee);
}
