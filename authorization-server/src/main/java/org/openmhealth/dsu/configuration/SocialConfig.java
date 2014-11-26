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
package org.openmhealth.dsu.configuration;

import org.openmhealth.dsu.domain.EndUserRegistrationData;
import org.openmhealth.dsu.service.EndUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.*;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;

/**
 * Spring Social Configuration.
 * @author Keith Donald
 */
@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {
    @Autowired
    EndUserService endUserService;

    /**
     * Register google social service connection. More connections (Facebook, Twitter, etc)
     * can be made here too.
     * @param config
     * @param environment
     */
    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer config, Environment environment) {
        config.addConnectionFactory(
                new GoogleConnectionFactory(
                        environment.getProperty("google.clientId"),
                        environment.getProperty("google.clientSecret")));
    }


    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    /**
     * A social ConnectionSignUp implementation that automatically sign up any
     * users that have been connected with a social service (e.g. google)
     */
    private class ImplicitEndUserSignUp implements ConnectionSignUp{
        @Override
        public String execute(Connection<?> connection) {
            String username = connection.getKey().toString();
            if(!endUserService.doesUserExist(username)){
                EndUserRegistrationData newUser = new EndUserRegistrationData();
                newUser.setUsername(username);
                newUser.setPassword("");
                newUser.setEmailAddress(connection.fetchUserProfile().getEmail());
                endUserService.registerUser(newUser);

            }
            return  username;
        }
    }

    /**
     * Create an in-memory repository to store the connections with social services.
     * A signUp object is registered to implicitly sign up any users that have connected
     * with a social service.
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
     * @param connectionFactoryLocator
     * @param connectionRepository
     * @return
     */
    @Bean
    public ConnectController connectController(
            ConnectionFactoryLocator connectionFactoryLocator,
            ConnectionRepository connectionRepository) {
        return new ConnectController(connectionFactoryLocator, connectionRepository);
    }
}
