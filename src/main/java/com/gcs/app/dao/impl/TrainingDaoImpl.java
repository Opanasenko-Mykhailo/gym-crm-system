package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainingDao;
import com.gcs.app.dao.transaction.GymTransactional;
import com.gcs.app.model.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TrainingDaoImpl implements TrainingDao {

    private final SessionFactory sessionFactory;

    @GymTransactional
    @Override
    public Training create(Training training) {
        getSession().persist(training);
        log.info("Created training with id: {}", training.getId());

        return training;
    }

    @GymTransactional(readOnly = true)
    @Override
    public Optional<Training> get(Long id) {
        Training training = getSession().byId(Training.class).load(id);
        log.debug("Retrieved training with id: {}, found: {}", id, training != null);

        return Optional.ofNullable(training);
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
