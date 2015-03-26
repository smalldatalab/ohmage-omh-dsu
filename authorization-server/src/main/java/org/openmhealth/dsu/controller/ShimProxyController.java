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
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
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
    private String captalize (String s){
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }


    @RequestMapping(value="/shims/authorize/{shim}", method= RequestMethod.GET)
    public String authorize(Authentication auth, @PathVariable("shim") String shim,
                            Model model,
                            HttpServletRequest servletRequest) throws IOException {



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
            // if the page is viewed on a mobile phone, use phone-specific uri instead
            // FIXME, this should be done at the shim server.
            if(DeviceUtils.getCurrentDevice(servletRequest).isMobile() && redirectUrl.startsWith("https://api.moves-app.com/oauth/v1/authorize")){
                redirectUrl = redirectUrl.replace("https://api.moves-app.com/oauth/v1/authorize", "moves://app/authorize");

            }
            model.addAttribute("appName", captalize(shim));
            model.addAttribute("url", redirectUrl);
            return "connect";
        }else{
            throw new IOException("Shim returns invalid auth url" + redirectUrl);
        }
    }


    @RequestMapping(value="/shims/authorize/{shim}/callback", method= RequestMethod.GET)
    public String callback(Authentication auth,
                           @PathVariable("shim") String shim,
                           @RequestParam(required = false) String error,
                           Model model,
                           HttpServletRequest servletRequest) throws IOException {
        if(error == null) {
            model.addAttribute("appName", captalize(shim));
            return "connected";
        }else{
            return authorize(auth, shim, model, servletRequest);
        }
    }
}
