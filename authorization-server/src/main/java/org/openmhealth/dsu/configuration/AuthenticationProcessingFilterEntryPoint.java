package org.openmhealth.dsu.configuration;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Cheng-Kang Hsieh on 9/24/15.
 */
public class AuthenticationProcessingFilterEntryPoint extends LoginUrlAuthenticationEntryPoint {
    public AuthenticationProcessingFilterEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws  ServletException, IOException {
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        String queryString;
        if(request.getParameter("gmailOnly") != null && request.getParameter("gmailOnly").equals("1")){
            redirectStrategy.sendRedirect(request, response, getLoginFormUrl()+"?gmailOnly=1");
        }else{
            redirectStrategy.sendRedirect(request, response, getLoginFormUrl());
        }
    }
}