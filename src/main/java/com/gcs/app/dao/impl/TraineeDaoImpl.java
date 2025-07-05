package com.gcs.app.dao.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.dao.UserDao;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.model.Trainee;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class TraineeDaoImpl extends UserDao implements TraineeDao {

    private final SessionFactory sessionFactory;

    public TraineeDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Trainee create(Trainee trainee) {
        getSession().persist(trainee);
        log.info("Created trainee with id: {}", trainee.getId());

        return trainee;
    }

    @Override
    public Optional<Trainee> get(Long userId) {
        Trainee trainee = getSession().byId(Trainee.class).load(userId);

        return Optional.ofNullable(trainee);
    }

    @Override
    public Trainee update(Trainee trainee) {
        Trainee existing = getSession().byId(Trainee.class).load(trainee.getId());

        if (existing == null) {
            throw new EntityNotFoundException(String.format("Trainee with id %d not found", trainee.getId()));
        }

        Trainee merged = getSession().merge(trainee);
        log.info("Updated trainee with id: {}", merged.getId());

        return merged;
    }

    @Override
    public void delete(Long userId) {
        Trainee trainee = getSession().byId(Trainee.class).load(userId);

        if (trainee == null) {
            throw new EntityNotFoundException(String.format("Trainee with id %d not found", userId));
        }

        getSession().remove(trainee);
        log.info("Deleted trainee with id: {}", userId);
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
