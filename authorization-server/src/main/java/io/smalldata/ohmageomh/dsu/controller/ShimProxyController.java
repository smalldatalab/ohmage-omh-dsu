package io.smalldata.ohmageomh.dsu.controller;

import io.smalldata.ohmageomh.dsu.service.ShimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * Controller for users to connect (i.e. authorize us to access their data) via a shim server.
 * Created by Cheng-Kang Hsieh on 3/5/15.
 */
@Controller
public class ShimProxyController {
    private static final Logger log = LoggerFactory.getLogger(ShimProxyController.class);
    @Autowired
    ShimService shimService;

    @RequestMapping(value = "/shims/authorize/{shim}", method = RequestMethod.GET)
    public String authorize(Authentication auth, @PathVariable("shim") String shim,
                            Model model,
                            HttpServletRequest servletRequest) throws IOException {
        Boolean connected;
        try {
            String redirectUrl = shimService.getAuthorizationUrl(shim, auth, servletRequest);
            model.addAttribute("authorizationUrl", redirectUrl);
            connected = false;
        } catch (ShimService.UserIsAuthorizedException e) {
            connected = true;
        }
        model.addAttribute("appName", shim);
        model.addAttribute("connected", connected);
        return "connect";
    }

    @RequestMapping(value = "/shims/reauthorize/{shim}", method = RequestMethod.GET)
    public ModelAndView reauthorize(Authentication auth, @PathVariable("shim") String shim,
                                    HttpServletRequest servletRequest) throws IOException {


        try {
            shimService.deauthorize(shim, auth);
            String redirectUrl = shimService.getAuthorizationUrl(shim, auth, servletRequest);
            // immediate redirect to authorize url
            return new ModelAndView("redirect:" + redirectUrl);
        } catch (ShimService.UserIsAuthorizedException e) {
            throw new RuntimeException("De-authorization failed");
        }

    }

    @RequestMapping(value = "/shims/authorize/{shim}/callback", method = RequestMethod.GET)
    public String callback(Authentication auth,
                           @PathVariable("shim") String shim,
                           @RequestParam(required = false) String error,
                           Model model,
                           HttpServletRequest servletRequest) throws IOException {
        if (error == null) {
            model.addAttribute("appName", shim);
            return "connected";
        } else {
            return authorize(auth, shim, model, servletRequest);
        }
    }
}
