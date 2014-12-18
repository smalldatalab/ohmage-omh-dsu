package org.openmhealth.dsu.controller;

/**
 * Created by changun on 12/17/14.
 */

import org.openmhealth.dsu.domain.ohmage.survey.Survey;
import org.openmhealth.dsu.repository.SurveyDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * A controller that manages user accounts.
 *
 * @author Emerson Farrugia
 */
@Controller
public class SurveyController {

    @Autowired
    SurveyDefinitionRepository repo;

    @RequestMapping(value="/surveys", method= RequestMethod.POST)
    @ResponseBody
    public Survey post(@RequestBody @Valid Survey s) {
        return repo.save(s);
    }


    @RequestMapping(value="/surveys", method= RequestMethod.GET)
    @ResponseBody
    public Iterable<Survey> get() {
        return repo.findAll();
    }
}

