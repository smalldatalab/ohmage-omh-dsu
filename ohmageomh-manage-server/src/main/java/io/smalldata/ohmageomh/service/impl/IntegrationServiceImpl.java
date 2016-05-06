package io.smalldata.ohmageomh.service.impl;

import io.smalldata.ohmageomh.service.IntegrationService;
import io.smalldata.ohmageomh.domain.Integration;
import io.smalldata.ohmageomh.repository.IntegrationRepository;
import io.smalldata.ohmageomh.repository.search.IntegrationSearchRepository;
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
 * Service Implementation for managing Integration.
 */
@Service
@Transactional
public class IntegrationServiceImpl implements IntegrationService{

    private final Logger log = LoggerFactory.getLogger(IntegrationServiceImpl.class);
    
    @Inject
    private IntegrationRepository integrationRepository;
    
    @Inject
    private IntegrationSearchRepository integrationSearchRepository;
    
    /**
     * Save a integration.
     * 
     * @param integration the entity to save
     * @return the persisted entity
     */
    public Integration save(Integration integration) {
        log.debug("Request to save Integration : {}", integration);
        Integration result = integrationRepository.save(integration);
        integrationSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the integrations.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Integration> findAll(Pageable pageable) {
        log.debug("Request to get all Integrations");
        Page<Integration> result = integrationRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one integration by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Integration findOne(Long id) {
        log.debug("Request to get Integration : {}", id);
        Integration integration = integrationRepository.findOneWithEagerRelationships(id);
        return integration;
    }

    /**
     *  Delete the  integration by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Integration : {}", id);
        integrationRepository.delete(id);
        integrationSearchRepository.delete(id);
    }

    /**
     * Search for the integration corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Integration> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Integrations for query {}", query);
        return integrationSearchRepository.search(queryStringQuery(query), pageable);
    }
}
