package io.smalldata.ohmageomh.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.smalldata.ohmageomh.domain.*;
import io.smalldata.ohmageomh.security.AuthoritiesConstants;
import io.smalldata.ohmageomh.service.IntegrationService;
import io.smalldata.ohmageomh.service.ParticipantService;
import io.smalldata.ohmageomh.service.StudyService;
import io.smalldata.ohmageomh.service.UserService;
import io.smalldata.ohmageomh.web.rest.dto.ParticipantDetailDTO;
import io.smalldata.ohmageomh.web.rest.dto.ParticipantSummaryDTO;
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
import org.springframework.security.core.context.SecurityContextHolder;
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
 * REST controller for managing Study.
 */
@RestController
@RequestMapping("/api")
public class StudyResource {

    private final Logger log = LoggerFactory.getLogger(StudyResource.class);

    @Inject
    private StudyService studyService;
    @Inject
    private UserService userService;
    @Inject
    private ParticipantService participantService;
    @Inject
    private IntegrationService integrationService;

    /**
     * POST  /studies : Create a new study.
     *
     * @param study the study to create
     * @return the ResponseEntity with status 201 (Created) and with body the new study, or with status 400 (Bad Request) if the study has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/studies",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Study> createStudy(@Valid @RequestBody Study study) throws URISyntaxException {
        log.debug("REST request to save Study : {}", study);
        if (study.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("study", "idexists", "A new study cannot already have an ID")).body(null);
        }
        Study result = studyService.save(study);
        return ResponseEntity.created(new URI("/api/studies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("study", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /studies : Updates an existing study.
     *
     * @param study the study to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated study,
     * or with status 400 (Bad Request) if the study is not valid,
     * or with status 500 (Internal Server Error) if the study couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/studies",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Study> updateStudy(@Valid @RequestBody Study study) throws URISyntaxException {
        log.debug("REST request to update Study : {}", study);
        if (study.getId() == null) {
            return createStudy(study);
        }
        Study result = studyService.save(study);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("study", study.getId().toString()))
            .body(result);
    }

    /**
     * GET  /studies : get all the studies.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of studies in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/studies",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Study>> getAllStudies(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Studies");

        // If request is from ROLE_USER, filter to studies they are manager on.
        User user = userService.getUserWithAuthorities();
        Page<Study> page = null;
        if(userService.hasAuthority(AuthoritiesConstants.ADMIN)) {
            page = studyService.findAll(pageable);
        } else {
            page = studyService.findAllByManager(user, pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/studies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /studies/:id : get the "id" study.
     *
     * @param id the id of the study to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the study, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/studies/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Study> getStudy(@PathVariable Long id) {
        log.debug("REST request to get Study : {}", id);
        Study study = studyService.findOne(id);
        return Optional.ofNullable(study)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /studies/:id/participants : get the participants for the study.
     *
     * @param id the id of the study to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the list of participants, or with status 404 (Not Found)
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/studies/{id}/participants",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Participant>> getStudyParticipants(@PathVariable Long id, Pageable pageable) throws URISyntaxException {
        Study study = studyService.findOne(id);
        Page<Participant> page = participantService.findAllByStudy(study, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/studies/" + study.getId() + "/participants");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /studies/:id/participantSummaries : get the participants for the study.
     *
     * @param id the id of the study to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the list of participants, or with status 404 (Not Found)
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/studies/{id}/participantSummaries",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ParticipantSummaryDTO>> getStudyParticipantSummaries(@PathVariable Long id, Pageable pageable) throws URISyntaxException {
        Study study = studyService.findOne(id);
        Page<ParticipantSummaryDTO> page = participantService.findAllSummariesByStudy(study, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/studies/" + study.getId() + "/participants");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /studies/:id/participants/:pt : get the participant details.
     *
     * @param id the id of the study to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the list of participants, or with status 404 (Not Found)
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/studies/{id}/participants/{pt}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ParticipantDetailDTO> getStudyParticipant(@PathVariable Long id, @PathVariable Long pt, Pageable pageable) throws URISyntaxException {
        Study study = studyService.findOne(id);
        Participant participant = participantService.findOne(pt);
        ParticipantDetailDTO dto = new ParticipantDetailDTO(participant);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * DELETE  /studies/:id : delete the "id" study.
     *
     * @param id the id of the study to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/studies/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteStudy(@PathVariable Long id) {
        log.debug("REST request to delete Study : {}", id);
        studyService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("study", id.toString())).build();
    }

    /**
     * SEARCH  /_search/studies?query=:query : search for the study corresponding
     * to the query.
     *
     * @param query the query of the study search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/studies",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Study>> searchStudies(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Studies for query {}", query);
        Page<Study> page = studyService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/studies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
