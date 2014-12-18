package org.openmhealth.dsu.repository;

import org.openmhealth.dsu.domain.ohmage.survey.Survey;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by changun on 12/17/14.
 */
public interface SurveyDefinitionRepository extends CrudRepository<Survey, Long> {
}
