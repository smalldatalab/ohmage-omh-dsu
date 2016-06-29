package io.smalldata.ohmageomh.dsu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Service that communicate with the shims server to get authorization url or remove the previous authorization.
 * Created by Cheng-Kang Hsieh on 3/31/15.
 */
@Service
public class ShimServiceImpl implements ShimService {
    private static final Logger log = LoggerFactory.getLogger(ShimService.class);
    private
    @Value("${internalShimServer.url}")
    String internalShimUrl;
    private
    @Value("${application.url}")
    String rootUrl;
    private
    @Autowired
    ObjectMapper mapper;


    private String getCallbackUrl(String shim) {
        return UriComponentsBuilder.fromUriString(rootUrl)
                .pathSegment("shims", "authorize", shim, "callback")
                .build()
                .toUriString();
    }

    private URI getInternalShimAuthorizeUrl(String shim, Authentication auth) {
        return UriComponentsBuilder.fromUriString(internalShimUrl)
                .pathSegment("authorize", shim)
                .queryParam("client_redirect_url", getCallbackUrl(shim))
                .queryParam("username", auth.getName()).build().toUri();
    }

    private URI getInternalShimDeauthorizeUrl(String shim, Authentication auth) {
        return UriComponentsBuilder.fromUriString(internalShimUrl)
                .pathSegment("de-authorize", shim)
                .queryParam("username", auth.getName()).build().toUri();
    }

    @Override
    public String getAuthorizationUrl(String shim, Authentication auth, HttpServletRequest servletRequest) throws UserIsAuthorizedException, IOException {
        HttpClient client = HttpClients.createDefault();
        // get the authorization url
        log.info(getInternalShimAuthorizeUrl(shim, auth).toString());
        HttpGet shimRequest = new HttpGet(getInternalShimAuthorizeUrl(shim, auth));
        shimRequest.setHeader("Accept", "application/json");

        // extract authorization URL from the response
        InputStream in = client.execute(shimRequest).getEntity().getContent();
        JsonNode node = mapper.readTree(in);
        if (node.has("isAuthorized") && node.get("isAuthorized").asBoolean()) {
            log.info("User " + auth.getName() + " has connected with " + shim);
            throw new UserIsAuthorizedException();
        } else if (node.has("authorizationUrl")) {
            // FIXME: What if Shim server fail
            String redirectUrl = node.get("authorizationUrl").textValue();
            // if the page is viewed on a mobile phone, use phone-specific uri instead
            // FIXME, this should be done at the shim server.
            if (DeviceUtils.getCurrentDevice(servletRequest).isMobile() && redirectUrl.startsWith("https://api.moves-app.com/oauth/v1/authorize")) {
                redirectUrl = redirectUrl.replace("https://api.moves-app.com/oauth/v1/authorize", "moves://app/authorize");

            }
            return redirectUrl;
        } else {
            throw new RuntimeException("Unexpected response from shim server");
        }
    }

    @Override
    public boolean deauthorize(String shim, Authentication auth) throws IOException {
        HttpClient client = HttpClients.createDefault();
        // de-authorize the user first
        HttpDelete shimDeauthorizeRequest = new HttpDelete(getInternalShimDeauthorizeUrl(shim, auth));
        return client.execute(shimDeauthorizeRequest).getStatusLine().getStatusCode() == 200;
    }

    @Override
    public boolean isAuthorized(String shim, Authentication auth) throws IOException {
        HttpClient client = HttpClients.createDefault();
        // get the authorization url
        log.info(getInternalShimAuthorizeUrl(shim, auth).toString());
        HttpGet shimRequest = new HttpGet(getInternalShimAuthorizeUrl(shim, auth));
        shimRequest.setHeader("Accept", "application/json");

        // extract authorization URL from the response
        InputStream in = client.execute(shimRequest).getEntity().getContent();
        JsonNode node = mapper.readTree(in);
        return node.has("isAuthorized") && node.get("isAuthorized").asBoolean();
    }
}
