package com.gcs.app.mapper;

import com.gcs.app.dto.TrainerDto;
import com.gcs.app.model.Trainer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    TrainerDto toDto(Trainer trainer);
    Trainer toEntity(TrainerDto dto);
}
