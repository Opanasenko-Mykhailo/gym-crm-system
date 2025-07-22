package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainingTypeDao;
import com.gcs.app.model.TrainingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    private final SessionFactory sessionFactory;

    @Override
    public List<TrainingType> findAll() {
        List<TrainingType> result = getSession()
                .createQuery("FROM TrainingType", TrainingType.class)
                .getResultList();

        log.info("Loaded {} training types", result.size());
        return result;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
