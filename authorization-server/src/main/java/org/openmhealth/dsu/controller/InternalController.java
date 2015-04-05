package org.openmhealth.dsu.controller;

import org.openmhealth.dsu.domain.EndUserUserDetails;
import org.openmhealth.dsu.service.EndUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.HashMap;

import static org.openmhealth.dsu.configuration.OAuth2Properties.END_USER_ROLE;

/**
 * Controller that is intended for the internal use only.
 * Created by Cheng-Kang Hsieh on 4/4/15.
 */
@Controller
public class InternalController {
    @Autowired
    ClientDetailsService clientService;
    @Autowired
    AuthorizationServerTokenServices tokenService;
    @Autowired
    UserDetailsService endUserService;

    @RequestMapping(value = "/internal/token", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<OAuth2AccessToken> createAccessToken( @RequestParam String username,  @RequestParam String client_id){
        OAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientService);

        clientService.loadClientByClientId(client_id);
        UserDetails user = endUserService.loadUserByUsername(username);
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
}
