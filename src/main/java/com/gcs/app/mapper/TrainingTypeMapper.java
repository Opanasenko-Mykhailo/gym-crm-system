package com.gcs.app.mapper;

import com.gcs.app.facade.dto.TrainingTypeResponseDto;
import com.gcs.app.model.TrainingType;
import com.gcs.app.rest.TrainingTypeResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    List<TrainingTypeResponseDto> toDtoList(List<TrainingType> trainingTypes);

    List<TrainingTypeResponse> toRestModelList(List<TrainingTypeResponseDto> dtoList);
}
