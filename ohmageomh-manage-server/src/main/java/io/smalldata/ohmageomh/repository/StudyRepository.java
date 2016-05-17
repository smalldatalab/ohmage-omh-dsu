package io.smalldata.ohmageomh.repository;

import io.smalldata.ohmageomh.domain.Study;

import io.smalldata.ohmageomh.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Study entity.
 */
public interface StudyRepository extends JpaRepository<Study,Long> {

    @Query("select distinct study from Study study left join fetch study.managers left join fetch study.surveys left join fetch study.integrations")
    List<Study> findAllWithEagerRelationships();

    @Query("select study from Study study left join fetch study.managers left join fetch study.surveys left join fetch study.integrations where study.id =:id")
    Study findOneWithEagerRelationships(@Param("id") Long id);

    Page<Study> findAllByManagers(User user, Pageable pageable);

}
