package org.openmhealth.dsu.controller;

/**
 * Created by changun on 12/17/14.
 */

import com.fasterxml.jackson.databind.JsonMappingException;
import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.dsu.domain.ohmage.survey.Survey;
import org.openmhealth.dsu.repository.EndUserRepository;
import org.openmhealth.dsu.service.SurveyDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    @Autowired
    private MappingJackson2HttpMessageConverter converter;


    /*@RequestMapping(value="/surveys", method= RequestMethod.POST)
    @ResponseBody
    public Survey post(@RequestBody @Valid Survey s) {
        return repo.save(s);
    } */


    /**
     * Return the surveys that are accessible to this users.
     * @param auth User authentication.
     * @return A list of surveys.
     * @throws SQLException
     */
    @RequestMapping(value="/surveys", method= RequestMethod.GET)
    @ResponseBody
    public Iterable<Survey> get(Authentication auth) throws SQLException {
        Optional<EndUser> user = endUserRepo.findOne(auth.getName())   ;
        if(user.isPresent()){
            return repo.findAllAvailableToUser(user.get());
        }
        else return null;
    }



    static class SurveySyntaxError extends IOException{
        SurveySyntaxError(IOException e){
            super(e);
        }
    }
    /**
     * A helper API that validates a survey schema, and return error messages if any errors exist.
     * @param json survey schema in json.
     * @return parsed survey
     * @throws SurveySyntaxError
     */
    @RequestMapping(value="/validate-survey", method=RequestMethod.POST)
    @ResponseBody
    public String validate(@RequestParam String json) throws SurveySyntaxError {
        try {
            converter.getObjectMapper().writeValueAsString(converter.getObjectMapper().readValue(json, Survey.class));
            return "Survey schema is valid";
        } catch (IOException e) {
            throw new SurveySyntaxError(e);
        }

    }

    @ExceptionHandler(SurveySyntaxError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleNoSuchClientException(SurveySyntaxError e) {
        return e.getMessage();

    }
}

