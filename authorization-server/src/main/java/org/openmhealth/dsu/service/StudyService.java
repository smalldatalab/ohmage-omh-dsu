package org.openmhealth.dsu.service;

import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.dsu.domain.Study;

import java.util.Optional;

/**
 * Service for Study-Participant management.
 * Created by Cheng-Kang Hsieh on 3/25/15.
 */

public interface StudyService {
    /**
     * Get the Study object by its name
     * @param studyName  the study name
     * @return A optional Study object
     */
    Optional<Study> getStudyByName(String studyName);

    /**
     * Check if a user already belongs to a study
     * @param user  the EndUser object of the current user
     * @param study the study object
     * @return if the user has already enrolled the study
     */
    boolean isUserInStudy(EndUser user, Study study);

    /**
     * Enroll a user to a study, or do nothing if the user has already enrolled.
     * @param user  the EndUser object of the current user
     * @param study the study object
     */
    void enrollUserToStudy(EndUser user, Study study);
}
