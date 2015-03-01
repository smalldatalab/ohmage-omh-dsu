package org.openmhealth.dsu.controller;

/**
 * Created by changun on 12/17/14.
 */

import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.dsu.domain.ohmage.survey.Survey;
import org.openmhealth.dsu.repository.EndUserRepository;
import org.openmhealth.dsu.service.SurveyDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.Optional;

/**
 * A controller that manages user accounts.
 *
 * @author Emerson Farrugia
 */
@ApiController
public class SurveyController {

    @Autowired
    SurveyDefinitionService repo;
    @Autowired
    EndUserRepository endUserRepo;

    /*@RequestMapping(value="/surveys", method= RequestMethod.POST)
    @ResponseBody
    public Survey post(@RequestBody @Valid Survey s) {
        return repo.save(s);
    } */


    @RequestMapping(value="/surveys", method= RequestMethod.GET)
    @ResponseBody
    public Iterable<Survey> get(Authentication auth) throws SQLException {
        Optional<EndUser> user = endUserRepo.findOne(auth.getName())   ;
        if(user.isPresent()){
            return repo.findAllAvailableToUser(user.get());
        }
        else return null;
    }
}

