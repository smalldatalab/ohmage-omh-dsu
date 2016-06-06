package io.smalldata.ohmageomh.dsu.controller;
import io.smalldata.ohmageomh.surveys.domain.survey.Survey;
import org.openmhealth.dsu.repository.EndUserRepository;
import io.smalldata.ohmageomh.dsu.service.SurveyService;
import org.openmhealth.dsu.controller.ApiController;
import org.openmhealth.dsu.domain.EndUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * A controller that returns surveys descriptions in JSON, mainly used by the ohmage apps
 * to retrieve surveys available for the app user. The current implementation is based on
 * the "admin dashboard", and uses the User->Study->Survey relations the dashboard maintains
 * to decide which surveys to show to different users.
 *
 * @author Andy Hsieh
 */
@ApiController
public class SurveyController {

    @Autowired
    SurveyService repo;
    @Autowired
    EndUserRepository endUserRepo;
    @Autowired
    private MappingJackson2HttpMessageConverter converter;


    /**
     * Return the surveys that are accessible to this users.
     * @param auth User authentication.
     * @return A list of surveys.
     * @throws SQLException
     */
    @RequestMapping(value="/surveys", method= RequestMethod.GET)
    @ResponseBody
    public Iterable<Survey> get(Authentication auth) throws SQLException {
        Optional<EndUser> user = endUserRepo.findOne(auth.getName());
        if(user.isPresent()){

            return repo.findAllSurveysAvailableToUser(user.get());
        }
        else return null;
    }



    static class SurveySyntaxError extends IOException{
        SurveySyntaxError(IOException e){
            super(e);
        }
    }
    /**
     * A helper endpoint that validates a survey schema, and return error messages if any errors exist in the schema.
     * @return parsed survey
     * @throws SurveySyntaxError
     */
    @RequestMapping(value="/surveys/validate-survey", method= RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String validate(HttpServletRequest request) throws SurveySyntaxError {
        try {

            converter.getObjectMapper().writeValueAsString(
                    converter.getObjectMapper().readValue(
                            request.getInputStream(), Survey.class));
            return "Survey schema is valid";
        } catch (IOException e) {
            throw new SurveySyntaxError(e);
        }

    }

    /**
     * A helper endpoint that validates a survey schema, and return error messages if any errors exist in the schema.
     * @return parsed survey
     */
    @RequestMapping(value="/surveys/validate-survey", method= RequestMethod.GET)
    public String validate() {
        return "validate-survey";
    }

    /**
     * Recursively get the root cause message of a Throwable.
     * @param th Throwable
     * @return the root cause message
     */
    private static String getRootCauseMessage(final Throwable th) {
        if(th.getCause() != null && th != th.getCause()){
            return getRootCauseMessage(th.getCause());
        }else{
            return th.getMessage();
        }

    }
    @ExceptionHandler(SurveySyntaxError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    String handleSurveySyntaxError(SurveySyntaxError e) {
        return  getRootCauseMessage(e);
    }

    /* Endpoint for users to create surveys.
       It is disabled since we use admindahboard to manage the surveys.
     */
    /*@RequestMapping(value="/surveys", method= RequestMethod.POST)
    @ResponseBody
    public Survey post(@RequestBody @Valid Survey s) {
        return repo.save(s);
    } */
}

