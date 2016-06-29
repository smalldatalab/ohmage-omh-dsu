package io.smalldata.ohmageomh.repository.search;

import io.smalldata.ohmageomh.domain.Note;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Note entity.
 */
public interface NoteSearchRepository extends ElasticsearchRepository<Note, Long> {
}
