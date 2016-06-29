package io.smalldata.ohmageomh.repository;

import io.smalldata.ohmageomh.domain.Organization;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Organization entity.
 */
public interface OrganizationRepository extends JpaRepository<Organization,Long> {

    @Query("select distinct organization from Organization organization left join fetch organization.studies left join fetch organization.owners")
    List<Organization> findAllWithEagerRelationships();

    @Query("select organization from Organization organization left join fetch organization.studies left join fetch organization.owners where organization.id =:id")
    Organization findOneWithEagerRelationships(@Param("id") Long id);

}
