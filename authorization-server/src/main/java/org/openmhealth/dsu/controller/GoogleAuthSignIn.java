package org.openmhealth.dsu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.util.JSONPObject;
import org.openmhealth.dsu.domain.EndUserRegistrationData;
import org.openmhealth.dsu.service.EndUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.google.config.GoogleApiHelper;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.google.security.GoogleAuthenticationService;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.util.*;

import static org.openmhealth.dsu.configuration.OAuth2Properties.END_USER_ROLE;

/**
 * Endpoints to facilitate Google Sign-In
 * @author Cheng-Kang Hsieh
 */
@Controller
public class GoogleAuthSignIn {
    private @Value("${application.url}") String rootUrl;
    @Autowired
    ClientDetailsService clientService;
    @Autowired
    AuthorizationServerTokenServices tokenService;
    @Autowired
    EndUserService endUserService;

    static class InvalidGoogleAccessTokenException extends RuntimeException{}

    private GoogleConnectionFactory googleConnFactory = new GoogleConnectionFactory("", "");


    /**
     * This endpoint is used to facilitate the sign-in process using One-Time Auth Code obtained from
     * Google Server-Side API Access.
     * (See: https://developers.google.com/+/mobile/ios/sign-in#enable_server-side_api_access_for_your_app)
     * A mobile app invoke this controller with the One-Time Auth Code and its DSU client id and secret in the Authorization header.
     * The controller will then perform the following operations:
     * 1) Sign-in DSU in behalf of the user with the One-Time Auth Code.
     * 2) Obtain DSU authorization code. This auth code is associated with the requesting oauth client's  (determined by the given client id and secret)
     *    and its default scopes.
     * 3) Exchange the code for the access token and return to the app.
     * @author Andy Hsieh
     */
    @RequestMapping(value="/google-signin", method= RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> GoogleAuthSignin(@RequestParam String code,
                       @RequestParam String client_id,
                       @RequestHeader(value="Authorization") String clientBasicAuth
                       ) throws Exception{

        // ** Step 1. Sign in using Google One-Time Auth Code
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(30000)
                .setSocketTimeout(30000).build();
        HttpClient client = HttpClients.custom()
                .disableRedirectHandling()
                .setDefaultRequestConfig(requestConfig).build();
        HttpGet signin = new HttpGet(rootUrl + "/auth/google?code="+code);
        HttpResponse res = client.execute(signin);
        // Check if sign-in succeeded
        if (res.getFirstHeader("Set-Cookie")==null
                || res.getFirstHeader("Location") == null
                || !res.getFirstHeader("Location").getValue().equals(rootUrl)) {
            // failed to sign in with the code
            Map<String, String> response = new HashMap<>();
            response.put("reason", "Failed to sign-in. The given Google One-Time Auth might be invalid.");
            return  new ResponseEntity<>(new ObjectMapper().writeValueAsString(response),
                                         HttpStatus.BAD_REQUEST);
        }
        res.getEntity().getContent().close();


        // ** Step 2. Send authorize request to DSU to obtain a DSU auth code **
        HttpGet getAuthCode = new HttpGet(rootUrl+"/oauth/authorize?client_id="+ client_id + "&response_type=code");
        String dsuAuthCode = null;
        res = client.execute(getAuthCode);
        if (res.getFirstHeader("Location") != null){
            // Extract the auth code from the redirect uri
            String locationUrl = res.getFirstHeader("Location").getValue();
            // parse the redirection uri and search for the "code" parameter
            List<NameValuePair> params = URLEncodedUtils.parse(new URI(locationUrl), "UTF-8");

            for (NameValuePair param : params) {
                if(param.getName().equals("code")){
                    dsuAuthCode = param.getValue();
                    break;
                }
            }
        }
        if (dsuAuthCode == null){
            Map<String, String> response = new HashMap<>();
            response.put("reason", String.format("Failed to obtain an auth code from DSU. Check your client id."));
            return  new ResponseEntity<>(new ObjectMapper().writeValueAsString(response),
                    HttpStatus.BAD_REQUEST);
        }
        res.getEntity().getContent().close();


        // ** Step 3. Send POST request to /oauth/token to exchange the auth code for the access token
        List<NameValuePair> data = new ArrayList<>(2);
        data.add(new BasicNameValuePair("grant_type", "authorization_code"));
        data.add(new BasicNameValuePair("code", dsuAuthCode));
        HttpPost exchangeForToken = new HttpPost(rootUrl + "/oauth/token");
        exchangeForToken.setEntity(new UrlEncodedFormEntity(data));
        // set Basic Authentication using the credential provided in the Authorization header of the current request.
        exchangeForToken.setHeader(new BasicHeader("Authorization", clientBasicAuth));
        // return the results.
        res = client.execute(exchangeForToken);
        return  new ResponseEntity<>(EntityUtils.toString(res.getEntity()),
                HttpStatus.valueOf(res.getStatusLine().getStatusCode()));


    }

    /**
     * This endpoint is used to facilitate the sign-in process using google access token.
     * The controller will then perform the following operations:
     * 1) Use the google access token to get user's profile
     * 2) Create the user if it does not exist
     * 3) Generate a DSU access token for the client
     */
    @RequestMapping(value="/google-signin", method= RequestMethod.POST, produces = "application/json")
    public ResponseEntity<OAuth2AccessToken>
        googleAccessTokenSignIn
            (@RequestParam String client_id,
             @RequestParam String client_secret,
             @RequestParam String google_access_token){

        // Make sure the client id/secret are correct
        // Throw NoSuchClientException
        ClientDetails client = clientService.loadClientByClientId(client_id);
        if(!client.getClientSecret().equals(client_secret)){
            throw new NoSuchClientException("");
        }
        OAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientService);
        // Get google connection using the access token
        // Throw org.springframework.web.client.HttpClientErrorException: 401 Unauthorized
        Connection<Google> conn;
        try {
            conn = googleConnFactory.createConnection(new AccessGrant(google_access_token));
        }catch (HttpClientErrorException ex){
              if(ex.getStatusCode().equals(HttpStatus.UNAUTHORIZED)){
                  throw new InvalidGoogleAccessTokenException();
              }else{
                  throw ex;
              }
        }
        if(!endUserService.doesUserExist(conn)){
            endUserService.registerUser(conn);
        }
        String username = endUserService.findUser(conn).get().getUsername();
        HashMap<String, String> authorizationParameters = new HashMap<String, String>();
        authorizationParameters.put("username", username);
        authorizationParameters.put("client_id", client_id);
        AuthorizationRequest authorizationRequest = requestFactory.createAuthorizationRequest(authorizationParameters);
        authorizationRequest.setApproved(true);


        Authentication authToken = new UsernamePasswordAuthenticationToken(username, "", Collections.singleton(new SimpleGrantedAuthority(END_USER_ROLE)));
        OAuth2Authentication authenticationRequest = new OAuth2Authentication(requestFactory.createOAuth2Request(authorizationRequest), authToken);
        OAuth2AccessToken accessToken = tokenService.createAccessToken(authenticationRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");

        return new ResponseEntity<OAuth2AccessToken>(accessToken, headers, HttpStatus.OK);
    }

    @ExceptionHandler(NoSuchClientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleNoSuchClientException(NoSuchClientException ex) {
        return "client id/secret is invalid";

    }
    @ExceptionHandler(InvalidGoogleAccessTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleNoSuchClientException(InvalidGoogleAccessTokenException ex) {
        return "google access token is invalid";

    }

}
