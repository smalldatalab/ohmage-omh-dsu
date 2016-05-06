package io.smalldata.ohmageomh.repository;

import io.smalldata.ohmageomh.domain.Participant;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Participant entity.
 */
public interface ParticipantRepository extends JpaRepository<Participant,Long> {

    @Query("select distinct participant from Participant participant left join fetch participant.studies")
    List<Participant> findAllWithEagerRelationships();

    @Query("select participant from Participant participant left join fetch participant.studies where participant.id =:id")
    Participant findOneWithEagerRelationships(@Param("id") Long id);

}
