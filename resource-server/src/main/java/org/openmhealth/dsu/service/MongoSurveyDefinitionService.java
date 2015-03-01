package org.openmhealth.dsu.service;

import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.dsu.domain.ohmage.survey.Survey;
import org.openmhealth.dsu.repository.MongoSurveyDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * Query survey definitions from the MongoDb survey collection. No user segregation is supported.
 * Created by changun on 3/1/15.
 */
@Service
public class MongoSurveyDefinitionService implements SurveyDefinitionService  {
    @Autowired
    MongoSurveyDefinitionRepository repo;
    @Override
    public Iterable<Survey> findAllAvailableToUser(EndUser user) throws SQLException {
        return repo.findAll();
    }
}
