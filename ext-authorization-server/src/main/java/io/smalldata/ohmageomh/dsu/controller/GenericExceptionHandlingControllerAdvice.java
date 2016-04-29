package io.smalldata.ohmageomh.dsu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.social.ResourceNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;


/**
 * A set of exception handlers that are applied to all controllers, primarily to prevent information disclosure.
 *
 * @author Emerson Farrugia
 */
@ControllerAdvice
public class GenericExceptionHandlingControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GenericExceptionHandlingControllerAdvice.class);

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleAuthenticationCredentialsNotFoundException(Exception e, HttpServletRequest request) {

        log.debug("A {} request for '{}' failed authentication.", request.getMethod(), request.getPathInfo(), e);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleMissingServletRequestParameterException(MissingServletRequestParameterException e,
                                                              HttpServletRequest request) {

        log.debug("A {} request for '{}' failed because parameter '{}' is missing.",
                request.getMethod(), request.getPathInfo(), e.getParameterName(), e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleException(Exception e, HttpServletRequest request) {

        log.warn("A {} request for '{}' failed.", request.getMethod(), request.getPathInfo(), e);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public
    @ResponseBody
    String handleResourceNotFoundException() {
        return "The page you request does not exists";
    }
}