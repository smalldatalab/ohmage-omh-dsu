package io.smalldata.ohmageomh.web.rest;

import io.smalldata.ohmageomh.OhmageApp;
import io.smalldata.ohmageomh.domain.Survey;
import io.smalldata.ohmageomh.repository.SurveyRepository;
import io.smalldata.ohmageomh.service.SurveyService;
import io.smalldata.ohmageomh.repository.search.SurveySearchRepository;

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
 * Test class for the SurveyResource REST controller.
 *
 * @see SurveyResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OhmageApp.class)
@WebAppConfiguration
@IntegrationTest
public class SurveyResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_VERSION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;
    private static final String DEFAULT_DEFINITION = "AAAAA";
    private static final String UPDATED_DEFINITION = "BBBBB";

    @Inject
    private SurveyRepository surveyRepository;

    @Inject
    private SurveyService surveyService;

    @Inject
    private SurveySearchRepository surveySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSurveyMockMvc;

    private Survey survey;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SurveyResource surveyResource = new SurveyResource();
        ReflectionTestUtils.setField(surveyResource, "surveyService", surveyService);
        this.restSurveyMockMvc = MockMvcBuilders.standaloneSetup(surveyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        surveySearchRepository.deleteAll();
        survey = new Survey();
        survey.setName(DEFAULT_NAME);
        survey.setVersion(DEFAULT_VERSION);
        survey.setDescription(DEFAULT_DESCRIPTION);
        survey.setIsPublic(DEFAULT_IS_PUBLIC);
        survey.setDefinition(DEFAULT_DEFINITION);
    }

    @Test
    @Transactional
    public void createSurvey() throws Exception {
        int databaseSizeBeforeCreate = surveyRepository.findAll().size();

        // Create the Survey

        restSurveyMockMvc.perform(post("/api/surveys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(survey)))
                .andExpect(status().isCreated());

        // Validate the Survey in the database
        List<Survey> surveys = surveyRepository.findAll();
        assertThat(surveys).hasSize(databaseSizeBeforeCreate + 1);
        Survey testSurvey = surveys.get(surveys.size() - 1);
        assertThat(testSurvey.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSurvey.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testSurvey.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSurvey.isIsPublic()).isEqualTo(DEFAULT_IS_PUBLIC);
        assertThat(testSurvey.getDefinition()).isEqualTo(DEFAULT_DEFINITION);

        // Validate the Survey in ElasticSearch
        Survey surveyEs = surveySearchRepository.findOne(testSurvey.getId());
        assertThat(surveyEs).isEqualToComparingFieldByField(testSurvey);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        survey.setName(null);

        // Create the Survey, which fails.

        restSurveyMockMvc.perform(post("/api/surveys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(survey)))
                .andExpect(status().isBadRequest());

        List<Survey> surveys = surveyRepository.findAll();
        assertThat(surveys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        survey.setVersion(null);

        // Create the Survey, which fails.

        restSurveyMockMvc.perform(post("/api/surveys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(survey)))
                .andExpect(status().isBadRequest());

        List<Survey> surveys = surveyRepository.findAll();
        assertThat(surveys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIsPublicIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        survey.setIsPublic(null);

        // Create the Survey, which fails.

        restSurveyMockMvc.perform(post("/api/surveys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(survey)))
                .andExpect(status().isBadRequest());

        List<Survey> surveys = surveyRepository.findAll();
        assertThat(surveys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDefinitionIsRequired() throws Exception {
        int databaseSizeBeforeTest = surveyRepository.findAll().size();
        // set the field null
        survey.setDefinition(null);

        // Create the Survey, which fails.

        restSurveyMockMvc.perform(post("/api/surveys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(survey)))
                .andExpect(status().isBadRequest());

        List<Survey> surveys = surveyRepository.findAll();
        assertThat(surveys).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSurveys() throws Exception {
        // Initialize the database
        surveyRepository.saveAndFlush(survey);

        // Get all the surveys
        restSurveyMockMvc.perform(get("/api/surveys?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(survey.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC.booleanValue())))
                .andExpect(jsonPath("$.[*].definition").value(hasItem(DEFAULT_DEFINITION.toString())));
    }

    @Test
    @Transactional
    public void getSurvey() throws Exception {
        // Initialize the database
        surveyRepository.saveAndFlush(survey);

        // Get the survey
        restSurveyMockMvc.perform(get("/api/surveys/{id}", survey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(survey.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC.booleanValue()))
            .andExpect(jsonPath("$.definition").value(DEFAULT_DEFINITION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSurvey() throws Exception {
        // Get the survey
        restSurveyMockMvc.perform(get("/api/surveys/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSurvey() throws Exception {
        // Initialize the database
        surveyService.save(survey);

        int databaseSizeBeforeUpdate = surveyRepository.findAll().size();

        // Update the survey
        Survey updatedSurvey = new Survey();
        updatedSurvey.setId(survey.getId());
        updatedSurvey.setName(UPDATED_NAME);
        updatedSurvey.setVersion(UPDATED_VERSION);
        updatedSurvey.setDescription(UPDATED_DESCRIPTION);
        updatedSurvey.setIsPublic(UPDATED_IS_PUBLIC);
        updatedSurvey.setDefinition(UPDATED_DEFINITION);

        restSurveyMockMvc.perform(put("/api/surveys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSurvey)))
                .andExpect(status().isOk());

        // Validate the Survey in the database
        List<Survey> surveys = surveyRepository.findAll();
        assertThat(surveys).hasSize(databaseSizeBeforeUpdate);
        Survey testSurvey = surveys.get(surveys.size() - 1);
        assertThat(testSurvey.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSurvey.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testSurvey.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSurvey.isIsPublic()).isEqualTo(UPDATED_IS_PUBLIC);
        assertThat(testSurvey.getDefinition()).isEqualTo(UPDATED_DEFINITION);

        // Validate the Survey in ElasticSearch
        Survey surveyEs = surveySearchRepository.findOne(testSurvey.getId());
        assertThat(surveyEs).isEqualToComparingFieldByField(testSurvey);
    }

    @Test
    @Transactional
    public void deleteSurvey() throws Exception {
        // Initialize the database
        surveyService.save(survey);

        int databaseSizeBeforeDelete = surveyRepository.findAll().size();

        // Get the survey
        restSurveyMockMvc.perform(delete("/api/surveys/{id}", survey.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean surveyExistsInEs = surveySearchRepository.exists(survey.getId());
        assertThat(surveyExistsInEs).isFalse();

        // Validate the database is empty
        List<Survey> surveys = surveyRepository.findAll();
        assertThat(surveys).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSurvey() throws Exception {
        // Initialize the database
        surveyService.save(survey);

        // Search the survey
        restSurveyMockMvc.perform(get("/api/_search/surveys?query=id:" + survey.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(survey.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC.booleanValue())))
            .andExpect(jsonPath("$.[*].definition").value(hasItem(DEFAULT_DEFINITION.toString())));
    }
}
