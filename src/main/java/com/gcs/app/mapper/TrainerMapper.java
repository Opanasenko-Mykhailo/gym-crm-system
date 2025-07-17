package com.gcs.app.mapper;

import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerResponseDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.model.Trainer;
import com.gcs.app.rest.TrainerRegistrationRequest;
import com.gcs.app.rest.TrainerProfileResponse;
import com.gcs.app.rest.TrainerUpdateRequest;
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

    TrainerUpdateRequestDto toUpdateRequestDto(TrainerUpdateRequest request);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "specialization.name", target = "specialization")
    TrainerProfileResponse toRestModel(Trainer trainer);
}
