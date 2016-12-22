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

package io.smalldata.ohmageomh.data.service;

import io.smalldata.ohmageomh.data.domain.EndUser;
import io.smalldata.ohmageomh.data.domain.EndUserRegistrationData;
import io.smalldata.ohmageomh.data.domain.EndUserRegistrationException;
import io.smalldata.ohmageomh.data.repository.EndUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * @author Emerson Farrugia
 */
@Service
public class EndUserServiceImpl implements EndUserService {

    @Autowired
    private EndUserRepository endUserRepository;

    @Autowired
    private MongoOperations mongoOperations;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(readOnly = true)
    public boolean doesUserExist(String username) {

        return endUserRepository.findOne(username).isPresent();
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
    public List<EndUser> findAuthorizedUsers(String shimKey) {
        Query query = new Query();

        query.addCriteria(where("shimKey").is(shimKey));
        query.addCriteria(where("serializedToken").exists(true));
        query.fields().include("username");

        List<String> ids = mongoOperations.find(query, String.class, "accessParameters");

        Set<EndUser> users = new HashSet<>();
        for(String id : ids) {
            EndUser user = new EndUser();
            // How do I create a user with the given ID????
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EndUser> findUser(String username) {

        return endUserRepository.findOne(username);
    }
}