package com.gcs.app.dao.criteria;

import com.gcs.app.facade.dto.TraineeTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TrainerTrainingSearchCriteriaDto;
import com.gcs.app.facade.dto.TrainingSearchCriteria;
import com.gcs.app.model.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TrainingQueryBuilder {

    public CriteriaQuery<Training> build(CriteriaBuilder cb, TrainingSearchCriteria criteria) {
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();

        buildUsernamePredicate(criteria, cb, root, predicates);
        buildDatePredicate(criteria.getFromDate(), criteria.getToDate(), cb, root, predicates);

        if (criteria instanceof TraineeTrainingSearchCriteriaDto traineeCriteria) {
            buildTrainerNamePredicate(traineeCriteria, cb, root, predicates);
            buildTrainingTypePredicate(traineeCriteria, cb, root, predicates);
        } else if (criteria instanceof TrainerTrainingSearchCriteriaDto trainerCriteria) {
            buildTraineeNamePredicate(trainerCriteria, cb, root, predicates);
        }

        return query.select(root).where(predicates.toArray(new Predicate[0]));
    }

    private void buildUsernamePredicate(TrainingSearchCriteria criteria, CriteriaBuilder cb,
                                        Root<Training> root, List<Predicate> predicates) {
        Optional.ofNullable(criteria.getUsername())
                .filter(username -> !username.isBlank())
                .ifPresent(username -> {
                    String path = (criteria instanceof TraineeTrainingSearchCriteriaDto)
                            ? "trainee" : "trainer";
                    predicates.add(cb.equal(root.get(path).get("user").get("username"), username));
                });
    }

    private void buildDatePredicate(java.time.LocalDate fromDate, java.time.LocalDate toDate,
                                    CriteriaBuilder cb, Root<Training> root, List<Predicate> predicates) {
        Optional.ofNullable(fromDate)
                .ifPresent(date -> predicates.add(cb.greaterThanOrEqualTo(root.get("date"), date)));

        Optional.ofNullable(toDate)
                .ifPresent(date -> predicates.add(cb.lessThanOrEqualTo(root.get("date"), date)));
    }

    private void buildTrainerNamePredicate(TraineeTrainingSearchCriteriaDto criteria, CriteriaBuilder cb,
                                           Root<Training> root, List<Predicate> predicates) {
        Optional.ofNullable(criteria.getTrainerName())
                .filter(name -> !name.isBlank())
                .ifPresent(name -> {
                    Expression<String> fullName = cb.concat(
                            cb.concat(root.get("trainer").get("user").get("firstName"), " "),
                            root.get("trainer").get("user").get("lastName")
                    );
                    predicates.add(cb.like(cb.lower(fullName), "%" + name.toLowerCase() + "%"));
                });
    }

    private void buildTrainingTypePredicate(TraineeTrainingSearchCriteriaDto criteria, CriteriaBuilder cb,
                                            Root<Training> root, List<Predicate> predicates) {
        Optional.ofNullable(criteria.getTrainingTypeName())
                .filter(name -> !name.isBlank())
                .ifPresent(typeName ->
                        predicates.add(cb.equal(cb.lower(root.get("type").get("name")), typeName.toLowerCase())));
    }

    private void buildTraineeNamePredicate(TrainerTrainingSearchCriteriaDto criteria, CriteriaBuilder cb,
                                           Root<Training> root, List<Predicate> predicates) {
        Optional.ofNullable(criteria.getTraineeName())
                .filter(name -> !name.isBlank())
                .ifPresent(name -> {
                    Expression<String> fullName = cb.concat(
                            cb.concat(root.get("trainee").get("user").get("firstName"), " "),
                            root.get("trainee").get("user").get("lastName")
                    );
                    predicates.add(cb.like(cb.lower(fullName), "%" + name.toLowerCase() + "%"));
                });
    }
}
