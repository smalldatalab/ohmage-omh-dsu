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

package org.openmhealth.dsu.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


/**
 * A controller that manages user accounts.
 *
 * @author Emerson Farrugia
 */
@Controller
public class EndUserController {
    @Autowired
    ConnectionRepository connectionRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public String home() {
        return "You have signed in.";
    }

    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public String singout() throws ServletException {
        return "signin";
    }

    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    @ResponseBody
    public String singout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "You have signed out.";
    }
}
