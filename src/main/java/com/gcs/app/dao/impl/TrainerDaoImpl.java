package com.gcs.app.dao.impl;

import com.gcs.app.dao.TrainerDao;
import com.gcs.app.exception.EntityNotFoundException;
import com.gcs.app.facade.dto.TraineeTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TrainerDaoImpl implements TrainerDao {

    private final SessionFactory sessionFactory;

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
    public List<Training> findByTrainerAndCriteria(TrainerTrainingSearchCriteriaDto criteria) {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(criteria.getUsername())
                .filter(s -> !s.isBlank())
                .ifPresent(username ->
                        predicates.add(cb.equal(root.get("trainer").get("user").get("username"), username))
                );

        Optional.ofNullable(criteria.getFromDate())
                .ifPresent(fromDate ->
                        predicates.add(cb.greaterThanOrEqualTo(root.get("date"), fromDate))
                );

        Optional.ofNullable(criteria.getToDate())
                .ifPresent(toDate ->
                        predicates.add(cb.lessThanOrEqualTo(root.get("date"), toDate))
                );

        Optional.ofNullable(criteria.getTraineeName())
                .filter(s -> !s.isBlank())
                .ifPresent(traineeName -> {
                    Expression<String> fullName = cb.concat(
                            cb.concat(root.get("trainee").get("user").get("firstName"), " "),
                            root.get("trainee").get("user").get("lastName")
                    );
                    predicates.add(cb.like(cb.lower(fullName), "%" + traineeName.toLowerCase() + "%"));
                });

        query.select(root).where(predicates.toArray(new Predicate[0]));

        return session.createQuery(query).getResultList();
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
