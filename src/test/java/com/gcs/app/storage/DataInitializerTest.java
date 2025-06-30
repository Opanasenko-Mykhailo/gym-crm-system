package com.gcs.app.storage;

import com.gcs.app.exception.StorageInitializationException;
import com.gcs.app.model.enums.EntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class DataInitializerTest {

    private ResourceLoader resourceLoader;
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        resourceLoader = Mockito.mock(ResourceLoader.class);
        dataInitializer = new DataInitializer(resourceLoader);
    }

    @Test
    void testInitializeData_success() throws Exception {
        String testData = buildTestDataFile();

        InputStream inputStream = new ByteArrayInputStream(testData.getBytes());
        Resource mockResource = Mockito.mock(Resource.class);
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.getInputStream()).thenReturn(inputStream);

        when(resourceLoader.getResource("classpath:test-data.csv")).thenReturn(mockResource);
        ReflectionTestUtils.setField(dataInitializer, "initFilePath", "classpath:test-data.csv");

        Map<EntityType, List<Object>> result = dataInitializer.initializeData();

        assertNotNull(result);
        assertEquals(3, result.get(EntityType.TRAINEE).size());
        assertEquals(2, result.get(EntityType.TRAINER).size());
        assertEquals(2, result.get(EntityType.TRAINING).size());
    }

    @Test
    void testInitializeData_invalidLineFormat_throwsException() throws Exception {
        String invalidData = "trainee,OnlyOneField";
        InputStream inputStream = new ByteArrayInputStream(invalidData.getBytes());
        Resource mockResource = Mockito.mock(Resource.class);

        when(mockResource.exists()).thenReturn(true);
        when(mockResource.getInputStream()).thenReturn(inputStream);
        when(resourceLoader.getResource("classpath:invalid.csv")).thenReturn(mockResource);

        ReflectionTestUtils.setField(dataInitializer, "initFilePath", "classpath:invalid.csv");

        assertThrows(StorageInitializationException.class, dataInitializer::initializeData);
    }

    @Test
    void testInitializeData_missingFile_throwsException() throws NoSuchFieldException {
        Resource mockResource = Mockito.mock(Resource.class);
        when(mockResource.exists()).thenReturn(false);
        when(resourceLoader.getResource("classpath:missing.csv")).thenReturn(mockResource);

        ReflectionTestUtils.setField(dataInitializer, "initFilePath", "classpath:missing.csv");

        assertThrows(StorageInitializationException.class, dataInitializer::initializeData);
    }

    private String buildTestDataFile() {
        return """
                trainee,John,Smith,John.Smith,1990-01-01,123 Main St,,NULL
                trainee,John,Smith,John.Smith1,1995-01-01,12 Main St,,NULL
                trainee,Anna,Johnson,Anna.Johnson,1995-02-15,456 Oak Ave,,NULL
                trainer,Jane,Doe,Jane.Doe,YOGA,,NULL,NULL
                trainer,Mike,Wilson,Mike.Wilson,CARDIO,,NULL,NULL
                training,2,1,,Cardio Session,CARDIO,2025-10-23,PT2H
                training,3,2,,Strength Training,STRENGTH,2025-10-24,PT1H30M
                """;
    }
}