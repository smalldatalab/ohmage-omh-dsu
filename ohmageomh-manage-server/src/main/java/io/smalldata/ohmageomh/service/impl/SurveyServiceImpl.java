package io.smalldata.ohmageomh.service.impl;

import io.smalldata.ohmageomh.service.SurveyService;
import io.smalldata.ohmageomh.domain.Survey;
import io.smalldata.ohmageomh.repository.SurveyRepository;
import io.smalldata.ohmageomh.repository.search.SurveySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Survey.
 */
@Service
@Transactional
public class SurveyServiceImpl implements SurveyService{

    private final Logger log = LoggerFactory.getLogger(SurveyServiceImpl.class);
    
    @Inject
    private SurveyRepository surveyRepository;
    
    @Inject
    private SurveySearchRepository surveySearchRepository;
    
    /**
     * Save a survey.
     * 
     * @param survey the entity to save
     * @return the persisted entity
     */
    public Survey save(Survey survey) {
        log.debug("Request to save Survey : {}", survey);
        Survey result = surveyRepository.save(survey);
        surveySearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the surveys.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Survey> findAll(Pageable pageable) {
        log.debug("Request to get all Surveys");
        Page<Survey> result = surveyRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one survey by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Survey findOne(Long id) {
        log.debug("Request to get Survey : {}", id);
        Survey survey = surveyRepository.findOne(id);
        return survey;
    }

    /**
     *  Delete the  survey by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Survey : {}", id);
        surveyRepository.delete(id);
        surveySearchRepository.delete(id);
    }

    /**
     * Search for the survey corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Survey> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Surveys for query {}", query);
        return surveySearchRepository.search(queryStringQuery(query), pageable);
    }
}
