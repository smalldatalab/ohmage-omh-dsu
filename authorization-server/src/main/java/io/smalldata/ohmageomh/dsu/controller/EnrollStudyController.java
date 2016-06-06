package io.smalldata.ohmageomh.dsu.controller;

import io.smalldata.ohmageomh.dsu.domain.Study;
import io.smalldata.ohmageomh.dsu.service.StudyService;
import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.dsu.repository.EndUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

/**
 * A controller for user to sign up for a study based on the StudyService implementation.
 * The current study service implementation assumes a Ruby-based "admin dashboard" is running on the
 * same server, and directly modify its database records to enroll users to the studies.
 * Created by Cheng-Kang Hsieh on 3/25/15.
 */
@Controller
public class EnrollStudyController {
    @Autowired
    EndUserRepository endUserRepo;
    @Autowired
    StudyService studyService;


    @RequestMapping(value = "/studies/{studyName}/enroll", method = RequestMethod.GET)
    public String enroll(Authentication auth, @PathVariable("studyName") String studyName, Model model) {
        Optional<EndUser> user = endUserRepo.findOne(auth.getName());
        Optional<Study> study = studyService.getStudyByName(studyName);
        boolean exist = user.isPresent() && study.isPresent();
        if (exist) {
            if (!studyService.isUserEnrolled(user.get(), study.get())) {
                model.addAttribute("study", study.get());
                model.addAttribute("user", user.get());
                return "enroll-study";
            } else {
                // user already in the study. show "Enrolled!" page.
                return enrollResult(auth, studyName, model);
            }
        } else {
            throw new RuntimeException();
        }

    }

    @RequestMapping(value = "/studies/{studyName}/enroll", method = RequestMethod.POST)
    public String enrollResult(Authentication auth, @PathVariable("studyName") String studyName, Model model) {
        Optional<EndUser> user = endUserRepo.findOne(auth.getName());
        Optional<Study> study = studyService.getStudyByName(studyName);
        if (user.isPresent() && study.isPresent()) {
            studyService.enrollUser(user.get(), study.get());
            // check if enrollment succeeded
            if (studyService.isUserEnrolled(user.get(), study.get())) {
                model.addAttribute("participantId", studyService.getParticipantId(user.get(), study.get()).get());
                model.addAttribute("user", user.get());
                model.addAttribute("study", study.get());
                return "enroll-study-result";
            } else {
                // throw error
                String msg = "Failed to add the " + user.get() + " to " + study.get();
                throw new RuntimeException(msg);
            }
        } else {
            throw new RuntimeException();
        }
    }
}


