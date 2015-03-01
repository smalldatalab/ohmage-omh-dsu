package org.openmhealth.dsu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.dsu.domain.ohmage.survey.Survey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Query survey definition from the Admin Dashboard database.
 * Created by changun on 2/26/15.
 */
//@Service
public class SurveyDefinitionServiceImpl implements SurveyDefinitionService {

    private static final Logger log = LoggerFactory.getLogger(SurveyDefinitionServiceImpl.class);

    @Resource(name="admindashboardDataSource")
    DataSource dataSource;
    @Autowired
    ObjectMapper objectMapper;

    final static String querySurveysByUserEmail =
        "SELECT definition " +
        "FROM   surveys " +
        "       INNER JOIN study_surveys " +
        "               ON survey_id = surveys.id " +
        "       INNER JOIN study_participants " +
        "               ON study_participants.study_id = study_surveys.study_id " +
        "       INNER JOIN users " +
        "               ON users.id = study_surveys.study_id " +
        "WHERE  gmail = ? " +
        "UNION " +
        "SELECT definition " +
        "FROM   surveys " +
        "WHERE  public = true; ";
    @Override
    public Iterable<Survey> findAllAvailableToUser(EndUser user) throws SQLException {
         log.info(user.toString());

        JdbcTemplate select = new JdbcTemplate(dataSource);
         String[] args = {user.getEmailAddress().toString()};

         List<Survey> surveys = new ArrayList<>(select.query(querySurveysByUserEmail, args, new JsonToSurveyExtractor()));
        surveys.removeIf(survey -> survey == null)                           ;
         return surveys;

    }
    private class JsonToSurveyExtractor implements RowMapper<Survey>{

        @Override
        public Survey mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                return objectMapper.readValue(rs.getString("definition"), Survey.class);
            } catch (IOException e) {
                  log.error("Failed to deserialize survey definition" + rs.getString("definition"), e);
            }
            return null;
        }
    }
}
