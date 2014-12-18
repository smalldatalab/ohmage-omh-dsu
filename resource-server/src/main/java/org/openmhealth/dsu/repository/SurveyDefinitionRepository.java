package org.openmhealth.dsu.repository;

import org.openmhealth.dsu.domain.survey.Survey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Created by changun on 12/17/14.
 */
public interface SurveyDefinitionRepository extends CrudRepository<Survey, Long> {
}
