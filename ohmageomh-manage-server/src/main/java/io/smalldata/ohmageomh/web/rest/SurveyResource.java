package io.smalldata.ohmageomh.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.smalldata.ohmageomh.domain.Survey;
import io.smalldata.ohmageomh.service.SurveyService;
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
 * REST controller for managing Survey.
 */
@RestController
@RequestMapping("/api")
public class SurveyResource {

    private final Logger log = LoggerFactory.getLogger(SurveyResource.class);
        
    @Inject
    private SurveyService surveyService;
    
    /**
     * POST  /surveys : Create a new survey.
     *
     * @param survey the survey to create
     * @return the ResponseEntity with status 201 (Created) and with body the new survey, or with status 400 (Bad Request) if the survey has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/surveys",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Survey> createSurvey(@Valid @RequestBody Survey survey) throws URISyntaxException {
        log.debug("REST request to save Survey : {}", survey);
        if (survey.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("survey", "idexists", "A new survey cannot already have an ID")).body(null);
        }
        Survey result = surveyService.save(survey);
        return ResponseEntity.created(new URI("/api/surveys/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("survey", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /surveys : Updates an existing survey.
     *
     * @param survey the survey to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated survey,
     * or with status 400 (Bad Request) if the survey is not valid,
     * or with status 500 (Internal Server Error) if the survey couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/surveys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Survey> updateSurvey(@Valid @RequestBody Survey survey) throws URISyntaxException {
        log.debug("REST request to update Survey : {}", survey);
        if (survey.getId() == null) {
            return createSurvey(survey);
        }
        Survey result = surveyService.save(survey);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("survey", survey.getId().toString()))
            .body(result);
    }

    /**
     * GET  /surveys : get all the surveys.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of surveys in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/surveys",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Survey>> getAllSurveys(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Surveys");
        Page<Survey> page = surveyService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/surveys");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /surveys/:id : get the "id" survey.
     *
     * @param id the id of the survey to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the survey, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/surveys/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Survey> getSurvey(@PathVariable Long id) {
        log.debug("REST request to get Survey : {}", id);
        Survey survey = surveyService.findOne(id);
        return Optional.ofNullable(survey)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /surveys/:id : delete the "id" survey.
     *
     * @param id the id of the survey to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/surveys/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        log.debug("REST request to delete Survey : {}", id);
        surveyService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("survey", id.toString())).build();
    }

    /**
     * SEARCH  /_search/surveys?query=:query : search for the survey corresponding
     * to the query.
     *
     * @param query the query of the survey search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/surveys",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Survey>> searchSurveys(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Surveys for query {}", query);
        Page<Survey> page = surveyService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/surveys");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
