package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.dao.criteria.TrainingQueryBuilder;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
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
public class TrainerDaoImpl implements TrainerDao {

    private final SessionFactory sessionFactory;
    private final TrainingQueryBuilder trainingQueryBuilder;

    @Override
    public Trainer create(Trainer trainer) {
        getSession().persist(trainer);
        log.info("Created trainer with id: {}", trainer.getId());

        return trainer;
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

    @Override
    public Optional<Trainer> findByUsername(String username) {
        String hql = "FROM Trainer t JOIN FETCH t.user u WHERE u.username = :username";

        Trainer result = getSession()
                .createQuery(hql, Trainer.class)
                .setParameter("username", username)
                .uniqueResult();

        log.info("Find trainer by username '{}': {}", username, result);

        return Optional.ofNullable(result);
    }

    @Override
    public List<Training> findByTrainerCriteria(TrainerTrainingSearchCriteriaDto criteria) {
        log.info("Searching trainings for trainer criteria: {}", criteria);

        Session session = getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Training> query = trainingQueryBuilder.build(cb, criteria);

        return session.createQuery(query).getResultList();
    }

    @Override
    public List<Trainer> findAllNotAssignedToTrainee(Trainee trainee) {
        String hql = "SELECT t FROM Trainer t WHERE t NOT IN" +
                "(SELECT tr FROM Trainee trn JOIN trn.trainers tr WHERE trn = :trainee)";

        List<Trainer> result = getSession()
                .createQuery(hql, Trainer.class)
                .setParameter("trainee", trainee)
                .getResultList();

        log.info("Found not assigned trainers for trainee '{}': {}", trainee.getId(), result);

        return result;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
