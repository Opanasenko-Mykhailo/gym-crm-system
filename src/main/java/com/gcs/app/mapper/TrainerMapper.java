package com.gcs.app.mapper;

import com.gcs.app.facade.dto.TrainerCreateRequestDto;
import com.gcs.app.facade.dto.TrainerResponseDto;
import com.gcs.app.facade.dto.TrainerUpdateRequestDto;
import com.gcs.app.model.Trainer;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TrainerMapper {

    Trainer toEntity(TrainerCreateRequestDto dto);

    Trainer toUpdateEntity(TrainerUpdateRequestDto dto);

    TrainerResponseDto toDto(Trainer trainer);
}
