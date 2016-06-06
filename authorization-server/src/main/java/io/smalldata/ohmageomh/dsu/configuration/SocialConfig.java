/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.smalldata.ohmageomh.dsu.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmhealth.dsu.domain.EndUserRegistrationData;
import org.openmhealth.dsu.service.EndUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.*;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.connect.GoogleAdapter;
import org.springframework.social.google.connect.GoogleOAuth2Template;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.util.MultiValueMap;

/**
 * Spring Social Configuration.
 *
 * @author Andy Hsieh
 */
@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {

    private static final Logger log = LoggerFactory.getLogger(SocialConfig.class);

    @Autowired
    EndUserService endUserService;
    @Autowired
    Environment environment;
    private
    @Value("${application.url}")
    String rootUrl;
    private
    @Value("${google.scope}")
    String googleScope;

    protected final Log logger = LogFactory.getLog(getClass());

    String getCustomRedirectUri() {
        if (rootUrl != null) {
            return rootUrl + "auth/google";
        }
        return null;
    }

    class CustomGoogleOAuthTemplate extends GoogleOAuth2Template {

        public CustomGoogleOAuthTemplate(String clientId, String clientSecret) {
            super(clientId, clientSecret);
        }

        @Override
        public String buildAuthorizeUrl(GrantType grantType, OAuth2Parameters parameters) {
            // always prompt user for approval
            parameters.set("approval_prompt", "force");
            if (parameters.getScope() == null || parameters.getScope().equals("")) {
                // the scope that allow access to the user's email address and other profile
                parameters.setScope(googleScope);
            }

            // if the application.url is given, set the redirect_url to it to let a DSU behind
            // a proxy work
            if (getCustomRedirectUri() != null) {
                parameters.setRedirectUri(getCustomRedirectUri());
            }
            return super.buildAuthorizeUrl(grantType, parameters);
        }

        @Override
        @Deprecated
        public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
            // DEPRECATED: Allow mobile apps to direct send authorization code to /auth/google. e.g. /auth/google?code=fromApp_{code}
            // such auth code is prefixed by "fromApp_" and so is distinguishable from the code redirected from Google
            if (authorizationCode.startsWith("fromApp_")) {
                authorizationCode = authorizationCode.substring("fromApp_".length());
                // Google requires a special redirect_url to be used with the mobile auth code
                redirectUri = "urn:ietf:wg:oauth:2.0:oob";
            } // if the application.url is given, set the redirect_url as it to let a DSU behind
            // a proxy to work
            else if (getCustomRedirectUri() != null) {
                redirectUri = getCustomRedirectUri();
            }
            logger.info(String.format("code: %s, redirect_uri: %s", authorizationCode, redirectUri));
            return super.exchangeForAccess(authorizationCode, redirectUri, additionalParameters);
        }
    }

    class CustomGoogleOAuth2ServiceProvider extends AbstractOAuth2ServiceProvider<Google> {
        public CustomGoogleOAuth2ServiceProvider(String clientId, String clientSecret) {
            super(new CustomGoogleOAuthTemplate(clientId, clientSecret));
        }

        @Override
        public Google getApi(String accessToken) {
            return new GoogleTemplate(accessToken);
        }
    }

    /**
     * Register google social service connection. More connections (Facebook, Twitter, etc)
     * can be added here too.
     *
     * @param config
     * @param environment
     */
    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer config, Environment environment) {

        OAuth2ConnectionFactory<Google> google =
                new OAuth2ConnectionFactory<Google>("google",
                        new CustomGoogleOAuth2ServiceProvider(
                                environment.getProperty("google.clientId"),
                                environment.getProperty("google.clientSecret")
                        ),
                        new GoogleAdapter());
        config.addConnectionFactory(google);
    }


    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    /**
     * A social ConnectionSignUp implementation that automatically sign up any
     * users that have been connected with a social service (e.g. google)
     */
    private class ImplicitEndUserSignUp implements ConnectionSignUp {
        @Override
        public String execute(Connection<?> connection) {
            if (!endUserService.doesUserExist(connection.getKey().toString())) {
                EndUserRegistrationData registrationData = new EndUserRegistrationData();
                registrationData.setUsername(connection.getKey().toString());
                registrationData.setPassword(new RandomValueStringGenerator(50).generate());

                UserProfile profile = connection.fetchUserProfile();
                registrationData.setEmailAddress(profile.getEmail());
                log.info("Register user from social connection " + connection.getKey().toString());

                endUserService.registerUser(registrationData);
            }
            return endUserService.findUser(connection.getKey().toString()).get().getUsername();
        }
    }

    /**
     * Create an in-memory repository to store the connections with social services.
     * A signUp object is registered to implicitly sign up any users that have connected
     * with a social service.
     *
     * @param connectionFactoryLocator
     * @return a in-memory users connection repository
     */
    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        InMemoryUsersConnectionRepository connectionRepository = new InMemoryUsersConnectionRepository(connectionFactoryLocator);
        connectionRepository.setConnectionSignUp(new ImplicitEndUserSignUp());
        return connectionRepository;
    }

    /**
     * Controller that handler oauth flow with social services.
     *
     * @param connectionFactoryLocator
     * @param connectionRepository
     * @return
     */
    @Bean
    public ConnectController connectController(
            ConnectionFactoryLocator connectionFactoryLocator,
            ConnectionRepository connectionRepository) throws Exception {
        ConnectController connectController =
                new ConnectController(connectionFactoryLocator, connectionRepository);
        // specify application root url so that redirect_url sent to providers will be the correct one
        connectController.afterPropertiesSet();
        connectController.setApplicationUrl(environment.getProperty("application.url"));
        return connectController;
    }
}
