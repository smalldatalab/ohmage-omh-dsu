package org.openmhealth.dsu.controller;

import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.dsu.domain.Study;
import org.openmhealth.dsu.exception.ResourceNotFoundException;
import org.openmhealth.dsu.repository.EndUserRepository;
import org.openmhealth.dsu.service.StudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * A controller for user to sign up for a study.
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
        if(exist){
            if (!studyService.isUserInStudy(user.get(), study.get())) {
                model.addAttribute("study", study.get());
                return "enroll-study";
            } else {
                // user already in the study. show "Enrolled!" page.
                return enrollResult(auth, studyName, model);
            }
        }else {
            throw new ResourceNotFoundException();
        }


    }

    @RequestMapping(value = "/studies/{studyName}/enroll", method = RequestMethod.POST)
    public String enrollResult(Authentication auth, @PathVariable("studyName") String studyName, Model model) {
        Optional<EndUser> user = endUserRepo.findOne(auth.getName());
        Optional<Study> study = studyService.getStudyByName(studyName);
        if (user.isPresent() && study.isPresent()) {
            studyService.enrollUserToStudy(user.get(), study.get());
            // check if enrollment succeeded
            if(studyService.isUserInStudy(user.get(), study.get())){
                model.addAttribute("study", study.get());
                return "enroll-study-result";
            }else{
                // throw error
                String msg = "Failed to add the " + user.get() + " to " + study.get();
                throw new RuntimeException(msg);
            }
        }else {
            throw new ResourceNotFoundException();
        }
    }
}


