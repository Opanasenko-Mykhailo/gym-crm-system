package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.dao.UserDao;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainer;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class TrainerDaoImpl extends UserDao implements TrainerDao {

    private final SessionFactory sessionFactory;

    public TrainerDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Trainer create(Trainer trainer) {
        getSession().persist(trainer);
        log.info("Created trainer with id: {}", trainer.getId());

        return trainer;
    }

    @Override
    public Optional<Trainer> get(Long userId) {
        Trainer trainer = getSession().byId(Trainer.class).load(userId);

        return Optional.ofNullable(trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        Trainer existing = getSession().byId(Trainer.class).load(trainer.getId());

        if (existing == null) {
            throw new EntityNotFoundException(String.format("Trainer with id %d not found", trainer.getId()));
        }

        Trainer merged = getSession().merge(trainer);
        log.info("Updated trainer with id: {}", merged.getId());

        return merged;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
