package com.gcs.app.service.impl;

import com.gcs.app.dao.TrainingTypeDao;
import com.gcs.app.dao.transaction.TransactionalContext;
import com.gcs.app.facade.dto.TrainingTypeResponseDto;
import com.gcs.app.mapper.TrainingTypeMapper;
import com.gcs.app.model.TrainingType;
import com.gcs.app.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeDao trainingTypeDao;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    @TransactionalContext(readOnly = true)
    public List<TrainingTypeResponseDto> getAll() {
        log.debug("Fetching all training types");
        List<TrainingType> trainingTypes = trainingTypeDao.findAll();

        return trainingTypeMapper.toDtoList(trainingTypes);
    }
}
