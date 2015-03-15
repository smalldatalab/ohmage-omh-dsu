/*
 * Copyright 2014 Open mHealth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openmhealth.dsu.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.security.GoogleAuthenticationService;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.social.security.SocialAuthenticationServiceRegistry;
import org.springframework.social.security.SpringSocialConfigurer;


/**
 * A Spring Security configuration that provides an authentication manager for user accounts and disables
 * Spring Boot's security configuration.
 *
 * @author Emerson Farrugia
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean(name = "authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/**/*.css", "/**/*.png", "/**/*.gif", "/**/*.jpg",
                        // allow check_token endpoint to be accessed by all
                        "/oauth/check_token");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
             // redirect all unauthenticated request to google sign-in
                .loginPage("/auth/google")
             .and()
             // permit unauthenticated access to favicon, signin page and auth pages for different social services
             .authorizeRequests()
                .antMatchers("/favicon.ico", "/auth/**", "/google-signin**", "/social-signin/**").permitAll()
                .antMatchers("/oauth/token", "/oauth/token", "/oauth/check_token").permitAll()
                .antMatchers("/**").authenticated()
             // enable cookie
              .and()
                .rememberMe()
              // apply Spring Social config that add Spring Social to be an AuthenticationProvider
              .and()
                .apply(new SpringSocialConfigurer())
              .and()
              // Disable CSRF protection FIXME: apply stricter access control to auth pages and oauth/authorize
              .csrf().disable();

    }
}
