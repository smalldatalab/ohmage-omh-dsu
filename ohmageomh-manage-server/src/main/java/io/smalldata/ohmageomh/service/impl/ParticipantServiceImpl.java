package io.smalldata.ohmageomh.service.impl;

import io.smalldata.ohmageomh.service.ParticipantService;
import io.smalldata.ohmageomh.domain.Participant;
import io.smalldata.ohmageomh.repository.ParticipantRepository;
import io.smalldata.ohmageomh.repository.search.ParticipantSearchRepository;
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
 * Service Implementation for managing Participant.
 */
@Service
@Transactional
public class ParticipantServiceImpl implements ParticipantService{

    private final Logger log = LoggerFactory.getLogger(ParticipantServiceImpl.class);
    
    @Inject
    private ParticipantRepository participantRepository;
    
    @Inject
    private ParticipantSearchRepository participantSearchRepository;
    
    /**
     * Save a participant.
     * 
     * @param participant the entity to save
     * @return the persisted entity
     */
    public Participant save(Participant participant) {
        log.debug("Request to save Participant : {}", participant);
        Participant result = participantRepository.save(participant);
        participantSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the participants.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Participant> findAll(Pageable pageable) {
        log.debug("Request to get all Participants");
        Page<Participant> result = participantRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one participant by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Participant findOne(Long id) {
        log.debug("Request to get Participant : {}", id);
        Participant participant = participantRepository.findOneWithEagerRelationships(id);
        return participant;
    }

    /**
     *  Delete the  participant by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Participant : {}", id);
        participantRepository.delete(id);
        participantSearchRepository.delete(id);
    }

    /**
     * Search for the participant corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Participant> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Participants for query {}", query);
        return participantSearchRepository.search(queryStringQuery(query), pageable);
    }
}
