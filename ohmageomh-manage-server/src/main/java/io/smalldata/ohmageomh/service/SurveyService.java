package io.smalldata.ohmageomh.service;

import io.smalldata.ohmageomh.domain.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Survey.
 */
public interface SurveyService {

    /**
     * Save a survey.
     * 
     * @param survey the entity to save
     * @return the persisted entity
     */
    Survey save(Survey survey);

    /**
     *  Get all the surveys.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Survey> findAll(Pageable pageable);

    /**
     *  Get the "id" survey.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Survey findOne(Long id);

    /**
     *  Delete the "id" survey.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the survey corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Survey> search(String query, Pageable pageable);
}
