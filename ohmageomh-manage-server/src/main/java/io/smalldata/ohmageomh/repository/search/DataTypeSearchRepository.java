package io.smalldata.ohmageomh.repository.search;

import io.smalldata.ohmageomh.domain.DataType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the DataType entity.
 */
public interface DataTypeSearchRepository extends ElasticsearchRepository<DataType, Long> {
}
