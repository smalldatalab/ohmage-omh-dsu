package io.smalldata.ohmageomh.dsu.service;

import io.smalldata.ohmageomh.dsu.domain.Study;
import org.openmhealth.dsu.domain.EndUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Jared Sieling.
 */
@Service
public class StudyServiceImpl implements StudyService {
    @Override
    public Optional<Study> getStudyByName(String studyName) {
        return null;
    }

    @Override
    public List<Study> getStudiesByUsername(String username) {
        return null;
    }

    @Override
    public boolean isUserEnrolled(EndUser user, Study study) {
        return false;
    }

    @Override
    public Optional<String> getParticipantId(EndUser user, Study study) {
        return null;
    }

    @Override
    public void enrollUser(EndUser user, Study study) {

    }
}
