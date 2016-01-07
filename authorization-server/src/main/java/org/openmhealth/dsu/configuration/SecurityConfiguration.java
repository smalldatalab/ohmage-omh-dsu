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
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
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
                .antMatchers("/css/**", "/images/**", "/js/**", "/fonts/**", "/favicon.ico");
    }

    /**
     *
     * @return a signin successHandler that redirect to the originally requested page
     */
    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler successHandler() {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setUseReferer(true);
        return successHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                // redirect all unauthenticated request to google sign-in
                .loginPage("/signin")
                    .successHandler(successHandler())
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .permitAll()

                .and()

                // permit unauthenticated access to favicon, signin page and auth pages for different social services
                .authorizeRequests()
                .antMatchers(
                        "/signin",
                        "/auth/**", // web social signin endpoints
                        "/social-signin/**", // mobile social signin endpoints
                        "/google-signin**" // mobile google social signin endpoint (FIXME: deprecated)
                ).permitAll()
                // oauth token endpoints
                // (oauth/authorize should only be accessed by users who have signin, so is excluded from here)
                .antMatchers("/oauth/token", "/oauth/check_token")
                    .permitAll()
                .antMatchers("/internal/**")
                    .hasIpAddress("127.0.0.1")// internal endpoints.
                .antMatchers("/**")
                    .authenticated()
                // enable cookie
                .and()
                .rememberMe()
                        // apply Spring Social config that add Spring Social to be an AuthenticationProvider
                .and()
                .apply(new SpringSocialConfigurer())
                .and()
                // Disable CSRF protection FIXME: apply stricter access control to auth pages and oauth/authorize
                .csrf().disable()
                // use a custom authentication entry point to preserve query parameter
                .exceptionHandling().authenticationEntryPoint(new AuthenticationProcessingFilterEntryPoint("/signin"));

    }

}
