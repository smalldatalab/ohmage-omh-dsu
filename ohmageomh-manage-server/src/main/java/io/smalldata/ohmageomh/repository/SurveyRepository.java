package io.smalldata.ohmageomh.repository;

import io.smalldata.ohmageomh.domain.Survey;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Survey entity.
 */
public interface SurveyRepository extends JpaRepository<Survey,Long> {

}
