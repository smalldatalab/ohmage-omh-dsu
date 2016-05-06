package io.smalldata.ohmageomh.repository.search;

import io.smalldata.ohmageomh.domain.Participant;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Participant entity.
 */
public interface ParticipantSearchRepository extends ElasticsearchRepository<Participant, Long> {
}
