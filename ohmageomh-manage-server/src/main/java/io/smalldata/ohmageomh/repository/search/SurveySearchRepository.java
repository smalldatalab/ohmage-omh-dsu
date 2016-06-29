package io.smalldata.ohmageomh.repository.search;

import io.smalldata.ohmageomh.domain.Survey;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Survey entity.
 */
public interface SurveySearchRepository extends ElasticsearchRepository<Survey, Long> {
}
