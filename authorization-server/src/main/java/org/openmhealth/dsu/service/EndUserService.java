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
import org.springframework.social.connect.Connection;
import org.springframework.social.google.api.Google;

import java.util.Optional;


/**
 * A service that manages user accounts, supporting both plain-text username or social connection.
 *
 * @author Emerson Farrugia
 * @author Cheng-Kang Hsieh
 *
 *
 */
public interface EndUserService {

    boolean doesUserExist(String username);
    boolean doesUserExist(Connection<?> socialConnection);

    void registerUser(EndUserRegistrationData registrationData);
    void registerUser(Connection<?> socialConnection);


    Optional<EndUser> findUser(String username);
    Optional<EndUser> findUser(Connection<?> socialConnection);
}
