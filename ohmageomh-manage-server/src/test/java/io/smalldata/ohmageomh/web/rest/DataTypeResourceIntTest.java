package io.smalldata.ohmageomh.web.rest;

import io.smalldata.ohmageomh.OhmageApp;
import io.smalldata.ohmageomh.domain.DataType;
import io.smalldata.ohmageomh.repository.DataTypeRepository;
import io.smalldata.ohmageomh.service.DataTypeService;
import io.smalldata.ohmageomh.repository.search.DataTypeSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the DataTypeResource REST controller.
 *
 * @see DataTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OhmageApp.class)
@WebAppConfiguration
@IntegrationTest
public class DataTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_SCHEMA_NAMESPACE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_SCHEMA_NAMESPACE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_SCHEMA_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_SCHEMA_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_SCHEMA_VERSION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_SCHEMA_VERSION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_CSV_MAPPER = "AAAAA";
    private static final String UPDATED_CSV_MAPPER = "BBBBB";

    @Inject
    private DataTypeRepository dataTypeRepository;

    @Inject
    private DataTypeService dataTypeService;

    @Inject
    private DataTypeSearchRepository dataTypeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDataTypeMockMvc;

    private DataType dataType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DataTypeResource dataTypeResource = new DataTypeResource();
        ReflectionTestUtils.setField(dataTypeResource, "dataTypeService", dataTypeService);
        this.restDataTypeMockMvc = MockMvcBuilders.standaloneSetup(dataTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        dataTypeSearchRepository.deleteAll();
        dataType = new DataType();
        dataType.setName(DEFAULT_NAME);
        dataType.setDescription(DEFAULT_DESCRIPTION);
        dataType.setSchemaNamespace(DEFAULT_SCHEMA_NAMESPACE);
        dataType.setSchemaName(DEFAULT_SCHEMA_NAME);
        dataType.setSchemaVersion(DEFAULT_SCHEMA_VERSION);
        dataType.setCsvMapper(DEFAULT_CSV_MAPPER);
    }

    @Test
    @Transactional
    public void createDataType() throws Exception {
        int databaseSizeBeforeCreate = dataTypeRepository.findAll().size();

        // Create the DataType

        restDataTypeMockMvc.perform(post("/api/data-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dataType)))
                .andExpect(status().isCreated());

        // Validate the DataType in the database
        List<DataType> dataTypes = dataTypeRepository.findAll();
        assertThat(dataTypes).hasSize(databaseSizeBeforeCreate + 1);
        DataType testDataType = dataTypes.get(dataTypes.size() - 1);
        assertThat(testDataType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDataType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testDataType.getSchemaNamespace()).isEqualTo(DEFAULT_SCHEMA_NAMESPACE);
        assertThat(testDataType.getSchemaName()).isEqualTo(DEFAULT_SCHEMA_NAME);
        assertThat(testDataType.getSchemaVersion()).isEqualTo(DEFAULT_SCHEMA_VERSION);
        assertThat(testDataType.getCsvMapper()).isEqualTo(DEFAULT_CSV_MAPPER);

        // Validate the DataType in ElasticSearch
        DataType dataTypeEs = dataTypeSearchRepository.findOne(testDataType.getId());
        assertThat(dataTypeEs).isEqualToComparingFieldByField(testDataType);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dataTypeRepository.findAll().size();
        // set the field null
        dataType.setName(null);

        // Create the DataType, which fails.

        restDataTypeMockMvc.perform(post("/api/data-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dataType)))
                .andExpect(status().isBadRequest());

        List<DataType> dataTypes = dataTypeRepository.findAll();
        assertThat(dataTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDataTypes() throws Exception {
        // Initialize the database
        dataTypeRepository.saveAndFlush(dataType);

        // Get all the dataTypes
        restDataTypeMockMvc.perform(get("/api/data-types?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(dataType.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].schemaNamespace").value(hasItem(DEFAULT_SCHEMA_NAMESPACE.toString())))
                .andExpect(jsonPath("$.[*].schemaName").value(hasItem(DEFAULT_SCHEMA_NAME.toString())))
                .andExpect(jsonPath("$.[*].schemaVersion").value(hasItem(DEFAULT_SCHEMA_VERSION.toString())))
                .andExpect(jsonPath("$.[*].csvMapper").value(hasItem(DEFAULT_CSV_MAPPER.toString())));
    }

    @Test
    @Transactional
    public void getDataType() throws Exception {
        // Initialize the database
        dataTypeRepository.saveAndFlush(dataType);

        // Get the dataType
        restDataTypeMockMvc.perform(get("/api/data-types/{id}", dataType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(dataType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.schemaNamespace").value(DEFAULT_SCHEMA_NAMESPACE.toString()))
            .andExpect(jsonPath("$.schemaName").value(DEFAULT_SCHEMA_NAME.toString()))
            .andExpect(jsonPath("$.schemaVersion").value(DEFAULT_SCHEMA_VERSION.toString()))
            .andExpect(jsonPath("$.csvMapper").value(DEFAULT_CSV_MAPPER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDataType() throws Exception {
        // Get the dataType
        restDataTypeMockMvc.perform(get("/api/data-types/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDataType() throws Exception {
        // Initialize the database
        dataTypeService.save(dataType);

        int databaseSizeBeforeUpdate = dataTypeRepository.findAll().size();

        // Update the dataType
        DataType updatedDataType = new DataType();
        updatedDataType.setId(dataType.getId());
        updatedDataType.setName(UPDATED_NAME);
        updatedDataType.setDescription(UPDATED_DESCRIPTION);
        updatedDataType.setSchemaNamespace(UPDATED_SCHEMA_NAMESPACE);
        updatedDataType.setSchemaName(UPDATED_SCHEMA_NAME);
        updatedDataType.setSchemaVersion(UPDATED_SCHEMA_VERSION);
        updatedDataType.setCsvMapper(UPDATED_CSV_MAPPER);

        restDataTypeMockMvc.perform(put("/api/data-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDataType)))
                .andExpect(status().isOk());

        // Validate the DataType in the database
        List<DataType> dataTypes = dataTypeRepository.findAll();
        assertThat(dataTypes).hasSize(databaseSizeBeforeUpdate);
        DataType testDataType = dataTypes.get(dataTypes.size() - 1);
        assertThat(testDataType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDataType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testDataType.getSchemaNamespace()).isEqualTo(UPDATED_SCHEMA_NAMESPACE);
        assertThat(testDataType.getSchemaName()).isEqualTo(UPDATED_SCHEMA_NAME);
        assertThat(testDataType.getSchemaVersion()).isEqualTo(UPDATED_SCHEMA_VERSION);
        assertThat(testDataType.getCsvMapper()).isEqualTo(UPDATED_CSV_MAPPER);

        // Validate the DataType in ElasticSearch
        DataType dataTypeEs = dataTypeSearchRepository.findOne(testDataType.getId());
        assertThat(dataTypeEs).isEqualToComparingFieldByField(testDataType);
    }

    @Test
    @Transactional
    public void deleteDataType() throws Exception {
        // Initialize the database
        dataTypeService.save(dataType);

        int databaseSizeBeforeDelete = dataTypeRepository.findAll().size();

        // Get the dataType
        restDataTypeMockMvc.perform(delete("/api/data-types/{id}", dataType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean dataTypeExistsInEs = dataTypeSearchRepository.exists(dataType.getId());
        assertThat(dataTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<DataType> dataTypes = dataTypeRepository.findAll();
        assertThat(dataTypes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDataType() throws Exception {
        // Initialize the database
        dataTypeService.save(dataType);

        // Search the dataType
        restDataTypeMockMvc.perform(get("/api/_search/data-types?query=id:" + dataType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dataType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].schemaNamespace").value(hasItem(DEFAULT_SCHEMA_NAMESPACE.toString())))
            .andExpect(jsonPath("$.[*].schemaName").value(hasItem(DEFAULT_SCHEMA_NAME.toString())))
            .andExpect(jsonPath("$.[*].schemaVersion").value(hasItem(DEFAULT_SCHEMA_VERSION.toString())))
            .andExpect(jsonPath("$.[*].csvMapper").value(hasItem(DEFAULT_CSV_MAPPER.toString())));
    }
}
