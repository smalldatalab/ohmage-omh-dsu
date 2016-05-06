package io.smalldata.ohmageomh.service.impl;

import io.smalldata.ohmageomh.service.DataTypeService;
import io.smalldata.ohmageomh.domain.DataType;
import io.smalldata.ohmageomh.repository.DataTypeRepository;
import io.smalldata.ohmageomh.repository.search.DataTypeSearchRepository;
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
 * Service Implementation for managing DataType.
 */
@Service
@Transactional
public class DataTypeServiceImpl implements DataTypeService{

    private final Logger log = LoggerFactory.getLogger(DataTypeServiceImpl.class);
    
    @Inject
    private DataTypeRepository dataTypeRepository;
    
    @Inject
    private DataTypeSearchRepository dataTypeSearchRepository;
    
    /**
     * Save a dataType.
     * 
     * @param dataType the entity to save
     * @return the persisted entity
     */
    public DataType save(DataType dataType) {
        log.debug("Request to save DataType : {}", dataType);
        DataType result = dataTypeRepository.save(dataType);
        dataTypeSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the dataTypes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<DataType> findAll(Pageable pageable) {
        log.debug("Request to get all DataTypes");
        Page<DataType> result = dataTypeRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one dataType by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public DataType findOne(Long id) {
        log.debug("Request to get DataType : {}", id);
        DataType dataType = dataTypeRepository.findOne(id);
        return dataType;
    }

    /**
     *  Delete the  dataType by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete DataType : {}", id);
        dataTypeRepository.delete(id);
        dataTypeSearchRepository.delete(id);
    }

    /**
     * Search for the dataType corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<DataType> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of DataTypes for query {}", query);
        return dataTypeSearchRepository.search(queryStringQuery(query), pageable);
    }
}
