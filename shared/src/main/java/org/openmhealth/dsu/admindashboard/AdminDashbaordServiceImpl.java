package org.openmhealth.dsu.admindashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.dsu.domain.Study;
import org.openmhealth.dsu.domain.ohmage.survey.Survey;
import org.openmhealth.dsu.service.StudyService;
import org.openmhealth.dsu.service.SurveyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * A service that manages study and survey via querying/modifying the admin dashboard's database
 * Disable it if the project does not use the admin dashboard.
 * Created by Cheng-Kang Hsieh on 3/25/15.
 */
@Service
public class AdminDashbaordServiceImpl implements StudyService, SurveyService {

    @Resource(name="admindashboardDataSource")
    DataSource dataSource;
    @Autowired
    ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(AdminDashbaordServiceImpl.class);
    /**
     *  We uses the "username" field to map the users between DSU and admindashboard.
     *  The values in this field corresponds to the EndUser.username field in the DSU
     */
    final static String queryUserIdByUsername =
            "SELECT id FROM users WHERE username = ? LIMIT 1;";
    final static String queryStudyByName =
            "SELECT id, name from studies WHERE name = ? LIMIT 1";
    final static String queryParticipantMembershipByUsernameAndStudyId =
            "SELECT COUNT(*) " +
            "FROM   users " +
            "       INNER JOIN study_participants " +
            "               ON users.id = study_participants.user_id " +
            "WHERE  users.username = ? " +
            "       AND study_id = ?; ";
    final static String insertNewStudyParticipant =
            "INSERT INTO study_participants (study_id, user_id, created_at, updated_at) VALUES (?, ?, ?, ?);";
    final static String insertNewUser =
            "INSERT INTO users (username, first_name, last_name, gmail, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?);";

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




    private Long createNewUser(EndUser user){
        log.info("User:"+user + "does not exist. Create one.");
        // create the user if it does not exist
        Timestamp timestamp = new Timestamp(new Date().getTime());
        String firstname = user.getFirstName()!=null ? user.getFirstName() : "";
        String lastname = user.getLastName()!=null ? user.getLastName(): "";
        JdbcTemplate select = new JdbcTemplate(dataSource);
        select.update(insertNewUser,
                user.getUsername(),
                firstname, lastname,
                user.getEmailAddress().isPresent() ?
                        user.getEmailAddress().get().getAddress() : null,
                timestamp,
                timestamp);
        // query the user id again
        try {
            return getUserId(user);
        }catch (UserNotFoundException e) {
            String msg = "Can't not create admin dashboard user:" + user;
            log.error(msg, e);
            throw new RuntimeException(msg);
        }
    }
    class UserNotFoundException extends Exception{}
    private Long getUserId(EndUser user) throws UserNotFoundException {
        Object[] args = {user.getUsername()};
        JdbcTemplate select = new JdbcTemplate(dataSource);
        try{
            return select.queryForObject(queryUserIdByUsername, args, Long.class);
        }catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException();
        }
    }

    private class RowToStudy implements RowMapper<Study> {
        @Override
        public Study mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Study(rs.getLong("id"), rs.getString("name"));        }
    }

    @Override
    public Optional<Study> getStudyByName(String studyName) {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        String[] args = {studyName};
        try{
            return Optional.of(select.queryForObject(queryStudyByName, args, new RowToStudy()));
        }catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    @Override
    public boolean isUserEnrolled(EndUser user, Study study) {

        JdbcTemplate select = new JdbcTemplate(dataSource);
        Object[] args = {user.getUsername(), study.getId()};
        return select.queryForObject(queryParticipantMembershipByUsernameAndStudyId, args, Integer.class) > 0;
    }

    /**
     * If the user is enrolled to the study. Use the zero-padding 4-digit userId in the admin dashboard as the participant id.
     * @param user  the EndUser object of the current user
     * @param study the study object
     * @return  zero-padding 4-digit UserId or null if the usr is not in the study.
     */
    @Override
    public Optional<String> getParticipantId(EndUser user, Study study) {
        if(isUserEnrolled(user, study)){
            try {
                return Optional.of(String.format("%04d", getUserId(user)));
            } catch (UserNotFoundException e) {
                return Optional.empty();
            }

        }
        return Optional.empty();
    }

    @Override
    public void enrollUser(EndUser user, Study study) {
        log.info("User:"+user + " Study:"+study);
        // only attempt to create a new "Study-Participant" entry if it does not exist.
        if(!isUserEnrolled(user, study)) {
            log.info("User:"+user + "does not in study" + study);
            JdbcTemplate select = new JdbcTemplate(dataSource);
            Long userId;
            // check if the user exists in the dashboard
            try {
                userId = getUserId(user);
            }catch (UserNotFoundException e) {
                 userId = createNewUser(user);

            }
            // insert a new "Study-Participant" entry.
            Timestamp timestamp = new Timestamp(new Date().getTime());
            Object[] args = new Object[]{study.getId(), userId, timestamp, timestamp};
            log.info("Create a new study-participant entry for " + user + " " + study);
            int numInsert = select.update(insertNewStudyParticipant, args);
            if(numInsert != 1){
                throw new RuntimeException("Failed to create a new study-participant entry");
            }
        }
    }

    @Override
    public Iterable<Survey> findAllSurveysAvailableToUser(EndUser user) throws SQLException {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        String[] args = {user.getUsername()};
        List<Survey> surveys = new ArrayList<>(select.query(querySurveysByUsername, args, new JsonToSurveyExtractor()));
        // remove any null values (i.e. the survey schemas with incorrect syntax)
        surveys.removeIf(survey -> survey == null);
        return surveys;

    }

    private class JsonToSurveyExtractor implements RowMapper<Survey>{
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
