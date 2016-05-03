package io.smalldata.ohmageomh.dsu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smalldata.ohmageomh.surveys.domain.survey.Survey;
import org.openmhealth.dsu.domain.EndUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Query survey definitions from the MongoDb survey collection. No user segregation is supported.
 * Created by changun on 3/1/15.
 */
@Service
public class SurveyServiceImpl implements SurveyService {

    @Resource(name="manageDataSource")
    DataSource dataSource;
    @Autowired
    ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(SurveyServiceImpl.class);

    /**
     * A user should see two kinds of surveys:
     * 1. The surveys belongs to the studies the user belongs to
     * 2. The public surveys that are visible to everyone
     */
    final static String querySurveysByUsername =
            "SELECT definition " +
                    "FROM   surveys " +
                    "       INNER JOIN s_surveys " +
                    "               ON survey_id = surveys.id " +
                    "       INNER JOIN study_participants " +
                    "               ON study_participants.study_id = s_surveys.study_id " +
                    "       INNER JOIN users " +
                    "               ON users.id = study_participants.user_id " +
                    "WHERE  users.username = ? " +
                    "UNION " +
                    "SELECT definition " +
                    "FROM   surveys " +
                    "WHERE  public_to_all_users = true; ";


    @Override
    public Iterable<Survey> findAllSurveysAvailableToUser(EndUser user) throws SQLException {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        String[] args = {user.getUsername()};
        List<Survey> surveys = new ArrayList<>(select.query(querySurveysByUsername, args, new JsonToSurveyExtractor()));
        // remove any null values (i.e. the survey schemas with incorrect syntax)
        surveys.removeIf(survey -> survey == null);
        return surveys;

    }

    private class JsonToSurveyExtractor implements RowMapper<Survey> {
        @Override
        public Survey mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {

                Survey survey = objectMapper.readValue(rs.getString("definition"), Survey.class);
                // try to serialize it to make sure the schema is correct.
                objectMapper.writeValueAsString(survey);
                return survey;
            } catch (IOException e) {
                log.error("Failed to (de)serialize survey definition" + rs.getString("definition"), e);
            }
            return null;
        }
    }
}
