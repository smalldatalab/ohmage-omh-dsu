package io.smalldata.ohmageomh.repository.search;

import io.smalldata.ohmageomh.domain.Organization;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Organization entity.
 */
public interface OrganizationSearchRepository extends ElasticsearchRepository<Organization, Long> {
}
