package io.smalldata.ohmageomh.dsu.controller;

import org.openmhealth.dsu.domain.EndUserRegistrationData;
import org.openmhealth.dsu.domain.EndUserUserDetails;
import org.openmhealth.dsu.service.EndUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.openmhealth.dsu.configuration.OAuth2Properties.END_USER_ROLE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller that is intended for the internal use only, MUST be only accessed by localhost.
 * Created by Cheng-Kang Hsieh on 4/4/15.
 */
@Controller
public class InternalController {
    @Autowired
    ClientDetailsService clientService;
    @Autowired
    AuthorizationServerTokenServices tokenService;
    @Autowired
    UserDetailsService userDetailService;
    @Autowired
    EndUserService endUserService;
    @Autowired
    private Validator validator;


    /**
     * Authorize the client (client_id) the access token to the given user (username)
     * @param username username
     * @param client_id client id
     * @return access token in JSON
     */
    @RequestMapping(value = "/internal/token", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<OAuth2AccessToken> createAccessToken( @RequestParam String username,  @RequestParam String client_id){
        OAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientService);

        clientService.loadClientByClientId(client_id);
        UserDetails user = userDetailService.loadUserByUsername(username);
        HashMap<String, String> authorizationParameters = new HashMap<String, String>();
        authorizationParameters.put("username", username);
        authorizationParameters.put("client_id", client_id);
        AuthorizationRequest authorizationRequest = requestFactory.createAuthorizationRequest(authorizationParameters);
        authorizationRequest.setApproved(true);


        Authentication authToken = new UsernamePasswordAuthenticationToken(user, "", Collections.singleton(new SimpleGrantedAuthority(END_USER_ROLE)));
        OAuth2Authentication authenticationRequest = new OAuth2Authentication(requestFactory.createOAuth2Request(authorizationRequest), authToken);
        OAuth2AccessToken accessToken = tokenService.createAccessToken(authenticationRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");

        return new ResponseEntity<>(accessToken, headers, HttpStatus.OK);
    }


    /**
     * Registers a new user that use username and password to sign in. Currently, we only allow system administrator
     * to do so via backend.
     *
     * @param registrationData the registration data of the user
     * @return a response entity with status OK if the user is registered, BAD_REQUEST if the request is invalid,
     * or CONFLICT if the user exists
     */
    @RequestMapping(value = "/internal/users", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody EndUserRegistrationData registrationData) {

        if (registrationData == null) {
            return new ResponseEntity<>(BAD_REQUEST);
        }

        Set<ConstraintViolation<EndUserRegistrationData>> constraintViolations = validator.validate(registrationData);

        if (!constraintViolations.isEmpty()) {
            return new ResponseEntity<>(asErrorMessageList(constraintViolations), BAD_REQUEST);
        }

        if (endUserService.doesUserExist(registrationData.getUsername())) {
            return new ResponseEntity<>(CONFLICT);
        }

        endUserService.registerUser(registrationData);

        return new ResponseEntity<>(OK);
    }

    protected List<String> asErrorMessageList(Set<ConstraintViolation<EndUserRegistrationData>> constraintViolations) {

        return constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
    }


}
