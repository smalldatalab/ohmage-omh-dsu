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

package org.openmhealth.dsu.service;

import org.openmhealth.dsu.domain.EndUser;
import org.openmhealth.dsu.domain.EndUserRegistrationData;
import org.openmhealth.dsu.domain.EndUserRegistrationException;
import org.openmhealth.dsu.repository.EndUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.OffsetDateTime;
import java.util.Optional;


/**
 * @author Emerson Farrugia
 */
@Service
public class EndUserServiceImpl implements EndUserService {

    @Autowired
    private EndUserRepository endUserRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Logger log = LoggerFactory.getLogger(EndUserServiceImpl.class);

    private String socialConnectionToUsername(Connection<?> socialConnection){
        return socialConnection.getKey().toString();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean doesUserExist(String username) {

        return endUserRepository.findOne(username).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean doesUserExist(Connection<?> socialConnection) {

        return endUserRepository.findOne(socialConnectionToUsername(socialConnection)).isPresent();
    }

    @Override
    @Transactional
    public void registerUser(EndUserRegistrationData registrationData) {

        if (doesUserExist(registrationData.getUsername())) {
            throw new EndUserRegistrationException(registrationData);
        }

        EndUser endUser = new EndUser();
        endUser.setUsername(registrationData.getUsername());
        endUser.setPasswordHash(passwordEncoder.encode(registrationData.getPassword()));
        endUser.setRegistrationTimestamp(OffsetDateTime.now());
        endUser.setFirstName(registrationData.getFirstName());
        endUser.setLastName(registrationData.getLastName());
        if (registrationData.getEmailAddress() != null) {
            try {
                endUser.setEmailAddress(new InternetAddress(registrationData.getEmailAddress()));
            }
            catch (AddressException e) {
                throw new EndUserRegistrationException(registrationData, e);
            }
        }

        endUserRepository.save(endUser);
    }

    @Override
    public void registerUser(Connection<?> socialConnection) {
        EndUserRegistrationData newUser = new EndUserRegistrationData();
        newUser.setUsername(socialConnectionToUsername(socialConnection));
        // use random password
        newUser.setPassword(new RandomValueStringGenerator(50).generate());
        ;

        UserProfile profile = socialConnection.fetchUserProfile();
        newUser.setEmailAddress(profile.getEmail());
        log.info("Register user from social connection" + profile.getFirstName() + profile.getLastName() + profile.getEmail());
        newUser.setFirstName(profile.getFirstName());
        newUser.setLastName(profile.getLastName());

        this.registerUser(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EndUser> findUser(String username) {

        return endUserRepository.findOne(username);
    }

    @Override
    public Optional<EndUser> findUser(Connection<?> socialConnection) {
        return endUserRepository.findOne(socialConnectionToUsername(socialConnection));
    }
}