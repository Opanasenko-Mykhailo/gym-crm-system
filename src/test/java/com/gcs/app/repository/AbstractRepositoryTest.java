package com.gcs.app.repository;

import com.gcs.app.config.TestConfig;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@DBRider
@DBUnit(cacheConnection = true, leakHunter = true, caseSensitiveTableNames = false, schema = "PUBLIC")
public abstract class AbstractRepositoryTest<T> {

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected SessionFactory sessionFactory;

    @Autowired
    protected T dao;

    @BeforeEach
    void setUp() {
        sessionFactory.getCurrentSession().beginTransaction();
    }

    @AfterEach
    void tearDownTransaction() {
        if (sessionFactory.getCurrentSession().getTransaction().isActive()) {
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }
}