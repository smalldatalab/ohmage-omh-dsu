package io.smalldata.ohmageomh.service;

import io.smalldata.ohmageomh.domain.Integration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Integration.
 */
public interface IntegrationService {

    /**
     * Save a integration.
     * 
     * @param integration the entity to save
     * @return the persisted entity
     */
    Integration save(Integration integration);

    /**
     *  Get all the integrations.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Integration> findAll(Pageable pageable);

    /**
     *  Get the "id" integration.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    Integration findOne(Long id);

    /**
     *  Delete the "id" integration.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the integration corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Integration> search(String query, Pageable pageable);
}
