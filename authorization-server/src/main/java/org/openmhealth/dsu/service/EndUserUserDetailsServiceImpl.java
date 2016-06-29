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
import org.openmhealth.dsu.domain.EndUserUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * A user details service that loads user details from an end user service.
 *
 * @author Emerson Farrugia
 * @author Jared Sieling
 */
@Service
public class EndUserUserDetailsServiceImpl implements UserDetailsService, SocialUserDetailsService {

    @Autowired
    private EndUserService endUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<EndUser> user = endUserService.findUser(username);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException("A user with username '" + username + "' doesn't exist.");
        }

        return new EndUserUserDetails(user.get().getUsername(), user.get().getPasswordHash());
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
        UserDetails userDetails = loadUserByUsername(userId);
        return new SocialUser(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
    }
}
