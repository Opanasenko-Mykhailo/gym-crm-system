package com.gcs.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDate;

@Entity
@Table(name = "trainings")
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trainee_id", nullable = false)
    private final Trainee trainee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    private final Trainer trainer;

    @Column(name = "training_name", nullable = false)
    private final String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "training_type_id", nullable = false)
    private final TrainingType type;

    @Column(name = "training_date", nullable = false)
    private final LocalDate date;

    @Column(name = "training_duration", nullable = false)
    private final Duration duration;
}


