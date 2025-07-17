package com.gcs.app.config;

import com.gcs.app.model.Trainee;
import com.gcs.app.model.Trainer;
import com.gcs.app.model.Training;
import com.gcs.app.model.TrainingType;
import com.gcs.app.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.Properties;

@org.springframework.context.annotation.Configuration
public class HibernateConfig {

    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${hibernate.hbm2ddl-auto}")
    private String hbm2ddlAuto;

    @Value("${hibernate.show_sql}")
    private boolean showSql;

    @Value("${hibernate.format_sql}")
    private boolean formatSql;

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        Configuration configuration = buildConfiguration(dataSource);

        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties());

        return configuration.buildSessionFactory(registryBuilder.build());
    }

    private Configuration buildConfiguration(DataSource dataSource) {
        Configuration configuration = new Configuration();
        configuration.setProperties(getHibernateProperties(dataSource));

        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Trainee.class);
        configuration.addAnnotatedClass(Trainer.class);
        configuration.addAnnotatedClass(Training.class);
        configuration.addAnnotatedClass(TrainingType.class);

        return configuration;
    }

    private Properties getHibernateProperties(DataSource dataSource) {
        Properties properties = new Properties();
        properties.put("hibernate.connection.datasource", dataSource);
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        properties.put("hibernate.show_sql", String.valueOf(showSql));
        properties.put("hibernate.format_sql", String.valueOf(formatSql));
        properties.put("hibernate.current_session_context_class", "thread");

        return properties;
    }
}
