package io.smalldata.ohmageomh.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.smalldata.ohmageomh.domain.Note;
import io.smalldata.ohmageomh.service.NoteService;
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
 * REST controller for managing Note.
 */
@RestController
@RequestMapping("/api")
public class NoteResource {

    private final Logger log = LoggerFactory.getLogger(NoteResource.class);
        
    @Inject
    private NoteService noteService;
    
    /**
     * POST  /notes : Create a new note.
     *
     * @param note the note to create
     * @return the ResponseEntity with status 201 (Created) and with body the new note, or with status 400 (Bad Request) if the note has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/notes",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Note> createNote(@Valid @RequestBody Note note) throws URISyntaxException {
        log.debug("REST request to save Note : {}", note);
        if (note.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("note", "idexists", "A new note cannot already have an ID")).body(null);
        }
        Note result = noteService.save(note);
        return ResponseEntity.created(new URI("/api/notes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("note", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /notes : Updates an existing note.
     *
     * @param note the note to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated note,
     * or with status 400 (Bad Request) if the note is not valid,
     * or with status 500 (Internal Server Error) if the note couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/notes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Note> updateNote(@Valid @RequestBody Note note) throws URISyntaxException {
        log.debug("REST request to update Note : {}", note);
        if (note.getId() == null) {
            return createNote(note);
        }
        Note result = noteService.save(note);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("note", note.getId().toString()))
            .body(result);
    }

    /**
     * GET  /notes : get all the notes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of notes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/notes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Note>> getAllNotes(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Notes");
        Page<Note> page = noteService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/notes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /notes/:id : get the "id" note.
     *
     * @param id the id of the note to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the note, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/notes/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Note> getNote(@PathVariable Long id) {
        log.debug("REST request to get Note : {}", id);
        Note note = noteService.findOne(id);
        return Optional.ofNullable(note)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /notes/:id : delete the "id" note.
     *
     * @param id the id of the note to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/notes/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        log.debug("REST request to delete Note : {}", id);
        noteService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("note", id.toString())).build();
    }

    /**
     * SEARCH  /_search/notes?query=:query : search for the note corresponding
     * to the query.
     *
     * @param query the query of the note search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/notes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Note>> searchNotes(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Notes for query {}", query);
        Page<Note> page = noteService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/notes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
