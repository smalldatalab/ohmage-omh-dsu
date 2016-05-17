package io.smalldata.ohmageomh.service;

import io.smalldata.ohmageomh.domain.Participant;
import io.smalldata.ohmageomh.domain.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Participant.
 */
public interface ParticipantService {

    /**
     * Save a participant.
     *
     * @param participant the entity to save
     * @return the persisted entity
     */
    Participant save(Participant participant);

    /**
     *  Get all the participants.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Participant> findAll(Pageable pageable);

    /**
     *  Get the "id" participant.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Participant findOne(Long id);

    /**
     *  Delete the "id" participant.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the participant corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Participant> search(String query, Pageable pageable);

    Page<Participant> findAllByStudy(Study study, Pageable pageable);
}
