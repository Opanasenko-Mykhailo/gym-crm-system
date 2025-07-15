package com.gcs.app.mapper;

import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerResponseDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.model.Trainer;
import com.gcs.app.dto.TrainerRegistrationRequest;
import com.gcs.app.dto.TrainerProfileResponse;
import com.gcs.app.dto.TrainerUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    Trainer toEntity(TrainerCreateRequestDto dto);

    TrainerResponseDto toDto(Trainer trainer);

    Trainer update(@MappingTarget Trainer trainer, TrainerUpdateRequestDto dto);

    TrainerCreateRequestDto toCreateRequestDto(TrainerRegistrationRequest request);

    TrainerUpdateRequestDto toRestModel(TrainerUpdateRequest request);

    @Mapping(source = "specialization.name", target = "specialization")
    TrainerProfileResponse toDtoSwagger(Trainer trainer);
}
