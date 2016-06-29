package io.smalldata.ohmageomh.web.rest;

import io.smalldata.ohmageomh.OhmageApp;
import io.smalldata.ohmageomh.domain.Integration;
import io.smalldata.ohmageomh.repository.IntegrationRepository;
import io.smalldata.ohmageomh.service.IntegrationService;
import io.smalldata.ohmageomh.repository.search.IntegrationSearchRepository;

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
 * Test class for the IntegrationResource REST controller.
 *
 * @see IntegrationResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OhmageApp.class)
@WebAppConfiguration
@IntegrationTest
public class IntegrationResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private IntegrationRepository integrationRepository;

    @Inject
    private IntegrationService integrationService;

    @Inject
    private IntegrationSearchRepository integrationSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restIntegrationMockMvc;

    private Integration integration;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        IntegrationResource integrationResource = new IntegrationResource();
        ReflectionTestUtils.setField(integrationResource, "integrationService", integrationService);
        this.restIntegrationMockMvc = MockMvcBuilders.standaloneSetup(integrationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        integrationSearchRepository.deleteAll();
        integration = new Integration();
        integration.setName(DEFAULT_NAME);
        integration.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createIntegration() throws Exception {
        int databaseSizeBeforeCreate = integrationRepository.findAll().size();

        // Create the Integration

        restIntegrationMockMvc.perform(post("/api/integrations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(integration)))
                .andExpect(status().isCreated());

        // Validate the Integration in the database
        List<Integration> integrations = integrationRepository.findAll();
        assertThat(integrations).hasSize(databaseSizeBeforeCreate + 1);
        Integration testIntegration = integrations.get(integrations.size() - 1);
        assertThat(testIntegration.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIntegration.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Integration in ElasticSearch
        Integration integrationEs = integrationSearchRepository.findOne(testIntegration.getId());
        assertThat(integrationEs).isEqualToComparingFieldByField(testIntegration);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = integrationRepository.findAll().size();
        // set the field null
        integration.setName(null);

        // Create the Integration, which fails.

        restIntegrationMockMvc.perform(post("/api/integrations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(integration)))
                .andExpect(status().isBadRequest());

        List<Integration> integrations = integrationRepository.findAll();
        assertThat(integrations).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllIntegrations() throws Exception {
        // Initialize the database
        integrationRepository.saveAndFlush(integration);

        // Get all the integrations
        restIntegrationMockMvc.perform(get("/api/integrations?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(integration.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getIntegration() throws Exception {
        // Initialize the database
        integrationRepository.saveAndFlush(integration);

        // Get the integration
        restIntegrationMockMvc.perform(get("/api/integrations/{id}", integration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(integration.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingIntegration() throws Exception {
        // Get the integration
        restIntegrationMockMvc.perform(get("/api/integrations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIntegration() throws Exception {
        // Initialize the database
        integrationService.save(integration);

        int databaseSizeBeforeUpdate = integrationRepository.findAll().size();

        // Update the integration
        Integration updatedIntegration = new Integration();
        updatedIntegration.setId(integration.getId());
        updatedIntegration.setName(UPDATED_NAME);
        updatedIntegration.setDescription(UPDATED_DESCRIPTION);

        restIntegrationMockMvc.perform(put("/api/integrations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedIntegration)))
                .andExpect(status().isOk());

        // Validate the Integration in the database
        List<Integration> integrations = integrationRepository.findAll();
        assertThat(integrations).hasSize(databaseSizeBeforeUpdate);
        Integration testIntegration = integrations.get(integrations.size() - 1);
        assertThat(testIntegration.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIntegration.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Integration in ElasticSearch
        Integration integrationEs = integrationSearchRepository.findOne(testIntegration.getId());
        assertThat(integrationEs).isEqualToComparingFieldByField(testIntegration);
    }

    @Test
    @Transactional
    public void deleteIntegration() throws Exception {
        // Initialize the database
        integrationService.save(integration);

        int databaseSizeBeforeDelete = integrationRepository.findAll().size();

        // Get the integration
        restIntegrationMockMvc.perform(delete("/api/integrations/{id}", integration.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean integrationExistsInEs = integrationSearchRepository.exists(integration.getId());
        assertThat(integrationExistsInEs).isFalse();

        // Validate the database is empty
        List<Integration> integrations = integrationRepository.findAll();
        assertThat(integrations).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchIntegration() throws Exception {
        // Initialize the database
        integrationService.save(integration);

        // Search the integration
        restIntegrationMockMvc.perform(get("/api/_search/integrations?query=id:" + integration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(integration.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
