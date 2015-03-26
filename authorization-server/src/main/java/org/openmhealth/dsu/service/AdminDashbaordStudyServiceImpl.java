package org.openmhealth.dsu.service;

import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.dsu.domain.Study;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Query and modify the study-participant records in the admin dashboard.
 * Disable it if the project does not use admin dashboard
 * Created by Cheng-Kang Hsieh on 3/25/15.
 */
@Service
public class AdminDashbaordStudyServiceImpl implements  StudyService{
    @Resource(name="admindashboardDataSource")
    DataSource dataSource;
    private static final Logger log = LoggerFactory.getLogger(AdminDashbaordStudyServiceImpl.class);

    final static String queryStudyByName =
            "SELECT id, name from studies WHERE name = ? LIMIT 1";
    final static String queryParticipantMembershipByEmailAndStudyId =
            "SELECT COUNT(*) " +
            "FROM   users " +
            "       INNER JOIN study_participants " +
            "               ON users.id = study_participants.user_id " +
            "WHERE  gmail = ? " +
            "       AND study_id = ?; ";
    final static String insertNewStudyParticipant =
            "INSERT INTO study_participants (study_id, user_id, created_at, updated_at) VALUES (?, ?, ?, ?);";
    final static String insertNewUser =
            "INSERT INTO users (first_name, last_name, gmail, created_at, updated_at) VALUES (?, ?, ?, ?, ?);";
    final static String queryUserIdByEmail =
            "SELECT id FROM users WHERE gmail = ? LIMIT 1;";

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
    public boolean isUserInStudy(EndUser user, Study study) {

        JdbcTemplate select = new JdbcTemplate(dataSource);
        Object[] args = {user.getEmailAddress().get().toString(), study.getId()};
        return select.queryForObject(queryParticipantMembershipByEmailAndStudyId, args, Integer.class) > 0;
    }
    private Long createNewUser(EndUser user){
        log.info("User:"+user + "does not exist. Create one.");
        // create the user if it does not exist
        Timestamp timestamp = new Timestamp(new Date().getTime());
        Object[] args = new Object[]{user.getFirstName(),
                user.getLastName(),
                user.getEmailAddress().get().getAddress(),
                timestamp,
                timestamp};
        JdbcTemplate select = new JdbcTemplate(dataSource);
        select.update(insertNewUser, args, Integer.class);
        // query the user id again
        try {
            return select.queryForObject(queryUserIdByEmail, args, Long.class);
        }catch (EmptyResultDataAccessException e) {
            String msg = "Can't not create admin dashboard user:" + user;
            log.error(msg, e);
            throw new RuntimeException(msg);
        }
    }
    @Override
    public void enrollUserToStudy(EndUser user, Study study) {
        log.info("User:"+user + " Study:"+study);
        // only attempt to create a new "Study-Participant" entry if it does not exist.
        if(!isUserInStudy(user, study)) {

            log.info("User:"+user + "does not in study" + study);
            JdbcTemplate select = new JdbcTemplate(dataSource);
            Object[] args = {user.getEmailAddress().get().getAddress()};
            Long userId;
            // check if the user exists in the dashboard
            try {
                userId = select.queryForObject(queryUserIdByEmail, args, Long.class);
            }catch (EmptyResultDataAccessException e) {
                 userId = createNewUser(user);

            }
            // insert a new "Study-Participant" entry.
            Timestamp timestamp = new Timestamp(new Date().getTime());
            args = new Object[]{study.getId(), userId, timestamp, timestamp};
            log.info("Create a new study-participant entry for " + user + " " + study);
            select.update(insertNewStudyParticipant, args);
        }
    }
}
