package io.smalldata.ohmageomh.repository;

import io.smalldata.ohmageomh.domain.Integration;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Integration entity.
 */
public interface IntegrationRepository extends JpaRepository<Integration,Long> {

    @Query("select distinct integration from Integration integration left join fetch integration.dataTypes")
    List<Integration> findAllWithEagerRelationships();

    @Query("select integration from Integration integration left join fetch integration.dataTypes where integration.id =:id")
    Integration findOneWithEagerRelationships(@Param("id") Long id);

}
