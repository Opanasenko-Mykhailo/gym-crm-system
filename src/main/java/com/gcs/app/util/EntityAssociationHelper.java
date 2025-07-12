package com.gcs.app.util;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;

import java.util.HashSet;
import java.util.Set;

public class EntityAssociationHelper {

    public static void addTrainerToTrainee(Trainee trainee, Trainer trainer) {
        if (!trainee.getTrainers().contains(trainer)) {
            trainee.getTrainers().add(trainer);
        }

        if (!trainer.getTrainees().contains(trainee)) {
            trainer.getTrainees().add(trainee);
        }
    }

    public static void removeTrainerFromTrainee(Trainee trainee, Trainer trainer) {
        trainee.getTrainers().remove(trainer);
        trainer.getTrainees().remove(trainee);
    }

    public static void setTraineeTrainers(Trainee trainee, Set<Trainer> newTrainers) {
        new HashSet<>(trainee.getTrainers())
                .forEach(oldTrainer -> removeTrainerFromTrainee(trainee, oldTrainer));

        newTrainers.forEach(trainer -> addTrainerToTrainee(trainee, trainer));
    }
}
