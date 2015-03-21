package org.openmhealth.dsu.controller;

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
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * Created by Cheng-Kang Hsieh on 3/5/15.
 */
@Controller
public class ShimProxyController {
    private static final Logger log = LoggerFactory.getLogger(ShimProxyController.class);

    private @Value("${internalShimServer.url}") String internalShimUrl;
    private @Value("${application.url}") String rootUrl;
    private @Autowired ObjectMapper mapper;


    private String getCallbackUrl(String shim){
        return UriComponentsBuilder.fromUriString(rootUrl)
                .pathSegment("shims", "authorize", shim, "callback")
                .build()
                .toUriString();
    }
    private URI getInternalShimAuthorizeUrl(String shim, Authentication auth){
        return UriComponentsBuilder.fromUriString(internalShimUrl)
                .pathSegment("authorize", shim)
                .queryParam("client_redirect_url", getCallbackUrl(shim))
                .queryParam("username", auth.getName()).build().toUri();
    }
    private URI getInternalShimDeauthorizeUrl(String shim, Authentication auth){
        return UriComponentsBuilder.fromUriString(internalShimUrl)
                .pathSegment("de-authorize", shim)
                .queryParam("username", auth.getName()).build().toUri();
    }
    @RequestMapping(value="/shims/authorize/{shim}", method= RequestMethod.GET)
    @ResponseBody
    public RedirectView authorize(Authentication auth, @PathVariable("shim") String shim, HttpServletRequest servletRequest) throws IOException {



        HttpClient client = HttpClients.createDefault();

        // de-authorize the user first
        HttpDelete shimDeauthorizeRequest = new HttpDelete(getInternalShimDeauthorizeUrl(shim, auth));
        client.execute(shimDeauthorizeRequest).getStatusLine();

        // get the authorization url
        log.info(getInternalShimAuthorizeUrl(shim, auth).toString());
        HttpGet shimRequest = new HttpGet(getInternalShimAuthorizeUrl(shim, auth));
        shimRequest.setHeader("Accept", "application/json");

        // extract authorization URL from the response
        InputStream in = client.execute(shimRequest).getEntity().getContent();
        String redirectUrl = mapper.readTree(in).get("authorizationUrl").textValue();

        // check if it is a valid URL
        if (UrlUtils.isAbsoluteUrl(redirectUrl)) {
            if(DeviceUtils.getCurrentDevice(servletRequest).isMobile() && redirectUrl.startsWith("https://api.moves-app.com/oauth/v1/authorize")){
                redirectUrl = redirectUrl.replace("https://api.moves-app.com/oauth/v1/authorize", "moves://app/authorize");

            }
            // redirect user to that url
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectUrl);
            return redirectView;
        }else{
            throw new IOException("Shim returns invalid auth url" + redirectUrl);
        }
    }


    @RequestMapping(value="/shims/authorize/{shim}/callback", method= RequestMethod.GET)
    @ResponseBody
    public String callback(Authentication auth, @PathVariable("shim") String shim) {
        return "You have connected your " + shim + ".";
    }
}
