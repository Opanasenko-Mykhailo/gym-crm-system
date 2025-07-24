package com.gcs.app.dao.impl;

import com.gcs.app.dao.TraineeDao;
import com.gcs.app.dao.criteria.TrainingQueryBuilder;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.facade.dto.TraineeTrainingSearchCriteriaDto;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TraineeDaoImpl implements TraineeDao {

    private final SessionFactory sessionFactory;
    private final TrainingQueryBuilder trainingQueryBuilder;

    @Override
    public Trainee create(Trainee trainee) {
        getSession().persist(trainee);
        log.info("Created trainee with id: {}", trainee.getId());

        return trainee;
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
    public void deleteByUsername(String username) {
        Trainee trainee = findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Trainee with username '%s' not found", username)));

        getSession().remove(trainee);
        log.info("Deleted trainee with username: {}", username);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        String hql = "FROM Trainee t JOIN FETCH t.user u LEFT JOIN FETCH t.trainers WHERE u.username = :username";

        Trainee result = getSession()
                .createQuery(hql, Trainee.class)
                .setParameter("username", username)
                .uniqueResult();

        log.info("Find trainee by username '{}': {}", username, result);

        return Optional.ofNullable(result);
    }

    @Override
    public List<Training> findByTraineeCriteria(TraineeTrainingSearchCriteriaDto criteria) {
        log.info("Searching trainings for trainee criteria: {}", criteria);

        Session session = getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Training> query = trainingQueryBuilder.build(cb, criteria);

        return session.createQuery(query).getResultList();
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
