package io.smalldata.ohmageomh.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.smalldata.ohmageomh.domain.DataType;
import io.smalldata.ohmageomh.service.DataTypeService;
import io.smalldata.ohmageomh.web.rest.util.HeaderUtil;
import io.smalldata.ohmageomh.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing DataType.
 */
@RestController
@RequestMapping("/api")
public class DataTypeResource {

    private final Logger log = LoggerFactory.getLogger(DataTypeResource.class);
        
    @Inject
    private DataTypeService dataTypeService;
    
    /**
     * POST  /data-types : Create a new dataType.
     *
     * @param dataType the dataType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dataType, or with status 400 (Bad Request) if the dataType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/data-types",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DataType> createDataType(@Valid @RequestBody DataType dataType) throws URISyntaxException {
        log.debug("REST request to save DataType : {}", dataType);
        if (dataType.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("dataType", "idexists", "A new dataType cannot already have an ID")).body(null);
        }
        DataType result = dataTypeService.save(dataType);
        return ResponseEntity.created(new URI("/api/data-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("dataType", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /data-types : Updates an existing dataType.
     *
     * @param dataType the dataType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dataType,
     * or with status 400 (Bad Request) if the dataType is not valid,
     * or with status 500 (Internal Server Error) if the dataType couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/data-types",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DataType> updateDataType(@Valid @RequestBody DataType dataType) throws URISyntaxException {
        log.debug("REST request to update DataType : {}", dataType);
        if (dataType.getId() == null) {
            return createDataType(dataType);
        }
        DataType result = dataTypeService.save(dataType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("dataType", dataType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /data-types : get all the dataTypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of dataTypes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/data-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<DataType>> getAllDataTypes(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of DataTypes");
        Page<DataType> page = dataTypeService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/data-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /data-types/:id : get the "id" dataType.
     *
     * @param id the id of the dataType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dataType, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/data-types/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DataType> getDataType(@PathVariable Long id) {
        log.debug("REST request to get DataType : {}", id);
        DataType dataType = dataTypeService.findOne(id);
        return Optional.ofNullable(dataType)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /data-types/:id : delete the "id" dataType.
     *
     * @param id the id of the dataType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/data-types/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDataType(@PathVariable Long id) {
        log.debug("REST request to delete DataType : {}", id);
        dataTypeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("dataType", id.toString())).build();
    }

    /**
     * SEARCH  /_search/data-types?query=:query : search for the dataType corresponding
     * to the query.
     *
     * @param query the query of the dataType search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/data-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<DataType>> searchDataTypes(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of DataTypes for query {}", query);
        Page<DataType> page = dataTypeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/data-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
