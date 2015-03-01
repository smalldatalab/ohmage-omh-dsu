package org.openmhealth.dsu.repository;

import org.openmhealth.dsu.domain.ohmage.survey.Survey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

/**
 * Created by changun on 3/1/15.
 */
@org.springframework.stereotype.Repository
public interface MongoSurveyDefinitionRepository extends CrudRepository<Survey, String> {
}
