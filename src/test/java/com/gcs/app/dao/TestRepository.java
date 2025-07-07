package com.gcs.app.dao;

import com.gcs.app.config.TestConfig;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
public abstract class TestRepository<T> {

    @Autowired
    protected DataSource dataSource;

    @Autowired
    protected SessionFactory sessionFactory;

    protected T dao;

    protected abstract T initDao();

    protected abstract String getXmlDataPath();

    @BeforeEach
    void setUpDbUnit() throws Exception {
        this.dao = initDao();

        try (Connection connection = dataSource.getConnection()) {
            IDatabaseConnection dbUnitConn = new DatabaseConnection(connection, "PUBLIC");

            dbUnitConn.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
            dbUnitConn.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

            try (InputStream xmlStream = getClass().getResourceAsStream(getXmlDataPath())) {

                if (xmlStream == null) {
                    throw new IllegalStateException("DBUnit XML dataset not found: " + getXmlDataPath());
                }

                IDataSet dataSet = new FlatXmlDataSetBuilder().build(xmlStream);
                DatabaseOperation.CLEAN_INSERT.execute(dbUnitConn, dataSet);
            }
        }
        sessionFactory.getCurrentSession().beginTransaction();
    }
}