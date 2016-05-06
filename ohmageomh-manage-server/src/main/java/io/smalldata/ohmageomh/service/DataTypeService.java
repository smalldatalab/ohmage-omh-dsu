package io.smalldata.ohmageomh.service;

import io.smalldata.ohmageomh.domain.DataType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing DataType.
 */
public interface DataTypeService {

    /**
     * Save a dataType.
     * 
     * @param dataType the entity to save
     * @return the persisted entity
     */
    DataType save(DataType dataType);

    /**
     *  Get all the dataTypes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<DataType> findAll(Pageable pageable);

    /**
     *  Get the "id" dataType.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    DataType findOne(Long id);

    /**
     *  Delete the "id" dataType.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the dataType corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<DataType> search(String query, Pageable pageable);
}
