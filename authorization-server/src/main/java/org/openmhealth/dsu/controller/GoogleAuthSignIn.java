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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller is used to facilitate the sign-in process using the One-Time Auth Code obtained from
 * Google Server-Side API Access.
 * (See: https://developers.google.com/+/mobile/ios/sign-in#enable_server-side_api_access_for_your_app)
 * An app invoke this controller with the One-Time Auth Code and its DSU client id and secret.
 * The controller will do the following:
 * 1) Sign-in DSU with the One-Time Auth Code.
 * 2) Obtain authorization code from DSU. This auth code is associated with the given client id's default scopes.
 * 3) Exchange the code for the access token and return it to the app.
 * @author Andy Hsieh
 */
@Controller
public class GoogleAuthSignIn {
    private @Value("${application.url}") String rootUrl;

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
            response.put("reason", String.format("Failed to sign-in. The given Google One-Time Auth might be invalid.", code));
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


        // ** Step 4. Send POST request to /oauth/token to exchange the auth code for the access token
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
}
